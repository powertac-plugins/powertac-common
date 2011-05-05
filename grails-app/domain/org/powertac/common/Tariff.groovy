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
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant
import org.powertac.common.enumerations.PowerType
import com.thoughtworks.xstream.annotations.XStreamOmitField

/**
 * Entity wrapper for TariffSpecification that supports Tariff evaluation 
 * and billing. Instances of this class are not intended to be serialized.
 * Tariffs are composed of Rates, which may be applicable for limited daily
 * and/or weekly times, and within particular usage tiers. The Tariff
 * transforms the list of Rates into an array, indexed first by tier and
 * second by hour, making it easy to find the correct Rate that applies for
 * a particular accounting event. This will also make it easy to estimate the
 * cost of a multi-Rate Tariff given an expected load/production profile.
 * <p>
 * <strong>NOTE:</strong> When creating one of these for the first time, you must
 * call the init() method to initialize the publication date. It does not work
 * to call it inside the constructor for some reason.</p>
 * @author John Collins
 */
class Tariff 
{
  // ----------- State enumeration --------------
  
  enum State
  {
    PENDING, OFFERED, ACTIVE, WITHDRAWN, KILLED, INACTIVE
  }

  @XStreamOmitField
  def timeService

  String specId

  /** The Tariff spec*/
  TariffSpecification tariffSpec
  
  /** The broker behind this tariff */
  Broker broker
  
  /** Last date new subscriptions will be accepted */
  Instant expiration
  
  /** Current state of this Tariff */
  State state = State.PENDING
  
  /** ID of Tariff that supersedes this Tariff */
  Tariff isSupersededBy

  /** Tracks the realized price for variable-rate tariffs. */
  Double totalCost = 0.0
  Double totalUsage = 0.0
  
  /** Records the date when the Tariff was first offered */
  Instant offerDate
  
  /** Maximum future interval over which price can be known */
  Duration maxHorizon // TODO lazy instantiation?
  
  /** True if the maps are keyed by hour-in-week rather than hour-in-day */
  Boolean isWeekly = false
  Boolean analyzed = false
  
  // map is an array, indexed by tier-threshold and hour-in-day/week
  def tiers = []
  def rateMap = []

  static transients = ["realizedPrice", "usageCharge", "expired", "revoked", "timeService",
                       "covered", "minDuration", "powerType", "signupPayment", 
                       "earlyWithdrawPayment", "periodicPayment"]
  
  static auditable = true
  
  //static hasMany = [subscriptions:TariffSubscription]
  
  static constraints = {
    specId(nullable: false, blank: false)
    broker(nullable:false)
    expiration(nullable: true)
    state(nullable: false)
    isSupersededBy(nullable: true)
    maxHorizon(nullable:true)
 }
  
  //static mapping = {
  //  id (generator: 'assigned')
  //}

  /**
   * Initializes the Tariff, setting the publication date and running
   * the internal analyzer to build the rate map.  
   */
  void init ()
  {
    specId = tariffSpec.id
    broker = (tariffSpec.broker)
    expiration = tariffSpec.getExpiration()
    tariffSpec.getSupersedes().each {
      Tariff.findBySpecId(it).isSupersededBy = this
    }
    offerDate = timeService.getCurrentTime()
    analyze()
    this.save()
    broker.addToTariffs(this)
    broker.save()
  }
  
  /**
   * Adds a new HourlyCharge to its Rate. Returns true just
   * in case the operation was successful.
   */
  boolean addHourlyCharge (HourlyCharge newCharge, String rateId)
  {
    Rate theRate = Rate.get(rateId)
    return theRate.addHourlyCharge(newCharge)
  }

  /** Returns the actual realized price, or 0.0 if information unavailable */
  double getRealizedPrice ()
  {
    if (totalUsage == 0.0)
      return 0.0
    else
      return totalCost / totalUsage
  }
  
  /** Pass-through for TariffSpecification.minDuration */
  long getMinDuration ()
  {
    tariffSpec.minDuration
  }
  
  /** Type of power covered by this tariff */
  PowerType getPowerType ()
  {
    tariffSpec.powerType
  }
  
  /** One-time payment for subscribing to tariff, positive for payment
   *  from customer, negative for payment to customer. */
  BigDecimal getSignupPayment ()
  {
    tariffSpec.signupPayment
  }
  
  /** Payment from customer to broker for canceling subscription before
   *  minDuration has elapsed. */
  BigDecimal getEarlyWithdrawPayment ()
  {
    tariffSpec.earlyWithdrawPayment
  }
  
  /** Flat payment per period for two-part tariffs */
  BigDecimal getPeriodicPayment ()
  {
    tariffSpec.periodicPayment
  }
  
  /**
   * Adds periodic payments to the total cost, so realized price includes it.
   */
  void addPeriodicPayment ()
  {
    totalCost += periodicPayment
  }

  /** 
   * Returns the usage charge for a single customer in the current timeslot. 
   * The kwh parameter
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
      this.save()
    }
    return amt
  }
  
  /** 
   * Returns the usage charge for a single customer using an amount of 
   * energy at some time in 
   * the past or future. If the requested time is farther in the future 
   * than maxHorizon, then the result will may be a default value, which 
   * may not be useful. The cumulativeUsage parameter sets the base for
   * probing the rate tier structure. Do not use this method for billing,
   * because it does not update the realized-price data.
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
            log.debug "accumulatedAmount ${accumulatedAmount} above threshold ${ti+1}: ${tiers[ti+1]}"
            ti += 1
          }
          else if (remainingAmount + accumulatedAmount > tiers[ti+1]) {
            double amt = tiers[ti+1] - accumulatedAmount
            log.debug "split off ${amt} below ${tiers[ti+1]}"
            result += amt * rateMap[ti++][di].getValue(when)
            remainingAmount -= amt
            accumulatedAmount += amt
          }
          else {
            // it all fits in the current tier
            log.debug "amount ${remainingAmount} fits in tier ${ti}"
            result += remainingAmount * rateMap[ti][di].getValue(when)
            remainingAmount = 0.0
          }
        }
        else {
          // last tier
          log.debug "remainder ${remainingAmount} fits in top tier"
          result += remainingAmount * rateMap[ti][di].getValue(when)
          remainingAmount = 0.0
        }
      }
      return result
    }
  }
  
  /**
   * True just in case the current time is past the expiration date
   * of this Tariff.
   */
  boolean isExpired ()
  {
    return timeService.getCurrentTime().millis >= expiration.millis
  }
  
  /**
   * True just in case the set of Rates cover all the possible hour
   * and tier slots. If false, then there is some combination of hour
   * and tier for which no Rate is specified.
   */
  boolean isCovered ()
  {
    for (tier in 0..<tiers.size()) {
      for (hour in 0..<(isWeekly? 24 * 7: 24)) {
        def cell = rateMap[tier][hour]
        //println "cell: ${cell}" 
        if (cell == null) {
          return false
        }
      }
    }
    return true
  }
  
  /**
   * True just in case this tariff has been revoked.
   */
  boolean isRevoked ()
  {
    return state == State.KILLED
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
    log.info "tiers: ${tiers}"
    
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
      log.debug "inserting ${value}, ${rate}"
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
        else {
          dayn = 6 // no days specified for weekly rate
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
      log.debug "day1=${day1}, dayn=${dayn}, hr1=${hr1}, hrn=${hrn}"
      // now we can fill in the array
      for (day in (dayn < day1? 0 : day1)..dayn) {
        // handle daily wrap-arounds
        for (hour in (hrn < hr1? 0 : hr1)..hrn) {
          rateMap[ti][hour + day * 24] = rate
        }
        if (hrn < hr1) {
          for (hour in hr1..23) {
            rateMap[ti][hour + (day * 24)] = rate
          }
        }
      }
      // handle weekly wrap-arounds
      if (dayn < day1) {
        for (day in day1..6) {
          // handle daily wrap-arounds
          for (hour in (hrn < hr1? 0 : hr1)..hrn) {
            rateMap[ti][hour + day * 24] = rate
          }
          if (hrn < hr1) {
            for (hour in hr1..23) {
              rateMap[ti][hour + (day * 24)] = rate
            }
          }
        }
      }
    }
    analyzed = true
  }
}
