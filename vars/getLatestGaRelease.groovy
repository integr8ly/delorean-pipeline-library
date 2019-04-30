#!/usr/bin/groovy
import org.integr8ly.GitHubUtils

/**
 * @param org - Name of the github organization where the repository is located
 * @param repoName - Name of the github repository
 * @param apiTokenID - Jenkins credentials ID where the github api token is stored
 * @param gaReleaseTagRef - Reference used to filter GA releases from the repository tags
 * @returns a string containing the product's latest release version
 */
String getLatestReleaseByTag(String org, String repoName, String apiTokenID, String gaReleaseTagRef) {
  def ghUtils = new GitHubUtils()
  def latestRelease = ""
  def endpoint = "repos/${org}/${repoName}/git/refs/tags"
  def response = ghUtils.ghApiRequest(endpoint, 'GET', null, apiTokenID, [], '200')

  def releases = readJSON text: response.content
  def gaReleases = releases.findAll { release -> release.ref.contains(gaReleaseTagRef) }

  if (gaReleases) {
    latestRelease = gaReleases.last().ref
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
String getLatestReleaseByRelease(String org, String repoName, String apiTokenID) {
  def ghUtils = new GitHubUtils()
  def latestRelease = ""
  def endpoint = "repos/${org}/${repoName}/releases/latest"
  def response = ghUtils.ghApiRequest(endpoint, 'GET', null, apiTokenID, [], '200')

  def release = readJSON text: response.content

  if (release) {
    latestRelease = release.tag_name
  }

  return latestRelease
}

def call(String gitOrg, String gitRepoName, String ghApiTokenID, String releaseFetchMethod, String gaReleaseTagRef) {
  if (releaseFetchMethod == "tag") {
    return getLatestReleaseByTag(gitOrg, gitRepoName, ghApiTokenID, gaReleaseTagRef)
  } else {
    return getLatestReleaseByRelease(gitOrg, gitRepoName, ghApiTokenID)
  }
}