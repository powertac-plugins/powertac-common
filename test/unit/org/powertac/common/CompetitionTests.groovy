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



package org.powertac.common

import grails.test.GrailsUnitTestCase

class CompetitionTests extends GrailsUnitTestCase {

  Competition competition
  
  protected void setUp() {
    super.setUp()
    competition = new Competition()
    mockForConstraintsTests(Competition, [competition])
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    Competition competition1 = new Competition(id: null, enabled: null, current: null, competitionStatus: null, initialDuration: null, durationBetweenShifts: null, timeslotLength: null, timeslotsOverall: null, timeslotsOpen: null, deactivateTimeslotsAhead: null, simulationStartTime: null, simulationBaseTime: null, simulationModulo: null, simulationRate: null, dateCreated: null, lastUpdated: null, balancingCostOver: null, balancingCostUnder: null, liquidityProviderEnabled: null, premium: null, fixCost: null, profileUrl: null, timeseriesName: null, profileId: null, authToken: null, regularCapacity: null, percentPriceIncrease: null, priceScaling: null)
    assertFalse(competition1.validate())
    assertEquals('nullable', competition1.errors.getFieldError('id').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('name').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('enabled').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('current').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('competitionStatus').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('initialDuration').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('durationBetweenShifts').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('timeslotLength').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('timeslotsOverall').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('timeslotsOpen').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('deactivateTimeslotsAhead').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('simulationStartTime').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('simulationBaseTime').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('simulationRate').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('simulationModulo').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('dateCreated').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('lastUpdated').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('balancingCostOver').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('balancingCostUnder').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('liquidityProviderEnabled').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('premium').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('fixCost').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('profileUrl').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('timeseriesName').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('profileId').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('authToken').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('regularCapacity').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('percentPriceIncrease').getCode())
    assertEquals('nullable', competition1.errors.getFieldError('priceScaling').getCode())
  }

  void testBlankValidationLogic() {
    competition = new Competition(id: '', name: '')
    assertFalse(competition.validate())
    assertEquals('blank', competition.errors.getFieldError('id').getCode())
    assertEquals('blank', competition.errors.getFieldError('name').getCode())
  }

  void testIdUniqueness() {
    Competition competition1 = new Competition(id: competition.id)
    assertFalse(competition1.validate())
    assertEquals('unique', competition1.errors.getFieldError('id').getCode())
  }

  void testTimeslotsOpen() {
    Competition competition1 = new Competition(timeslotsOpen: 1, timeslotsOverall: 3, deactivateTimeslotsAhead: 3)
    assertFalse(competition1.validate())
    assertEquals('timeslotsOpen.greater.timeslotsAhead', competition1.errors.getFieldError('timeslotsOpen').getCode())
    assertEquals('deactivateTimeslotsAhead.greater.timeslotsOpen', competition1.errors.getFieldError('deactivateTimeslotsAhead').getCode())

    competition1.deactivateTimeslotsAhead = 2
    competition1.validate()
    assertNull(competition1.errors.getFieldError('timeslotsOpen'))
    assertNull(competition1.errors.getFieldError('deactivateTimeslotsAhead'))
  }
}
