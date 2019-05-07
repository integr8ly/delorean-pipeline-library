#!/usr/bin/groovy
import org.integr8ly.GitHubUtils

def call(String gitOrg, String gitRepoName, String ghApiTokenID, String releaseFetchMethod, String gaReleaseTagRef) {
  def ghUtils = new GitHubUtils()
  if (releaseFetchMethod == "tag") {
    return ghUtils.ghGetLatestReleaseByTag(gitOrg, gitRepoName, ghApiTokenID, gaReleaseTagRef)
  } else {
    return ghUtils.ghGetLatestReleaseByRelease(gitOrg, gitRepoName, ghApiTokenID)
  }
}