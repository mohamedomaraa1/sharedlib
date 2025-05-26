package org.session3;

def login(USERNAME, PASSWORD){
    sh "docker login -u ${USERNAME} -p ${PASSWORD}"
}

def build(IMAGE_NAME, IMAGE_TAG){
    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
}

def push(IMAGE_NAME, IMAGE_TAG){
    sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
}

def gitClone(String repoUrl, String branch = 'main', String targetDir = '.') {
    sh "rm -rf ${targetDir}"
    sh "git clone --branch ${branch} ${repoUrl} ${targetDir}"
}

// build the Java app Docker image due to issue in the building
// def buildJava(){
//     dir('java') {
//         sh "mvn clean package -DskipTests"
//         sh "docker build -t mohamedomaraa/java:latest ."
//     }
def buildJava(){
    def javaHome = tool name: 'java-8', type: 'jdk'
    env.JAVA_HOME = javaHome
    env.PATH = "${javaHome}/bin:${env.PATH}"

    dir('java') {
        sh 'echo "📦 Running Maven build..."'
        sh 'mvn clean package -DskipTests'
        sh 'echo "🐳 Building Docker image..."'
        sh 'docker build -t mohamedomaraa/java:latest .'
    }
}
