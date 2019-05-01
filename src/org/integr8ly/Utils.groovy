#!/usr/bin/groovy
package org.integr8ly

/**
 * @param productVersion - The product version used to extract the semver from
 * @param productName - Name of the product
 * @returns a list [MAJOR, MINOR, PATCH] extracted from the product version
 */
String[] extractSemVer(productVersion, productName) {
  if (productName == "fuse") {
      return productVersion.replaceAll("[^0-9/-]", "").tokenize("-")
  } else {
      return productVersion.replaceAll("[^0-9/.]", "").tokenize(".")
  }
}

/**
 * Checks if the current version is lower than the latest version
 * @param current - The current product version
 * @param latest - The latest product version
 * @returns true/false
 */
boolean hasNewRelease(current, latest) {
  def previousVer = 0
  def currentVer = 0

  for (i = 0; i <= 2; i++) {
    if (i > 0) {
        previousVer = i - 1
    }
    currentVer = i

    def previousDiff = current[previousVer] == latest[previousVer]
    if (i == 0) {
        previousDiff = true
    }
    def currentDiff = latest[currentVer] > current[currentVer]
    if (previousDiff && currentDiff) {
        return true
    }
  }

  return false
}