package pe.com.sura.util

abstract class Base {
  protected script;
  protected String type;

  protected def buildTimestamp
  protected String branchName;
  protected String gitCommit;
  protected String gitURL;
  protected String buildUserMail;
  protected String gitProjectName;
  //protected Docker docker;
  protected String buildId;
  protected String buildResult;

  protected Base(){
  }

  protected Base(script, String type = '') {
    this.script = script
    this.type = type
    //this.docker = new Docker(this.script);
  }

  public void configure(){
    setBuildTimestamp()
    setBranchName()
    setGitCommit()
    setGitURL()
    setBuildUserMail()
    setGitProjectName()
  }

  protected void printMessage(String message){
    this.script.steps.echo "[Sura-Architecture] ${message}"
  }

  protected void setBuildTimestamp() {
    this.buildTimestamp = this.script.steps.sh(
      script: "date '+%Y%m%d%H%M%S'",
      returnStdout: true
    ).trim();
    this.printMessage("BuildTimestamp: ${buildTimestamp}")
  }

  protected String getBuildTimestamp() {
    return this.buildTimestamp
  }

  protected void setBranchName() {
    this.branchName = this.script.steps.sh(
      script: 'git name-rev --name-only HEAD | sed "s?.*remotes/origin/??"',
      returnStdout: true
    ).trim();
    this.printMessage("BranchName: ${branchName}")
  }

  protected String getBranchName() {
    return this.branchName
  }

  protected void setGitCommit() {
    this.gitCommit = this.script.steps.sh(
      script: 'git rev-parse HEAD',
      returnStdout: true
    ).trim();
    this.printMessage("CommitId: ${gitCommit}.substring(0,8)")
  }

  protected String getGitCommit() {
    return this.gitCommit
  }

  protected void setGitURL() {
    this.gitURL = this.script.steps.sh(
      script: 'git config --get remote.origin.url',
      returnStdout: true
    ).trim();
    this.printMessage("Url Git: ${gitURL}")
  }

  protected String getGitURL() {
    return this.gitURL
  }

  protected void setBuildUserMail() {
    if (this.type == 'ligthweight') {
      def commitUserName = this.script.steps.sh(
        script: "git show --format=\"%aN\" ${this.gitCommit} | head -n 1",
        returnStdout: true
      ).trim();
      printMessage("User Name: ${commitUserName}")
      this.buildUserMail = this.script.steps.sh(
        script: "git show --format=\"%aE\" ${this.gitCommit} | head -n 1",
        returnStdout: true
      ).trim()
      this.printMessage("User Email: ${this.buildUserMail}")
    }
  }

  protected String getBuildUserMail() {
    return this.buildUserMail
  }

  protected throwException(String message) {
    throw new Exception("[Sura-Architecture] ${message}");
  }

  protected void setGitProjectName() {
    this.gitProjectName = this.script.steps.sh(
      script:"echo '${this.gitURL}' | cut -d '/' -f6 | sed 's/\\.git//g' ",
      returnStdout: true
    ).trim();
    this.printMessage("Url Git: ${this.gitProjectName}")
  }

  public String getGitProjectName(){
    return this.gitProjectName
  }

  public String getBuildResult() {
    return this.script.currentBuild.result
  }

  public String getBuildId() {
    return this.script.currentBuild.id
  }

  public void executePostExecutionTasks() {
    //Clean up workspace when job was executed ok, this improve performance on server
    this.script.steps.step([$class: 'WsCleanup', cleanWhenFailure: false])
    if(this.type=='ligthweight'){
      this.script.currentBuild.result = 'SUCCESS'
      if(gitCommit!=null){
        this.script.steps.notifyBitbucket commitSha1: "${gitCommit}"
      }
    }
  }

  /*
  * This method will perform common post execution task
  */
  public void executeOnErrorExecutionTasks() {
    //Clean up workspace when job was executed ok, this improve performance on server
    //TO FIX CLEAN UP
    if(this.type=='ligthweight'){
      this.script.currentBuild.result = 'FAILED'
      if(gitCommit!=null){
        this.script.steps.notifyBitbucket commitSha1: "${gitCommit}"
      }
    }
  }


}
