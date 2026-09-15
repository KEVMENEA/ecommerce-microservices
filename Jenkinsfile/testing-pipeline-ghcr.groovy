node {
    stage('Test Production SSH') {
        withCredentials([
                sshUserPrivateKey(
                        credentialsId: 'ecommerce-prod-ssh',
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                )
        ]) {
            sh '''
                chmod 600 "$SSH_KEY"

                echo "Testing private key..."
                ssh-keygen -y -f "$SSH_KEY" > /dev/null

                echo "Private key is valid."

                ssh -i "$SSH_KEY" \
                    -o StrictHostKeyChecking=no \
                    "$SSH_USER@34.97.42.240" \
                    "hostname && whoami && docker --version"
            '''
        }
    }
}