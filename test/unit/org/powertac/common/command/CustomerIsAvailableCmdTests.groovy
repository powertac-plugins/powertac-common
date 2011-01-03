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

import grails.test.GrailsUnitTestCase

class CustomerIsAvailableCmdTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
    mockForConstraintsTests(CustomerIsAvailableCmd)
  }

  protected void tearDown() {
    super.tearDown()
  }

   void testNullableValidationLogic() {
    CustomerIsAvailableCmd cmd = new CustomerIsAvailableCmd()
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('id').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('competitionId').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('name').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('customerType').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('multiContracting').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('canNegotiate').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('upperPowerCap').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('lowerPowerCap').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('carbonEmissionRate').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('windToPowerConversion').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('tempToPowerConversion').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('sunToPowerConversion').getCode())
  }

  void testBlankValidationLogic() {
    CustomerIsAvailableCmd cmd = new CustomerIsAvailableCmd(id: '', competitionId: '', name: '')
    assertFalse(cmd.validate())
    assertEquals('blank', cmd.errors.getFieldError('id').getCode())
    assertEquals('blank', cmd.errors.getFieldError('competitionId').getCode())
    assertEquals('blank', cmd.errors.getFieldError('name').getCode())
  }
}
