package org.powertac.common

import org.apache.log4j.FileAppender
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class LogService
{
  static transactional = false

  static String logPattern = '%d [%t] %-5p %c{2} %x - %m%n'

  def appender
  def logFilename
  def loggers = []


  def start()
  {
    Competition competition = Competition.list()?.get(0)
    if (competition) {
      def fmt = DateTimeFormat.forPattern("yyyyMMdd_HHmmss");
      def filename = "logs/${competition.name}_${fmt.print(new DateTime())}.log"
      def loggerNames = ['org.powertac',
                         'grails.app.service.org.powertac',
                         'grails.app.controller.org.powertac',
                         'grails.app.domain.org.powertac']
      start(filename, loggerNames)
    } else {
      log.warn("No competition exists")
    }

  }

  def start (String filename, List loggerNames)
  {

    if (appender) {
      stop()
    }

    appender = new FileAppender(new PatternLayout(logPattern), filename)

    for (String loggerName in loggerNames) {
      Logger logger = Logger.getLogger(loggerName)
      if (logger) {
        loggers << logger
        logger.addAppender(appender)
      } else {
        log.warn("Could not find logger for '${loggerName}'")
      }
    }
  }

  def stop ()
  {
    if (appender) {
      for (Logger logger in loggers) {
        logger.removeAppender(appender)
      }

      if (appender) {
        appender.close()
      }

      loggers = []
      appender = null
    } else {
      log.warn("LogService has not been started.")
    }
  }
}
