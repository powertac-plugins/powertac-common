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

class CustomerTests extends GrailsUnitTestCase {

  Competition competition
  Customer customer

  protected void setUp() {
    super.setUp()
    competition = new Competition(name: 'testCompetition')
    registerMetaClass(Competition)
    Competition.metaClass.'static'.currentCompetition = {-> return competition }
    customer = new Customer()
    mockForConstraintsTests(Customer, [customer])
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    customer.id = null
    customer.competition = null
    assertFalse(customer.validate())
    assertEquals('nullable', customer.errors.getFieldError('id').getCode())
    assertEquals('nullable', customer.errors.getFieldError('competition').getCode())
    assertEquals('nullable', customer.errors.getFieldError('name').getCode())
    assertEquals('nullable', customer.errors.getFieldError('customerType').getCode())
    assertEquals('nullable', customer.errors.getFieldError('multiContracting').getCode())
    assertEquals('nullable', customer.errors.getFieldError('canNegotiate').getCode())
    assertEquals('nullable', customer.errors.getFieldError('upperPowerCap').getCode())
    assertEquals('nullable', customer.errors.getFieldError('lowerPowerCap').getCode())
    assertEquals('nullable', customer.errors.getFieldError('carbonEmissionRate').getCode())
    assertEquals('nullable', customer.errors.getFieldError('windToPowerConversion').getCode())
    assertEquals('nullable', customer.errors.getFieldError('tempToPowerConversion').getCode())
    assertEquals('nullable', customer.errors.getFieldError('sunToPowerConversion').getCode())
  }

  void testBlankValidationLogic() {
    customer = new Customer(id: '', name: '')
    assertFalse(customer.validate())
    assertEquals('blank', customer.errors.getFieldError('id').getCode())
    assertEquals('blank', customer.errors.getFieldError('name').getCode())
  }

  void testIdUniqueness() {
    Customer customer1 = new Customer(id: customer.id)
    assertFalse(customer1.validate())
    assertEquals('unique', customer1.errors.getFieldError('id').getCode())
  }
}
