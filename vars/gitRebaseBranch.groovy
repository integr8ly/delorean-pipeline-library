#!/usr/bin/groovy

/**
 * @param targetBranch - Git branch to rebase against
 * @param baseBranch - Git branch to rebase the target branch into
 * @returns void
 */
def call(String targetBranch, String baseBranch) {
  try {
      sh "git rebase origin/${targetBranch}"
  } catch (Exception e) {
      sh "git rebase --abort"
      println "We were unable to automatically rebase the target branch '${targetBranch}' into the base branch '${baseBranch}'. Please fix these conflicts locally and push the changes to ${baseBranch} before running this job again!"
  }
}