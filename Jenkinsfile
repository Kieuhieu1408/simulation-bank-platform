pipeline {
  agent any
  environment {
    REGISTRY = "harbor.sim-bank.local"
    IMAGE_TAG = "${env.GIT_COMMIT[0..7]}"
    SERVICES = "api-gateway corebank money-bank cms"
  }
  stages {
    stage('Build & Test') {
      steps {
        sh 'mvn -B verify -pl api-gateway,corebank,money-bank,cms -am'
      }
    }
    stage('Docker Build') {
      steps {
        script {
          def services = SERVICES.split(' ')
          def parallelBuilds = [:]
          
          for (int i = 0; i < services.length; i++) {
            def service = services[i]
            parallelBuilds[service] = {
              sh "docker build -t ${REGISTRY}/sim-bank/${service}:${IMAGE_TAG} -f ${service}/Dockerfile ."
            }
          }
          parallel parallelBuilds
        }
      }
    }
    stage('Security Scan') {
      steps {
        script {
          def services = SERVICES.split(' ')
          for (int i = 0; i < services.length; i++) {
            sh "trivy image --exit-code 1 --severity HIGH,CRITICAL ${REGISTRY}/sim-bank/${services[i]}:${IMAGE_TAG}"
          }
        }
      }
    }
    stage('Push Harbor') {
      steps {
        script {
          def services = SERVICES.split(' ')
          def parallelPushes = [:]
          
          for (int i = 0; i < services.length; i++) {
            def service = services[i]
            parallelPushes[service] = {
              sh "docker push ${REGISTRY}/sim-bank/${service}:${IMAGE_TAG}"
            }
          }
          parallel parallelPushes
        }
      }
    }
    stage('GitOps Update') {
      steps {
        script {
          def services = SERVICES.split(' ')
          for (int i = 0; i < services.length; i++) {
            sh "sed -i \"s|image:.*|image: ${REGISTRY}/sim-bank/${services[i]}:${IMAGE_TAG}|\" k8s/apps/${services[i]}/deployment.yaml"
          }
          sh '''
            git add k8s/apps/*/deployment.yaml
            git commit -m "Update image tags to ${IMAGE_TAG} [skip ci]"
            git push origin HEAD:main
          '''
        }
      }
    }
  }
  post {
    failure {
      echo 'Pipeline failed! Check logs and notify slack/email.'
    }
  }
}
