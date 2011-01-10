package org.powertac.common.command

import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.ModReasonCode
import org.powertac.common.enumerations.OrderType
import org.powertac.common.enumerations.ProductType
import org.powertac.common.*

class ShoutDoDeleteCommandTests extends GroovyTestCase {

  Competition competition
  Product product
  Timeslot timeslot
  Broker broker
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
    ShoutDoDeleteCmd cmd = new ShoutDoDeleteCmd()
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('competition').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('broker').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('shoutId').getCode())
  }

  void testBlankValidationLogic() {
    ShoutDoDeleteCmd cmd = new ShoutDoDeleteCmd(shoutId: '')
    assertFalse(cmd.validate())
    assertEquals('blank', cmd.errors.getFieldError('shoutId').getCode())
  }

  void testInactiveCompetition() {
    competition.current = false
    competition.save()
    ShoutDoDeleteCmd cmd = new ShoutDoDeleteCmd(competition: competition)
    assertFalse(cmd.validate())
    assertEquals(Constants.COMPETITION_INACTIVE, cmd.errors.getFieldError('competition').getCode())
  }

  void testInvalidShoutId() {
    ShoutDoDeleteCmd cmd = new ShoutDoDeleteCmd(shoutId: 'invalidShoutId')
    assertFalse(cmd.validate())
    assertEquals(Constants.SHOUT_NOT_FOUND, cmd.errors.getFieldError('shoutId').getCode())
  }

  void testDeletedShout() {
    shout.modReasonCode = ModReasonCode.DELETIONBYSYSTEM
    ShoutDoDeleteCmd cmd = new ShoutDoDeleteCmd(competition: competition, shoutId: shout.shoutId, broker: broker)
    assertFalse(cmd.validate())
    assertEquals(Constants.SHOUT_DELETED, cmd.errors.getFieldError('shoutId').getCode())


    shout.modReasonCode = ModReasonCode.DELETIONBYUSER
    ShoutDoDeleteCmd cmd1 = new ShoutDoDeleteCmd(competition: competition, shoutId: shout.shoutId, broker: broker)
    assertFalse(cmd1.validate())
    assertEquals(Constants.SHOUT_DELETED, cmd1.errors.getFieldError('shoutId').getCode())
  }

  void testExecutedShout() {
    shout.modReasonCode = ModReasonCode.EXECUTION
    ShoutDoDeleteCmd cmd = new ShoutDoDeleteCmd(competition: competition, shoutId: shout.shoutId, broker: broker)
    assertFalse(cmd.validate())
    assertEquals(Constants.SHOUT_EXECUTED, cmd.errors.getFieldError('shoutId').getCode())
  }

  void testValidShoutDoDeleteCmd() {
    competition.current = true
    competition.save()
    shout.modReasonCode = ModReasonCode.INSERT
    shout.save()
    ShoutDoDeleteCmd cmd = new ShoutDoDeleteCmd(competition: competition, shoutId: shout.shoutId, broker: broker)
    assertTrue(cmd.validate())
  }
}
