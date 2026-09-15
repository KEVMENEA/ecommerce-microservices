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

                echo "Deploying to ecommerce-prod..."

                ssh -i "$SSH_KEY" \
                    -o IdentitiesOnly=yes \
                    -o StrictHostKeyChecking=no \
                    meneakev@34.97.42.240 \
                    'cd /opt/ecommerce &&
                     docker compose --env-file .env.prod -f docker-compose.prod.yml pull &&
                     docker compose --env-file .env.prod -f docker-compose.prod.yml up -d'

                echo "Production deployment command completed."
            '''
        }
    }
}