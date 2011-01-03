package org.powertac.common.command

import grails.test.GrailsUnitTestCase

class CashDoUpdateCommandTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
    mockForConstraintsTests(CashDoUpdateCmd)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    CashDoUpdateCmd cmd = new CashDoUpdateCmd()
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('competitionId').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('brokerId').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('relativeChange').getCode())
  }

  void testBlankValidationLogic() {
    CashDoUpdateCmd cmd = new CashDoUpdateCmd(competitionId: '', brokerId: '', relativeChange: 1)
    assertFalse(cmd.validate())
    assertEquals('blank', cmd.errors.getFieldError('competitionId').getCode())
    assertEquals('blank', cmd.errors.getFieldError('brokerId').getCode())
  }
}
