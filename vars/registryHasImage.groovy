#!/usr/bin/groovy
import org.integr8ly.RegistryUtils

def call(String credentials, String host, String image, String tag) {
  def utils = new RegistryUtils()
  def token = utils.getAccessToken(credentials)

  if (!utils.isValidAccessToken(host, token)) {
    error '[ERROR] Registry authentication failed'
  }

  return utils.tagExists(host, token, image, tag)
}