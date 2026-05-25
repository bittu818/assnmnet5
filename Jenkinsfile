pipeline {
    agent { label 'sri-27-kube' }

    environment {
        IMAGE_NAME = "sridharkovela/assignment5-app:v1"
        GIT_REPO = "https://github.com/bittu818/assnmnet5.git"
    }

    stages {

        stage('Checkout Source Code') {
            steps {
                git branch: 'main', url: "${GIT_REPO}"
            }
        }

        stage('Build Maven Application') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Docker Compose Build') {
            steps {
                sh 'docker compose build'
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {

                    sh '''
                    echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                    '''
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                sh 'docker push $IMAGE_NAME'
            }
        }

        stage('Deploy Kubernetes YAML Files') {
            steps {
                sh '''
                kubectl apply -f k8s/
                '''
            }
        }

        stage('Commit YAML Files to GitHub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'github-creds',
                    usernameVariable: 'GIT_USER',
                    passwordVariable: 'GIT_PASS'
                )]) {

                    sh '''
                    git config --global user.email "jenkins@local.com"
                    git config --global user.name "Jenkins"

                    git add k8s/*.yaml Jenkinsfile docker-compose.yml
                    git commit -m "Updated Kubernetes YAML and Jenkins Pipeline" || true

                    git push https://${GIT_USER}:${GIT_PASS}@github.com/bittu818/assnmnet5.git HEAD:main
                    '''
                }
            }
        }

        stage('Application Health Check') {
            steps {
                sh '''
                sleep 20
                kubectl get pods
                kubectl get svc

                NODE_PORT=$(kubectl get svc assignment5-service -o jsonpath='{.spec.ports[0].nodePort}')
                NODE_IP=$(hostname -I | awk '{print $1}')

                curl http://$NODE_IP:$NODE_PORT
                '''
            }
        }
    }
}
