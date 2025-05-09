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

    stages{
        stage ('Print Params') {
            when { expression { return params.PRINT_ENV } }
            steps {
                echo "Build #${env.BUILD_NUMBER} on branch ${env.GIT_BRANCH}"
                sh 'printenv'
            }
        }

        stage ('Verify maven') {
            steps {
                sh 'mvn -v || echo "Maven not executable"'
            }
        }

        stage ('Build & Test') {
            steps {
                script {
                    if ( params.RUN_TESTS ) {
                        sh 'mvn -B clean verify'
                    } else {
                        sh 'mvn -B -DskipTests clean package'
                    }
                }
            }
        }

        stage ('Integration & Lint') {
            steps {
                sh 'mvn checkstyle:check'
                sh 'mvn verify'
            }
        }

        stage('Prepare Deploy Target') {
            steps{
                script {
                    env.DEPLOY_TARGET = generateDeploymentName(APP_NAME,GIT_BRANCH,BUILD_NUMBER)
                    echo "📦 Deployment target: ${env.DEPLOY_TARGET}"
                }
            }
        }

        stage('Deploy') {
            when {
                allOf {
                    branch 'master'
                    environment name: 'ENV', value: 'prod'
                }
            }
            steps {
                echo "Deploying ${APP_NAME} to local machine at ${DEPLOY_TARGET}"
                sh "mkdir -p ${DEPLOY_DIR}"
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def jarFile =  "target/${pom.artifactId}-${pom.version}.jar"
                    sh "cp ${jarFile} ${DEPLOY_TARGET}"
                }
            }
        }

        stage('Changelog') {
            steps {
                script {
                    def changes = currentBuild.changeSets.collect {cs -> cs.items.collect { it.msg }.join("\n")}.flatten().join("\n")
                    echo "Changes in this build:\n${changes ?: '— no changes'}"
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