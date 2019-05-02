#!/usr/bin/groovy
package org.integr8ly

import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

/**
 * @param config {
 *     credentials = secret name to use in request authentication (username + password)
 * }
 * @return String
 */
def getAccessToken(config) {
    def url = 'https://sso.redhat.com/auth/realms/rhcc/protocol/redhat-docker-v2/auth?service=docker-registry&client_id=curl&scope=repository:rhel:pull'
    def response = httpRequest authentication: config.credentials, 
        consoleLogResponseBody: true,
        contentType: 'APPLICATION_JSON',
        quiet: true,
        timeout: 60,
        url: url

    if (response.status != 200) {
        error "[ERROR] Access token request failed: ${response.code} ${response.content}"
    }

    def data = readJSON text: response.content
    if (!data.containsKey('access_token')) {
        error "[ERROR] Response body does not contain an access_token key: ${data}"
    }

    return data['access_token']
}

/**
 * @param config {
 *     host = container registry host
 *     token = container registry access token
 * }
 * @return boolean
 */
def isValidAccessToken(config) {
    def url = "${config.host}/v2"
    def headers = [
        [name: 'Authorization', value: "Bearer ${config.token}"]
    ]
    def response = httpRequest consoleLogResponseBody: true,
        customHeaders: headers,
        contentType: 'APPLICATION_JSON',
        quiet: true,
        timeout: 60,
        url: url

    if (response.status != 200) {
        error "[ERROR] Test access token request failed: ${response.code} - ${response.content}"
    }

    return response.status == 200
}

/**
 * @param config {
 *     host = container registry host
 *     token = container registry access token
 *     image = image full name to retrieve info from, should not contain image tags
 * }
 * @return List
 */
def getTags(config) {
    def url = "${config.host}/v2/${config.image}/tags/list"
    def headers = [
        [name: 'Authorization', value: "Bearer ${config.token}"]
    ]
    def response = httpRequest consoleLogResponseBody: true,
        customHeaders: headers,
        contentType: 'APPLICATION_JSON',
        quiet: true,
        timeout: 60,
        url: url

    if (response.status != 200) {
        error "[ERROR] Tag list request failed: ${response.code} - ${response.content}"
    }

    def data = readJSON text: response.content
    if (!data.containsKey('tags')) {
        error "[ERROR] Response body does not contain a tags key: ${data}"
    }

    return data.tags
}

/**
 * @param config {
 *     host = container registry host
 *     token = container registry access token
 *     image = image full name to retrieve info from, should not contain image tags
 *     tag = image tag to check
 * }
 * @return booleam
 */
def tagExists(config) {
    def tags = getTags([
        host: config.host,
        token: config.token,
        image: config.image
    ])

    return tags.contains(config.tag)
}