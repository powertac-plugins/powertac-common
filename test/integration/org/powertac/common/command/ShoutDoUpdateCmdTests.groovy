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

import org.joda.time.DateTime
import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.ModReasonCode
import org.powertac.common.enumerations.OrderType
import org.powertac.common.enumerations.ProductType
import org.powertac.common.*

class ShoutDoUpdateCmdTests extends GroovyTestCase {

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
    broker = new Broker(userName: userName, apiKey: apiKey)
    assert (broker.validate() && broker.save())
    broker2 = new Broker(userName: userName+'2', apiKey: apiKey+'2')
    assert (broker2.validate() && broker2.save())
    product = new Product(productType: ProductType.Future)
    assert (product.validate() && product.save())
    timeslot = new Timeslot(serialNumber: 0, startDateTime: new DateTime(), endDateTime: new DateTime())
    assert (timeslot.validate() && timeslot.save())
    shout = new Shout(product: product, timeslot: timeslot, broker: broker, quantity: 1.0, limitPrice: 10.0, buySellIndicator: BuySellIndicator.BUY, orderType: OrderType.LIMIT, transactionId: 'testTransaction', latest: true, shoutId: 'testShoutId')
    assert (shout.validate() && shout.save())
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd()
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('broker').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('shout').getCode())
  }

  void testMinValidationLogic() {
    ShoutDoUpdateCmd cmd= new ShoutDoUpdateCmd(limitPrice: -1.0, quantity:  -1.0)
    assertFalse(cmd.validate())
    assertEquals('min.notmet', cmd.errors.getFieldError('limitPrice').getCode())
    assertEquals('min.notmet', cmd.errors.getFieldError('quantity').getCode())
  }

  void testQuantityAndLimitNullLogic() {
    ShoutDoUpdateCmd cmd= new ShoutDoUpdateCmd()
    assertFalse(cmd.validate())
    assertEquals(Constants.SHOUT_UPDATE_WITHOUT_LIMIT_AND_QUANTITY, cmd.errors.getFieldError('limitPrice').getCode())
    assertEquals(Constants.SHOUT_UPDATE_WITHOUT_LIMIT_AND_QUANTITY, cmd.errors.getFieldError('quantity').getCode())

    ShoutDoUpdateCmd cmd1= new ShoutDoUpdateCmd(limitPrice: 1.0)
    assertFalse(cmd1.validate())
    assertNull(cmd1.errors.getFieldError('limitPrice')?.getCode())
    assertNull(cmd1.errors.getFieldError('quantity')?.getCode())

    ShoutDoUpdateCmd cmd2= new ShoutDoUpdateCmd(quantity: 1.0)
    assertFalse(cmd2.validate())
    assertNull(cmd2.errors.getFieldError('limitPrice')?.getCode())
    assertNull(cmd2.errors.getFieldError('quantity')?.getCode())

    ShoutDoUpdateCmd cmd3= new ShoutDoUpdateCmd(quantity: 1.0, limitPrice: 1.0)
    assertFalse(cmd3.validate())
    assertNull(cmd3.errors.getFieldError('limitPrice')?.getCode())
    assertNull(cmd3.errors.getFieldError('quantity')?.getCode())
  }

  void testShoutOutdated() {
    shout.latest = false
    shout.save()
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(shout: shout)
    assertFalse(cmd.validate())
    assertEquals(Constants.SHOUT_OUTDATED, cmd.errors.getFieldError('shout').getCode())
  }

  void testShoutExecuted() {
    shout.modReasonCode = ModReasonCode.EXECUTION;
    shout.save()
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(shout: shout)
    assertFalse(cmd.validate())
    assertEquals(Constants.SHOUT_EXECUTED, cmd.errors.getFieldError('shout').getCode())
  }

  void testShoutDeleted() {
    shout.modReasonCode = ModReasonCode.DELETIONBYUSER;
    shout.save()
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(shout: shout)
    assertFalse(cmd.validate())
    assertEquals(Constants.SHOUT_DELETED, cmd.errors.getFieldError('shout').getCode())

    shout.modReasonCode = ModReasonCode.DELETIONBYSYSTEM;
    shout.save()
    ShoutDoUpdateCmd cmd1 = new ShoutDoUpdateCmd(shout: shout)
    assertFalse(cmd1.validate())
    assertEquals(Constants.SHOUT_DELETED, cmd1.errors.getFieldError('shout').getCode())
  }

  void testInvalidBroker(){
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(shout: shout, broker: broker2)
    assertFalse(cmd.validate())
    assertEquals(Constants.SHOUT_WRONG_BROKER, cmd.errors.getFieldError('broker').getCode())
  }

  void testValidShoutDoUpdateCmd() {
    shout.modReasonCode = ModReasonCode.INSERT
    shout.save()
    ShoutDoUpdateCmd cmd = new ShoutDoUpdateCmd(shout: shout, broker: broker, quantity: 9.99, limitPrice: 99.99)
    assertTrue(cmd.validate())
  }
}
