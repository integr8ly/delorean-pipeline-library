#!/usr/bin/groovy

import org.integr8ly.RegistryImage

/**
 * @param config { 
 *     imageUrls = List of product image urls to check against the target registry
 *     registryCredentials = The secret name to use in request authentication (username + password)
 *     targetRegistryHost = The registry host to check the image against
 * }
 * @return List of product image urls found in the template directory
 */
def call(config) {
  def imageUrls = config.imageUrls ?: []
  def preReleaseImages = []

  preReleaseImages = imageUrls.collect { imageUrl ->
    def image = new RegistryImage(imageUrl)
    def params = [
      credentials: config.registryCredentials,
      host: config.targetRegistryHost ?: image.getHttpsHost(),
      image: image.getPath(),
      tag: image.getTag()
    ]

    try {
      registryHasImage(params) ? null : imageUrl
    } catch (Exception e) {
      println "Failed checking ${imageUrl}, adding image to productPreReleaseImages"
      imageUrl
    }
  }.flatten() - null - ''

  return preReleaseImages
}