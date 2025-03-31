pipeline {
    agent any

    environment {
        // Change this if you're using a custom Docker network or ports
        DOCKER_IMAGE = "springboot-taskmanager"
    }

    stages {
        stage('Clone Code') {
            steps {
                echo "Checking out source code..."
                checkout scm
            }
        }

        stage('Build with Maven') {
            steps {
                echo "Building the Spring Boot project..."
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image..."
                sh "docker build -t ${DOCKER_IMAGE}:latest ."
            }
        }

        stage('Stop and Remove Old Container') {
            steps {
                echo "Stopping old container if exists..."
                sh "docker rm -f ${DOCKER_IMAGE} || true"
            }
        }

        stage('Run New Container') {
            steps {
                echo "Running new Docker container..."
                sh """
                docker run -d --name ${DOCKER_IMAGE} \\
                    --network=taskmanager_task-net \\
                    -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/taskdb \\
                    -e SPRING_DATASOURCE_USERNAME=root \\
                    -e SPRING_DATASOURCE_PASSWORD=root \\
                    -p 8080:8080 \\
                    ${DOCKER_IMAGE}:latest
                """
            }
        }
    }
}