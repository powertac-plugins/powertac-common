package org.powertac.common

import org.joda.time.DateTimeFieldType
import org.joda.time.Duration
import org.joda.time.LocalDateTime
import org.joda.time.Partial
import org.joda.time.ReadablePartial

 /**
* Tariffs are composed of Rates, all of which are subtypes of this class.
* Rates may be applicable on particular days of the week, particular times
* of day, or above some usage threshold.
* @author jcollins
*/
class Rate implements Serializable
{
  Tariff tariff
  int weeklyBegin = -1// weekly and daily validity
  int weeklyEnd = -1
  int dailyBegin = -1
  int dailyEnd = -1
  BigDecimal tierThreshold = 0.0 // tier applicability
  boolean isFixed = true // if true, minValue is fixed rate
  BigDecimal minValue // min amd max rate values
  BigDecimal maxValue
  Duration noticeInterval // notice interval for variable rate
  BigDecimal expectedMean // expected mean value for variable rate
  SortedSet<HourlyCharge> rateHistory // history of values for variable rate

  static belongsTo = Tariff
  static hasMany = [rateHistory:HourlyCharge]

  static constraints = {
    tariff(nullable:false)
    dailyBegin(nullable:true)
    dailyEnd(nullable:true)
    weeklyBegin(nullable:true)
    weeklyEnd(nullable:true)
    isFixed(nullable:false)
    minValue(min:0.0)
    maxValue(min:0.0)
  }

  // introduce dependency on TimeService
  def timeService
  
  // TODO: Tier applicability, variable rate
  
  /**
   * Constructor must mung the Partials for weeklyBegin, weeklyEnd,
   * dailyBegin, and dailyEnd
   */
  public Rate (Map m = null)
  {
    m?.each { k,v ->
      if (k == "weeklyBegin")
        setWeeklyBegin(v)
      else if (k == "weeklyEnd")
        setWeeklyEnd(v)
      else if (k == "dailyBegin")
        setDailyBegin(v)
      else if (k == "dailyEnd")
        setDailyEnd(v)
      else if (k == "value")
        minValue = v
      else
        this."$k" = v }
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

  /**
   * Process weeklyEnd spec to extract dayOfWeek field
   */
  void setWeeklyEnd (ReadablePartial end)
  {
    if (end!= null) {
      weeklyEnd= end.get(DateTimeFieldType.dayOfWeek())
    }
  }

  /**
   * Process dailyBegin spec to extract dayOfWeek field
   */
  void setDailyBegin (ReadablePartial begin)
  {
    if (begin != null) {
      dailyBegin = begin.get(DateTimeFieldType.hourOfDay())
    }
  }

  /**
   * Process dailyEnd spec to extract dayOfWeek field
   */
  void setDailyEnd (ReadablePartial end)
  {
    if (end!= null) {
      dailyEnd= end.get(DateTimeFieldType.hourOfDay())
    }
  }

  /**
   * True just in case this Rate applies at this moment, ignoring the
   * tier.
   * @return
   */
  boolean applies ()
  {
    return applies(timeService.getCurrentTime())
  }

  /**
   * True just in case this Rate applies at the given DateTime, ignoring the
   * tier.
   */
  boolean applies (LocalDateTime when)
  {
    def appliesWeekly = false
    def appliesDaily = false

    // check weekly applicability
    def day = when.getDayOfWeek()
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
    def hour = when.getHourOfDay()
    if (dailyBegin == -1 || dailyEnd == -1) {
      appliesDaily = true
    }
    else if (dailyEnd > dailyBegin) {
      // Interval does not span midnight
      appliesDaily = ((hour >= dailyBegin) && (hour < dailyEnd))
    }
    else {
      // Interval spans midnight
      appliesDaily =  ((hour >= dailyBegin) || (hour < dailyEnd))
    }

    return (appliesWeekly && appliesDaily)
  }

  private void setValue(BigDecimal value) {
    //make value property read only
  }

  /**
   * Returns the rate for the current time
   */
  BigDecimal getValue ()
  {
    //return getValue(Timeslot.currentTimeslot().getStartDateTime())
    return getValue(timeService.getCurrentTime())
  }
  
  /**
   * Returns the rate for some time in the past or future
   */
  BigDecimal getValue (LocalDateTime when)
  {
    if (isFixed)
      return minValue
    else {
      return expectedMean // stub
    }
  }
}
