#!/usr/bin/groovy
import org.integr8ly.GitUtils

def call(String[] gitBranches, String baseBranch, errOnFailedMerge) {
  def gitUtil = new GitUtils()
  if (gitBranches.size() == 0) {
    error "No branches to merge to ${baseBranch}"
  }

  for (i = 0; i < gitBranches.size(); i++) {        
    gitUtil.gitMerge(gitBranches[i], baseBranch, errOnFailedMerge)
  }
}