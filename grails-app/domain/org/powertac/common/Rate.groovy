/*
 * Copyright (c) 2011 by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powertac.common

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.joda.time.base.AbstractDateTime
import org.joda.time.base.AbstractInstant
import org.joda.time.*

/**
* Tariffs are composed of Rates.
* Rates may be applicable on particular days of the week, particular times
* of day, or above some usage threshold. Rates may be fixed or variable. 
* @author jcollins
*/
class Rate implements Serializable
{
  int weeklyBegin = -1 // weekly applicability
  int weeklyEnd = -1
  int dailyBegin = -1 // daily applicability
  int dailyEnd = -1
  BigDecimal tierThreshold = 0.0 // tier applicability
  boolean isFixed = true // if true, minValue is fixed rate
  BigDecimal minValue = 0.0 // min and max rate values
  BigDecimal maxValue = 0.0
  int noticeInterval = 0 // notice interval for variable rate in hours
  BigDecimal expectedMean = 0.0 // expected mean value for variable rate
  TreeSet<HourlyCharge> rateHistory // history of values for variable rate

  static belongsTo = TariffSpecification
  static hasMany = [rateHistory:HourlyCharge]
  static transients = ["value"]

  static constraints = {
    minValue(min:0.0)
    maxValue(min:0.0)
  }

  // introduce dependency on TimeService
  //def timeService
  /**
   * Retrieves the timeService (Singleton) reference from the main application context
   * This is necessary as DI by name (i.e. def timeService) stops working if a class
   * instance is deserialized rather than constructed.
   * Note: In the code below you can can still user timeService.xyzMethod()
   */
  private getTimeService() {
    ApplicationHolder.application.mainContext.timeService
  }
  
  /**
   * Constructor must mung the Partials for weeklyBegin, weeklyEnd,
   * dailyBegin, and dailyEnd
   */
  public Rate (Map m = null)
  {
    m?.each { k,v ->
      if (k == "weeklyBegin")
        setWeeklyBegin(v) // extract day-of-week
      else if (k == "weeklyEnd")
        setWeeklyEnd(v) // extract day-of-week
      else if (k == "dailyBegin")
        setDailyBegin(v) // extract hour-of-day
      else if (k == "dailyEnd")
        setDailyEnd(v) // extract hour-of-day
      else if (k == "noticeInterval")
        setNoticeInterval(v) // truncate to integer hours
      else if (k == "value")
        setValue(v)
      else
        this."$k" = v }
    if (weeklyBegin >= 0 && weeklyEnd == -1) {
      weeklyEnd = weeklyBegin
    }
  }

  /**
   * Process weeklyBegin spec to extract dayOfWeek field
   */
  void setWeeklyBegin (AbstractDateTime begin)
  {
    if (begin != null) {
      weeklyBegin = begin.getDayOfWeek()
    }
  }
  
  /**
   * Process weeklyBegin spec to extract dayOfWeek field
   */
  void setWeeklyBegin (ReadablePartial begin)
  {
    if (begin != null) {
      weeklyBegin = begin.get(DateTimeFieldType.dayOfWeek())
    }
  }
  
  // normal setter also, for Hibernate
  void setWeeklyBegin (int begin)
  {
    weeklyBegin = begin
  }

  /**
   * Process weeklyEnd spec to extract dayOfWeek field
   */
  void setWeeklyEnd (AbstractDateTime end)
  {
    if (end!= null) {
      weeklyEnd= end.getDayOfWeek()
    }
  }
  
  /**
   * Process weeklyEnd spec to extract dayOfWeek field
   */
  void setWeeklyEnd (ReadablePartial end)
  {
    if (end!= null) {
      weeklyEnd= end.get(DateTimeFieldType.dayOfWeek())
    }
  }

  // normal setter also
  void setWeeklyEnd (int end)
  {
    weeklyEnd = end
  }

  /**
   * Process dailyBegin specification to extract hourOfDay field
   */
  void setDailyBegin (AbstractDateTime begin)
  {
    if (begin != null) {
      dailyBegin = begin.getHourOfDay()
    }
  }
  
  /**
   * Process dailyBegin specification to extract hourOfDay field
   */
  void setDailyBegin (ReadablePartial begin)
  {
    if (begin != null) {
      dailyBegin = begin.get(DateTimeFieldType.hourOfDay())
    }
  }

  // normal setter also
  void setDailyBegin (int begin)
  {
    dailyBegin = begin
  }
  
  /**
   * Process dailyEnd specification to extract hourOfDay field
   */
  void setDailyEnd (AbstractDateTime end)
  {
    if (end != null) {
      dailyEnd = end.getHourOfDay()
    }
  }
  
  /**
   * Process dailyEnd specification to extract hourOfDay field
   */
  void setDailyEnd (ReadablePartial end)
  {
    if (end != null) {
      dailyEnd = end.get(DateTimeFieldType.hourOfDay())
    }
  }

  // normal setter also
  void setDailyEnd (int end)
  {
    dailyEnd = end
  }
  
  /**
   * Truncate noticeInterval field to integer hours
   */
  void setNoticeInterval (Duration interval)
  {
    // we assume that integer division will do the Right Thing here
    noticeInterval = interval.getMillis() / TimeService.HOUR
  }
  
  // normal setter also, for Hibernate
  void setNoticeInterval (int interval)
  {
    noticeInterval = interval
  }
  
  /**
   * True just in case this Rate applies at this moment, ignoring the
   * tier.
   */
  boolean applies ()
  {
    return applies(timeService.getCurrentTime())
  }

  /**
   * True just in case this Rate applies at the given DateTime, ignoring the
   * tier.
   */
  boolean applies (AbstractInstant when)
  {
    def appliesWeekly = false
    def appliesDaily = false
    DateTime time = new DateTime(when, DateTimeZone.UTC)

    // check weekly applicability
    def day = time.getDayOfWeek()
    if (weeklyBegin == -1) {
      appliesWeekly = true
    }
    else if (weeklyEnd == -1) {
      appliesWeekly = (day == weeklyBegin)
    }
    else if (weeklyEnd >= weeklyBegin) {
      appliesWeekly = (day >= weeklyBegin && day <= weeklyEnd)
    }
    else {
      appliesWeekly = (day >= weeklyBegin || day <= weeklyEnd)
    }
    
    // check daily applicability
    def hour = time.getHourOfDay()
    if (dailyBegin == -1 || dailyEnd == -1) {
      appliesDaily = true
    }
    else if (dailyEnd > dailyBegin) {
      // Interval does not span midnight
      appliesDaily = ((hour >= dailyBegin) && (hour < dailyEnd))
    }
    else {
      // Interval spans midnight
      appliesDaily = ((hour >= dailyBegin) || (hour < dailyEnd))
    }

    return (appliesWeekly && appliesDaily)
  }
  
  /**
   * True just in case this Rate applies at this moment, for the
   * indicated usage tier.
   */
  boolean applies (BigDecimal usage)
  {
    return applies(usage, timeService.getCurrentTime())
  }
  
  /**
   * True just in case this Rate applies at the specified
   * time, for the indicated usage tier.
   */
  boolean applies (BigDecimal usage, AbstractInstant when)
  {
    if (usage >= tierThreshold) {
      return applies(when)
    }
    else {
      return false
    }
  }

  /**
   * Allows Hibernate to set the value
   */
  void setValue(BigDecimal value) {
    minValue = value
  }

  /**
   * Returns the rate for the current time. Note that the value is returned
   * even in case the Rate does not apply at the current time or current
   * usage tier. For variable rates, the value returned during periods of
   * inapplicability is meaningless, of course.
   */
  BigDecimal getValue ()
  {
    //return getValue(Timeslot.currentTimeslot().getStartDateTime())
    return getValue(timeService.getCurrentTime())
  }
  
  /**
   * Returns the rate for some time in the past or future, regardless of
   * whether the Rate applies at that time, and regardless of whether
   * the requested time is beyond the notification interval of a
   * variable rate.
   */
  BigDecimal getValue (AbstractInstant when)
  {
    if (isFixed)
      return minValue
    else if (rateHistory.size() == 0) {
      println "no rate history, return default"
      return expectedMean // default
    }
    else {
      Instant inst = new Instant(when)
      // if looking beyond the notification interval, return default
      long horizon = inst.getMillis() - timeService.getCurrentTime().getMillis()
      if (horizon / TimeService.HOUR > noticeInterval) {
        //println "Horizon ${horizon / TimeService.HOUR} > notice interval ${noticeInterval}"
        return expectedMean
      }
      // otherwise, return the most recent price announcement for the given time
      HourlyCharge probe = new HourlyCharge(when: inst.plus(1000l), value: 0)
      SortedSet<HourlyCharge> head = rateHistory.headSet(probe)
      if (head == null || head.size() == 0) {
        return expectedMean // default
      }
      else {
        return head.last().value
      }
    }
  }
  
  String toString ()
  {
    String result = "Rate:"
    if (isFixed)
      result += " Fixed ${value}"
    else
      result += " Variable"
    if (weeklyBegin >= 0) {
      result += ", ${weeklyEnd?'starts':''} day ${weeklyBegin}"
      if (weeklyEnd >= 0) {
        result += " ends day ${weeklyEnd}"
      }
    }
    if (dailyBegin >= 0) {
      result += ", ${dailyBegin}:00 -- ${dailyEnd}:00"
    }
    if (tierThreshold > 0.0) {
      result += ", usage > ${tierThreshold}"
    }
    return result
  }
}
