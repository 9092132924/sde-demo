node {
	stage('Scm Poilling') {
		git credentialsId: 'github', url: 'https://github.com/9092132924/sde-demo.git'
	}
	stage("Build jar") {
		def mavenHome = tool name: 'MAVEN_HOME', type: 'maven'
		def mavenCMD = "${mavenHome}/bin/mvn"
		sh "${mavenCMD} clean package"
	}
	stage("Build Docker images") {

		sh "docker build -t 557523153113.dkr.ecr.ap-south-1.amazonaws.com/login-app-backend ."
	}
	stage("docker push") {
			withDockerRegistry(credentialsId: 'ecr:ap-south-1:AWS-CREDS', url: 'https://557523153113.dkr.ecr.ap-south-1.amazonaws.com/login-app-backend') {
				docker.image('557523153113.dkr.ecr.ap-south-1.amazonaws.com/login-app-backend').push('latest')
			}
	}

	stage("k8-deploy") {

		sshagent(['k8-ssh']) {
		   sh "scp -o strictHostKeyChecking=no backend-deployment.yml ubuntu@13.127.246.55:/home/ubuntu/"
			script {
				try {
				  sh "ssh ubuntu@13.127.246.55 kubectl delete -f ."
				  sh "ssh ubuntu@13.127.246.55 kubectl apply -f ."
					} catch (error) {
		   		sh "ssh ubuntu@13.127.246.55 kubectl create -f ."
					}
			}

		}
			
	}
			
}