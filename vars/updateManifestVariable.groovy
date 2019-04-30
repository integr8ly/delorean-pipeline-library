#!/usr/bin/groovy

/**
 * @param manifestFileTxt - Content of the manifest file
 * @param name - The manifest variable name to be used as an identifier
 * @param value - The manifest variable value to be updated
 * @returns the updated manifest file content
 */
def call(String manifestFileTxt, String name, String value) {
  if (name && value) {
    println("Updating manifest variable: name = ${name}, value = ${value}")
    manifestFileTxt = manifestFileTxt.replaceFirst(/${name}: .*/, "${name}: '${value}'")
  } else {
    println("Unable to update manifest variable: name = ${name}, value = ${value}")
  }
  return manifestFileTxt
}
