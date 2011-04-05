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

import org.powertac.common.enumerations.PowerType
import org.powertac.common.transformer.BrokerConverter
import org.joda.time.Duration
import org.joda.time.Instant
import com.thoughtworks.xstream.annotations.*

/**
 * Represents a Tariff offered by a Broker to customers. A Tariff specifies
 * prices for energy in various circumstances, along with upfront and periodic
 * payments and basic constraints. This class is a value type -- a 
 * serializable, immutable data structure. You need to initialize a 
 * Tariff from it to make serious use of it.
 * <p>
 * <strong>Note:</strong> Must be serialized "deep" to gather up the Rates and
 * associated HourlyCharge instances.</p>
 * @author John Collins
 */
@XStreamAlias("tariff-spec")
class TariffSpecification //implements Serializable
{
  String id = IdGenerator.createId()
  
  /** The Broker who offers this Tariff */
  @XStreamConverter(BrokerConverter)
  Broker broker
  
  /** Last date new subscriptions will be accepted */
  Instant expiration
  
  /** Minimum contract duration (in milliseconds) */
  @XStreamAsAttribute
  Long minDuration = 0
  
  /** Type of power covered by this tariff */
  @XStreamAsAttribute
  PowerType powerType = PowerType.CONSUMPTION
  
  /** One-time payment for subscribing to tariff, positive for payment
   *  from customer, negative for payment to customer. */
  @XStreamAsAttribute
  BigDecimal signupPayment = 0.0
  
  /** Payment from customer to broker for canceling subscription before
   *  minDuration has elapsed. */
  @XStreamAsAttribute
  BigDecimal earlyWithdrawPayment = 0.0
  
  /** Flat payment per period for two-part tariffs */
  @XStreamAsAttribute
  BigDecimal periodicPayment = 0.0
  
  List<Rate> rates

  List<String> supersedes
  
  static hasMany = [rates: Rate, supersedes: String]
  
  static constraints = {
    id (nullable: false, blank: false, unique: true)
    broker(nullable: false)
    expiration(nullable: true)
    minDuration(min: 24*60*60*1000l) // one day
    powerType(nullable: false)
    rates(nullable: false, minSize: 1)
  }

  static mapping = {
    id (generator: 'assigned')
  }
}
