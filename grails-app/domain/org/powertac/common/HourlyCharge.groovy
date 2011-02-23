package org.powertac.common

import org.joda.time.Instant

class HourlyCharge implements Serializable, Comparable
{
  BigDecimal value
  Instant atTime
	
  static belongsTo = [Rate]
  static constraints = {
    value(nullable:false, min:0.0)
    atTime(nullable:false)
  }
  
  int compareTo (obj)
  {
    atTime.compareTo(obj.atTime)
  }
}
