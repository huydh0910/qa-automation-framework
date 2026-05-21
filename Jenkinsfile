pipeline {
    agent any

    parameters {
        choice(name: 'ENV',     choices: ['qa', 'dev', 'staging'], description: 'Target environment')
        choice(name: 'SUITE',   choices: ['web', 'api', 'mobile', 'e2e', 'parallel'], description: 'Test suite to run')
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Browser (web tests only)')
    }

    environment {
        MAVEN_OPTS = '-Xmx2g'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                sh """
                    mvn test \
                        -P${params.SUITE} \
                        -Denv=${params.ENV} \
                        -Dbrowser=${params.BROWSER} \
                        -Dheadless=true
                """
            }
            post {
                always {
                    junit 'target/surefire-reports/**/*.xml'
                }
            }
        }

        stage('Publish Reports') {
            steps {
                publishHTML(target: [
                    allowMissing         : false,
                    alwaysLinkToLastBuild: true,
                    keepAll              : true,
                    reportDir            : 'reports',
                    reportFiles          : 'index.html',
                    reportName           : 'Extent Test Report'
                ])
            }
        }
    }

    post {
        failure {
            emailext(
                subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: "Build failed. Check ${env.BUILD_URL} for details.",
                to: '$DEFAULT_RECIPIENTS'
            )
        }
        always {
            archiveArtifacts artifacts: 'reports/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'screenshots/**/*', allowEmptyArchive: true
        }
    }
}
