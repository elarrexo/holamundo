pipeline{
    agent {
         label 'docker'
    }
    /*
    tools {
         maven 'maven 3.6'
         jdk 'java'
    }
    */
    environment {
        // This can be nexus3 or nexus2
        NEXUS_VERSION = "nexus3"
        // This can be http or https
        NEXUS_PROTOCOL = "http"
        // Where your Nexus is running. 'nexus-3' is defined in the docker-compose file
        NEXUS_URL = "172.16.232.160:8081"
        // Repository where we will upload the artifact
        NEXUS_REPOSITORY = "maven-releases"
        // Jenkins credential id to authenticate to Nexus OSS
        NEXUS_CREDENTIAL_ID = "nexus"
        
        // Workfolder
        //WORKFOLDER = "/usr/jenkins/node_agent/workspace"
    }

    stages{
        stage('Checkout'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'Github', url: 'git@github.com:elarrexo/holamundo.git']]])
            }
        }
        stage('Download artifact from nexus'){
            agent {
                label 'docker'
            }
            steps{
                sh '''
                    pwd 
                    curl -v -u admin:admin -o app.jar http://172.16.232.160:8081/repository/maven-public/org/springframework/jb-hello-world-maven/0.2.1/jb-hello-world-maven-0.2.1.jar
                '''
            }
        }
        stage('Build container'){
            agent {
                label 'docker'
            }
            steps{
                sh '''
                    docker build -t holamundo .
                '''

            }
        } //fin stage build container
        stage('Deploy container'){
            agent {
                label 'docker'
            }
            steps{
                sh '''
                    docker run -d --name holamundo -p 8085:80 holamundo
                '''

            }
        } //fin stage build container
        
        stage("Post") {
            agent {
                label 'docker'
            }
            steps {
                sh '''
                    pwd
                    echo "Clean up workfolder"
                    rm -Rf *
                '''
            }
        } //fin stage post
        
    }
}
