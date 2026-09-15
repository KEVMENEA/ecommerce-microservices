stage('Test Production SSH') {
    steps {
        sshagent(credentials: ['ecommerce-prod-ssh']) {
            sh '''
                ssh -o StrictHostKeyChecking=no meneakev@34.97.42.240 \
                  "hostname && whoami && docker --version"
            '''
        }
    }
}