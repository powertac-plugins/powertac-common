package org.powertac.common.command

import grails.test.GrailsUnitTestCase
import org.powertac.common.Constants
import org.powertac.common.Tariff

class TariffDoSubscribeCmdTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
    mockForConstraintsTests(TariffDoSubscribeCmd)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableConstraints() {
    def tariffDoSubscribeCmd = new TariffDoSubscribeCmd()
    assertFalse(tariffDoSubscribeCmd.validate())
    assertEquals('nullable', tariffDoSubscribeCmd.errors.getFieldError('customer').getCode())
    assertEquals('nullable', tariffDoSubscribeCmd.errors.getFieldError('tariff').getCode())
  }

  void testTariffOutdatedConstraint() {
    def tariff = new Tariff()
    tariff.latest = false
    def tariffDoSubscribeCmd = new TariffDoSubscribeCmd(tariff: tariff)
    assertFalse(tariffDoSubscribeCmd.validate())
    assertEquals(Constants.TARIFF_OUTDATED, tariffDoSubscribeCmd.errors.getFieldError('tariff').getCode())
  }
}
