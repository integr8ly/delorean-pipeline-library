#!/usr/bin/groovy

/**
 * @param manifestVar - The manifest variable name to use as an identifier for the product version
 * @param manifestFilePath - The file path of the manifest file
 * @returns a string value of the product version
 */
def call(manifestVar, manifestFilePath) {
    def yaml = readYaml file: manifestFilePath
    return yaml[manifestVar]
}