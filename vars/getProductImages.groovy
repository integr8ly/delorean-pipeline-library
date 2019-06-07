#!/usr/bin/groovy

/**
 * @param config { 
 *     targetDir = Directory where the product templates and resources are located
 *     excludeFiles = List of file names to be excluded from the search
 *     registries = List of registries to look for
 *     registryIDs = List of registry IDs to look for
 * }
 * @return List of product image urls found in the template directory
 */
def call(config) {
  def targetDir = config.targetDir ?: '.'
  def excludeFiles = config.excludeFiles ?: []
  def registries = config.registries ?: []
  def registryIDs = config.registryIDs ?: []
  def productImages = []

  def excludeFilesFlag = ""
  excludeFiles.each{ fileName ->
    excludeFilesFlag = "${excludeFilesFlag} ! -name \'${fileName}\'"
  }

  dir(targetDir) {
    productImages = registries.collect { registry ->
      registryIDs.collect { registryID ->
        sh(returnStdout: true, script: "find . -name \'*.y*ml\' ${excludeFilesFlag} -exec grep -oP \'${registry}/${registryID}.*/[^[:blank:]].*\' {} \\; | sort | uniq").replace('"', '').split('\n')
      }
    }.flatten() - null - ''
    productImages = productImages.unique()
  }

  return productImages
}