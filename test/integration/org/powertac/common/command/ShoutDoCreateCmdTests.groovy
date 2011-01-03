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

import org.powertac.common.Broker
import org.powertac.common.Competition
import org.powertac.common.Product
import org.powertac.common.Timeslot
import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.OrderType
import org.powertac.common.enumerations.ProductType

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
    assertFalse (cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('competitionId').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('userName').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('apiKey').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('productId').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('timeslotId').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('buySellIndicator').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('quantity').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('orderType').getCode())
  }

  void testBlankValidationLogic() {
    ShoutDoCreateCmd cmd= new ShoutDoCreateCmd(competitionId: '', userName: '', apiKey: '', productId: '', timeslotId: '')
    assertFalse (cmd.validate())
    assertEquals('blank', cmd.errors.getFieldError('competitionId').getCode())
    assertEquals('blank', cmd.errors.getFieldError('userName').getCode())
    assertEquals('blank', cmd.errors.getFieldError('apiKey').getCode())
    assertEquals('blank', cmd.errors.getFieldError('productId').getCode())
    assertEquals('blank', cmd.errors.getFieldError('timeslotId').getCode())
  }

  void testMinValidationLogic() {
    ShoutDoCreateCmd cmd= new ShoutDoCreateCmd(limitPrice: -1.0, quantity:  -1.0)
    assertFalse(cmd.validate())
    assertEquals('min.notmet', cmd.errors.getFieldError('limitPrice').getCode())
    assertEquals('min.notmet', cmd.errors.getFieldError('quantity').getCode())
  }

  void testInvalidCompetitionId() {
    ShoutDoCreateCmd cmd= new ShoutDoCreateCmd(competitionId: 'invalidCompetitionId')
    assertFalse(cmd.validate())
    assertEquals('invalid.competition', cmd.errors.getFieldError('competitionId').getCode())
  }

  void testInvalidProductId() {
    ShoutDoCreateCmd cmd= new ShoutDoCreateCmd(productId: 'invalidProductId')
    assertFalse(cmd.validate())
    assertEquals('invalid.product', cmd.errors.getFieldError('productId').getCode())
  }

  void testInvalidTimeslotId() {
    ShoutDoCreateCmd cmd= new ShoutDoCreateCmd(timeslotId: 'invalidTimeslotId')
    assertFalse(cmd.validate())
    assertEquals('invalid.timeslot', cmd.errors.getFieldError('timeslotId').getCode())
  }

  void testInactiveCompetitionId() {
    competition.current = false
    competition.save()
    ShoutDoCreateCmd cmd= new ShoutDoCreateCmd(competitionId: competition.id)
    assertFalse(cmd.validate())
    assertEquals('inactive.competition', cmd.errors.getFieldError('competitionId').getCode())
  }
  
  void testInvalidCredentials() {
    ShoutDoCreateCmd cmd = new ShoutDoCreateCmd(competitionId: competition.id, userName: "$userName invalid", apiKey: apiKey)
    assertFalse (cmd.validate())
    assertEquals('invalid.credentials', cmd.errors.getFieldError('apiKey').getCode())

    ShoutDoCreateCmd cmd2 = new ShoutDoCreateCmd(competitionId: competition.id, userName: userName, apiKey: "$apiKey invalid")
    assertFalse (cmd2.validate())
    assertEquals('invalid.credentials', cmd2.errors.getFieldError('apiKey').getCode())

    ShoutDoCreateCmd cmd3 = new ShoutDoCreateCmd(competitionId: "${competition.id} invalid", userName: userName, apiKey: apiKey)
    assertFalse (cmd3.validate())
    assertEquals('invalid.credentials', cmd3.errors.getFieldError('apiKey').getCode())
  }

  void testMarketLimitOrderConstraints() {
    ShoutDoCreateCmd cmd = new ShoutDoCreateCmd(orderType: OrderType.MARKET, limitPrice: 1.0)
    assertFalse (cmd.validate())
    assertEquals('nullable.marketorder', cmd.errors.getFieldError('limitPrice').getCode())

    ShoutDoCreateCmd cmd2 = new ShoutDoCreateCmd(orderType: OrderType.LIMIT)
    assertFalse (cmd2.validate())
    assertEquals('nullable.limitorder', cmd2.errors.getFieldError('limitPrice').getCode())
  }

  void testValidShoutDoCreateCmd(){
    competition.current = true
    competition.save()
    ShoutDoCreateCmd cmd = new ShoutDoCreateCmd(competitionId: competition.id, productId: product.id, timeslotId: timeslot.id, userName: broker.userName, apiKey: broker.apiKey, quantity: 1.0, limitPrice: 10.0, buySellIndicator: BuySellIndicator.BUY, orderType: OrderType.LIMIT)
    assertTrue (cmd.validate())

    ShoutDoCreateCmd cmd2 = new ShoutDoCreateCmd(competitionId: competition.id, productId: product.id, timeslotId: timeslot.id, userName: broker.userName, apiKey: broker.apiKey, quantity: 1.0, buySellIndicator: BuySellIndicator.SELL, orderType: OrderType.MARKET)
    assertTrue (cmd2.validate())
  }
}
