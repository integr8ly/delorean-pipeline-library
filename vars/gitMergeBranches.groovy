#!/usr/bin/groovy
import org.integr8ly.Utils

/**
 * @param gitBranches - List of the git branch names to be merged
 * @param baseBranch - The base git branch
 * @returns void
 */
def call(String[] gitBranches, baseBranch) {
  def util = new Utils()
  if (gitBranches.size() == 0) {
    error "No branches to merge to ${baseBranch}"
  }

  // If a merge fails, the whole job will fail
  for (i = 0; i < gitBranches.size(); i++) {        
    util.gitMerge(gitBranches[i], baseBranch)
  }
}