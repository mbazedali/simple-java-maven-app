@Library('my-shared-lib') _

pipeline{

    parameters{
        booleanParam(name: 'RUN_TESTS', defaultValue: true, description: 'Whether to run unit tests')
        choice(name: 'ENV', choices: ['dev', 'staging', 'prod'], description: 'Deployment environment')
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
            steps {
                echo "Build #${env.BUILD_NUMBER} on branch ${env.GIT_BRANCH}"
                sh 'printenv'
            }
        }

        stage ('Verify maven') {
            steps {
                sh 'which mvn || echo "Maven not in PATH"'
                sh 'mvn -v || echo "Maven not executable"'
            }
        }

        stage ('Build & Test') {
            parallel {
                stage ('Compile') {
                    steps {
                        sh 'mvn -B -DskipTests clean package'
                    }
                }

                stage ('Unit Tests') {
                    when { expression { return params.RUN_TESTS } }
                    steps {
                        sh 'mvn -B test'
                    }
                }
            }
        }

        stage ('Integration & Lint') {
            steps {
                script {
                    sh 'mvn verify'
                    sh 'mvn checkstyle:check'
                }
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
                sh "cp target/${APP_NAME}.jar ${DEPLOY_TARGET}"
            }
        }

        stage('Changelog') {
            steps {
                script {
                    def changes = currentBuild.changeSets.collect {cs -> cs.items.collect { it.msg }.join("\n")}.flatten().join("\n")
                }
            }
        }
    }
}