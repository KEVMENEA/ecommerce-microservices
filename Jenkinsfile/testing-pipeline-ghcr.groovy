node {
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
            echo "Waiting for production services to stabilize..."
            sleep 30

            HEALTH_URL="https://ecommerce.meneakevit.store/actuator/health"

            echo "Checking API Gateway and Eureka registrations..."

            HEALTH_RESPONSE=$(curl \
                --fail \
                --silent \
                --show-error \
                --retry 5 \
                --retry-delay 10 \
                --retry-all-errors \
                "$HEALTH_URL")

            echo "$HEALTH_RESPONSE"

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
            done

            echo ""
            echo "All required services are registered."
            echo "Production health check passed."
        '''
    }
}