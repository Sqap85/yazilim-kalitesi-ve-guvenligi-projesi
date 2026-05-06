import hudson.model.ParametersAction
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def instance = Jenkins.get()
def jobName = "techstore-pipeline"
def pipelinePath = "/private/tmp/techstore-jenkins-workspace/Jenkinsfile.local"
def pipelineScript = new File(pipelinePath).text

WorkflowJob job = instance.getItem(jobName)
if (job == null) {
    job = instance.createProject(WorkflowJob, jobName)
}

job.definition = new CpsFlowDefinition(pipelineScript, true)
job.description = "TechStore icin yerel Jenkins pipeline"
job.save()

if (job.getLastBuild() == null && job.isBuildable()) {
    job.scheduleBuild2(5)
}
