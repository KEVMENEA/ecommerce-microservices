node {
    stage('Checkout') {
        deleteDir()
        checkout scm

        sh '''
            echo "Building Git commit:"
            git rev-parse HEAD

            echo "Gateway Config Client dependency:"
            grep "spring-cloud-starter-config" apigateway/build.gradle
        '''
    }

    stage('Build Docker Images') {
        sh """
            echo "Building Docker images for release ${BUILD_NUMBER}..."

            docker build \
                -t ghcr.io/kevmenea/ecommerce-api-gateway:${BUILD_NUMBER} \
                ./apigateway

            docker build \
                -t ghcr.io/kevmenea/ecommerce-product-service:${BUILD_NUMBER} \
                ./product-service

            docker build \
                -t ghcr.io/kevmenea/ecommerce-user-service:${BUILD_NUMBER} \
                ./user-service

            docker build \
                -t ghcr.io/kevmenea/ecommerce-inventory-service:${BUILD_NUMBER} \
                ./inventory-service

            docker build \
                -t ghcr.io/kevmenea/ecommerce-order-service:${BUILD_NUMBER} \
                ./order-service

            docker build \
                -t ghcr.io/kevmenea/ecommerce-eureka-server:${BUILD_NUMBER} \
                ./eureka-server

            docker build \
                -t ghcr.io/kevmenea/ecommerce-config-server:${BUILD_NUMBER} \
                ./configserver

            echo "All Docker images built successfully."
        """
    }

    stage('Push Docker Images to GHCR') {
        withCredentials([
                usernamePassword(
                        credentialsId: 'github-ghcr',
                        usernameVariable: 'GHCR_USERNAME',
                        passwordVariable: 'GHCR_TOKEN'
                )
        ]) {
            sh """
                echo "\$GHCR_TOKEN" | docker login ghcr.io \
                    -u "\$GHCR_USERNAME" \
                    --password-stdin

                docker push ghcr.io/kevmenea/ecommerce-api-gateway:${BUILD_NUMBER}
                docker push ghcr.io/kevmenea/ecommerce-product-service:${BUILD_NUMBER}
                docker push ghcr.io/kevmenea/ecommerce-user-service:${BUILD_NUMBER}
                docker push ghcr.io/kevmenea/ecommerce-inventory-service:${BUILD_NUMBER}
                docker push ghcr.io/kevmenea/ecommerce-order-service:${BUILD_NUMBER}
                docker push ghcr.io/kevmenea/ecommerce-eureka-server:${BUILD_NUMBER}
                docker push ghcr.io/kevmenea/ecommerce-config-server:${BUILD_NUMBER}

                docker logout ghcr.io

                echo "All Docker images pushed successfully."
            """
        }
    }
    stage('Deploy Production') {
        withCredentials([
                file(
                        credentialsId: 'ecommerce-prod-key-file',
                        variable: 'SSH_KEY'
                )
        ]) {
            sh """
                chmod 600 "\$SSH_KEY"

                echo "Deploying build ${BUILD_NUMBER} to ecommerce-prod..."

                ssh -i "\$SSH_KEY" \
                    -o IdentitiesOnly=yes \
                    -o StrictHostKeyChecking=no \
                    meneakev@34.97.42.240 \
                    'cd /opt/ecommerce &&
                     IMAGE_TAG=${BUILD_NUMBER} docker compose \
                       --env-file .env.prod \
                       -f docker-compose.prod.yml pull &&
                     IMAGE_TAG=${BUILD_NUMBER} docker compose \
                       --env-file .env.prod \
                       -f docker-compose.prod.yml up -d'

                echo "Production deployment completed."
            """
        }
    }

    stage('Production Health Check') {
        sh '''
        HEALTH_URL="https://ecommerce.meneakevit.store/actuator/health"

        MAX_ATTEMPTS=24
        SLEEP_SECONDS=10

        echo "Waiting for production and Eureka registrations..."

        for ATTEMPT in $(seq 1 $MAX_ATTEMPTS)
        do
            echo ""
            echo "Health check attempt $ATTEMPT/$MAX_ATTEMPTS..."

            HEALTH_RESPONSE=$(curl \
                --fail \
                --silent \
                --show-error \
                --connect-timeout 5 \
                --max-time 10 \
                "$HEALTH_URL" 2>/dev/null) || HEALTH_RESPONSE=""

            if [ -z "$HEALTH_RESPONSE" ]; then
                echo "API Gateway is not ready yet."
            else
                echo "API Gateway is responding."

                ALL_REGISTERED=true

                for SERVICE in \
                    api-gateway \
                    product-service \
                    user-service \
                    inventory-service \
                    order-service
                do
                    if echo "$HEALTH_RESPONSE" | grep -q "\\"$SERVICE\\""; then
                        echo "  OK: $SERVICE"
                    else
                        echo "  WAITING: $SERVICE"
                        ALL_REGISTERED=false
                    fi
                done

                if [ "$ALL_REGISTERED" = "true" ]; then
                    echo ""
                    echo "All required services are registered."
                    echo "Production health check passed."
                    exit 0
                fi
            fi

            if [ "$ATTEMPT" -eq "$MAX_ATTEMPTS" ]; then
                echo ""
                echo "ERROR: Production did not become fully ready."
                exit 1
            fi

            echo "Waiting ${SLEEP_SECONDS}s before retry..."
            sleep "$SLEEP_SECONDS"
        done
    '''
    }
}