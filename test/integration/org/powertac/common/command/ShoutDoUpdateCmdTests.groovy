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
import org.powertac.common.enumerations.ModReasonCode

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
    assertEquals('nullable', cmd.errors.getFieldError('competition').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('broker').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('shoutId').getCode())
  }

  void testBlankValidationLogic() {
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(shoutId: '')
    assertFalse(cmd.validate())
    assertEquals('blank', cmd.errors.getFieldError('shoutId').getCode())
  }

  void testInactiveCompetitionId() {
    competition.current = false
    competition.save()
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(competition: competition)
    assertFalse(cmd.validate())
    assertEquals(Constants.COMPETITION_INACTIVE, cmd.errors.getFieldError('competition').getCode())
  }

  void testInvalidShoutId() {
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(shoutId: 'invalidShoutId')
    assertFalse(cmd.validate())
    assertEquals(Constants.SHOUT_NOT_FOUND, cmd.errors.getFieldError('shoutId').getCode())
  }

  void testShoutExecuted() {
    shout.modReasonCode = ModReasonCode.EXECUTION;
    shout.save()
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(shoutId: shout.shoutId)
    assertFalse(cmd.validate())
    assertEquals(Constants.SHOUT_EXECUTED, cmd.errors.getFieldError('shoutId').getCode())
  }

  void testShoutDeleted() {
    shout.modReasonCode = ModReasonCode.DELETIONBYUSER;
    shout.save()
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(shoutId: shout.shoutId)
    assertFalse(cmd.validate())
    assertEquals(Constants.SHOUT_DELETED, cmd.errors.getFieldError('shoutId').getCode())

    shout.modReasonCode = ModReasonCode.DELETIONBYSYSTEM;
    shout.save()
    ShoutDoUpdateCmd cmd1 = new ShoutDoUpdateCmd(shoutId: shout.shoutId)
    assertFalse(cmd1.validate())
    assertEquals(Constants.SHOUT_DELETED, cmd1.errors.getFieldError('shoutId').getCode())
  }

  void testValidShoutDoUpdateCmd() {
    competition.current = true
    competition.save()
    shout.modReasonCode = ModReasonCode.INSERT
    shout.save()
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(competition: competition, shoutId: shout.shoutId, broker: broker, quantity: 9.99, limitPrice: 99.99)
    assertTrue(cmd.validate())
  }
}
