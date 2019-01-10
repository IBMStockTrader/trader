pipeline {
    environment {
         componentName = "trader"
         imagename = "${componentName}:${BUILD_NUMBER}"
    }
    
    tools { 
        maven 'Maven 3.6.0' 
        jdk 'jdk9' 
    }

    agent any

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
        
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}
