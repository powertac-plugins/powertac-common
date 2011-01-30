package org.powertac.common

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant
import org.joda.time.Partial

/**
 * Server-side wrapper for Tariffs that supports Tariff evaluation and billing.
 * Tariffs are composed of Rates, which may be applicable for limited daily
 * and/or weekly times, and within particular usage tiers.
 * @author jcollins
 */
class TariffExaminer 
{
  /** The Tariff itself */
  Tariff tariff
  boolean analyzed = false
  //boolean allFixed = true
  
  /** Tracks the realized price for variable-rate tariffs. */
  double totalCost = 0.0
  double totalUsage = 0.0
  
  /** Records the date when the Tariff was first offered */
  Instant offerDate
  
  /** Maximum future interval over which price can be known */
  Duration maxHorizon
  
  /** True if the maps are keyed by hour-in-week rather than hour-in-day */
  boolean isWeekly = false
  
  // map needs to be an array, indexed by tier-threshold and hour-in-day/week
  def tierIndexMap = [:]
  def rateMap = []
  
  static transients = ["realizedPrice", "usageCharge"]
  
  static constraints = {
  }
  
  // link the Time Service
  def timeService
  
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
   */
  double getUsageCharge (kwh = 1.0, cumulativeUsage = 0.0)
  {
    getUsageCharge(timeService.currentTime, kwh, cumulativeUsage)
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
    if (!analyzed)
      analyzeTariff()

    // first, get the time index
    DateTime dt = new DateTime(when, DateTimeZone.UTC)
    int di = dt.getHourOfDay()
    if (isWeekly)
      di += 24 * (dt.getDayOfWeek() - 1)

    // Then work out the tier index. Keep in mind that the kwh value could
    // cross a tier boundary
    if (tierIndexMap.size() == 1) {
      return kwh * rateMap[0][di].getValue(when)
    }
    else {
      double remainingAmount = kwh
      double accumulatedAmount = cumulativeUsage
      double result = 0.0
      int ti = 0 // tier index
      while (remainingAmount > 0.0) {
        if (tierMap.size() > ti + 1) {
          // still tiers remaining
          if (accumulatedAmount >= tierMap[ti+1]) {
            ti += 1
          }
          else if (remainingAmount + accumulatedAmount > tierMap[ti+1]) {
            double amt = remainingAmount + accumulatedAmount - tierMap[ti+1]
            result += amt * rateMap[0][ti++].getValue(when)
            remainingAmount -= amt
            accumulatedAmount += amt
          }
        }
        else {
          // last tier
          result += remainingAmount * rateMap[0][ti].getValue(when)
        }
      }
      return result
    }
  }
  
  /**
   * Processes the tariff, extracting tiers and rates, building a map. We start by
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
  void analyzeTariff ()
  {
    // Start by computing tier indices, and array width
    //boolean isWeekly = false
    def tiers = [] as SortedSet<BigDecimal>
    tariff.rates.each { rate ->
      if (rate.weeklyBegin >= 0)
        isWeekly = true
      if (rate.tierThreshold > 0.0)
        tiers.add rate.tierThreshold
    }
    
    // Next, fill in the tierIndexMap, which maps tier thresholds to
    // array indices. Remember that there's always a 0.0 tier.
    ti = 0
    tierIndexMap[0.0] = ti++
    tiers.each { threshold ->
      tierIndexMap[threshold] = ti++
    }
    
    // Now we can compute the sort keys. Note that the lowest-priority
    // rates will sort first.
    def annotatedRates = [:] as SortedMap<Integer, Rate>
    tariff.rates.each { rate ->
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
      annotatedRates[value] = rate
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
      // now we can fill in the array
      for (day in day1..(Math.max(dayn, 6))) {
        for (hour in hr1..(Math.max(hrn, 23))) {
          rateMap[ti][hour + day * 24] = rate
        }
        // handle daily wrap-arounds
        if (hrn < hr1) {
          for (hour in 0..hrn) {
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
            for (hour in 0..hrn) {
              rateMap[ti][hour + day * 24] = rate
            }
          }
        }
      }
    }
    analyzed = true
  }
}
