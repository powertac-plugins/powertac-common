package org.powertac.common

import org.joda.time.DateTimeFieldType
import org.joda.time.Duration
import org.joda.time.LocalDateTime
import org.joda.time.Partial

 /**
* Tariffs are composed of Rates, all of which are subtypes of this class.
* Rates may be applicable on particular days of the week, particular times
* of day, or above some usage threshold.
* @author jcollins
*/
class Rate implements Serializable
{
  Tariff tariff
  Partial weeklyBegin // weekly and daily validity
  Partial weeklyEnd
  Partial dailyBegin
  Partial dailyEnd
  BigDecimal tierThreshold = 0.0 // tier applicability
  boolean isFixed = true // if true, minValue is fixed rate
  BigDecimal minValue // min amd max rate values
  BigDecimal maxValue
  Duration noticeInterval // notice interval for variable rate
  BigDecimal expectedMean // expected mean value for variable rate
  Set<HourlyCharge> rateHistory // history of values for variable rate

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
  void setWeeklyBegin (Partial begin)
  {
    if (begin != null) {
      weeklyBegin = new Partial(DateTimeFieldType.dayOfWeek(),
          begin.get(DateTimeFieldType.dayOfWeek()))
    }
  }

  /**
   * Process weeklyBegin spec to extract dayOfWeek field
   */
  void setWeeklyEnd (Partial end)
  {
    if (end!= null) {
      weeklyEnd= new Partial(DateTimeFieldType.dayOfWeek(),
          end.get(DateTimeFieldType.dayOfWeek()))
    }
  }

  /**
   * Process weeklyBegin spec to extract dayOfWeek field
   */
  void setDailyBegin (Partial begin)
  {
    if (begin != null) {
      dailyBegin = new Partial(DateTimeFieldType.hourOfDay(),
          begin.get(DateTimeFieldType.hourOfDay()))
    }
  }

  /**
   * Process weeklyBegin spec to extract dayOfWeek field
   */
  void setDailyEnd (Partial end)
  {
    if (end!= null) {
      weeklyEnd= new Partial(DateTimeFieldType.hourOfDay(),
          end.get(DateTimeFieldType.hourOfDay()))
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
    day = when.getDayOfWeek()
    appliesWeekly = (weeklyBegin == null || 
                     (weeklyEnd == null && day == weeklyBegin) ||
                     (weeklyEnd != null && day >= weeklyBegin && day <= weeklyEnd))
    
    hour = when.getHourOfDay()
    if (dailyEnd > dailyBegin)
      // Interval does not span midnight
      appliesDaily = (hour >= dailyBegin && hour < dailyEnd)
    else
      // Interval spans midnight
      appliesDaily =  (hour >= dailyBegin || hour < dailyEnd)

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
    return getValue(Timeslot.currentTimeslot())
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
