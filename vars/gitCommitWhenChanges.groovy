#!/usr/bin/groovy
import org.integr8ly.GitUtils

/**
 * @param commitMsgTitle - Title of the commit message
 * @param body - Closure
 * @returns void
 */
def call(String commitMsgTitle, body) {
  def gitUtils = new GitUtils()
  def changes = []
  def msgs = []
  body(msgs)
  msgs = msgs - null - ""

  if(gitUtils.gitRepoIsDirty()) {
    changes = [commitMsgTitle]
    changes << msgs.join('\n')
    changes = changes.join('\n\n')
    sh "git commit -a -m \"${changes}\""
  }
}
