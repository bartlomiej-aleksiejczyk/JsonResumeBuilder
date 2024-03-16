pipeline {
    agent any
    environment {
        // Define your environment variables here
        PHP_TZ = 'America/New_York'
        CELERY_BROKER_URL = 'amqp://user:pass@rabbitmqhost//'
        SCHEDULER_API_URL = 'http://localhost:5000/status'
        WORKER_TZ = 'Europe/Warsaw'
    }
    stages {
        stage('Ensure Traefik is Running') {
            steps {
                ensureTraefik() // Call the shared library step to ensure Traefik is running
            }
        }
        stage('Build and Run Docker Containers') {
            steps {
                script {
                    // Inject Jenkins environment variables into Docker Compose
                    withEnv(["PHP_TZ=${env.PHP_TZ}", "CELERY_BROKER_URL=${env.CELERY_BROKER_URL}", "SCHEDULER_API_URL=${env.SCHEDULER_API_URL}", "WORKER_TZ=${env.WORKER_TZ}"]) {
                        // Run Docker Compose
                        sh 'docker-compose up -d'
                    }
                }
            }
        }
    }
}
