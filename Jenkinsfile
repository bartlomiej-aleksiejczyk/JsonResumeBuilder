pipeline {
    agent any
    environment {
        // Define your image name here
        IMAGE_NAME = "${JOB_NAME}".toLowerCase().replaceAll(/[^a-z0-9._-]/, '-')
        // Define a tag for your image
        IMAGE_TAG = 'latest'
        RABBITMQ_HOST = "${env.RABBITMQ_HOST}"
        RABBITMQ_PORT = "${env.RABBITMQ_PORT}"
        RABBITMQ_VHOST = "${env.RABBITMQ_VHOST}"

        DB_HOST = "${env.DB_HOST}"
        DB_PORT = "${env.DB_PORT}"
        DB_DATABASE = "${env.PROD_DB_NAME}"

        // Define common environment variables here
        CELERY_BROKER_URL = 'amqp://user:pass@rabbitmqhost//'
        SCHEDULER_API_URL = 'http://localhost:5000/status'
        APP_URL = 'http://192.168.1.28/laravel-app'
        RABBITMQ_QUEUE = 'json-cv-builder'
    // Add other Laravel specific environment variables as needed
    }
    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }
        stage('Get Host IP') {
            steps {
                script {
                    // Using a shell command to get the host IP address. Adjust the command according to your OS and network configuration.
                    // Note the use of double dollar signs ($$) to escape the dollar sign in the Groovy string.
                    env.HOST_IP = sh(script: "hostname -I | awk '{print \$1}'", returnStdout: true).trim()
                }
            }
        }
        stage('Ensure Traefik is Running') {
            steps {
                ensureTraefik() // Call the shared library step to ensure Traefik is running
            }
        }
        stage('Build and Run Docker Containers') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'database-config', passwordVariable: 'DB_PASSWORD', usernameVariable: 'DB_USERNAME'),
                     usernamePassword(credentialsId: 'rabbitmq-credentials', passwordVariable: 'RABBITMQ_PASSWORD', usernameVariable: 'RABBITMQ_USER'),
                     string(credentialsId: 'rabbitmq-url-with-credentials', variable: 'CELERY_BROKER_URL'),
                     string(credentialsId: 'laravel-app-key-resume-builder', variable: 'APP_URL')]) {
                        // Inject Jenkins environment variables into Docker Compose
                        withEnv([
                        "CELERY_BROKER_URL=${env.CELERY_BROKER_URL}",
                        "SCHEDULER_API_URL=${env.SCHEDULER_API_URL}",
                        "APP_KEY=${env.APP_KEY}",
                        "APP_URL=${env.APP_URL}",
                        "DB_CONNECTION=${env.DB_CONNECTION}",
                        "DB_HOST=${env.DB_HOST}",
                        "DB_PORT=${env.DB_PORT}",
                        "DB_DATABASE=${env.DB_DATABASE}",
                        "DB_USERNAME=${env.DB_USERNAME}",
                        "DB_PASSWORD=${env.DB_PASSWORD}",
                        "RABBITMQ_HOST=${env.RABBITMQ_HOST}",
                        "RABBITMQ_PORT=${env.RABBITMQ_PORT}",
                        "RABBITMQ_VHOST=${env.RABBITMQ_VHOST}",
                        "RABBITMQ_USER=${env.RABBITMQ_USER}",
                        "RABBITMQ_PASSWORD=${env.RABBITMQ_PASSWORD}",
                        "RABBITMQ_QUEUE=${env.RABBITMQ_QUEUE}"
                    // Add other variables as needed
                    ]) {
                            // Run Docker Compose
                            sh 'docker-compose up -d'
                    }
                    }
                }
            }
        }
    }
}
