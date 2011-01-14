package org.powertac.common.command

import grails.test.GrailsUnitTestCase
import org.powertac.common.Constants
import org.powertac.common.Tariff
import org.powertac.common.enumerations.TariffState

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
    def tariff = new Tariff(parent: null, latest: false)
    def tariffDoSubscribeCmd = new TariffDoSubscribeCmd(tariff: tariff)
    assertFalse(tariffDoSubscribeCmd.validate())
    assertEquals(Constants.TARIFF_OUTDATED, tariffDoSubscribeCmd.errors.getFieldError('tariff').getCode())
  }

  void testTariffInvalidStateConstraint() {
    //Case 1: parent != null && TariffState != InNegotiation
    def tariff = new Tariff(latest: true)
    tariff.parent = tariff
    tariff.tariffState = TariffState.NegotiationAborted
    def tariffDoSubscribeCmd = new TariffDoSubscribeCmd(tariff: tariff)
    assertFalse(tariffDoSubscribeCmd.validate())
    assertEquals(Constants.TARIFF_INVALID_STATE, tariffDoSubscribeCmd.errors.getFieldError('tariff').getCode())
    tariff.tariffState = TariffState.InNegotiation
    tariffDoSubscribeCmd.validate()
    assertNull(tariffDoSubscribeCmd.errors.getFieldError('tariff'))

    //Case 2: parent == null && tariffState & published --> should never occur
    def tariff2 = new Tariff(latest: true, tariffState: TariffState.Revoked)
    def tariffDoSubscribeCmd2 = new TariffDoSubscribeCmd(tariff: tariff2)
    assertFalse(tariffDoSubscribeCmd2.validate())
    assertEquals(Constants.TARIFF_INVALID_STATE, tariffDoSubscribeCmd2.errors.getFieldError('tariff').getCode())
    tariff2.tariffState = TariffState.Published
    tariffDoSubscribeCmd2.validate()
    assertNull(tariffDoSubscribeCmd2.errors.getFieldError('tariff'))


  }
}
