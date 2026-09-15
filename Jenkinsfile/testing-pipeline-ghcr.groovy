node {
    stage('Deploy Production') {
        withCredentials([
                file(
                        credentialsId: 'ecommerce-prod-key-file',
                        variable: 'SSH_KEY'
                )
        ]) {
            sh '''
                chmod 600 "$SSH_KEY"

                echo "Deploying latest images to ecommerce-prod..."

                ssh -i "$SSH_KEY" \
                    -o IdentitiesOnly=yes \
                    -o StrictHostKeyChecking=no \
                    meneakev@34.97.42.240 \
                    'cd /opt/ecommerce &&
                     docker compose --env-file .env.prod -f docker-compose.prod.yml pull &&
                     docker compose --env-file .env.prod -f docker-compose.prod.yml up -d'

                echo "Production deployment completed."
            '''
        }
    }

    stage('Production Health Check') {
        sh '''
            echo "Waiting for production services to stabilize..."
            sleep 30

            echo "Checking API Gateway health..."

            curl --fail --silent --show-error \
                --retry 5 \
                --retry-delay 10 \
                --retry-all-errors \
                https://ecommerce.meneakevit.store/actuator/health

            echo ""
            echo "Production health check passed."
        '''
    }
}