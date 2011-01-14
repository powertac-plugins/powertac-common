package org.powertac.common.command

import grails.test.GrailsUnitTestCase
import org.powertac.common.Constants
import org.powertac.common.Customer
import org.powertac.common.Tariff
import org.powertac.common.enumerations.TariffState

class TariffDoEarlyExitCmdTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
    mockForConstraintsTests(TariffDoEarlyExitCmd)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableConstraints() {
    def cmd = new TariffDoEarlyExitCmd()
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('customer').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('tariff').getCode())
  }

  void testTariffStateConstraint() {
    def tariff = new Tariff()
    tariff.tariffState = TariffState.Revoked
    def cmd = new TariffDoEarlyExitCmd(tariff: tariff)
    assertFalse(cmd.validate())
    assertEquals(Constants.TARIFF_INVALID_STATE, cmd.errors.getFieldError('tariff').getCode())
  }

  void testTariffCustomerMatchConstraint() {
    def customer1 = new Customer(id: 'test1')
    def tariff = new Tariff(tariffState: TariffState.Subscribed, customer: customer1)
    def customer2 = new Customer(id: 'test2')
    def cmd = new TariffDoEarlyExitCmd(tariff: tariff, customer: customer2)
    assertFalse(cmd.validate())
    assertEquals(Constants.TARIFF_WRONG_CUSTOMER, cmd.errors.getFieldError('tariff').getCode())
  }
}
