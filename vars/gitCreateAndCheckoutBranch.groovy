#!/usr/bin/groovy

/**
 * @param gitBranch - The git branch name to be created
 * @param useExistingBranch - Use an existing branch if found
 * @param pushOnCreate - Pushes the branch created locally to the remote git repository if set to true. Defaults to false
 * @return void
 */
def call(String gitBranch, boolean useExistingBranch, boolean pushOnCreate = false) {
  String remoteBranchCommit = sh(returnStdout: true, script: "git ls-remote origin refs/heads/${gitBranch} | cut -f 1").trim()

  if (remoteBranchCommit && useExistingBranch) {
    sh "git checkout ${gitBranch}"
  } else {
    sh "git checkout -b ${gitBranch}"

    if (pushOnCreate) {
      if (params.dryRun) {
        println "Would push the local branch '${gitBranch}' to the remote repository"
      } else {
        sh "git push origin ${gitBranch}"
      }
    }
  }  
}