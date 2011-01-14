package org.powertac.common.command

import grails.test.GrailsUnitTestCase
import org.powertac.common.Tariff
import org.powertac.common.Constants

class TariffDoRevokeCmdTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
    mockForConstraintsTests(TariffDoRevokeCmd)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableConstraints() {
    def tariffDoRevokeCmd = new TariffDoRevokeCmd()
    assertFalse(tariffDoRevokeCmd.validate())
    assertEquals('nullable', tariffDoRevokeCmd.errors.getFieldError('broker').getCode())
    assertEquals('nullable', tariffDoRevokeCmd.errors.getFieldError('tariff').getCode())
  }

  void testTariffHasParentConstraints() {
    def tariff = new Tariff()
    tariff.parent = tariff
    def tariffDoRevokeCmd = new TariffDoRevokeCmd(tariff: tariff)
    assertFalse(tariffDoRevokeCmd.validate())
    assertEquals(Constants.TARIFF_HAS_PARENT, tariffDoRevokeCmd.errors.getFieldError('tariff').getCode())

  }
  void testTariffOutdatedConstraint() {
    def tariff = new Tariff()
    tariff.latest = false
    def tariffDoRevokeCmd = new TariffDoRevokeCmd(tariff: tariff)
    assertFalse(tariffDoRevokeCmd.validate())
    assertEquals(Constants.TARIFF_OUTDATED, tariffDoRevokeCmd.errors.getFieldError('tariff').getCode())
  }
}
