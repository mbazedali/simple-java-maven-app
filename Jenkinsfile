pipeline{

    agent any

    tools{
        jdk 'jdk-21-local'
    }

    environment{
        APP_NAME = 'MavenSimpleApp'
    }

    options{
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
    }

    stages{
        stage ('Print Env Variables') {
            steps {
                echo 'Printing env variables...'
                script{
                    env.getEnvironment().each {k,v -> echo "${k} = ${v}"}
                }
            }
        }
    }
}