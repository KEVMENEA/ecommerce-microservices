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

        echo "Waiting for production to become healthy..."

        MAX_ATTEMPTS=18
        SLEEP_SECONDS=10

        HEALTH_RESPONSE=""

        for ATTEMPT in $(seq 1 $MAX_ATTEMPTS)
        do
            echo "Health check attempt $ATTEMPT/$MAX_ATTEMPTS..."

            if HEALTH_RESPONSE=$(curl \
                --fail \
                --silent \
                --show-error \
                --connect-timeout 5 \
                --max-time 10 \
                "$HEALTH_URL")
            then
                echo "API Gateway is responding."
                break
            fi

            if [ "$ATTEMPT" -eq "$MAX_ATTEMPTS" ]; then
                echo "ERROR: Production did not become healthy."
                exit 1
            fi

            sleep $SLEEP_SECONDS
        done

        echo "$HEALTH_RESPONSE"

        echo "Checking Eureka registrations..."

        for SERVICE in \
            api-gateway \
            product-service \
            user-service \
            inventory-service \
            order-service
        do
            echo "Checking $SERVICE..."

            echo "$HEALTH_RESPONSE" | grep -q "\\"$SERVICE\\"" || {
                echo "ERROR: $SERVICE is not registered in Eureka."
                exit 1
            }

            echo "$SERVICE registered."
        done

        echo ""
        echo "All required services are registered."
        echo "Production health check passed."
    '''
    }
}