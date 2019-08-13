#!/usr/bin/groovy
import org.integr8ly.RegistryUtils

/**
 * @param config {
 *     credentials = secret name to use in request authentication (username + password)
 *     host = container registry host
 *     image = image full name to retrieve info from, should not contain image tags
 *     tag = image tag to check
 * }
 * @return booleam
 */
def call(config) {
    def utils = new RegistryUtils()

    def token = utils.getAccessToken([
        credentials: config.credentials
    ])

    return utils.tagExists([
        host: config.host,
        token: token,
        image: config.image,
        tag: config.tag
    ])
}