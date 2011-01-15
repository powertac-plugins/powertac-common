package org.powertac.common.command

import grails.test.GrailsUnitTestCase
import org.powertac.common.Constants
import org.powertac.common.Tariff

class TariffDoUpdateCmdTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
    mockForConstraintsTests(TariffDoUpdateCmd)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableConstraints() {
    def cmd = new TariffDoUpdateCmd()
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('parent').getCode())
  }

  void testTariffIsDynamicConstraint() {
    def tariff = new Tariff()
    tariff.isDynamic = false
    def cmd = new TariffDoUpdateCmd(parent: tariff)
    assertFalse(cmd.validate())
    assertEquals(Constants.TARIFF_NON_DYNAMIC, cmd.errors.getFieldError('parent').getCode())
  }
}
