#!/usr/bin/groovy
import org.integr8ly.JiraUtils

def call(String jiraCredentials, String productName, String summary, String[] labels, String gitPrUrl) {
  def jiraUtils = new JiraUtils()

  def (found, key) = jiraUtils.jiraHasIssue(jiraCredentials, labels)
  if (!found) {
    if (params.dryRun) {
      println "Would create a Jira issue for ${productName}: with the summary: ${summary}, PR URL: ${gitPrUrl} and labels: ${labels}"
    } else {
      println("Creating Jira Issue for ${productName}: ${summary}")
      jiraUtils.jiraCreateIssue(jiraCredentials, gitPrUrl, labels, summary)
    }
  } else {
    println "Issue already open for ${productName}: https://issues.jboss.org/browse/${key}"
  }
}