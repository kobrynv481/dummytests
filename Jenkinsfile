pipeline {
    agent any

    tools {
        maven 'maven3'
    }

    parameters {
        choice(
            name: 'SUITE',
            choices: ['testng-smoke.xml', 'testng-integration.xml', 'testng-e2e.xml', 'testng.xml'],
            description: 'Який сьют запустити?'
        )
    }

    stages {
        stage('Copy dummyproject artifact') {
            steps {
                copyArtifacts(
                    projectName: 'dummyproject-build',
                    selector: lastSuccessful(),
                    filter: 'target/dummyproject-*-exec.jar',
                    target: 'dummyproject-exec'
                )
            }
        }

        stage('Start dummyproject') {
            steps {
                sh '''
                    nohup java -jar dummyproject-exec/target/dummyproject-1.0.0-exec.jar --server.port=8081 > dummyproject.log 2>&1 &
                    for i in $(seq 1 30); do
                        curl -sf http://localhost:8081/actuator/health && exit 0
                        sleep 1
                    done
                    echo "dummyproject did not start in time"
                    cat dummyproject.log
                    exit 1
                '''
            }
        }

        stage('Run tests') {
            steps {
                sh "mvn clean test -Dapi.base.url=http://localhost:8081 -DsuiteXmlFile=src/test/resources/${params.SUITE} -Dallure.results.directory=target/allure-results"
            }
        }
    }

    post {
        always {
            step([$class: 'TestNGResultsPublisher', reportFilenamePattern: '**/testng-results.xml'])
            allure results: [[path: 'target/allure-results']]
        }
    }
}
