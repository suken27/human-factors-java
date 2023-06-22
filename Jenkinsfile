node {
    stage('SCM Checkout') {
        checkout scm
    }
    stage('Initial test') {
        sh 'docker build -t suken27/humanfactors .'
    }
}