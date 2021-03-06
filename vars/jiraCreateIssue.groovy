#!/usr/bin/groovy
import org.integr8ly.JiraUtils

def call(String jiraCredentials, String url, String query, String body = null) {
  def jiraUtils = new JiraUtils()

  def (found, key) = jiraUtils.jiraHasIssue(jiraCredentials, url, query)
  if (!found) {
    if (params.dryRun) {
      println "Would create a Jira issue for ${productName} with the details: ${body}"
      key = "dryRun"
    } else {
      key = jiraUtils.jiraCreateIssue(jiraCredentials, url, body)
    }
  }

  return key
}