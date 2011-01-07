/*
 * Copyright 2009-2010 the original author or authors.
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

import org.joda.time.LocalDateTime
import org.powertac.common.enumerations.CompetitionStatus

class Competition implements Serializable {
  String id = IdGenerator.createId()
  String name
  Boolean enabled = false
  Boolean current = false
  LocalDateTime currentCompetitionTime
  CompetitionStatus competitionStatus = CompetitionStatus.Created
  String description
  Long initialDuration = 60l //seconds before and initial deactivation / activation cylce is started
  Long durationBetweenShifts = 60l //seconds between deactivation of oldest and activation of newest product
  Integer timeslotLength = 60 //minutes
  Integer timeslotsOverall = 24 //overall timeslotsOverall
  Integer timeslotsOpen = 12 //concurrently open timeslotsOverall (time window)
  Integer deactivateTimeslotsAhead = 1
  LocalDateTime simulationStartTime = new LocalDateTime()
  LocalDateTime dateCreated = new LocalDateTime()
  LocalDateTime lastUpdated = new LocalDateTime()
  BigDecimal balancingCostOver = 0
  BigDecimal balancingCostUnder = 0

  //Liquidity Provider properties
  Boolean liquidityProviderEnabled = true
  BigDecimal premium = 0.01
  BigDecimal fixCost = 0
  String profileUrl = 'http://ibwmarkets.iw.uni-karlsruhe.de/ps/rest'
  String timeseriesName = 'Average Price'
  Long profileId = 82 //EEX Timeseries for 2008
  String authToken = 'FYtlyOAmeCmfdVbgkm37oQJsyZ0U1d2u' //Special TAC Liquidity provider user account in profile store
  Integer regularCapacity = Integer.MAX_VALUE - 1
  Integer maxCapacity = Integer.MAX_VALUE
  BigDecimal percentPriceIncrease = 0.05
  BigDecimal priceScaling = 1

  static hasMany = [brokers: Broker, cashUpdates: CashUpdate, customers: Customer, meterReadings: MeterReading, orderbooks: Orderbook, positionUpdates: PositionUpdate, products: Product, shouts: Shout, tariffs: Tariff, timeslots: Timeslot, transactionLogs: TransactionLog]


  static namedQueries = {
    currentCompetition {
      uniqueResult = true
      eq 'current', true
    }
  }

  static constraints = {
    id (nullable: false, unique: true, blank: false)
    name(unique: true, blank: false)
    enabled(nullable: false)
    current(nullable: false)
    currentCompetitionTime(nullable: true)
    competitionStatus(nullable: false)
    description(nullable: true)
    initialDuration(nullable: false, min: 1l)
    durationBetweenShifts(nullable: false, min: 1l)
    timeslotLength(nullable: false, min: 1)
    timeslotsOverall(nullable: false, min: 1)
    timeslotsOpen(nullable: false, min: 1, validator: { timeslotsOpen, competition ->
      timeslotsOpen <= (competition.timeslotsOverall - competition.deactivateTimeslotsAhead) ? true : ['timeslotsOpen.greater.timeslotsAhead']
    })
    deactivateTimeslotsAhead(nullable: false, min: 0, validator: {deactivateTimeslotsAhead, competition ->
      deactivateTimeslotsAhead <= (competition.timeslotsOverall - competition.timeslotsOpen) ? true : ['deactivateTimeslotsAhead.greater.timeslotsOpen']
    })
    simulationStartTime(nullable: false)
    dateCreated(nullable: false)
    lastUpdated(nullable: false)
    balancingCostOver(nullable: false, scale: Constants.DECIMALS)
    balancingCostUnder(nullable: false, scale: Constants.DECIMALS)
    liquidityProviderEnabled(nullable: false)
    premium(nullable: false)
    fixCost(nullable: false)
    profileUrl(nullable: false)
    timeseriesName(nullable: false)
    profileId(nullable: false)
    authToken(nullable: false)
    regularCapacity(nullable: false)
    percentPriceIncrease(nullable: false)
    priceScaling(nullable: false)
  }

  static mapping = {
    cache true
    id (generator: 'assigned')
  }

  public String toString() {
    return name
  }
}
