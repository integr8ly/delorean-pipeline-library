#!/usr/bin/groovy

/**
 * @param targetBranch - Git branch to rebase against
 * @param baseBranch - Git branch to rebase the target branch into
 * @param errOnFailedRebase - Fails the build if the rebase was not successful. Defaults to false
 * @returns void
 */
def call(String targetBranch, String baseBranch, errOnFailedRebase = false) {
  try {
    sh "git rebase origin/${targetBranch}"
  } catch (Exception e) {
    def errMsg = "We were unable to automatically rebase the target branch '${targetBranch}' into the base branch '${baseBranch}'. Please fix these conflicts locally and push the changes to ${baseBranch} before running this job again!"
    sh "git rebase --abort"

    if (errOnFailedRebase) {
      error "${errMsg}"
    } else {
      println "${errMsg}"
    }
  }
}