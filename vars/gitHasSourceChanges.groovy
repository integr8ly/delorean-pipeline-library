#!/usr/bin/groovy

/**
 * @param sourceBranch - The git branch containing your changes
 * @param targetBranch - The target git branch that the source branch will be compared against to
 * @returns true/false
 */
def call(String sourceBranch, String targetBranch) {
  def sourceChanges = false
  def totalCommits = sh(returnStdout: true, script: "git log origin/${targetBranch}..HEAD --pretty=o | wc -l").trim()
  if ((totalCommits as int) > 0) {
    sourceChanges = true
    println("Changes detected on ${sourceBranch}, totalCommits:${totalCommits}")
  } else {
    sourceChanges = false
    println("No changes detected on ${sourceBranch}")
  }

  return sourceChanges
}