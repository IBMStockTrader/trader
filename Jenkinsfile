//Needed for Microlimate
#!groovy

@Library('MicroserviceBuilder') _
microserviceBuilderPipeline {
  image = 'messaging'
  test = 'false'
}


//Needed for basic Jenkins
/*
pipeline {
    environment {
         componentName = "trader"
         imagename = "${componentName}:demo"
    }
    
    tools { 
        maven 'Maven 3.6.0' 
        jdk 'jdk9' 
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

        stage('MVN Build') {
          steps {
              sh 'mvn clean package' 
          }
        }
        
       stage('Docker Build&Push') {
            steps {
                script {
//                echo 'Delivering....'
                docker.build imagename
                }
                sh '/pushdocker2icp.sh $imagename'
            }
       }
        
        stage('Deploy2ICP') {
            steps {
                echo 'Deploying....'
              sh '/deploy2icp.sh $componentName'
            }
        }
    }
}
*/
