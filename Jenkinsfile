pipeline {
    agent any
    environment {
        NETWORK_NAME = "${env.STANDARD_TRAEFIK_DOCKER_NETWORK}"
        IMAGE_NAME = "${JOB_NAME}".toLowerCase().replaceAll(/[^a-z0-9._-]/, '-')
        IMAGE_TAG = 'latest'
        RABBITMQ_HOST = "${env.RABBITMQ_HOST}"
        RABBITMQ_PORT = "${env.RABBITMQ_PORT}"
        RABBITMQ_VHOST = "${env.RABBITMQ_VHOST}"

        DB_HOST = "${env.DB_HOST}"
        DB_PORT = "${env.DB_PORT}"
        DB_DATABASE = "${env.PROD_DB_NAME}"
    }
    stages {
        stage('Get Host IP') {
            steps {
                script {
                    env.HOST_IP = sh(script: "hostname -I | awk '{print \$1}'", returnStdout: true).trim()
                }
            }
        }
        stage('Build URL for apps') {
            steps {
                script {
                    env.APP_URL = "http://${env.HOST_IP}/${env.IMAGE_NAME}"
                    env.SCHEDULER_API_URL = "http://${env.HOST_IP}/${env.IMAGE_NAME}/api/v1/update-job-status"
                }
            }
        }
        stage('Ensure Traefik is Running') {
            steps {
                ensureTraefik()
            }
        }
        stage('Build and Run Docker Containers') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'database-config', passwordVariable: 'DB_PASSWORD', usernameVariable: 'DB_USERNAME'),
                     usernamePassword(credentialsId: 'rabbitmq-credentials', passwordVariable: 'RABBITMQ_PASSWORD', usernameVariable: 'RABBITMQ_USER'),
                     string(credentialsId: 'rabbitmq-url-with-credentials', variable: 'CELERY_BROKER_URL'),
                     string(credentialsId: 'laravel-app-key-resume-builder', variable: 'APP_KEY')]) {
                        withEnv([
                        "IMAGE_NAME=${env.IMAGE_NAME}",
                        "CELERY_BROKER_URL=${env.CELERY_BROKER_URL}",
                        "SCHEDULER_API_URL=${env.SCHEDULER_API_URL}",
                        "HOST_IP=${HOST_IP}",
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
                        "RABBITMQ_QUEUE=${env.IMAGE_NAME}" ,
                        "NETWORK_NAME=${env.NETWORK_NAME}"
                    ]) {
                            sh 'docker compose down'
                            sh 'docker compose up --build -d'
                    }
                     }
                }
            }
        }
    }
}
