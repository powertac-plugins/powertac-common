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

class CustomerInfoTests extends GrailsUnitTestCase {

  //Competition competition
  CustomerInfo customerInfo

  protected void setUp() {
    super.setUp()
    //competition = new Competition(name: 'testCompetition')
    //registerMetaClass(Competition)
    //Competition.metaClass.'static'.currentCompetition = {-> return competition }
    customerInfo = new CustomerInfo()
    mockForConstraintsTests(CustomerInfo, [customerInfo])
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    customerInfo.id = null
    //customerInfo.competition = null
    assertFalse(customerInfo.validate())
    assertEquals('nullable', customerInfo.errors.getFieldError('id').getCode())
    //assertEquals('nullable', customerInfo.errors.getFieldError('competition').getCode())
    assertEquals('nullable', customerInfo.errors.getFieldError('name').getCode())
    assertEquals('nullable', customerInfo.errors.getFieldError('customerType').getCode())
    //assertEquals('nullable', customer.errors.getFieldError('multiContracting').getCode())
    //assertEquals('nullable', customer.errors.getFieldError('canNegotiate').getCode())
    //assertEquals('nullable', customer.errors.getFieldError('upperPowerCap').getCode())
    //assertEquals('nullable', customer.errors.getFieldError('lowerPowerCap').getCode())
    //assertEquals('nullable', customer.errors.getFieldError('carbonEmissionRate').getCode())
    //assertEquals('nullable', customer.errors.getFieldError('windToPowerConversion').getCode())
    //assertEquals('nullable', customer.errors.getFieldError('tempToPowerConversion').getCode())
    //assertEquals('nullable', customer.errors.getFieldError('sunToPowerConversion').getCode())
  }

  void testBlankValidationLogic() {
    customerInfo = new CustomerInfo(id: '', name: '')
    assertFalse(customerInfo.validate())
    assertEquals('blank', customerInfo.errors.getFieldError('id').getCode())
    assertEquals('blank', customerInfo.errors.getFieldError('name').getCode())
  }

  void testIdUniqueness() {
    CustomerInfo customer1 = new CustomerInfo(id: customerInfo.id)
    assertFalse(customer1.validate())
    assertEquals('unique', customer1.errors.getFieldError('id').getCode())
  }
}
