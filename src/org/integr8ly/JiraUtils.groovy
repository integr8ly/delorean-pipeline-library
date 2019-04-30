#!/usr/bin/groovy
package org.integr8ly

/**
 * Requires the http_request plugin to be installed (https://plugins.jenkins.io/http_request)
 * @param endpoint - Jira endpoint to send the request to
 * @param httpMethod - HTTP method to be used for the request
 * @param requestBody - HTTP request body
 * @param jiraCredentials - Jira credentials to be used for authentication
 * @param customHeaders - Additional headers to be used for the request
 * @param validResponseCodes - Valid HTTP response codes
 * @returns HTTP response
 */
def jiraApiRequest(String endpoint, String httpMethod = 'GET', String requestBody = null, String jiraCredentials, customHeaders = [], validResponseCodes = '100:399') {
  def url = "https://issues.jboss.org/rest/api/2/${endpoint}"

  def response = httpRequest authentication: jiraCredentials, 
                             consoleLogResponseBody: true, 
                             contentType: 'APPLICATION_JSON',
                             customHeaders: customHeaders, 
                             httpMode: httpMethod, 
                             quiet: true, 
                             requestBody: requestBody, 
                             responseHandle: 'NONE', 
                             timeout: 60, 
                             url: url, 
                             validResponseCodes: validResponseCodes
  return response
}

/**
 * @param jiraCredentials - Jira credentials to be used for authentication
 * @param gitPrUrl - The git pr url to be linked to the issue
 * @param labels - The list of labels to be added to the issue
 * @param summary - The summary of the issue
 * @returns void
 */
void jiraCreateIssue(String jiraCredentials, String gitPrUrl, String[] labels, String summary) {
  def body = """{
      "fields": {
          "project": {
              "id": "12321620",
              "key": "INTLY",
              "name": "Integreatly"
          },
          "summary": "${summary}",
          "description": "${gitPrUrl}",
          "labels": ${labels},
          "issuetype": {
              "name": "Task"
          },
          "priority": {
              "id": "3",
              "name": "Major"
          },
          "customfield_12310220": "${gitPrUrl}"
      }
  }"""

  def endpoint = 'issue'
  def response = jiraApiRequest(endpoint, 'POST', body, jiraCredentials, [], '201')
  def data = readJSON text: response.content
  println "[INFO] Issue created: https://issues.jboss.org/browse/${data.key}"
}

/**
 * @param credentials - The jira credentials to be used for authentication
 * @param labels - List of labels used to find the jira issue
 * @returns [boolean, string] Returns true and the id of the jira issue if found
 */
def jiraHasIssue(String jiraCredentials, String[] labels) {
  String query = "project = INTLY AND status = Open"

  for (i = 0; i < labels.size(); i++) {
    def label = labels[i].replaceAll('"', "'")
    query = "${query} AND labels=${label}"
  }

  def body = """{
      "jql": "${query}"
  }"""

  def endpoint = 'search'
  def response = jiraApiRequest(endpoint, 'POST', body, jiraCredentials, [], '200')

  def data = readJSON text: response.content

  def found = response.status == 200 && data.total > 0
  def key = ''
  if (found) {
      key = data.issues[0].key
  }

  return [found, key]
}