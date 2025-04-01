pipeline {
    agent any

    environment {
        COMPOSE_PROJECT_NAME = "taskmanager"  // Optional: keeps volumes/networks isolated
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo "Cloning repo..."
                // This clones the repo automatically from the configured job's SCM
                checkout scm
            }
        }

        stage('Run Unit Tests') {
            steps {
                echo "Running test..."
                sh 'mvn test'
            }
        }

        stage('Build and Deploy with Docker Compose') {
            steps {
                echo "Stopping old containers..."
                sh 'docker-compose down'

                echo "Building and starting containers..."
                sh 'docker-compose up -d --build'
            }
        }
    }
}
