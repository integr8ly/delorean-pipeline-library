#!/usr/bin/groovy

/**
 * @param gitBranch - The git branch name to be created
 * @param useExistingBranch - Use an existing branch if found
 * @param pushOnCreate - Pushes the branch created locally to the remote git repository if set to true. Defaults to false
 * @param remote - The name of the remote the branch should be created from/pushed to. Defaults to 'origin'
 * @return void
 */
def call(String gitBranch, boolean useExistingBranch = false, boolean pushOnCreate = false, String remote = 'origin') {
    String remoteBranchCommit = sh(returnStdout: true, script: "git ls-remote ${remote} refs/heads/${gitBranch} | cut -f 1").trim()

    if (remoteBranchCommit && useExistingBranch) {
        sh "git checkout ${gitBranch}"
    } else {
        sh "git checkout -b ${gitBranch}"

        if (pushOnCreate) {
            if (params.dryRun) {
                println "Would push the local branch '${gitBranch}' to the remote '${remote}' repository"
            } else {
                sh "git push ${remote} ${gitBranch}"
            }
        }
    }
}
