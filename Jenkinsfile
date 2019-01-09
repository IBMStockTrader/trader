pipeline {  
    environment {
         componentName = "trader"
         imagename = "${componentName}:${BUILD_NUMBER}"
     }

    agent any
    stages {
       stage('Build') { 
          steps {
              sh 'mvn clean package' 
          }
       }  
       stage('Deliver') {
            steps {
                script {
                    docker.build imagename
                }
//                sh '/push2dockerhub.sh $imagename'
            }
       }
       stage('Deploy') {
       }
    }
}

// This is what we used for "Microclimate"
//#!groovy
//
//@Library('MicroserviceBuilder') _
//microserviceBuilderPipeline {
//  image = 'trader'
//  test = 'false'
//}
