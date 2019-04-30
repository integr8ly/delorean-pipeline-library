#!/usr/bin/groovy
package org.integr8ly

import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

/**
 * @param tagetBranch - The git branch to merge to the base branch
 * @param baseBranch - The base git branch
 * @param errOnFailedMerge - Fails the build if the merge was not successful. Defaults to false
 * @returns void
 */
void gitMerge(String targetBranch, String baseBranch, errOnFailedMerge = false) {
  try {
    sh "git pull --no-edit origin ${targetBranch}"
  } catch (Exception e) {
    def errMsg = "We were unable to merge the branch '${targetBranch}' into the branch '${baseBranch}'"
    if (errOnFailedMerge) {
      error "${errMsg}"
    } else {
      println "${errMsg}"
    }
  }
}

/**
 * @param gitBranch - The git branch to push to the remote repository
 * @param forcePush - Enables force push. Defaults to false
 * @returns void
 */
void gitPush(String gitBranch, forcePush = false) {
  if (forcePush) {
    // We should be using force-with-lease flag, but the agents appear to have a really old git version in them.
    sh "git push origin ${gitBranch} --force"
  } else {
    sh "git push origin ${gitBranch}"
  }
}
/**
 * @param untrackedFiles - Mode to specify the handling of untracked files. Options: 'no', 'normal', 'all'. Defaults to 'no'
 * @returns a string stating the changes made if the repo is dirty. Will return an empty string if no changes are found.
 */
String gitRepoIsDirty(String untrackedFiles = 'no') {
  return sh(returnStdout: true, script: "git status --porcelain --untracked-files=${untrackedFiles}")?.trim()
}

