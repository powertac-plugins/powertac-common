package org.powertac.common

import org.apache.log4j.FileAppender
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.apache.log4j.Level

class LogService
{
  static transactional = false

  static final String LOG_PATTERN = '%d [%t] %-5p %c{2} %x - %m%n'
  static final Level DEFAULT_ON = Level.ALL
  static final Level DEFAULT_OFF = Level.OFF

  def appender
  def logFilename
  def loggers = []
  def savedDefaultAppenderThreshold = Level.DEBUG
  def start()
  {
    Competition competition = Competition.list()?.get(0)
    if (competition) {
      def filename = "logs/PowerTAC-${competition.id}.log"
      def loggerNames = ['org.powertac',
                         'grails.app.service.org.powertac',
                         'grails.app.controller.org.powertac',
                         'grails.app.domain.org.powertac',
                         'grails.app.task.org.powertac']
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

    appender = new FileAppender(new PatternLayout(LOG_PATTERN), filename)

    for (String loggerName in loggerNames) {
      Logger logger = Logger.getLogger(loggerName)
      if (logger) {
        loggers << logger
        logger.addAppender(appender)
      } else {
        log.warn("Could not find logger for '${loggerName}'")
      }
    }

    setDefaultAppenderEnable(false)
  }

  def stop ()
  {
    setDefaultAppenderEnable(true)
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

  def getDefaultAppender() {
    Logger rootLogger = Logger.getRootLogger()
    return rootLogger.getAppender("file")
  }

  def setDefaultAppenderEnable(boolean enable) {
    def appender = getDefaultAppender()
    if (appender) {
      if (enable) {
        appender.threshold = savedDefaultAppenderThreshold
      } else {
        if (appender.threshold != DEFAULT_OFF) {
          savedDefaultAppenderThreshold = appender.threshold
        }
        appender.threshold = DEFAULT_OFF
      }
    }
  }
}
