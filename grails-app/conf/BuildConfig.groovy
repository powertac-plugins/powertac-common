grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits("global") {
    // uncomment to disable ehcache
    // excludes 'ehcache'
  }
  log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  repositories {
    grailsPlugins()
    grailsHome()
    grailsCentral()

    // uncomment the below to enable remote dependency resolution
    // from public Maven repositories
    mavenLocal()
    mavenCentral()
    //mavenRepo "http://snapshots.repository.codehaus.org"
    //mavenRepo "http://repository.codehaus.org"
    //mavenRepo "http://download.java.net/maven/2/"
    //mavenRepo "http://repository.jboss.com/maven2/"
  }
  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
    //compile( group: 'joda-time', name: 'joda-time', version: '1.6.2', export: false )
    //compile( group: 'joda-time', name: 'joda-time-hibernate', version: '1.2', export: false )

    // runtime 'mysql:mysql-connector-java:5.1.13'
  }
}

grails.project.dependency.distribution = {
  remoteRepository(id: "powertacPlugins", url: "http://ibwstinger.iw.uni-karlsruhe.de/artifactory/plugins-release-local/") {
    authentication username: "[username]", password: "[password]"
  }
}
