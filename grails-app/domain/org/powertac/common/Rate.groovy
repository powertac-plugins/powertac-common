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

import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.joda.time.*
import org.joda.time.base.AbstractDateTime
import org.joda.time.base.AbstractInstant

import com.thoughtworks.xstream.annotations.*

/**
 * Tariffs are composed of Rates.
 * Rates may be applicable on particular days of the week, particular times
 * of day, or above some usage threshold. Rates may be fixed or variable. 
 * Tariffs and their rates are public information. New tariffs and their Rates
 * are communicated to Customers and to Brokers when tariffs are published.
 * @author jcollins
 */
@XStreamAlias("rate")
class Rate //implements Serializable
{
  private static final log = LogFactory.getLog(this)

  @XStreamAsAttribute
  String id = IdGenerator.createId()

  @XStreamAsAttribute
  int weeklyBegin = -1 // weekly applicability
  @XStreamAsAttribute
  int weeklyEnd = -1
  @XStreamAsAttribute
  int dailyBegin = -1 // daily applicability
  @XStreamAsAttribute
  int dailyEnd = -1
  @XStreamAsAttribute
  double tierThreshold = 0.0 // tier applicability
  @XStreamAsAttribute
  boolean isFixed = true // if true, minValue is fixed rate
  @XStreamAsAttribute
  BigDecimal minValue = 0.0 // min and max rate values
  @XStreamAsAttribute
  BigDecimal maxValue = 0.0
  @XStreamAsAttribute
  int noticeInterval = 0 // notice interval for variable rate in hours
  @XStreamAsAttribute
  double expectedMean = 0.0 // expected mean value for variable rate
  TreeSet<HourlyCharge> rateHistory // history of values for variable rate

  static auditable = true
  //static belongsTo = TariffSpecification
  static hasMany = [rateHistory:HourlyCharge]
  static transients = ["value"]

  static constraints = {
  }

  static mapping = { id (generator: 'assigned') }

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
   * Adds a new HourlyCharge to a variable rate. If this
   * Rate is not variable, or if the HourlyCharge arrives
   * past its noticeInterval, then we log an error and
   * drop it on the floor. If the update is valid but there's
   * already an HourlyCharge in the specified timeslot, then
   * the update must replace the existing HourlyCharge.
   * Returns true just in case the new charge was added successfully.
   */
  boolean addHourlyCharge (HourlyCharge newCharge)
  {
    boolean result = false
    if (isFixed) {
      // cannot change this rate
      log.error "Cannot change Rate $this"
      //println "Cannot change Rate $this"
    }
    else {
      Instant now = timeService.getCurrentTime()
      int warning = newCharge.atTime.millis - now.millis
      if (warning < noticeInterval) {
        // too late
        //println "Too late (${now.millis}) to change rate for ${newCharge.atTime.millis}"
        log.error "Too late (${now.millis}) to change rate for ${newCharge.atTime.millis}"
      }
      else {
        // first, remove the existing charge for the specified time
        HourlyCharge probe = new HourlyCharge(atTime: newCharge.atTime.plus(1000l), value: 0)
        SortedSet<HourlyCharge> head = rateHistory.headSet(probe)
        if (head != null || head.size() > 0) {
          HourlyCharge item = head.last()
          if (item.atTime == newCharge.atTime)
            rateHistory.remove(item)
        }
        log.info "Adding $newCharge to $this"
        //println "Adding $newCharge to $this"
        rateHistory.add(newCharge)
        assert this.save()
        result = true
      }
    }
    return result
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
  boolean applies (double usage)
  {
    return applies(usage, timeService.getCurrentTime())
  }

  /**
   * True just in case this Rate applies at the specified
   * time, for the indicated usage tier.
   */
  boolean applies (double usage, AbstractInstant when)
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
  void setValue(double value) {
    minValue = value
  }

  /**
   * Returns the rate for the current time. Note that the value is returned
   * even in case the Rate does not apply at the current time or current
   * usage tier. For variable rates, the value returned during periods of
   * inapplicability is meaningless, of course.
   */
  double getValue ()
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
  double getValue (AbstractInstant when)
  {
    if (isFixed)
      return minValue
    else if (rateHistory.size() == 0) {
      log.info "no rate history, return default"
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
      HourlyCharge probe = new HourlyCharge(atTime: inst.plus(1000l), value: 0)
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
