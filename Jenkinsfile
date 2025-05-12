@Library('my-shared-lib') _
import pipelineStages

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
        
        stage { steps { pipelineStages.verifyMaven() } }
        stage { steps { pipelineStages.buildAndTest() } }
        stage { steps { pipelineStages.integrationAndLint() } }
        stage { steps { pipelineStages.prepareDeployTarget() } }
        stage { steps { pipelineStages.deploy() } }
        stage { steps { pipelineStages.changelog() } }

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