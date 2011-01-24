package org.powertac.common

import org.joda.time.Instant
import org.joda.time.base.AbstractDateTime
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationEvent

class TimeService 
{
  static transactional = false

  def grailsApplication

  static final long HOUR = 1000l * 60 * 60
  
  long base
  long start
  long rate

  Instant currentTime

  def updateTime () 
  {
    systemTime = new Instant().getMillis()
    currentTime = new Instant(base + (systemTime - start) * rate)
  }
  
  void setCurrentTime (Instant time)
  {
    currentTime = time
  }
  
  void setCurrentTime (AbstractDateTime time)
  {
    currentTime = new Instant(time)
  }

  void publish(ApplicationEvent event) 
  {
    grailsApplication.mainContext.publishEvent(event)
  }
}
