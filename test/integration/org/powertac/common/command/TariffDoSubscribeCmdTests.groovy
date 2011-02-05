package org.powertac.common.command

import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.OrderType
import org.powertac.common.enumerations.ProductType
import org.powertac.common.*

class TariffDoSubscribeCmdTests extends GroovyTestCase {
  Competition competition
  Product product
  Timeslot timeslot
  Broker broker
  Broker broker2
  Shout shout
  String userName
  String apiKey

  protected void setUp() {
    super.setUp()
    userName = 'testBroker'
    apiKey = 'testApiKey-which-needs-to-be-longer-than-32-characters'
    competition = new Competition(name: "test", current: true)
    assert (competition.validate() && competition.save())
    broker = new Broker(competition: competition, userName: userName, apiKey: apiKey)
    assert (broker.validate() && broker.save())
    broker2 = new Broker(competition: competition, userName: userName + '2', apiKey: apiKey + '2')
    assert (broker2.validate() && broker2.save())
    product = new Product(competition: competition, productType: ProductType.Future)
    assert (product.validate() && product.save())
    timeslot = new Timeslot(competition: competition, serialNumber: 0)
    assert (timeslot.validate() && timeslot.save())
    shout = new Shout(competition: competition, product: product, timeslot: timeslot, broker: broker, quantity: 1.0, limitPrice: 10.0, buySellIndicator: BuySellIndicator.BUY, orderType: OrderType.LIMIT, transactionId: 'testTransaction', latest: true, shoutId: 'testShoutId')
    assert (shout.validate() && shout.save())
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    TariffDoSubscribeCmd cmd = new TariffDoSubscribeCmd()
    cmd.id = null
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('id').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('competition').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('customer').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('tariff').getCode())
  }

  void testInactiveCompetition() {
    competition.current = false
    competition.save()
    TariffDoSubscribeCmd cmd = new TariffDoSubscribeCmd(competition: competition)
    assertFalse(cmd.validate())
    assertEquals(Constants.COMPETITION_INACTIVE, cmd.errors.getFieldError('competition').getCode())
  }

  void testTariffIsLegacy() {
    competition.current = true
    competition.save()
    Tariff tariff = new Tariff(broker: broker, state: Tariff.State.LEGACY)
    TariffDoSubscribeCmd cmd = new TariffDoSubscribeCmd(tariff: tariff)
    assertFalse(cmd.validate())
    assertEquals(Constants.TARIFF_OUTDATED, cmd.errors.getFieldError('tariff').getCode())
  }
}
