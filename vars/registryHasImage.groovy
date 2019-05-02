#!/usr/bin/groovy
import org.integr8ly.RegistryUtils

def call(String credentials, String host, String image, String tag) {
  def utils = new RegistryUtils()
  def token = utils.getAccessToken(credentials)

  if (!utils.isValidAccessToken(token)) {
    error '[ERROR] Registry authentication failed'
  }

  return utils.hasTag(host, token, image, tag)
}