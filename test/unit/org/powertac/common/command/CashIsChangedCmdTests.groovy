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

class CashIsChangedCmdTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
    mockForConstraintsTests(CashIsChangedCmd)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    CashIsChangedCmd cmd = new CashIsChangedCmd(dateCreated: null)
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('id').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('competitionId').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('transactionId').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('brokerId').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('relativeChange').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('overallBalance').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('dateCreated').getCode())
  }

  void testBlankValidationLogic() {
    CashIsChangedCmd cmd = new CashIsChangedCmd(id: '', competitionId: '', transactionId: '', brokerId: '')
    assertFalse(cmd.validate())
    assertEquals('blank', cmd.errors.getFieldError('id').getCode())
    assertEquals('blank', cmd.errors.getFieldError('competitionId').getCode())
    assertEquals('blank', cmd.errors.getFieldError('transactionId').getCode())
    assertEquals('blank', cmd.errors.getFieldError('brokerId').getCode())
  }
}
