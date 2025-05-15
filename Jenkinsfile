@Library('my-shared-lib') _

pipeline{

    parameters{
        booleanParam(name: 'RUN_TESTS', defaultValue: true, description: 'Whether to run unit tests')
        choice(name: 'ENV', choices: ['dev', 'staging', 'prod'], description: 'Deployment environment')
        booleanParam(name: 'PRINT_ENV', defaultValue: true, description: 'Whether to print the env, to see what\'s under the hood')
    }

    agent any

    tools{
        jdk 'jdk-21-local'
        maven 'maven-local'
    }

    environment{
        APP_NAME = 'MavenSimpleApp'
        DEPLOY_DIR = '/opt/app_deployments'
    }

    options{
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
        timeout(time: 1, unit: 'HOURS')
        ansiColor('xterm')
    }

    stages {

        stage('Print Params') {
            steps {
                script {
                    pipelineStages.printParams()
                    def lastSuccessful = currentBuild.previousSuccessfulBuild
                    def buildData = lastSuccessful.getAction(hudson.plugins.git.util.BuildData)
                    def lsbSha = buildData.lastBuiltRevision.SHA1.toString()
                    def headSha = env.GIT_COMMIT
                    if (lastSuccessful == null) {
                        echo "No previous successful build found."
                    } else {
                        echo "Last successful build: ${lastSuccessful.displayName} (Number: ${lastSuccessful.number})"
                        echo "Last successful build commit hash: ${lsbSha}"
                        echo "Current build commit hash (HEAD): ${headSha}"
                    }
                }
            }
        }

        stage('Verify Maven') {
            steps {
                script {
                    pipelineStages.verifyMaven()
                }
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    pipelineStages.buildAndTest()
                }
            }
        }

        stage('Integration & Lint') {
            steps {
                script {
                    pipelineStages.integrationAndLint()
                }
            }
        }

        stage('Prepare Deploy Target') {
            steps {
                script {
                    pipelineStages.prepareDeployTarget()
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    pipelineStages.deploy()
                }
            }
        }

        stage('Changelog') {
            steps {
                script {
                    pipelineStages.changelog()
                }
            }
        }

    }


    post {
        always {
            echo "Pipeline finished for ${env.BRANCH_NAME}"
            deleteDir()
        }

        success {
            echo "✅ Pipeline SUCCEEDED!"
        }

        failure {
            echo "❌ Pipeline FAILED!"
        }
    }
}