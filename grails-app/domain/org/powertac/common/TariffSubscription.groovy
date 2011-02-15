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

//import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.joda.time.Instant

/**
 * A TariffSubscription is an entity representing an association between a Customer
 * and a Tariff. Instances of this class are not intended to be serialized.
 * You get one by calling the subscribe() method on Tariff. If there is no
 * current subscription for that Customer (which in most cases is actually
 * a population model), then a new TariffSubscription is created and
 * returned from the Tariff.  
 * @author Carsten Block, John Collins
 */
class TariffSubscription {

  def timeService

  String id = IdGenerator.createId()
  CustomerInfo customerInfo
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
  
  /** Total usage so far in the current day, needed to compute charges for
   *  tiered rates. */
  double totalUsage = 0.0

  static constraints = {
    id(nullable: false, blank: false, unique: true)
    //competition(nullable: false)
    customerInfo(nullable: false)
    tariff(nullable: false)
    customersCommitted(min: 0)
    expirations(nullable: true)
  }
  static mapping = {
    id(generator: 'assigned')
  }
  
  static transients = ['expiredCustomerCount']
  
  // TODO: revoke
  
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
    // post the signup bonus
    if (tariff.signupPayment != 0.0) {
      println "signup bonus: ${customerCount} customers, total = ${customerCount * tariff.getSignupPayment()}"
      Timeslot current = Timeslot.currentTimeslot()
      TariffTransaction tx = new TariffTransaction(txType: TariffTransaction.TxType.SIGNUP,
          customerInfo: customerInfo, customerCount: customerCount, tariff: tariff,
          timeslot: current, charge: customerCount * tariff.getSignupPayment())
      tx.save()
      current.addToTariffTx(tx)
    }
    this.save()
  }
  
  /**
   * Removes customerCount customers (at most) from this subscription,
   * posts early-withdrawal fees if appropriate. 
   */
  void unsubscribe (int customerCount)
  {
    // first, make customerCount no larger than the subscription count
    customerCount = Math.min(customerCount, customersCommitted)
    // find the number of customers who can withdraw without penalty
    int freeAgentCount = getExpiredCustomerCount()
    int penaltyCount = Math.max (customerCount - freeAgentCount, 0)
    // update the expirations list
    int expCount = customerCount
    while (expCount > 0) {
      int cec = expirations[0][1]
      if (cec <= expCount) {
        expCount -= cec
        expirations.remove(0)
      }
      else {
        expirations[0][1] -= expCount
        expCount = 0
      }
    }
    customersCommitted -= customerCount
    // Post early-withdrawal penalties
    if (tariff.earlyWithdrawPayment != 0.0 && penaltyCount > 0) {
      Timeslot current = Timeslot.currentTimeslot()
      TariffTransaction tx = new TariffTransaction(txType: TariffTransaction.TxType.WITHDRAW,
          customerInfo: customerInfo, customerCount: customerCount, tariff: tariff,
          timeslot: current, charge: penaltyCount * tariff.getEarlyWithdrawPayment())
      tx.save()
      current.addToTariffTx(tx)
    }
    this.save()
  }

  /**
   * Generates and returns a TariffTransaction instance for the current timeslot that
   * represents the amount of production (positive amount) or consumption
   * (negative amount), along with the credit/debit that results. Also generates
   * a separate TariffTransaction for the fixed periodic payment if it's non-zero.
   */
  TariffTransaction usePower (double amount)
  {
    // generate the usage transaction
    def txType = amount > 0 ? TariffTransaction.TxType.PRODUCTION: TariffTransaction.TxType.CONSUMPTION
    TariffTransaction result = new TariffTransaction(txType: txType,
        customerInfo: customerInfo, customerCount: customerCount, tariff: tariff,
        timeslot: Timeslot.currentTimeslot(), 
        charge: -customersCommitted * tariff.getUsageCharge(amount / customersCommitted, totalUsage, true))
    result.save()
    Timeslot.currentTimeslot().addToTariffTx(result)
    // update total usage for the day
    if (timeService.getHourOfDay() == 0) {
      //reset the daily usage counter
      totalUsage = 0.0
    }
    totalUsage += amount / customersCommitted
    // generate the periodic payment if necessary
    if (tariff.periodicPayment != 0.0) {
      TariffTransaction pp = new TariffTransaction(txType: TariffTransaction.TxType.PERIODIC,
          customerInfo: customerInfo, customerCount: customerCount, tariff: tariff,
          timeslot: Timeslot.currentTimeslot(),
          charge: customersCommitted * tariff.getPeriodicPayment())
      pp.save()
      Timeslot.currentTimeslot().addToTariffTx(pp)
    }
    this.save()
    return result
  }
  
  /**
   * Returns the number of individual customers who may withdraw from this
   * subscription without penalty.
   */
  int getExpiredCustomerCount ()
  {
    int cc = 0
    Instant today = timeService.truncateInstant(timeService.currentTime, TimeService.DAY)
    expirations.each { time, num ->
      if (time.millis <= today.millis) {
        cc += num
      }
    }
    return cc
  }
}
