#!/usr/bin/groovy
import org.integr8ly.GitUtils

/**
 * @param gitBranch - The name of the branch to push
 * @param forcePush - Enables force push. Defaults to false
 * @returns void
 */
def call(String gitBranch, forcePush = false) {
  def gitUtil = new GitUtils()

  if (params.dryRun) {
    println "Would push '${gitBranch}' to remote branch. Force push enabled: ${forcePush}"
  } else {
    gitUtil.gitPush(gitBranch, forcePush)
  }
}