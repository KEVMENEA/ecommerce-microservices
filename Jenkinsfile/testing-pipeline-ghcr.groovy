node {
    stage('Test Production SSH') {
        withCredentials([
                file(
                        credentialsId: 'ecommerce-prod-key-file',
                        variable: 'SSH_KEY'
                )
        ]) {
            sh '''
                chmod 600 "$SSH_KEY"

                echo "Testing private key..."
                ssh-keygen -y -f "$SSH_KEY" > /dev/null

                echo "Private key is valid."

                ssh -i "$SSH_KEY" \
                    -o IdentitiesOnly=yes \
                    -o StrictHostKeyChecking=no \
                    meneakev@34.97.42.240 \
                    "hostname && whoami && docker --version"
            '''
        }
    }
}