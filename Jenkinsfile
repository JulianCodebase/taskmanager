pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "springboot-taskmanager"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:latest ."
            }
        }

        stage('Stop and Remove Old Container') {
            steps {
                sh "docker rm -f ${DOCKER_IMAGE} || true"
            }
        }

        stage('Run New Container') {
            steps {
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
