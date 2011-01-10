package org.powertac.common.command

import grails.test.GrailsUnitTestCase

class PositionDoUpdateCommandTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
    mockForConstraintsTests(PositionDoUpdateCmd)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    PositionDoUpdateCmd cmd = new PositionDoUpdateCmd()
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('competition').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('broker').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('relativeChange').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('product').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('timeslot').getCode())
  }
}
