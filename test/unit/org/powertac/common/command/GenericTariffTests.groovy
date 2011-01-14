package org.powertac.common.command

import grails.test.GrailsUnitTestCase

class GenericTariffTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
    mockForConstraintsTests(GenericTariffCmd)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    GenericTariffCmd cmd = new GenericTariffCmd(id: null)
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('id').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('competition').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('broker').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('isDynamic').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('isNegotiable').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice0').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice1').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice2').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice3').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice4').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice5').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice6').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice7').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice8').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice9').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice10').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice11').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice12').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice13').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice14').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice15').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice16').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice17').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice18').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice19').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice20').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice21').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice22').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerConsumptionPrice23').getCode())

    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice0').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice1').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice2').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice3').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice4').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice5').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice6').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice7').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice8').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice9').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice10').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice11').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice12').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice13').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice14').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice15').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice16').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice17').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice18').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice19').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice20').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice21').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice22').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('powerProductionPrice23').getCode())
  }
}
