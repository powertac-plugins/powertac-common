package org.powertac.common

import org.joda.time.LocalDateTime

class HourlyCharge implements Serializable
{
	BigDecimal value
	LocalDateTime when
	
	static belongsTo = [Rate]
    static constraints = {
		value(nullable:false, min:0)
		when(nullable:false)
    }
}
