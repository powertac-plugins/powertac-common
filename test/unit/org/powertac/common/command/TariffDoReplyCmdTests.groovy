package org.powertac.common.command

import grails.test.GrailsUnitTestCase

class TariffDoReplyCmdTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
    mockForConstraintsTests(TariffDoReplyCmd)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidation() {
    def cmd = new TariffDoReplyCmd()
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('customer').getCode())
  }
}
