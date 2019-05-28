#!/usr/bin/groovy
import org.integr8ly.GitHubUtils

def call(String gitOrg, String gitRepoName, String ghCrendentialsID, String releaseFetchMethod, String gaReleaseTagRef) {
  def ghUtils = new GitHubUtils()
  if (releaseFetchMethod == "tag") {
    return ghUtils.ghGetLatestReleaseByTag(gitOrg, gitRepoName, ghCrendentialsID, gaReleaseTagRef)
  } else {
    return ghUtils.ghGetLatestReleaseByRelease(gitOrg, gitRepoName, ghCrendentialsID)
  }
}