package org.powertac.common.command

import grails.test.GrailsUnitTestCase
import org.powertac.common.Competition
import org.powertac.common.Constants

class PositionDoUpdateCmdTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
    mockForConstraintsTests(PositionDoUpdateCmd)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    PositionDoUpdateCmd cmd = new PositionDoUpdateCmd(id: null)
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('id').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('competition').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('broker').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('relativeChange').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('product').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('timeslot').getCode())
  }

  void testInactiveCompetition() {
    Competition competition = new Competition(current: false)
    PositionDoUpdateCmd cmd = new PositionDoUpdateCmd(competition: competition)
    assertFalse(cmd.validate())
    assertEquals(Constants.COMPETITION_INACTIVE, cmd.errors.getFieldError('competition').getCode())
  }

}
