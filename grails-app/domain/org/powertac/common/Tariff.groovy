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

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.joda.time.Instant
import org.powertac.common.enumerations.PowerType

/**
 * Represents a Tariff offered by a Broker to customers. A Tariff specifies
 * prices for energy in various circumstances, along with upfront and periodic
 * payments and basic constraints. This class is simply a serializable,
 * immutable data structure. You need a TariffBuilder to create one, and a
 * TariffExaminer to make serious use of one.
 * @author John Collins
 */
class Tariff 
{
  // ----------- State enumeration --------------
  
  enum State
  {
    OFFERED, ACTIVE, LEGACY
  }

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
  
  /** The broker who offers the tariff */
  Broker broker

  /** Last date new subscriptions will be accepted */
  Instant expiration
  
  /** Current state of this Tariff */
  State state = State.OFFERED
  
  /** Minimum contract duration (in milliseconds) */
  long minDuration = 0
  
  /** Type of power covered by this tariff */
  PowerType powerType = PowerType.CONSUMPTION
  
  /** One-time payment for subscribing to tariff, positive for payment
   *  from customer, negative for payment to customer. */
  BigDecimal signupPayment = 0.0
  
  /** Payment from customer to broker for canceling subscription before
   *  minDuration has elapsed. */
  BigDecimal earlyWithdrawPayment = 0.0
  
  /** Flat payment per period for two-part tariffs */
  BigDecimal periodicPayment = 0.0
  
  /** The TariffExaminer associated with this Tariff */
  TariffExaminer examiner
  
  /** IDs of tariffs superseded by this Tariff */
  Tariff isSupersededBy

  //static belongsTo = [broker:Broker]
  static hasMany = [rates:Rate, subscriptions:TariffSubscription]
  static constraints = {
    broker(nullable:false)
    expiration(nullable: true)
    state(nullable: false)
    powerType(nullable: false)
    rates(nullable: false)
    examiner(nullable: true)
    isSupersededBy(nullable: true)
  }
  
  static transients = ['timeService', 'expired']

  /**
   * Returns the TariffExaminer associated with this Tariff.
   */
  TariffExaminer getTariffExaminer ()
  {
    if (examiner == null) {
      examiner = new TariffExaminer(tariff: this)
      examiner.init()
    }
    return examiner
  }
  
  // GORM wants a setter
  void setTariffExaminer (TariffExaminer ex)
  {
    examiner = ex
  }
  
  /**
   * Subscribes a block of Customers from a single Customer model to
   * this Tariff, as long as this Tariff has not expired. If the
   * subscription succeeds, then the TariffSubscription instance is
   * return, otherwise null.
   */
  TariffSubscription subscribe (Customer customer, int customerCount)
  {
    if (isExpired())
      return null
    
    TariffSubscription sub = subscriptions?.findByCustomer(customer)
    if (sub == null) {
      sub = new TariffSubscription(customer: customer,
                                   tariff: this,
                                   tariffExaminer: this.getTariffExaminer())
    }
    sub.subscribe(customerCount)
    return sub
  }
  
  /**
   * True just in case the current time is past the expiration date
   * of this Tariff.
   */
  boolean isExpired ()
  {
    return timeService.currentTime.millis > expiration.millis
  }
}
