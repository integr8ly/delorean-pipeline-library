import org.integr8ly.Utils

def call(String currentVersion, String newVersion, String productName) {
  def util = new Utils()
  def current = util.extractSemVer(currentVersion, productName)
  def latest = util.extractSemVer(newVersion, productName)

  return util.hasNewRelease(current, latest)
}