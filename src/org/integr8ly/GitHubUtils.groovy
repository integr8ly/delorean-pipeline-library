#!/usr/bin/groovy
package org.integr8ly

import com.cloudbees.groovy.cps.NonCPS
import org.kohsuke.github.GHCommitState
import org.kohsuke.github.GHCommitStatus
import org.kohsuke.github.GHIssueState
import org.kohsuke.github.GHLabel
import org.kohsuke.github.GHPullRequest
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import org.kohsuke.github.GitHubBuilder
import org.kohsuke.github.PagedIterable

/**
 * Requires the http_request plugin to be installed (https://plugins.jenkins.io/http_request)
 * @param endpoint - Github endpoint to send the request to (https://developer.github.com/v3/apps/available-endpoints/)
 * @param httpMethod - HTTP method to be used for the request
 * @param requestBody - HTTP request body
 * @param ghApiTokenID - The credentials id where the github api token is stored in Jenkins
 * @param customHeaders - Additional headers to be used for the request
 * @param validResponseCodes - Valid HTTP response codes
 * @returns HTTP response
 */
def ghApiRequest(String endpoint, String httpMethod = 'GET', String requestBody = null, String ghApiTokenID, customHeaders = [], validResponseCodes = '100:399') {
  def url = "https://api.github.com/${endpoint}"
  withCredentials([string(credentialsId: ghApiTokenID, variable: 'gitToken')]) {
    customHeaders.plus(["name": "Authorization", "value": "token ${env.gitToken}"])
  }

  def response = httpRequest httpMode: httpMethod,
                             contentType: 'APPLICATION_JSON',
                             requestBody: requestBody,
                             customHeaders: customHeaders,
                             url: url,
                             validResponseCodes: validResponseCodes
  return response
}

/**
 * @param repo - Github repository
 * @param head - Head user and branch name to be used to filter pull requests (user:branch-name)
 * @param base - Base branch name
 * @param title - Title of the pull request to be created
 * @param body - Content of the pull request to be created
 * @param labels - List of labels to be added to the pull request
 * @returns github pull request found/created
 */
@NonCPS
GHPullRequest ghFindOrCreatePullRequest(GHRepository repo, String head, String base, String title, String body, String[] labels) {
  GHPullRequest pr = ghGetPullRequest(repo, head, base, GHIssueState.OPEN)
  if (pr) {
      println "Found already open PR on ${repo.getName()} head:${head} base:${base} - ${pr.getHtmlUrl()}"
  } else {
      //head for the query above requires the user e.g. integr8ly:branch_name, but here we only want branch_name see https://developer.github.com/v3/pulls/#list-pull-requests
      head = head.split(':').last()
      pr = repo.createPullRequest(title, head, base, body)
      pr.setLabels(labels)
      println "Opened new PR on ${repo.getName()} head:${head} base:${base} - ${pr.getHtmlUrl()}"
  }
  return pr
}

/**
 * @param repo - Github repository
 * @param head - Head user and branch name to be used to filter pull requests (user:branch-name)
 * @param base - Base branch name
 * @param state - State of the pull request. Options: open/closed
 * @returns github pull request
 */
@NonCPS
GHPullRequest ghGetPullRequest(GHRepository repo, String head, String base, GHIssueState state) {
  PagedIterable<GHPullRequest> pullRequests = repo.queryPullRequests()
        .head(head)
        .base(base)
        .state(state)
        .list()
  return pullRequests[0]
}


/**
 * @param github - Github client
 * @param prUrl - Url of the github pull request
 * @returns github pull request
 */
@NonCPS
GHPullRequest ghGetPullRequestFromUrl(GitHub gitHub, String prUrl) {
  URL url = new URL(prUrl)
  String[] pathSegments = url.path.trim().split('/') - '' - 'pull'
  String ghOwner = pathSegments[0]
  String ghRepo = pathSegments[1]
  String ghPrNumber = pathSegments[2]
  GHRepository repo = gitHub.getRepository("${ghOwner}/${ghRepo}")
  return repo.getPullRequest(ghPrNumber as int)
}

/**
 * @param pr - Github pull request to check the label against
 * @param labelName - Name of the PR label
 * @returns boolean
 */
@NonCPS
boolean ghPrHasLabel(GHPullRequest pr, String labelName) {
  boolean hasLabel = false
  for (GHLabel label : pr.getLabels()) {
    if (label.getName() == labelName) {
        hasLabel = true
    }
  }
  return hasLabel
}

/**
 * @param pr - Github pull request
 * @param state - Github commit state
 * @param targetUrl - Jenkins build url
 * @param description - Github pull request commit status description. Options: 'Pending', 'Failure', 'Success'
 * @param context - Github commit status context
 * @returns github commit status
 */
@NonCPS
GHCommitStatus ghUpdatePrCommitStatus(GHPullRequest pr, GHCommitState state, String targetUrl, String description, String context) {
  return pr.getRepository().createCommitStatus(pr.getHead().getSha(), state, targetUrl, description, context)
}

/**
 * @param github - Github client
 * @param prUrl - Github pull request url
 * @param state - Github commit state
 * @param targetUrl - Jenkins build url
 * @param description - Github pull request commit status description. Options: 'Pending', 'Failure', 'Success'
 * @param context - Github commit status context
 * @returns github commit status
 */
@NonCPS
GHCommitStatus ghUpdatePrCommitStatus(GitHub gitHub, String prUrl, GHCommitState state, String targetUrl, String description, String context) {
  return ghUpdatePrCommitStatus(ghGetPullRequestFromUrl(gitHub, prUrl), state, targetUrl, description, context)
}

/**
 * @param url - A GitHub project URL
 * @param type - Type of URL required (https or ssh)
 * @param webAccess - Should the URL be web accessible
 * @returns transformed URL.
 */
static String ghTransformUrl(String url, type = 'https', webAccess = false) {
    String transformedUrl
    if(type == 'https') {
        transformedUrl = url.replace("git@github.com:", "https://github.com/")
        if(webAccess) {
            transformedUrl = transformedUrl.replace('.git', '')
        }
    } else {
        transformedUrl = url.replace("https://github.com/", "git@github.com:")
    }
    return transformedUrl
}

/**
 * @param org - Name of the github organization where the repository is located
 * @param repoName - Name of the github repository
 * @param apiTokenID - Jenkins credentials ID where the github api token is stored
 * @param filterByTagRef - Reference used to filter the repository tags
 * @returns a string containing the product's latest release version
 */
def ghGetRepoTags(String org, String repoName, String apiTokenID, String filterByTagRef = "") {
  def endpoint = "repos/${org}/${repoName}/git/refs/tags"
  def response = ghApiRequest(endpoint, 'GET', null, apiTokenID, [], '200')

  def tags = readJSON text: response.content
  tags = tags.findAll { tag -> tag.ref.contains(filterByTagRef) }
  return tags
}


/**
 * @param org - Name of the github organization where the repository is located
 * @param repoName - Name of the github repository
 * @param apiTokenID - Jenkins credentials ID where the github api token is stored
 * @param filterByTagRef - Reference used to filter the repository tags
 * @returns a string containing the product's latest release version
 */
String ghGetLatestReleaseByTag(String org, String repoName, String apiTokenID, String filterByTagRef = "") {
  def latestRelease = ""
  def tags = ghGetRepoTags(org, repoName, apiTokenID, filterByTagRef)

  if (tags) {
    latestRelease = tags.last().ref
    latestRelease = latestRelease.minus("refs/tags/")
  }

  return latestRelease
}

/**
 * @param org - Name of the github organization where the repository is located
 * @param repoName - Name of the github repository
 * @param apiTokenID - Jenkins credentials ID where the github api token is stored
 * @returns a string containing the product's latest release version
 */
String ghGetLatestReleaseByRelease(String org, String repoName, String apiTokenID) {
  def latestRelease = ""
  def endpoint = "repos/${org}/${repoName}/releases/latest"
  def response = ghApiRequest(endpoint, 'GET', null, apiTokenID, [], '200')

  def release = readJSON text: response.content

  if (release) {
    latestRelease = release.tag_name
  }

  return latestRelease
}
