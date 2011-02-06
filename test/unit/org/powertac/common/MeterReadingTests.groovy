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

class MeterReadingTests extends GrailsUnitTestCase {

  Competition competition
  MeterReading meterReading

  protected void setUp() {
    super.setUp()
    competition = new Competition(name: 'testCompetition')
    registerMetaClass(Competition)
    Competition.metaClass.'static'.currentCompetition = {-> return competition }
    meterReading = new MeterReading()
    mockForConstraintsTests(MeterReading)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    MeterReading meterReading1 = new MeterReading(id: null, competition: null)
    assertNull(meterReading1.id)
    assertFalse(meterReading1.validate())
    assertEquals('nullable', meterReading1.errors.getFieldError('id').getCode())
    assertEquals('nullable', meterReading1.errors.getFieldError('competition').getCode())
    assertEquals('nullable', meterReading1.errors.getFieldError('customer').getCode())
    assertEquals('nullable', meterReading1.errors.getFieldError('timeslot').getCode())
    assertEquals('nullable', meterReading1.errors.getFieldError('amount').getCode())
    assertEquals('nullable', meterReading1.errors.getFieldError('latest').getCode())
  }

  void testBlankValidationLogic() {
    MeterReading meterReading1 = new MeterReading(id: '')
    assertFalse(meterReading1.validate())
    assertEquals('blank', meterReading1.errors.getFieldError('id').getCode())
  }

/*
  void testIdUniqueness() {
    meterReading.id = 'test'
    MeterReading meterReading1 = new MeterReading(id: 'test')
    assertFalse(meterReading1.validate())
    assertEquals('unique', meterReading1.errors.getFieldError('id').getCode())
  }*/
}
