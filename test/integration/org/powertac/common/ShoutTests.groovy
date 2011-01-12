/*
 * Copyright 2009-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.powertac.common

import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.ModReasonCode
import org.powertac.common.enumerations.OrderType
import org.powertac.common.enumerations.ProductType

class ShoutTests extends GroovyTestCase {

  Competition competition
  Product product
  Timeslot timeslot
  Broker broker
  String userName
  String apiKey

  protected void setUp() {
    super.setUp()
    userName = 'testBroker'
    apiKey = 'testApiKey-which-needs-to-be-longer-than-32-characters'
    competition = new Competition(name: "test")
    assert (competition.validate() && competition.save())
    broker = new Broker(competition: competition, userName: userName, apiKey: apiKey)
    assert (broker.validate() && broker.save())
    product = new Product(competition: competition, productType: ProductType.Future)
    assert (product.validate() && product.save())
    timeslot = new Timeslot(competition: competition, serialNumber: 0)
    assert (timeslot.validate() && timeslot.save())
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    Shout shout = new Shout(orderType: null)
    assertFalse(shout.validate())
    assertEquals('nullable', shout.errors.getFieldError('competition').getCode())
    assertEquals('nullable', shout.errors.getFieldError('broker').getCode())
    assertEquals('nullable', shout.errors.getFieldError('product').getCode())
    assertEquals('nullable', shout.errors.getFieldError('timeslot').getCode())
    assertEquals('nullable', shout.errors.getFieldError('buySellIndicator').getCode())
    assertEquals('nullable', shout.errors.getFieldError('quantity').getCode())
    assertEquals('nullable', shout.errors.getFieldError('orderType').getCode())
  }

  void testMinValidationLogic() {
    Shout shout = new Shout(limitPrice: -1.0, quantity: -1.0)
    assertFalse(shout.validate())
    assertEquals('min.notmet', shout.errors.getFieldError('limitPrice').getCode())
    assertEquals('min.notmet', shout.errors.getFieldError('quantity').getCode())
  }

  void testInactiveCompetitionId() {
    competition.current = false
    assertNotNull(competition.save(flush: true))
    assertFalse(competition.current)
    Shout shout = new Shout(competition: competition)
    assertFalse(shout.validate())
    assertEquals(Constants.COMPETITION_INACTIVE, shout.errors.getFieldError('competition').getCode())
  }

  void testMarketLimitOrderConstraints() {
    Shout shout1 = new Shout(orderType: OrderType.MARKET, limitPrice: 1.0)
    assertFalse(shout1.validate())
    assertEquals(Constants.SHOUT_MARKETORDER_WITH_LIMIT, shout1.errors.getFieldError('limitPrice').getCode())

    Shout shout2 = new Shout(orderType: OrderType.LIMIT)
    assertFalse(shout2.validate())
    assertEquals(Constants.SHOUT_LIMITORDER_NULL_LIMIT, shout2.errors.getFieldError('limitPrice').getCode())
  }

  void testValidShoutDoCreateCmd() {
    competition.current = true
    competition.save()
    Shout shout = new Shout(competition: competition, product: product, timeslot: timeslot, broker: broker, quantity: 1.0, limitPrice: 10.0, buySellIndicator: BuySellIndicator.BUY, orderType: OrderType.LIMIT, transactionId: 'testTransaction', latest: true, shoutId: 'testShoutId')
    assertTrue(shout.validate())

    Shout shout1 = new Shout(competition: competition, product: product, timeslot: timeslot, broker: broker, quantity: 1.0, buySellIndicator: BuySellIndicator.SELL, orderType: OrderType.MARKET, transactionId: 'testTransaction2', latest: true, shoutId: 'testShoutId')
    assertTrue(shout1.validate())
  }

  void testInitModification() {
    competition.current = true
    competition.save(flush: true)
    assertTrue(competition.current)
    Shout shout1 = new Shout(competition: competition, product: product, timeslot: timeslot, broker: broker, quantity: 1.0, buySellIndicator: BuySellIndicator.SELL, orderType: OrderType.MARKET, transactionId: 'testTransaction2', latest: true, shoutId: 'testShoutId')
    assertTrue(shout1.validate())
    Shout shout2 = shout1.initModification(ModReasonCode.DELETIONBYUSER)
    assertNotNull(shout2.id)
    assertFalse(shout1.id.equals(shout2.id))
    assertEquals(shout1.competition, shout2.competition)
    assertEquals(shout1.broker, shout2.broker)
    assertEquals(shout1.product, shout2.product)
    assertEquals(shout1.timeslot, shout2.timeslot)
    assertEquals(shout1.buySellIndicator, shout2.buySellIndicator)
    assertEquals(shout1.quantity, shout2.quantity)
    assertEquals(shout1.limitPrice, shout2.limitPrice)
    assertEquals(shout1.executionQuantity, shout2.executionQuantity)
    assertEquals(shout1.executionPrice, shout2.executionPrice)
    assertEquals(shout1.orderType, shout2.orderType)
    //assertEquals(shout1.dateCreated, shout2.dateCreated) TODO: check back - copied localDateTime instances differ by some milliseconds for whatever reason...
    assertTrue(shout1.dateMod < shout2.dateMod)
    assertEquals(shout1.orderType, shout2.orderType)
    assertEquals(ModReasonCode.INSERT, shout1.modReasonCode)
    assertEquals(ModReasonCode.DELETIONBYUSER, shout2.modReasonCode)
    assertEquals('testTransaction2', shout1.transactionId)
    assertNull(shout2.transactionId)
    assertEquals(shout1.shoutId, shout2.shoutId)
    assertEquals(shout1.comment, shout2.comment)
    assertFalse(shout1.latest)
    assertTrue(shout2.latest)

  }

  void testShoutCreateByNestedIds() {
    competition.current = true
    competition.save(flush: true)
    assertTrue(competition.current)
    Shout shout1 = new Shout('competition.id': competition.id, 'product.id': product.id, 'timeslot.id': timeslot.id, 'broker.id': broker.id, quantity: 1.0, buySellIndicator: BuySellIndicator.SELL, orderType: OrderType.MARKET, transactionId: 'testTransaction2', latest: true, shoutId: 'testShoutId')
    assertTrue(shout1.validate())
  }

  void testGetCurrentCompetition() {
    competition.current = false
    competition.save()
    Competition competition2 = new Competition(name: "test2")
    assertTrue (competition2.validate() && competition2.save())
    Competition competition3 = Competition.currentCompetition.domainClass
    assertEquals("test2", competition3.name)
  }
}
