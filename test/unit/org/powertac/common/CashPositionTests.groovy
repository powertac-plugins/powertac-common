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
import org.joda.time.DateTime

class CashPositionTests extends GrailsUnitTestCase {

  def timeService

  protected void setUp() {
    super.setUp()
    //timeService = new TimeService()
    //timeService.setCurrentTime(new DateTime())
    //registerMetaClass(CashPosition)
    //CashPosition.metaClass.getTimeService = {-> return timeService}
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    CashPosition cashPosition = new CashPosition()
    mockForConstraintsTests(CashPosition, [cashPosition])
    assertFalse(cashPosition.validate())
    //assertEquals('nullable', cashUpdate.errors.getFieldError('id').getCode()) TODO: check nullable validation logic on cashUpdate id field
    //assertEquals('nullable', cashPosition.errors.getFieldError('transactionId').getCode())
    assertEquals('nullable', cashPosition.errors.getFieldError('broker').getCode())
    //assertEquals('nullable', cashPosition.errors.getFieldError('relativeChange').getCode())
    //assertEquals('nullable', cashPosition.errors.getFieldError('overallBalance').getCode())
    //assertEquals('nullable', cashPosition.errors.getFieldError('dateCreated').getCode())
  }
}
