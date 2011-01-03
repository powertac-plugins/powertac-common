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

import grails.test.GrailsUnitTestCase
import org.powertac.common.enumerations.TransactionType
import org.powertac.common.enumerations.BuySellIndicator

class TransactionLogTests extends GrailsUnitTestCase {

  Broker broker

  protected void setUp() {
    super.setUp()
    broker = new Broker (userName: 'testBroker')
    mockForConstraintsTests(TransactionLog)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    TransactionLog transactionLog = new TransactionLog(id: null, dateCreated: null)
    assertNull(transactionLog.id)
    assertFalse(transactionLog.validate())
    assertEquals('nullable', transactionLog.errors.getFieldError('id').getCode())
    assertEquals('nullable', transactionLog.errors.getFieldError('competition').getCode())
    assertEquals('nullable', transactionLog.errors.getFieldError('product').getCode())
    assertEquals('nullable', transactionLog.errors.getFieldError('timeslot').getCode())
    assertEquals('nullable', transactionLog.errors.getFieldError('transactionType').getCode())
    assertEquals('nullable', transactionLog.errors.getFieldError('dateCreated').getCode())
    assertEquals('nullable', transactionLog.errors.getFieldError('transactionId').getCode())
    assertEquals('nullable', transactionLog.errors.getFieldError('latest').getCode())
  }

  void testQuoteValidationLogic() {
    TransactionLog transactionLog = new TransactionLog(transactionType: TransactionType.QUOTE, price: 1.0, quantity: 10.0, buyer: broker, seller: broker, buySellIndicator: BuySellIndicator.BUY)
    assertFalse (transactionLog.validate())
    assertEquals('quote.price.notnull', transactionLog.errors.getFieldError('price').getCode())
    assertEquals('quote.quantity.notnull', transactionLog.errors.getFieldError('quantity').getCode())
    assertEquals('quote.buyer.notnull', transactionLog.errors.getFieldError('buyer').getCode())
    assertEquals('quote.seller.notnull', transactionLog.errors.getFieldError('seller').getCode())
    assertEquals('quote.buysellindicator.notnull', transactionLog.errors.getFieldError('buySellIndicator').getCode())
    assertEquals('quote.bid.null', transactionLog.errors.getFieldError('bid').getCode())
    assertEquals('quote.bidSize.null', transactionLog.errors.getFieldError('bidSize').getCode())
    assertEquals('quote.ask.null', transactionLog.errors.getFieldError('ask').getCode())
    assertEquals('quote.askSize.null', transactionLog.errors.getFieldError('askSize').getCode())
  }

  void testTradeValidationLogic() {
    TransactionLog transactionLog = new TransactionLog(transactionType: TransactionType.TRADE, bid: 1.0, bidSize: 10.0, ask: 1.0, askSize: 10.0)
    assertFalse (transactionLog.validate())
    assertEquals('trade.price.null', transactionLog.errors.getFieldError('price').getCode())
    assertEquals('trade.quantity.null', transactionLog.errors.getFieldError('quantity').getCode())
    assertEquals('trade.buyer.null', transactionLog.errors.getFieldError('buyer').getCode())
    assertEquals('trade.seller.null', transactionLog.errors.getFieldError('seller').getCode())
    assertEquals('trade.buysellindicator.null', transactionLog.errors.getFieldError('buySellIndicator').getCode())
    assertEquals('trade.bid.notnull', transactionLog.errors.getFieldError('bid').getCode())
    assertEquals('trade.bidSize.notnull', transactionLog.errors.getFieldError('bidSize').getCode())
    assertEquals('trade.ask.notnull', transactionLog.errors.getFieldError('ask').getCode())
    assertEquals('trade.askSize.notnull', transactionLog.errors.getFieldError('askSize').getCode())
  }
}
