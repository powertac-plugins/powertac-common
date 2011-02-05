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
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant
import org.joda.time.Partial

/**
 * Entity wrapper for TariffSpecification that supports Tariff evaluation 
 * and billing.
 * Tariffs are composed of Rates, which may be applicable for limited daily
 * and/or weekly times, and within particular usage tiers. The Tariff
 * transforms the list of Rates into an array, indexed first by tier and
 * second by hour, making it easy to find the correct Rate that applies for
 * a particular accounting event. This will also make it easy to estimate the
 * cost of a multi-Rate Tariff given an expected load/production profile.
 * <p>
 * <strong>NOTE:</strong> When creating one of these, for the first time, you must
 * call the init() method to initialize the publication date.</p>
 * @author John Collins
 */
class Tariff 
{
  
  // link the Time Service
  def timeService

    // ----------- State enumeration --------------  
  enum State
  {
    OFFERED, ACTIVE, LEGACY
  }

  /** Explicit tariff ID - Must be the same in Broker and Server */
  String id
  
  /** The Tariff spec*/
  TariffSpecification tariffSpec
  
  /** The broker who offers the tariff */
  Broker broker

  /** Last date new subscriptions will be accepted */
  Instant expiration
  
  /** Current state of this Tariff */
  State state = State.OFFERED
  
  /** ID of Tariff that supersedes this Tariff */
  Tariff isSupersededBy

  /** Tracks the realized price for variable-rate tariffs. */
  double totalCost = 0.0
  double totalUsage = 0.0
  
  /** Records the date when the Tariff was first offered */
  Instant offerDate
  
  /** Maximum future interval over which price can be known */
  Duration maxHorizon // TODO lazy instantiation?
  
  /** True if the maps are keyed by hour-in-week rather than hour-in-day */
  boolean isWeekly = false
  boolean analyzed = false
  
  // map is an array, indexed by tier-threshold and hour-in-day/week
  def tiers = []
  def rateMap = []

  static transients = ["realizedPrice", "usageCharge", "expired", "timeService"]
  static hasMany = [subscriptions:TariffSubscription]
  
  static constraints = {
    id(nullable: false, blank: false)
    broker(nullable:false)
    expiration(nullable: true)
    state(nullable: false)
    isSupersededBy(nullable: true)
    maxHorizon(nullable:true)
  }
  
  static mapping = {
    id (generator: 'assigned')
  }

  // Default constructor, needed by Grails
  //Tariff () {}

  void init ()
  {
    if (timeService)
      offerDate = timeService.getCurrentTime()
    else
      println "timeService not available"
    if (tariffSpec)
      analyze()
    else
      println "no tariff specification available"
  }
  
  // Map constructor
  Tariff(Map args = null) 
  {
    args?.each { key, val ->
      if (key == "tariffSpec") {
        tariffSpec = val
        id = spec.id
        broker = Broker.findById(tariffSpec.brokerId)
        expiration = spec.expiration
        spec.supersedes.each {
          Tariff.findById(it).isSupersededBy = this 
        }
      }
      else
        this."$key" = val
    }
  }
  
  /** Returns the actual realized price, or 0.0 if information unavailable */
  double getRealizedPrice ()
  {
    if (totalUsage == 0.0)
      return 0.0
    else
      return totalCost / totalUsage
  }
  
  /** 
   * Returns the usage charge for the current timeslot. The kwh parameter
   * defaults to 1.0, in which case you get the per-kwh value. If you supply
   * the cumulativeUsage parameter, then the charge may be affected by the
   * rate tier structure.
   * <p>
   * If the recordUsage parameter is true, then the usage and price will be
   * recorded to update the realizedPrice.</p>
   */
  double getUsageCharge (double kwh = 1.0, double cumulativeUsage = 0.0, boolean recordUsage = false)
  {
    double amt = getUsageCharge(timeService.currentTime, kwh, cumulativeUsage)
    if (recordUsage) {
      totalUsage += kwh
      totalCost += amt
    }
    return amt
  }
  
  /** 
   * Returns the usage charge for an amount of energy at some time in 
   * the past or future. If the requested time is farther in the future 
   * than maxHorizon, then the result will be a default value, which 
   * may not be useful. The cumulativeUsage parameter sets the base for
   * probing the rate tier structure.
   */
  double getUsageCharge (Instant when, double kwh = 1.0, double cumulativeUsage = 0.0)
  {
    // first, get the time index
    DateTime dt = new DateTime(when, DateTimeZone.UTC)
    int di = dt.getHourOfDay()
    if (isWeekly)
      di += 24 * (dt.getDayOfWeek() - 1)

    // Then work out the tier index. Keep in mind that the kwh value could
    // cross a tier boundary
    if (tiers.size() == 1) {
      return kwh * rateMap[0][di].getValue(when)
    }
    else {
      double remainingAmount = kwh
      double accumulatedAmount = cumulativeUsage
      double result = 0.0
      int ti = 0 // tier index
      while (remainingAmount > 0.0) {
        if (tiers.size() > ti + 1) {
          // still tiers remaining
          if (accumulatedAmount >= tiers[ti+1]) {
            println "accumulatedAmount ${accumulatedAmount} above threshold ${ti+1}: ${tiers[ti+1]}"
            ti += 1
          }
          else if (remainingAmount + accumulatedAmount > tiers[ti+1]) {
            double amt = tiers[ti+1] - accumulatedAmount
            println "split off ${amt} below ${tiers[ti+1]}"
            result += amt * rateMap[ti++][di].getValue(when)
            remainingAmount -= amt
            accumulatedAmount += amt
          }
          else {
            // it all fits in the current tier
            println "amount ${remainingAmount} fits in tier ${ti}"
            result += remainingAmount * rateMap[ti][di].getValue(when)
            remainingAmount = 0.0
          }
        }
        else {
          // last tier
          println "remainder ${remainingAmount} fits in top tier"
          result += remainingAmount * rateMap[ti][di].getValue(when)
          remainingAmount = 0.0
        }
      }
      return result
    }
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
                                   tariff: this)
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

  /**
   * Processes the tariffSpec, extracting tiers and rates, building a map. We start by
   * imposing a priority order on rate constraints, as follows:
   * <ol>
   *   <li>tierThreshold &gt; 0</li>
   *   <li>weeklyStart &gt; 0</li>
   *   <li>dailyStart &gt; 0</li>
   *   <li>no constraint</li>
   * </ol>
   * We sort the set of rates on these criteria, then populate an array of size
   * [number of tiers][number of hours] where number of hours is either 24 or
   * 168 depending on whether there are any weekly constraints. 
   */
  void analyze ()
  {
    // Start by computing tier indices, and array width
    def tierIndexMap = [:]
    tiers.add 0.0
    tariffSpec.rates.each { rate ->
      if (rate.weeklyBegin >= 0)
        isWeekly = true
      if (rate.tierThreshold > 0.0)
        tiers.add rate.tierThreshold
    }
    tiers = tiers.sort()
    println "${tiers}"
    
    // Next, fill in the tierIndexMap, which maps tier thresholds to
    // array indices. Remember that there's always a 0.0 tier.
    int tidx = 0
    tiers.each { threshold ->
      tierIndexMap[(threshold)] = tidx++
    }
    
    // Now we can compute the sort keys. Note that the lowest-priority
    // rates will sort first.
    def annotatedRates = [:] as TreeMap
    tariffSpec.rates.each { rate ->
      int value = 0
      if (rate.dailyBegin >= 0) {
        value = rate.dailyBegin
      }
      if (rate.weeklyBegin >= 0) {
        // The first day is 1, otherwise we would have to add 1 here
        value += rate.weeklyBegin * 24
      }
      if (rate.tierThreshold > 0.0) {
        value += tierIndexMap[rate.tierThreshold] * 7 * 24
      }
      println "inserting ${value}, ${rate}"
      annotatedRates[(value)] = rate
    }

    // Next, we create the rateMap
    rateMap = new Rate[tierIndexMap.size()][isWeekly ? 7*24 : 24]

    // Finally, we step through the sorted Rates and fill in the
    // array. For each Rate, we add it to the array everywhere it
    // applies, even if we are overwriting other Rates that have
    // already been entered.
    annotatedRates.each { key, rate ->
      int ti = tierIndexMap[rate.tierThreshold]
      int day1 = 0
      int dayn = 0
      if (isWeekly) {
        // get first and last applicable day
        if (rate.weeklyBegin >= 0) {
          day1 = rate.weeklyBegin - 1 // days start at 1
          dayn = rate.weeklyBegin - 1
        }
        if (rate.weeklyEnd >= 0) {
          dayn = rate.weeklyEnd - 1
        }
      }
      int hr1 = 0
      int hrn = 23
      if (rate.dailyBegin >= 0) {
        hr1 = rate.dailyBegin
        hrn = rate.dailyEnd
      }
      println "day1=${day1}, dayn=${dayn}, hr1=${hr1}, hrn=${hrn}"
      // now we can fill in the array
      for (day in day1..(Math.max(dayn, isWeekly? 6 : 0))) {
        for (hour in hr1..(Math.max(hrn, 23))) {
          rateMap[ti][hour + (day * 24)] = rate
        }
        // handle daily wrap-arounds
        if (hrn < hr1) {
          for (hour in 0..(hrn - 1)) {
            rateMap[ti][hour + day * 24] = rate
          }
        }
      }
      // handle weekly wrap-arounds
      if (dayn < day1) {
        for (day in 0..dayn) {
          for (hour in hr1..(Math.max(hrn, 23))) {
            rateMap[ti][hour + day * 24] = rate
          }
          // handle daily wrap-arounds
          if (hrn < hr1) {
            for (hour in 0..(hrn - 1)) {
              rateMap[ti][hour + day * 24] = rate
            }
          }
        }
      }
    }
    analyzed = true
  }
}
