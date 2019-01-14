//Needed for Microlimate
/*#!groovy

@Library('MicroserviceBuilder') _
microserviceBuilderPipeline {
  image = 'messaging'
  test = 'false'
}
*/

//Needed for basic Jenkins
pipeline {
    environment {
         componentName = "trader"
         imagename = "${componentName}:${BUILD_NUMBER}"
    }
    
    tools { 
        maven 'Maven 3.6.0' 
        jdk 'jdk9' 
//        Docker 'docker - latest' 
    }

    agent any 
//    agent { dockerfile true }

    stages {
         stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                ''' 
            }
         }

        stage('Build') {
          steps {
              sh 'mvn clean package' 
          }
        }
        
       stage('Deliver') {
            steps {
                script {
                echo 'Delivering....'
//                   docker.build imagename
                }
//                sh '/push2dockerhub.sh $imagename'
            }
       }
        
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}
