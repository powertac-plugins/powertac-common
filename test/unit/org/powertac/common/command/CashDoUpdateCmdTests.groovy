package org.powertac.common.command

import grails.test.GrailsUnitTestCase
import org.powertac.common.Competition
import org.powertac.common.Constants

class CashDoUpdateCmdTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
    mockDomain(Competition)
    mockForConstraintsTests(CashDoUpdateCmd)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    CashDoUpdateCmd cmd = new CashDoUpdateCmd(id: null)
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('id').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('competition').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('broker').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('relativeChange').getCode())
  }

  void testInactiveCompetition() {
    Competition competition = new Competition(current: false)
    CashDoUpdateCmd cmd = new CashDoUpdateCmd(competition: competition)
    assertFalse(cmd.validate())
    assertEquals(Constants.COMPETITION_INACTIVE, cmd.errors.getFieldError('competition').getCode())
  }
}
