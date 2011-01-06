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

class ShoutDoUpdateCmdTests extends GroovyTestCase {

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
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd()
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('competitionId').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('userName').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('apiKey').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('shoutId').getCode())
  }

  void testBlankValidationLogic() {
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(competitionId: '', userName: '', apiKey: '', shoutId: '')
    assertFalse(cmd.validate())
    assertEquals('blank', cmd.errors.getFieldError('competitionId').getCode())
    assertEquals('blank', cmd.errors.getFieldError('userName').getCode())
    assertEquals('blank', cmd.errors.getFieldError('apiKey').getCode())
    assertEquals('blank', cmd.errors.getFieldError('shoutId').getCode())
  }

  void testInvalidCompetitionId() {
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(competitionId: 'invalidCompetitionId')
    assertFalse(cmd.validate())
    assertEquals('invalid.competition', cmd.errors.getFieldError('competitionId').getCode())
  }

  void testInactiveCompetitionId() {
    competition.current = false
    competition.save()
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(competitionId: competition.id)
    assertFalse(cmd.validate())
    assertEquals('inactive.competition', cmd.errors.getFieldError('competitionId').getCode())
  }

  void testInvalidShoutId() {
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(shoutId: 'invalidShoutId')
    assertFalse(cmd.validate())
    assertEquals('invalid.shout', cmd.errors.getFieldError('shoutId').getCode())
  }

  void testInvalidCredentials() {
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(competitionId: competition.id, userName: "$userName invalid", apiKey: apiKey)
    assertFalse(cmd.validate())
    assertEquals('invalid.credentials', cmd.errors.getFieldError('apiKey').getCode())

    ShoutDoUpdateCmd cmd2 = new ShoutDoUpdateCmd(competitionId: competition.id, userName: userName, apiKey: "$apiKey invalid")
    assertFalse(cmd2.validate())
    assertEquals('invalid.credentials', cmd2.errors.getFieldError('apiKey').getCode())

    ShoutDoUpdateCmd cmd3 = new ShoutDoUpdateCmd(competitionId: "${competition.id} invalid", userName: userName, apiKey: apiKey)
    assertFalse(cmd3.validate())
    assertEquals('invalid.credentials', cmd3.errors.getFieldError('apiKey').getCode())
  }

  void testValidShoutDoUpdateCmd() {
    competition.current = true
    competition.save()
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(competitionId: competition.id, shoutId: shout.id, userName: broker.userName, apiKey: broker.apiKey, quantity: 9.99, limitPrice: 99.99)
    assertTrue(cmd.validate())
  }
}
