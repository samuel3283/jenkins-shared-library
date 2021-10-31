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

  def TAG_PROJECT="ParamTagProject"
  def TAG_ENVIRONMENT="ParamTagEnv"
  def TAG_COST_CENTER="ParamTagCostCenter"
  
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

  def deployIaC(String type){
	this.script.steps.echo "Deploy IaC: ${type}"  
	if(type=="s3"){
      deployS3IaC()
	}else{
	  this.script.steps.echo "Not Found: ${type}"  		  
    }
    
  }

  def deployS3IaC(){
    
    docker.withRegistry("https://${script.env.REGISTRY_CONTAINER_URL}", "ecr:us-east-1:credential-user-devops"){
		 
	this.script.steps.withCredentials([[
     $class: 'UsernamePasswordMultiBinding',
     credentialsId: 'account-aws-user-devops',
     usernameVariable: 'ACCESS',
     passwordVariable: 'SECRET']]) {
       def dockerParameters = "--network=host"
       def dockerVolumen="-v ${script.env.WORKSPACE}:/home/workspace -w /home/workspace "
       def dockerCommand =" aws configure set aws_access_key_id ${script.env.ACCESS} && aws configure set aws_secret_access_key ${script.env.SECRET} && aws configure set default.region ${script.env.AWS_REGION} "
	   
	   def paramTag = getValuesTag()
	   def paramS3 = getValuesS3()
	   String fecha = this.buildTimestamp.toString()
	   def nameStack = "stack-s3-${fecha}"
	   
       dockerCommand+=" && aws cloudformation create-stack --stack-name ${nameStack} --template-body file:///home/workspace/template.yml --parameters ${paramS3} ${paramTag}"
	   String dockerCmd = "docker run ${dockerParameters} ${dockerVolumen} ${script.env.REGISTRY_CONTAINER_URL}/${script.env.REGISTRY_ECR_NAME}:awscli-kubectl sh -c \"${dockerCommand}\""
	   
	   def projectName="${script.env.project}".toLowerCase()
       this.script.steps.echo "Deploy Project::: ${projectName}"
  
	   this.script.steps.sh "${dockerCmd}"
     }
   
    }

  }


  def getValuesS3() {

	def jsonResult = this.script.steps.sh(script: "cat parameter.json", returnStdout: true).trim()
	JsonSlurper jsonSlurper = new JsonSlurper()
	def jsonResultParsed = jsonSlurper.parseText(jsonResult.toString())
	String paramTag = "ParameterKey=ResourceName,ParameterValue=${jsonResultParsed.s3.name} "
	return paramTag
	  
  }

  def getValuesTag() {

	def jsonResult = this.script.steps.sh(script: "cat parameter.json", returnStdout: true).trim()
	JsonSlurper jsonSlurper = new JsonSlurper()
	def jsonResultParsed = jsonSlurper.parseText(jsonResult.toString())
	String paramTag = "ParameterKey=${TAG_PROJECT},ParameterValue=${jsonResultParsed.tag.project} ParameterKey=${TAG_ENVIRONMENT},ParameterValue=${jsonResultParsed.tag.environment} ParameterKey=${TAG_COST_CENTER},ParameterValue=${jsonResultParsed.tag.cost_center} "
	return paramTag
	  
  }


}
