package pe.com.sura.services;

/***
* JenkinsCloudFormation - version 1.0.0
* @uthor: Samuel Navarro
* Date: 20/10/2021
*/
import pe.com.sura.util.Base;
import org.jenkinsci.plugins.docker.workflow.*;
import org.jenkinsci.plugins.docker.workflow.Docker;
import com.cloudbees.plugins.credentials.*;
//import com.cloudbees.plugins.credentials.common.*;
//import com.cloudbees.plugins.amazonecr;
//import com.amazonaws;
//import org.apache.http.wire;
//import groovy.json.JsonBuilder
import groovy.json.JsonSlurper;
import groovy.json.JsonSlurperClassic 


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

/*
  def deployS3IaC(){
    def projectName="${script.env.project}".toLowerCase()

     docker.withRegistry("https://${script.env.REGISTRY_CONTAINER_URL}", "ecr:us-east-1:credential-user-devops"){
	 	 
	 this.script.steps.withCredentials([[
      $class: 'UsernamePasswordMultiBinding',
      credentialsId: 'account-aws-user-devops',
      usernameVariable: 'ACCESS',
      passwordVariable: 'SECRET']]) {
        def dockerParameters = "--network=host"
        def dockerVolumen="-v ${script.env.WORKSPACE}:/home/workspace -w /home/workspace "
 	    def dockerCommand
		dockerCommand+=" aws configure set aws_access_key_id ${script.env.ACCESS} && aws configure set aws_secret_access_key ${script.env.SECRET} && aws configure set default.region ${script.env.AWS_REGION} "
        dockerCommand+=" && aws cloudformation create-stack --stack-name stack001 --template-body file:///home/workspace/template.yml --parameters ParameterKey=ResourceName,ParameterValue=sura-dev-config-s3-demo ParameterKey=ParamTagProject,ParameterValue=PROYECTO001 ParameterKey=ParamTagEnv,ParameterValue=DEV "
		String dockerCmd = "docker run ${dockerParameters} ${dockerVolumen} ${script.env.REGISTRY_CONTAINER_URL}/${script.env.REGISTRY_ECR_NAME}:awscli-kubectl sh -c \"${dockerCommand}\""       
		
        this.script.steps.sh "${dockerCmd}"
      }
    }

  }
*/
/*
  @NonCPS
  def jsonParse(def json) {
      new groovy.json.JsonSlurperClassic().parseText(json)
  }
  */
  
  def deployIaC(){
    def projectName="${script.env.project}".toLowerCase()

/*
    def config =  jsonParse(readFile("parameter.json"))
	this.script.steps.echo "Objecto ${jsonResultParsed}"
	this.script.steps.echo "Objecto ${jsonResultParsed['s3']}"
*/

/*
    String jsonResult = this.script.steps.sh(
      script: "cat parameter.json",
      returnStdout: true).trim()
*/

/*
    def jsonResult = this.script.steps.sh(script: "cat parameter.json", returnStdout: true).trim()
    this.printMessage("json: ${jsonResult}")

    JsonSlurper jsonSlurper = new JsonSlurper()
    def jsonResultParsed = jsonSlurper.parseText(jsonResult)
	//this.script.steps.echo "Objecto ${jsonResultParsed}"
  */
  
     docker.withRegistry("https://${script.env.REGISTRY_CONTAINER_URL}", "ecr:us-east-1:credential-user-devops"){
	 	 
	 this.script.steps.withCredentials([[
      $class: 'UsernamePasswordMultiBinding',
      credentialsId: 'account-aws-user-devops',
      usernameVariable: 'ACCESS',
      passwordVariable: 'SECRET']]) {
        def dockerParameters = "--network=host"
        def dockerVolumen="-v ${script.env.WORKSPACE}:/home/workspace -w /home/workspace "
        def dockerCommand =" aws configure set aws_access_key_id ${script.env.ACCESS} && aws configure set aws_secret_access_key ${script.env.SECRET} && aws configure set default.region ${script.env.AWS_REGION} "
        dockerCommand+=" && aws cloudformation create-stack --stack-name stack001 --template-body file:///home/workspace/template.yml --parameters ParameterKey=ResourceName,ParameterValue=sura-dev-config-s3-demo ParameterKey=ParamTagProject,ParameterValue=PROYECTO001 ParameterKey=ParamTagEnv,ParameterValue=DEV "
		String dockerCmd = "docker run ${dockerParameters} ${dockerVolumen} ${script.env.REGISTRY_CONTAINER_URL}/${script.env.REGISTRY_ECR_NAME}:awscli-kubectl sh -c \"${dockerCommand}\""

        String jsonResult = sh (
            script: "cat  ${script.env.WORKSPACE}/parameter.json",
            returnStdout: true
        ).trim()
		JsonSlurper jsonSlurper = new JsonSlurper()
		//def jsonResultParsed = jsonSlurper.parseText(jsonResult)
		def jsonResultParsed = jsonSlurper.parseText('{"firstName":"Guillame","lastName":"Laforge"}')

        this.script.steps.sh "${dockerCmd}"
      }
    }

  }




/*

  def deployIaC(){
    def projectName="${script.env.project}".toLowerCase()

     docker.withRegistry("https://${script.env.REGISTRY_CONTAINER_URL}", "ecr:us-east-1:credential-user-devops"){
	 	 
	 this.script.steps.withCredentials([[
      $class: 'UsernamePasswordMultiBinding',
      credentialsId: 'account-aws-user-devops',
      usernameVariable: 'ACCESS',
      passwordVariable: 'SECRET']]) {
        def dockerParameters = "--network=host"
        def dockerVolumen="-v ${script.env.WORKSPACE}:/home/workspace -w /home/workspace "
        def dockerCommand =" aws configure set aws_access_key_id ${script.env.ACCESS} && aws configure set aws_secret_access_key ${script.env.SECRET} && aws configure set default.region ${script.env.AWS_REGION} "
        dockerCommand+=" && aws cloudformation create-stack --stack-name stack001 --template-body file:///home/workspace/template.yml --parameters ParameterKey=ResourceName,ParameterValue=sura-dev-config-s3-demo ParameterKey=ParamTagProject,ParameterValue=PROYECTO001 ParameterKey=ParamTagEnv,ParameterValue=DEV "
		String dockerCmd = "docker run ${dockerParameters} ${dockerVolumen} ${script.env.REGISTRY_CONTAINER_URL}/${script.env.REGISTRY_ECR_NAME}:awscli-kubectl sh -c \"${dockerCommand}\""

        this.script.steps.sh "${dockerCmd}"
      }
    }

  }
*/




}
