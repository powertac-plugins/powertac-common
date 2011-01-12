includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsSettings")

target(installValidateableConfiguration: "Updates Config.groovy with the validateable settings required to make Command objects validateable.") {
  def configFile = new File(basedir, "grails-app/conf/Config.groovy")
  if (configFile.exists()) {
    def appConfig = configSlurper.parse(configFile.toURI().toURL())
    if (appConfig.grails.validateable.packages) {
      event "StatusUpdate", ["Config.groovy already contains 'grails.validateable.packages'"]
    } else {
      event "StatusUpdate", ["Adding validateable Configuration to Config.groovy"]
      configFile.withWriterAppend {
        it.write """
// Added by the powertac-common plugin:
grails.validateable.packages = ['org.powertac.common.command']
"""
      }
    }
  }
}

setDefaultTarget(installValidateableConfiguration)