package org.powertac.common

import org.joda.time.Instant

class HourlyCharge implements Serializable, Comparable
{
  BigDecimal value
  Instant when
	
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
