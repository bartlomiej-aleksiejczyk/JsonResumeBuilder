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
        SPRING_DB_PROD_URL = "${env.SPRING_DB_PROD_URL}"
        RABBITMQ_QUEUE = "${env.IMAGE_NAME}"
    }
    stages {
        stage('Set up queue enviromental variables') {
            steps {
                script {
                    env.QUEUE_EXCHANGE = "${env.IMAGE_NAME}"
                    env.QUEUE_ROUTING_KEY = "${env.IMAGE_NAME}"
                    env.QUEUE_NAME =  "${env.IMAGE_NAME}"
                    env.QUEUE_CELERY_TASK_BUILD_CV =  'build-cv'
                }
            }
        }
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
                     usernamePassword(credentialsId: 'spring-credidentials-standard-dev', passwordVariable: 'SPRING_SINGLE_PASSWORD', usernameVariable: 'SPRING_SINGLE_LOGIN')]) {
                        withEnv([
                        "IMAGE_NAME=${env.IMAGE_NAME}",
                        "SCHEDULER_API_URL=${env.SCHEDULER_API_URL}",
                        "HOST_IP=${HOST_IP}",
                        "APP_URL=${env.APP_URL}",
                        "DB_CONNECTION=${env.DB_CONNECTION}",
                        "DB_HOST=${env.DB_HOST}",
                        "DB_PORT=${env.DB_PORT}",
                        "DB_DATABASE=${env.DB_DATABASE}",
                        "SPRING_DB_PROD_URL=${SPRING_DB_PROD_URL}",
                        "RABBITMQ_HOST=${env.RABBITMQ_HOST}",
                        "RABBITMQ_PORT=${env.RABBITMQ_PORT}",
                        "RABBITMQ_VHOST=${env.RABBITMQ_VHOST}",
                        "QUEUE_EXCHANGE=${env.QUEUE_EXCHANGE}",
                        "QUEUE_ROUTING_KEY=${env.QUEUE_ROUTING_KEY}",
                        "QUEUE_NAME=${env.QUEUE_NAME}",
                        "QUEUE_CELERY_TASK_BUILD_CV=${env.QUEUE_CELERY_TASK_BUILD_CV}",

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
