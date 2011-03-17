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

import org.joda.time.Instant
import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.ModReasonCode
import org.powertac.common.enumerations.OrderType
import org.powertac.common.enumerations.ProductType

class ShoutTests extends GroovyTestCase {

  TimeService timeService
  Product product
  Timeslot timeslot
  Broker broker
  String username
  String password
  String apiKey

  protected void setUp() {
    super.setUp()
    username = 'testBroker'
    password = 'testPassword'
    apiKey = 'testApiKey-which-needs-to-be-longer-than-32-characters'
    timeService.setCurrentTime(new Instant())
    broker = new Broker(username: username, password: password, apiKey: apiKey)
    assert (broker.validate() && broker.save())
    product = new Product(productType: ProductType.Future)
    assert (product.validate() && product.save())
    timeslot = new Timeslot(serialNumber: 0, startInstant: new Instant(), endInstant: new Instant())
    assert (timeslot.validate() && timeslot.save())
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    Shout shout = new Shout(competition: null, orderType: null)
    assertFalse(shout.validate())
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

  void testMarketLimitOrderConstraints() {
    Shout shout1 = new Shout(orderType: OrderType.MARKET, limitPrice: 1.0)
    assertFalse(shout1.validate())
    assertEquals(Constants.SHOUT_MARKETORDER_WITH_LIMIT, 
                 shout1.errors.getFieldError('limitPrice').getCode())

    Shout shout2 = new Shout(orderType: OrderType.LIMIT)
    assertFalse(shout2.validate())
    assertEquals(Constants.SHOUT_LIMITORDER_NULL_LIMIT, 
                 shout2.errors.getFieldError('limitPrice').getCode())
  }

  void testValidShoutDoCreateCmd() {
    Shout shout = new Shout(product: product, timeslot: timeslot, 
                            broker: broker, quantity: 1.0, limitPrice: 10.0, 
                            buySellIndicator: BuySellIndicator.BUY, 
                            orderType: OrderType.LIMIT, transactionId: 'testTransaction', 
                            dateCreated: timeService.currentTime,
                            dateMod: timeService.currentTime)
    if (!shout.validate()) println shout.errors.allErrors
    assertTrue(shout.validate())

    Shout shout1 = new Shout(product: product, timeslot: timeslot, 
                             broker: broker, quantity: 1.0, 
                             buySellIndicator: BuySellIndicator.SELL, 
                             orderType: OrderType.MARKET, transactionId: 'testTransaction2', 
                             dateCreated: timeService.currentTime,
                             dateMod: timeService.currentTime)
    assertTrue(shout1.validate())
  }

  void testInitModification() {
    Shout shout1 = new Shout(product: product, timeslot: timeslot,
                             broker: broker, quantity: 1.0, 
                             buySellIndicator: BuySellIndicator.SELL, 
                             orderType: OrderType.MARKET, transactionId: 'testTransaction2', 
                             dateCreated: timeService.currentTime,
                             dateMod: timeService.currentTime)
    assertTrue(shout1.validate())
    timeService.currentTime = new Instant() //update competition time so that modification date for shout is later then creation date
    Shout shout2 = shout1.initModification(ModReasonCode.DELETIONBYUSER)
    assertNotNull(shout2.id)
    assertEquals(shout1.id, shout2.id)
    assertEquals(shout1.broker, shout2.broker)
    assertEquals(shout1.product, shout2.product)
    assertEquals(shout1.timeslot, shout2.timeslot)
    assertEquals(shout1.buySellIndicator, shout2.buySellIndicator)
    assertEquals(shout1.quantity, shout2.quantity)
    assertEquals(shout1.limitPrice, shout2.limitPrice)
    assertEquals(shout1.executionQuantity, shout2.executionQuantity)
    assertEquals(shout1.executionPrice, shout2.executionPrice)
    assertEquals(shout1.orderType, shout2.orderType)
    //assertEquals(shout1.dateCreated, shout2.dateCreated) TODO: check back - copied DateTime instances differ by some milliseconds for whatever reason...
    //assertTrue(shout1.dateMod < shout2.dateMod)
    assertEquals(shout1.orderType, shout2.orderType)
    //assertEquals(ModReasonCode.INSERT, shout1.modReasonCode)
    assertEquals(ModReasonCode.DELETIONBYUSER, shout2.modReasonCode)
    assertEquals('testTransaction2', shout1.transactionId)
    //assertNull(shout2.transactionId)
    assertEquals(shout1.comment, shout2.comment)

  }

  void testShoutCreateByNestedIds() {
    Shout shout1 = new Shout('product.id': product.id, 'timeslot.id': timeslot.id, 
                             'broker.id': broker.id, quantity: 1.0, 
                             buySellIndicator: BuySellIndicator.SELL, 
                             orderType: OrderType.MARKET, transactionId: 'testTransaction2', 
                             dateCreated: timeService.currentTime,
                             dateMod: timeService.currentTime)
    assertTrue(shout1.validate())
  }
}
