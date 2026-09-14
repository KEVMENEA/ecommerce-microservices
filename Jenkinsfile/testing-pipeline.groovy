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

        stage('Test Product Service') {
            steps {
                dir('product-service') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean test'
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

        stage('Test Inventory Service') {
            steps {
                dir('inventory-service') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean test'
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

        stage('Test Eureka Server') {
            steps {
                dir('eureka-server') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean test'
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
}