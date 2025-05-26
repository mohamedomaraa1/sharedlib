package org.session3

class Pipeline implements Serializable {
    def script

   Pipeline(script) {
        this.script = script
    }

    def runPipeline() {
        script.node('agent-0') {
            script.tools {
                jdk "java-8"
            }

            script.withCredentials([
                script.usernamePassword(credentialsId: 'docker-user', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')
            ]) {

                script.stage('Login to DockerHub') {
                    login(script.env.DOCKER_USER, script.env.DOCKER_PASS)
                }

                script.stage('Clone Repositories') {
                    gitClone('https://github.com/mohamedomaraa1/java.git', 'master', 'java')
                    gitClone('https://github.com/mohamedomaraa1/pytohn1.git', 'main', 'python')
                }

                script.stage('Build and Push Docker Images') {
                    script.parallel(
                        "Java Image": {
                            buildJava()
                            push('mohamedomaraa/java-app-pipeline', 'latest')
                        },
                        "Python Image": {
                            script.dir('python') {
                                build('mohamedomaraa/python-app', 'latest')
                                push('mohamedomaraa/python-app', 'latest')
                            }
                        }
                    )
                }
            }
        }
    }

    def login(USERNAME, PASSWORD) {
        script.sh "docker login -u ${USERNAME} -p ${PASSWORD}"
    }

    def build(IMAGE_NAME, IMAGE_TAG) {
        script.sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
    }

    def push(IMAGE_NAME, IMAGE_TAG) {
        script.sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
    }

    def gitClone(String repoUrl, String branch = 'main', String targetDir = '.') {
        script.sh "rm -rf ${targetDir}"
        script.sh "git clone --branch ${branch} ${repoUrl} ${targetDir}"
    }

    def buildJava() {
        script.dir('java') {
            script.sh "mvn clean package -DskipTests"
            script.sh "docker build -t mohamedomaraa/java-app-pipeline:latest ."
        }
    }
}
