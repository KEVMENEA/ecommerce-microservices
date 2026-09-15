pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test API Gateway') {
            steps {
                dir('apigateway') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean test'
                }
            }
        }
        stage('SonarQube - API Gateway') {
            steps {
                dir('apigateway') {
                    withSonarQubeEnv('sonarqube') {
                        sh '''
                            ./gradlew sonar \
                              -Dsonar.projectKey=ecommerce-api-gateway \
                              -Dsonar.projectName=ecommerce-api-gateway
                        '''
                    }
                }
            }
        }


        stage('Test Product Service') {
            steps {
                dir('product-service') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean test'
                }
            }
        }

        stage('SonarQube - Product Service') {
            steps {
                dir('productservice') {
                    withSonarQubeEnv('sonarqube') {
                        sh '''
                            ./gradlew sonar \
                              -Dsonar.projectKey=ecommerce-product-service \
                              -Dsonar.projectName=ecommerce-product-service 
                        '''
                    }
                }
            }
        }
        stage('Test User Service') {
            steps {
                dir('user-service') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean test'
                }
            }
        }
        stage('SonarQube - User Service') {
            steps {
                dir('userservice') {
                    withSonarQubeEnv('sonarqube') {
                        sh '''
                            ./gradlew sonar \
                              -Dsonar.projectKey=ecommerce-user-service \
                              -Dsonar.projectName=ecommerce-user-service 
                        '''
                    }
                }
            }
        }


        stage('Test Inventory Service') {
            steps {
                dir('inventory-service') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean test'
                }
            }
        }

        stage('SonarQube - Inventory Service') {
            steps {
                dir('inventoryservice') {
                    withSonarQubeEnv('sonarqube') {
                        sh '''
                            ./gradlew sonar \
                              -Dsonar.projectKey=ecommerce-inventory-service \
                              -Dsonar.projectName=ecommerce-inventory-service 
                        '''
                    }
                }
            }
        }
        stage('Test Order Service') {
            steps {
                dir('order-service') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean test'
                }
            }
        }
        stage('SonarQube - Order Service') {
            steps {
                dir('orderservice') {
                    withSonarQubeEnv('sonarqube') {
                        sh '''
                            ./gradlew sonar \
                              -Dsonar.projectKey=ecommerce-order-service \
                              -Dsonar.projectName=ecommerce-order-service 
                        '''
                    }
                }
            }
        }

        stage('Test Eureka Server') {
            steps {
                dir('eureka-server') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean test'
                }
            }
        }

        stage('SonarQube - Eureka Server') {
            steps {
                dir('eurekaserver') {
                    withSonarQubeEnv('sonarqube') {
                        sh '''
                            ./gradlew sonar \
                              -Dsonar.projectKey=ecommerce-eureka-server \
                              -Dsonar.projectName=ecommerce-eureka-server
                        '''
                    }
                }
            }
        }

        stage('Test Config Server') {
            steps {
                dir('configserver') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean test'
                }
            }
        }
    }


    stage('SonarQube - Config Server') {
        steps {
            dir('configserver') {
                withSonarQubeEnv('sonarqube') {
                    sh '''
                            ./gradlew sonar \
                              -Dsonar.projectKey=ecommerce-config-server \
                              -Dsonar.projectName=ecommerce-config-server
                        '''
                }
            }
        }
    }
}