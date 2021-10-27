package pe.com.sura.services;

/***
* JenkinsCloudFormation - version 1.0.0
* @uthor: Samuel Navarro
* Date: 20/10/2021
*/
import pe.com.sura.util.Base;
import org.jenkinsci.plugins.docker.workflow.*;
import org.jenkinsci.plugins.docker.workflow.Docker;
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*

class JenkinsCloudFormation extends Base implements Serializable {
  /* General Properties */
  def execMavenCpu="2"
  def execNodeCpu="2"
  def steps

  private JenkinsCloudFormation() {
  }

  public JenkinsCloudFormation(script,String type = '') {
    super(script, type)
    this.script.steps.echo "script: ${script}"
    this.script.steps.echo "type: ${type}"
  }

  public void configure(){
    super.configure()
  }

  def deployIaC(){
    def projectName="${script.env.project}".toLowerCase()

     docker.withRegistry("https://${env.REGISTRY_CONTAINER_URL}", "account-aws-user-devops"){
	 	 
	 this.script.steps.withCredentials([[
      $class: 'UsernamePasswordMultiBinding',
      credentialsId: 'account-aws-user-devops',
      usernameVariable: 'ACCESS',
      passwordVariable: 'SECRET']]) {
        String dockerParameters = "--network=host"
        String dockerCommand = "aws configure set aws_access_key_id $ACCESS && aws configure set aws_secret_access_key $SECRET && aws configure set default.region ${env.AWS_REGION} && aws --version"       
        String dockerCmd = "docker run ${dockerParameters} ${env.REGISTRY_NAME}:awscli sh -c \"${dockerCommand}\""
       
        this.script.steps.sh "${dockerCmd}"
      }
    }

  }


}
