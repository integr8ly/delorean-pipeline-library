#!/usr/bin/groovy
package org.integr8ly

import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

/**
 * @param productVersion - The product version used to extract the semver from
 * @param productName - Name of the product
 * @returns a list [MAJOR, MINOR, PATCH] extracted from the product version
 */
String[] extractSemVer(productVersion, productName) {
  if (productName == "fuse") {
      return productVersion.replaceAll("[^0-9/-]", "").tokenize("-")
  } else {
      return productVersion.replaceAll("[^0-9/.]", "").tokenize(".")
  }
}

/**
 * Checks if the current version is lower than the latest version
 * @param current - The current product version
 * @param latest - The latest product version
 * @returns true/false
 */
boolean hasNewRelease(current, latest) {
  def previousVer = 0
  def currentVer = 0

  for (i = 0; i <= 2; i++) {
    if (i > 0) {
        previousVer = i - 1
    }
    currentVer = i

    def previousDiff = current[previousVer] == latest[previousVer]
    if (i == 0) {
        previousDiff = true
    }
    def currentDiff = latest[currentVer] > current[currentVer]
    if (previousDiff && currentDiff) {
        return true
    }
  }

  return false
}

/**
 * @param tagetBranch - The git branch to merge to the base branch
 * @param baseBranch - The base git branch
 */
void gitMerge(String targetBranch, String baseBranch) {
  try {
    sh "git pull --no-edit origin ${targetBranch}"
  } catch (Exception e) {
    error "We were unable to merge the branch '${targetBranch}' into the branch '${baseBranch}'"
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

