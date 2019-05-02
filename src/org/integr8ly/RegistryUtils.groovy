#!/usr/bin/groovy
package org.integr8ly

import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

def getAccessToken(String credentials) {
    def url = 'https://sso.redhat.com/auth/realms/rhcc/protocol/redhat-docker-v2/auth?service=docker-registry&client_id=curl&scope=repository:rhel:pull'
    def response = httpRequest authentication: credentials, 
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

def isValidAccessToken(String host, String token) {
    def url = "${host}/v2"
    def headers = [
        'Authorization': "Bearer ${token}"
    ]
    def response = httpRequest
        consoleLogResponseBody: true,
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

def getTags(String host, String token, String image) {
    def url = "${host}/v2/${image}/tags/list"
    def headers = [
        'Authorization': "Bearer ${token}"
    ]
    def response = httpRequest
        consoleLogResponseBody: true,
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

    return data['tags']
}

def tagExists(String host, String token, String image, String tag) {
    def tags = getTags(host, token, image)

    return tags.contains(tag)
}