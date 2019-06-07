#!/usr/bin/groovy

import org.integr8ly.RegistryImage

/**
 * @param config { 
 *     imageUrls = List of product image urls to check against the target registry
 *     registryCredentials = The secret name to use in request authentication (username + password)
 *     registryHost = The registry host to check the image against
 * }
 * @return List of prerelease images
 */
def call(config) {
  def imageUrls = config.imageUrls ?: []
  def preReleaseImages = []

  preReleaseImages = imageUrls.collect { imageUrl ->
    def image = new RegistryImage(imageUrl)
    try {
      def params = [
        credentials: config.registryCredentials,
        host: config.registryHost ?: image.getHttpsHost(),
        image: image.getPath(),
        tag: image.getTag()
      ]
      registryHasImage(params) ? null : imageUrl
    } catch (Exception e) {
      println "Failed checking ${imageUrl}, adding image as prerelease"
      imageUrl
    }
  }.flatten() - null - ''

  return preReleaseImages
}