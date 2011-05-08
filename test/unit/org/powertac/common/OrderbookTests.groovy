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
import org.joda.time.DateTime
import org.powertac.common.enumerations.BuySellIndicator

class OrderbookTests extends GrailsUnitTestCase {

  def timeService

  protected void setUp() {
    super.setUp()
    /*timeService = new TimeService()
    timeService.setCurrentTime(new DateTime())
    registerMetaClass(Orderbook)
    Orderbook.metaClass.getTimeService = {-> return timeService}
    */
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testBidOrder() {
    Orderbook ob = new Orderbook(dateExecuted: new DateTime().toInstant())
    ob.bids.add(new OrderbookEntry(limitPrice: 12, quantity: 1, buySellIndicator: BuySellIndicator.BUY))
    ob.bids.add(new OrderbookEntry(limitPrice: 10, quantity: 1, buySellIndicator: BuySellIndicator.BUY))
    ob.bids.add(new OrderbookEntry(limitPrice: 11, quantity: 1, buySellIndicator: BuySellIndicator.BUY))

    assertEquals(12, ob.bids.first().limitPrice)
    assertEquals(10, ob.bids.last().limitPrice)
  }

   void testAskOrder() {
    Orderbook ob = new Orderbook(dateExecuted: new DateTime().toInstant())
    ob.asks.add(new OrderbookEntry(limitPrice: 12, quantity: 1, buySellIndicator: BuySellIndicator.SELL))
    ob.asks.add(new OrderbookEntry(limitPrice: 10, quantity: 1, buySellIndicator: BuySellIndicator.SELL))
    ob.asks.add(new OrderbookEntry(limitPrice: 11, quantity: 1, buySellIndicator: BuySellIndicator.SELL))

    assertEquals(10, ob.asks.first().limitPrice)
    assertEquals(12, ob.asks.last().limitPrice)
  }

  void testClearingPriceForExistingClearingPrice() {
    Orderbook ob = new Orderbook(dateExecuted: new DateTime().toInstant(), clearingPrice: 11)
    ob.asks.add(new OrderbookEntry(limitPrice: 12, quantity: 1, buySellIndicator: BuySellIndicator.SELL))
    ob.bids.add(new OrderbookEntry(limitPrice: 10, quantity: 1, buySellIndicator: BuySellIndicator.BUY))

    assertEquals(11, ob.determineClearingPrice())
  }

  void testClearingPriceForExistingBid() {
    Orderbook ob = new Orderbook(dateExecuted: new DateTime().toInstant())
    ob.bids.add(new OrderbookEntry(limitPrice: 12, quantity: 1, buySellIndicator: BuySellIndicator.BUY))
    ob.bids.add(new OrderbookEntry(limitPrice: 10, quantity: 1, buySellIndicator: BuySellIndicator.BUY))

    assertEquals(12, ob.determineClearingPrice())
  }

   void testClearingPriceForExistingAsk() {
    Orderbook ob = new Orderbook(dateExecuted: new DateTime().toInstant())
    ob.asks.add(new OrderbookEntry(limitPrice: 12, quantity: 1, buySellIndicator: BuySellIndicator.SELL))
    ob.asks.add(new OrderbookEntry(limitPrice: 10, quantity: 1, buySellIndicator: BuySellIndicator.SELL))

    assertEquals(10, ob.determineClearingPrice())
  }


}
