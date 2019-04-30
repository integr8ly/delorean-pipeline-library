#!/usr/bin/groovy
import org.integr8ly.Utils

/**
 * @param commitMsgTitle - Title of the commit message
 * @param body - Closure
 * @returns void
 */
def call(String commitMsgTitle, body) {
  def utils = new Utils()
  def changes = []
  def msgs = []
  body(msgs)
  msgs = msgs - null - ""

  if(utils.gitRepoIsDirty()) {
    changes = [commitMsgTitle]
    changes << msgs.join('\n')
    changes = changes.join('\n\n')
    sh "git commit -a -m \"${changes}\""
  }
}
