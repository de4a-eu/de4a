pipeline {
    agent none

    stages {
        stage('Test') {
            when {
                anyOf {
                    branch pattern: 'develop.*', comparator: 'REGEXP';
                    branch pattern: 'PR-\\d+', comparator: 'REGEXP'
                }
            }
            agent {
                docker {
                    image 'maven:3-adoptopenjdk-11'
                    args '-v $HOME/.m2:/root/.m2 --network docker-ci_default'
                }
            }
            steps {
                sh 'mvn clean test sonar:sonar -Dsonar.host.url=http://sonarqube:9000/sonarqube -Dsonar.login=$SONAR_TOKEN'
            }
        }

        stage('Build'){
            when {
                anyOf{
                    branch 'master';
                    branch pattern: 'iteration\\d+', comparator: 'REGEXP'
                }
            }
            agent {
                docker {
                    image 'maven:3-adoptopenjdk-11'
                    args '-v $HOME/.m2:/root/.m2 --network docker-ci_default'
                }
            }
            steps {
                sh 'mvn clean package -U'
            }

            // TODO: add pushing to a future de4a maven repo?
            // TODO: add building a release on a tag and push to GitHub?
        }

        stage('Docker'){
            when {
                branch 'master'
            }
            agent { label 'master' }
            steps {
                script{
                    def img
                    if (env.BRANCH_NAME == 'master') {
                        dir('de4a-idk') {
                            env.VERSION = readMavenPom().getVersion()
                            img = docker.build('de4a/mock-idk',".")
                            docker.withRegistry('','docker-hub-token') {
                                img.push('latest')
                                img.push("${env.VERSION}")
                            }
                        }
                        dir('de4a-connector') {
                            env.VERSION = readMavenPom().getVersion()
                            img = docker.build('de4a/connector','.')
                            docker.withRegistry('','docker-hub-token') {
                                img.push('latest')
                                img.push("${env.VERSION}")
                            }
                        }
                    }
                }
                sh 'docker system prune -f'
            }
        }

        stage('Docker iteration'){
            when {
                branch pattern: 'iteration\\d+', comparator: 'REGEXP'
            }
            agent { label 'master' }
            steps {
                script{
                    def img
                    dir('de4a-idk') {
                        env.VERSION = readMavenPom().getVersion()
                        img = docker.build('de4a/mock-idk',".")
                        docker.withRegistry('','docker-hub-token') {
                            img.push("${env.BRANCH_NAME}")
                            img.push("${env.VERSION}")
                        }
                    }
                    dir('de4a-connector') {
                        env.VERSION = readMavenPom().getVersion()
                        img = docker.build('de4a/connector','.')
                        docker.withRegistry('','docker-hub-token') {
                            img.push("${env.BRANCH_NAME}")
                            img.push("${env.VERSION}")
                        }
                    }
                }
                sh 'docker system prune -f'
            }
        }
    }
    post {
        failure {
            node('master') {
                script {
                    env.ORG=env.JOB_NAME.split('/')[0]
                    env.REPO=env.JOB_NAME.split('/')[1]
                    env.BR=env.JOB_NAME.split('/')[2]
                    env.ERRORLOG = sh returnStdout: true, script: "cat ${env.JENKINS_HOME}/jobs/${env.ORG}/jobs/${env.REPO}/branches/${env.BR}/builds/${BUILD_NUMBER}/log | grep -B 1 -A 5 '\\[ERROR\\]'"
                    slackSend color: "danger", message: ":darth_maul: Build fail! :darth_maul:\nJob name: ${env.JOB_NAME}, Build number: ${env.BUILD_NUMBER}\nGit Author: ${env.CHANGE_AUTHOR}, Branch: ${env.BRANCH_NAME}, ${env.GIT_URL}\nMaven [ERROR] log below:\n ${env.ERRORLOG}"
                }
            }
        }
        fixed {
            node('master') {
                script {
                        slackSend color: "good", message: ":baby-yoda: This is the way! :baby-yoda: \nJob name: ${env.JOB_NAME}, Build number: ${env.BUILD_NUMBER}\nGit Author: ${env.CHANGE_AUTHOR}, Branch: ${env.BRANCH_NAME}, ${env.GIT_URL}\n"
                }
            }
        }
    }
}
