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

import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.Instant

/**
 * A TariffSubscription is an entity representing an association between a Customer
 * and a Tariff. You get one by
 * calling the subscribe() method on Tariff. If there is no
 * current subscription for that Customer (which in most cases is actually
 * a population model), then a new TariffSubscription is created and
 * returned from the Tariff.  
 * @author Carsten Block, John Collins
 */
class TariffSubscription {

  def timeService

  String id = IdGenerator.createId()
  Competition competition
  Customer customer
  Tariff tariff
  
  /** Total number of customers within a customer model that are committed 
   * to this tariff subscription. This needs to be a count, otherwise tiered 
   * rates cannot be applied properly. */
  int customersCommitted = 0 
  
  /** List of expiration dates. This is used only if the Tariff has a minDuration,
   *  before which a subscribed Customer cannot back out without a penalty. Each
   *  entry in this list is a pair [expiration-date, customer-count]. New entries
   *  are added chronologically at the end of the list, so the front of the list
   *  holds the oldest subscriptions - the ones that can be unsubscribed soonest
   *  without penalty. */
  List expirations = []

  static transients = ['timeService']

  static constraints = {
    id(nullable: false, blank: false, unique: true)
    competition(nullable: false)
    customer(nullable: false)
    tariff(nullable: false)
    customersCommitted(min: 0)
    expirations(nullable: true)
  }
  static mapping = {
    id(generator: 'assigned')
  }
  
  // TODO: withdraw, assess fee/bonus, count customers free to withdraw
  
  /**
   * Subscribes some number of discrete customers. This is typically some portion of the population in a
   * population model. We assume this is called from Tariff, as a result of calling tariff.subscribe().
   * Also, we record the expiration date of the tariff contract, just in case the tariff has a
   * minDuration. For the purpose of computing expiration, all contracts are assumed to begin at
   * 00:00 on the day of the subscription.
   */
  void subscribe (int customerCount)
  {
    // first, update the customer count
    customersCommitted += customerCount
    
    // if the Tariff has a minDuration, then we have to record the expiration date.
    // we do this by adding an entry to end of list, or updating the entry at the end.
    // An entry is a pair [Instant, count]
    long minDuration = tariff.minDuration
    if (minDuration > 0) {
      // Compute the 00:00 Instant for the current time
      Instant start = timeService.truncateInstant(timeService.currentTime, TimeService.DAY)
      if (expirations.size() > 0 &&
          expirations[expirations.size() - 1][0].millis == start + minDuration) {
        // update existing entry
        expirations[expirations.size() - 1][1] += customerCount
      }
      else {
        // need a new entry
        expirations.add([start + minDuration, customerCount])
      }
    }
    // Compute signup bonus here?
  }
  
  void withdraw (int customerCount)
  {
    // start by updating the customer count
    customersCommitted -= customerCount
    // Compute early-withdrawal penalties here?
  }
}
