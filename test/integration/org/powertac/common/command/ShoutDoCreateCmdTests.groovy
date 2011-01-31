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

package org.powertac.common.command

import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.OrderType
import org.powertac.common.enumerations.ProductType
import org.powertac.common.*

class ShoutDoCreateCmdTests extends GroovyTestCase {

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
    assert(product.validate() && product.save())
    timeslot = new Timeslot(competition: competition, serialNumber: 0)
    assert(timeslot.validate() && timeslot.save())
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    ShoutDoCreateCmd cmd= new ShoutDoCreateCmd()
    cmd.id = null
    assertFalse (cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('id').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('competition').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('product').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('timeslot').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('broker').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('buySellIndicator').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('quantity').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('orderType').getCode())
  }

  void testMinValidationLogic() {
    ShoutDoCreateCmd cmd= new ShoutDoCreateCmd(limitPrice: -1.0, quantity:  -1.0)
    assertFalse(cmd.validate())
    assertEquals('min.notmet', cmd.errors.getFieldError('limitPrice').getCode())
    assertEquals('min.notmet', cmd.errors.getFieldError('quantity').getCode())
  }

  void testInactiveCompetition() {
    competition.current = false
    competition.save()
    ShoutDoCreateCmd cmd= new ShoutDoCreateCmd(competition: competition)
    assertFalse(cmd.validate())
    assertEquals(Constants.COMPETITION_INACTIVE, cmd.errors.getFieldError('competition').getCode())
  }

  void testInactiveTimeslot() {
    competition.current = true
    competition.save()
    timeslot.enabled = false
    timeslot.save()
    ShoutDoCreateCmd cmd= new ShoutDoCreateCmd(timeslot: timeslot)
    assertFalse(cmd.validate())
    assertEquals(Constants.TIMESLOT_INACTIVE, cmd.errors.getFieldError('timeslot').getCode())
  }

  void testMarketLimitOrderConstraints() {
    ShoutDoCreateCmd cmd = new ShoutDoCreateCmd(orderType: OrderType.MARKET, limitPrice: 1.0)
    assertFalse (cmd.validate())
    assertEquals(Constants.SHOUT_MARKETORDER_WITH_LIMIT, cmd.errors.getFieldError('limitPrice').getCode())

    ShoutDoCreateCmd cmd2 = new ShoutDoCreateCmd(orderType: OrderType.LIMIT)
    assertFalse (cmd2.validate())
    assertEquals(Constants.SHOUT_LIMITORDER_NULL_LIMIT, cmd2.errors.getFieldError('limitPrice').getCode())
  }

  void testValidShoutDoCreateCmd(){
    timeslot.enabled = true
    timeslot.save()
    competition.current = true
    competition.save()
    ShoutDoCreateCmd cmd = new ShoutDoCreateCmd(competition: competition, product: product, timeslot: timeslot, broker: broker, quantity: 1.0, limitPrice: 10.0, buySellIndicator: BuySellIndicator.BUY, orderType: OrderType.LIMIT)
    assertTrue (cmd.validate())

    ShoutDoCreateCmd cmd2 = new ShoutDoCreateCmd(competition: competition, product: product, timeslot: timeslot, broker: broker, quantity: 1.0, buySellIndicator: BuySellIndicator.SELL, orderType: OrderType.MARKET)
    assertTrue (cmd2.validate())
  }
}
