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

import org.joda.time.Instant

/**
 * Stores Rate arrays on behalf of Tariffs, bypassing the database.
 * @author John Collins
 */
class TariffRateService 
{
  static transactional = false

  Map tiers = [:]
  Map rates = [:]

  // manage tier lists
  def getTiers (Tariff tariff)
  {
    long tariffId = tariff.id
    return tiers[tariffId]
  }
  
  def addTier (Tariff tariff, BigDecimal value)
  {
    long tariffId = tariff.id
    // make sure we already have the list
    if (tiers[tariffId] == null) {
      log.debug "create tier list for tariff ${tariffId}"
      tiers[tariffId] = []
    }
    // make sure we don't already have this tier in the list
    if (!tiers[tariffId].find { it == value }) {
      //log.info "add tier ${value}, tariff ${tariffId}"
      tiers[tariffId] << value
    }
  }
  
  def sortTiers (Tariff tariff)
  {
    long tariffId = tariff.id
    // make sure we already have the list
    if (tiers[tariffId] != null) {
      tiers[tariffId] = tiers[tariffId].sort()
      //log.info "sorted tiers = ${tiers[tariffId]}, tariff ${tariffId}"
    }
  }

  // manage rate maps
  void createRateMap (Tariff tariff, int tiers, int hours)
   {
    log.info "create rate map for tariff ${tariff.id} [${tiers}][${hours}]"
    rates[tariff.id] = new String[tiers][hours]
  }
  
  void setRate (Tariff tariff, int tier, int hour, Rate rate)
  {
    def rateMap = rates[tariff.id]
    if (rateMap == null) {
      log.error "could not find rate map for tariff ${tariff.id}"
      return
    }
    rateMap[tier][hour] = rate.id
  }
  
  boolean hasRate (Tariff tariff, int tierIndex, int timeIndex) 
  {
    def rateMap = rates[tariff.id]
    if (rateMap == null) {
      return false
    }
    return (rateMap[tierIndex][timeIndex] != null)
  }
  
  BigDecimal rateValue (Tariff tariff, int tierIndex, int timeIndex, Instant when)
  {
    def rateMap = rates[tariff.id]
    if (rateMap == null) {
      log.error "could not find rate map for tariff ${tariff.id}"
      return 0.0
    }
    String rateId = rateMap[tierIndex][timeIndex]
    Rate rate = Rate.get(rateId)
    if (rate == null) {
      log.error "could not find rate ${rateId}"
      return 0.0
    }
    return rate.getValue(when)
  }
  
  
}
