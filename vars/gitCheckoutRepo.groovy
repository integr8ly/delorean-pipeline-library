#!/usr/bin/groovy

/**
 * @param gitRepoUrl - The git url of the repository to checkout
 * @param gitBranch - The remote branch to be pulled
 * @param gitCredentialsID - The github credential to be used for cloning the repository
 * @param checkoutDir - The target directory that the repo will be cloned in
 * @returns void
 */
def call(String gitRepoUrl, String gitBranch, String gitCredentialsID, String checkoutDir) {
  try {
    checkout([$class: 'GitSCM', 
      branches: [[name: gitBranch]],
      doGenerateSubmoduleConfigurations: false,
      extensions: [
        [$class: 'RelativeTargetDirectory', relativeTargetDir: checkoutDir],
      ],
      submoduleCfg: [],
      userRemoteConfigs: [
        [credentialsId: gitCredentialsID, url: gitRepoUrl]
      ]])
  } catch (Exception e) {
    error "Failed to checkout ${gitRepoUrl} in the ${checkoutDir} directory."
  }
}