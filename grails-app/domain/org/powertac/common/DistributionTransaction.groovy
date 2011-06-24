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
import org.powertac.common.enumerations.TariffTransactionType
import org.powertac.common.transformer.BrokerConverter
import org.powertac.common.transformer.CustomerConverter
import org.powertac.common.transformer.TariffConverter
import com.thoughtworks.xstream.annotations.*

/**
 * A {@code DistributionTransaction} instance represents the fee assessed
 * by the Distribution Utility for transport of energy over its facilities
 * during the current timeslot. The quantity is the total energy delivered,
 * which is the sum of the positive net load of the broker's customers, and 
 * the positive net export of energy through the wholesale market. Negative
 * values are ignored.
 *
 * @author John Collins
 */
@XStreamAlias("distribution-tx")
class DistributionTransaction
{
  @XStreamAsAttribute
  Integer id
  
  /** Whose transaction is this? */
  @XStreamConverter(BrokerConverter)
  Broker broker

  /** The timeslot for which this meter reading is generated */
  Instant postedTime

  /** The total positive amount of energy transported by the in kWH.
   */
  @XStreamAsAttribute
  BigDecimal quantity = 0.0
  
  /** The total charge imposed by the DU for this transport. Since this
   * is a debit, it will always be negative. */
  @XStreamAsAttribute
  BigDecimal charge = 0.0

  static constraints = {
    //id (nullable: false, unique: true)
    broker(nullable: false)
    postedTime (nullable: false)
    quantity (nullable: false, scale: Constants.DECIMALS)
    charge (nullable: false, scale: Constants.DECIMALS)
  }

  static mapping = {
    // id (generator: 'assigned')
  }

  public String toString() {
    return "${customerInfo}-${postedTime.millis/TimeService.HOUR}-${txType}-${quantity}"
  }
}
