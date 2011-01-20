package org.powertac.common

import org.joda.time.LocalDateTime

class HourlyCharge implements Serializable, Comparable
{
  BigDecimal value
  LocalDateTime when
	
  static belongsTo = [Rate]
  static constraints = {
    value(nullable:false, min:0.0)
    when(nullable:false)
  }
  
  int compareTo (obj)
  {
    when.compareTo(obj.when)
  }
}
