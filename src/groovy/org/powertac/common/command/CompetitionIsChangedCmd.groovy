/*
 * Copyright 2009-2011 the original author or authors.
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



package org.powertac.common.command

import org.codehaus.groovy.grails.validation.Validateable
import org.joda.time.LocalDateTime
import org.powertac.common.Constants
import org.powertac.common.enumerations.CompetitionStatus

/**
 * Command objects that represents the new state of a competition after it was changed
 *
 * @author Carsten Block
 * @version 1.0, Date: 02.01.11
 */
@Validateable class CompetitionIsChangedCmd implements Serializable {
  String id
  String name
  Boolean enabled
  Boolean current
  LocalDateTime currentCompetitionTime
  CompetitionStatus competitionStatus
  String description
  Long initialDuration
  Long durationBetweenShifts
  Integer timeslotLength
  Integer timeslotsOverall
  Integer timeslotsOpen
  Integer deactivateTimeslotsAhead
  LocalDateTime simulationStartTime
  LocalDateTime dateCreated
  LocalDateTime lastUpdated
  BigDecimal balancingCostOver
  BigDecimal balancingCostUnder

  //Liquidity Provider properties
  Boolean liquidityProviderEnabled
  BigDecimal premium
  BigDecimal fixCost
  String profileUrl
  String timeseriesName
  Long profileId
  String authToken
  Integer regularCapacity
  Integer maxCapacity
  BigDecimal percentPriceIncrease
  BigDecimal priceScaling

  static constraints = {
    name(nullable: false, blank: false)
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
}
