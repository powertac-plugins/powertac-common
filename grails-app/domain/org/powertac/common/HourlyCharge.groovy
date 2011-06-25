/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.powertac.common

import org.joda.time.Instant
import com.thoughtworks.xstream.annotations.*

/**
 * Represents the cost of power during a specific timeslot in a variable
 * Rate. The value slot represents the charge/kWh; atTime is the Instant
 * at the start of the relevant timeslot. Therefore, the charge is in effect
 * from atTime until atTime + 1 hour. These are created by brokers and sent
 * to the server to update tariff pricing.
 * 
 * @author jcollins
 */
@XStreamAlias("charge")
class HourlyCharge implements Serializable, Comparable
{
  @XStreamAsAttribute
  BigDecimal value
  Instant atTime
  
  static belongsTo = [Rate]
                      
  static constraints = {
    value(nullable:false, min:0.0)
    atTime(nullable:false)
  }

  int compareTo (obj) {
    atTime.compareTo(obj.atTime)
  }
}
