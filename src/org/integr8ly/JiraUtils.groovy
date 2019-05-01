#!/usr/bin/groovy
package org.integr8ly

/**
 * Requires the http_request plugin to be installed (https://plugins.jenkins.io/http_request)
 * @param url - Jira rest api url
 * @param httpMethod - HTTP method to be used for the request
 * @param requestBody - HTTP request body
 * @param jiraCredentials - Jira credentials to be used for authentication
 * @param customHeaders - Additional headers to be used for the request
 * @param validResponseCodes - Valid HTTP response codes
 * @returns HTTP response
 */
def jiraApiRequest(String url, String httpMethod = 'GET', String requestBody = null, String jiraCredentials, customHeaders = [], validResponseCodes = '100:399') {
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
 * @param url - Jira rest api url
 * @param body - Contains the details of the jira issue to be created
 * @returns the id of the created jira issue
 */
void jiraCreateIssue(String jiraCredentials, String url, String body) {
  url = "${url}/issue"
  def response = jiraApiRequest(url, 'POST', body, jiraCredentials, [], '201')
  def data = readJSON text: response.content
  return data.key
}

/**
 * @param credentials - The jira credentials to be used for authentication
 * @param url - Jira rest api url
 * @param query - JQL query used to find the jira issue
 * @returns [boolean, string] Returns true and the id of the jira issue if found
 */
def jiraHasIssue(String jiraCredentials, String url, String query) {
  url = "${url}/search"

  def body = """{
      "jql": "${query}"
  }"""

  def response = jiraApiRequest(url, 'POST', body, jiraCredentials, [], '200')

  def data = readJSON text: response.content

  def found = response.status == 200 && data.total > 0
  def key = ''
  if (found) {
      key = data.issues[0].key
  }

  return [found, key]
}