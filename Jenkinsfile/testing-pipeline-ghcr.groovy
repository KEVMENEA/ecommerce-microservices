pipeline {
    agent any
    {

        {
            stage('Login to GHCR') {
                steps {
                    withCredentials([
                            usernamePassword(
                                    credentialsId: 'github-ghcr',
                                    usernameVariable: 'GHCR_USERNAME',
                                    passwordVariable: 'GHCR_TOKEN'
                            )
                    ]) {
                        sh '''
                echo "$GHCR_TOKEN" | docker login ghcr.io \
                    -u "$GHCR_USERNAME" \
                    --password-stdin
            '''
                    }
                }
            }

            stage('Build Docker Images') {
                steps {
                    script {
                        sh '''
                docker build \
                    -t ghcr.io/kevmenea/ecommerce-api-gateway:${BUILD_NUMBER} \
                    -t ghcr.io/kevmenea/ecommerce-api-gateway:latest \
                    ./apigateway

                docker build \
                    -t ghcr.io/kevmenea/ecommerce-product-service:${BUILD_NUMBER} \
                    -t ghcr.io/kevmenea/ecommerce-product-service:latest \
                    ./product-service

                docker build \
                    -t ghcr.io/kevmenea/ecommerce-user-service:${BUILD_NUMBER} \
                    -t ghcr.io/kevmenea/ecommerce-user-service:latest \
                    ./user-service

                docker build \
                    -t ghcr.io/kevmenea/ecommerce-inventory-service:${BUILD_NUMBER} \
                    -t ghcr.io/kevmenea/ecommerce-inventory-service:latest \
                    ./inventory-service

                docker build \
                    -t ghcr.io/kevmenea/ecommerce-order-service:${BUILD_NUMBER} \
                    -t ghcr.io/kevmenea/ecommerce-order-service:latest \
                    ./order-service

                docker build \
                    -t ghcr.io/kevmenea/ecommerce-eureka-server:${BUILD_NUMBER} \
                    -t ghcr.io/kevmenea/ecommerce-eureka-server:latest \
                    ./eureka-server

                docker build \
                    -t ghcr.io/kevmenea/ecommerce-config-server:${BUILD_NUMBER} \
                    -t ghcr.io/kevmenea/ecommerce-config-server:latest \
                    ./configserver
            '''
                    }
                }
            }

            stage('Push Docker Images to GHCR') {
                steps {
                    sh '''
            for IMAGE in \
                ecommerce-api-gateway \
                ecommerce-product-service \
                ecommerce-user-service \
                ecommerce-inventory-service \
                ecommerce-order-service \
                ecommerce-eureka-server \
                ecommerce-config-server
            do
                docker push ghcr.io/kevmenea/$IMAGE:${BUILD_NUMBER}
                docker push ghcr.io/kevmenea/$IMAGE:latest
            done
        '''
                }
            }

            stage('Logout from GHCR') {
                steps {
                    sh 'docker logout ghcr.io || true'
                }
            }
        }
    }
}