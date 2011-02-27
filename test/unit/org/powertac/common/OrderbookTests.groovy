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

class OrderbookTests extends GrailsUnitTestCase {

  def timeService

  protected void setUp() {
    super.setUp()
    timeService = new TimeService()
    timeService.setCurrentTime(new DateTime())
    registerMetaClass(Orderbook)
    Orderbook.metaClass.getTimeService = {-> return timeService}
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    Orderbook orderbook = new Orderbook(id: null, dateExecuted: null, bidSize0: null, bidSize1: null, bidSize2: null, bidSize3: null, bidSize4: null, bidSize5: null, bidSize6: null, bidSize7: null, bidSize8: null, bidSize9: null, askSize0: null, askSize1: null, askSize2: null, askSize3: null, askSize4: null, askSize5: null, askSize6: null, askSize7: null, askSize8: null, askSize9: null)
    mockForConstraintsTests(Orderbook, [orderbook])
    assertNull(orderbook.id)
    assertFalse(orderbook.validate())
    assertEquals('nullable', orderbook.errors.getFieldError('id').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('dateExecuted').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('transactionId').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('product').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('timeslot').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('bidSize0').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('bidSize1').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('bidSize2').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('bidSize3').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('bidSize4').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('bidSize5').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('bidSize6').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('bidSize7').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('bidSize8').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('bidSize9').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('askSize0').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('askSize1').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('askSize2').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('askSize3').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('askSize4').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('askSize5').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('askSize6').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('askSize7').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('askSize8').getCode())
    assertEquals('nullable', orderbook.errors.getFieldError('askSize9').getCode())
  }

  void testSetOrderbookArray() {
    Orderbook orderbook = new Orderbook()
    BigDecimal[][][] orderbookArray = new BigDecimal[2][2][10]
    orderbookArray[0][0][0] = 1.0
    orderbookArray[0][0][1] = 2.0
    orderbookArray[0][0][2] = 3.0
    orderbookArray[0][0][3] = 4.0
    orderbookArray[0][0][4] = 5.0
    orderbookArray[0][0][5] = 6.0
    orderbookArray[0][0][6] = 7.0
    orderbookArray[0][0][7] = 8.0
    orderbookArray[0][0][8] = 9.0
    orderbookArray[0][0][9] = 10.0

    orderbookArray[1][0][0] = 100.0
    orderbookArray[1][0][1] = 101.0
    orderbookArray[1][0][2] = 102.0
    orderbookArray[1][0][3] = 103.0
    orderbookArray[1][0][4] = 104.0
    orderbookArray[1][0][5] = 105.0
    orderbookArray[1][0][6] = 106.0
    orderbookArray[1][0][7] = 107.0
    orderbookArray[1][0][8] = 108.0
    orderbookArray[1][0][9] = 109.0

    orderbookArray[0][1][0] = 1000.0
    orderbookArray[0][1][1] = 1001.0
    orderbookArray[0][1][2] = 1002.0
    orderbookArray[0][1][3] = 1003.0
    orderbookArray[0][1][4] = 1004.0
    orderbookArray[0][1][5] = 1005.0
    orderbookArray[0][1][6] = 1006.0
    orderbookArray[0][1][7] = 1007.0
    orderbookArray[0][1][8] = 1008.0
    orderbookArray[0][1][9] = 1009.0

    orderbookArray[1][1][0] = 10000.0
    orderbookArray[1][1][1] = 10001.0
    orderbookArray[1][1][2] = 10002.0
    orderbookArray[1][1][3] = 10003.0
    orderbookArray[1][1][4] = 10004.0
    orderbookArray[1][1][5] = 10005.0
    orderbookArray[1][1][6] = 10006.0
    orderbookArray[1][1][7] = 10007.0
    orderbookArray[1][1][8] = 10008.0
    orderbookArray[1][1][9] = 10009.0

    orderbook.setOrderbookArray(orderbookArray)

    assertEquals(1.0, orderbook.bid0)
    assertEquals(2.0, orderbook.bid1)
    assertEquals(3.0, orderbook.bid2)
    assertEquals(4.0, orderbook.bid3)
    assertEquals(5.0, orderbook.bid4)
    assertEquals(6.0, orderbook.bid5)
    assertEquals(7.0, orderbook.bid6)
    assertEquals(8.0, orderbook.bid7)
    assertEquals(9.0, orderbook.bid8)
    assertEquals(10.0, orderbook.bid9)

    assertEquals(100.0, orderbook.ask0)
    assertEquals(101.0, orderbook.ask1)
    assertEquals(102.0, orderbook.ask2)
    assertEquals(103.0, orderbook.ask3)
    assertEquals(104.0, orderbook.ask4)
    assertEquals(105.0, orderbook.ask5)
    assertEquals(106.0, orderbook.ask6)
    assertEquals(107.0, orderbook.ask7)
    assertEquals(108.0, orderbook.ask8)
    assertEquals(109.0, orderbook.ask9)

    assertEquals(1000.0, orderbook.bidSize0)
    assertEquals(1001.0, orderbook.bidSize1)
    assertEquals(1002.0, orderbook.bidSize2)
    assertEquals(1003.0, orderbook.bidSize3)
    assertEquals(1004.0, orderbook.bidSize4)
    assertEquals(1005.0, orderbook.bidSize5)
    assertEquals(1006.0, orderbook.bidSize6)
    assertEquals(1007.0, orderbook.bidSize7)
    assertEquals(1008.0, orderbook.bidSize8)
    assertEquals(1009.0, orderbook.bidSize9)

    assertEquals(10000.0, orderbook.askSize0)
    assertEquals(10001.0, orderbook.askSize1)
    assertEquals(10002.0, orderbook.askSize2)
    assertEquals(10003.0, orderbook.askSize3)
    assertEquals(10004.0, orderbook.askSize4)
    assertEquals(10005.0, orderbook.askSize5)
    assertEquals(10006.0, orderbook.askSize6)
    assertEquals(10007.0, orderbook.askSize7)
    assertEquals(10008.0, orderbook.askSize8)
    assertEquals(10009.0, orderbook.askSize9)
  }

  void testGetOrderbookArray() {
    Orderbook orderbook = new Orderbook(bid0: 1.0, bid1: 2.0, bid2: 3.0, bid3: 4.0, bid4: 5.0, bid5: 6.0, bid6: 7.0, bid7: 8.0, bid8: 9.0, bid9: 10.0, ask0: 100.0, ask1: 101.0, ask2: 102.0, ask3: 103.0, ask4: 104.0, ask5: 105.0, ask6: 106.0, ask7: 107.0, ask8: 108.0, ask9: 109.0, bidSize0: 1000.0, bidSize1: 1001.0, bidSize2: 1002.0, bidSize3: 1003.0, bidSize4: 1004.0, bidSize5: 1005.0, bidSize6: 1006.0, bidSize7: 1007.0, bidSize8: 1008.0, bidSize9: 1009.0, askSize0: 10000.0, askSize1: 10001.0, askSize2: 10002.0, askSize3: 10003.0, askSize4: 10004.0, askSize5: 10005.0, askSize6: 10006.0, askSize7: 10007.0, askSize8: 10008.0, askSize9: 10009.0)

    BigDecimal[][][] orderbookArray = orderbook.getOrderbookArray()
    assertEquals(1.0, orderbookArray[0][0][0])
    assertEquals(2.0, orderbookArray[0][0][1])
    assertEquals(3.0, orderbookArray[0][0][2])
    assertEquals(4.0, orderbookArray[0][0][3])
    assertEquals(5.0, orderbookArray[0][0][4])
    assertEquals(6.0, orderbookArray[0][0][5])
    assertEquals(7.0, orderbookArray[0][0][6])
    assertEquals(8.0, orderbookArray[0][0][7])
    assertEquals(9.0, orderbookArray[0][0][8])
    assertEquals(10.0, orderbookArray[0][0][9])

    assertEquals(100.0, orderbookArray[1][0][0])
    assertEquals(101.0, orderbookArray[1][0][1])
    assertEquals(102.0, orderbookArray[1][0][2])
    assertEquals(103.0, orderbookArray[1][0][3])
    assertEquals(104.0, orderbookArray[1][0][4])
    assertEquals(105.0, orderbookArray[1][0][5])
    assertEquals(106.0, orderbookArray[1][0][6])
    assertEquals(107.0, orderbookArray[1][0][7])
    assertEquals(108.0, orderbookArray[1][0][8])
    assertEquals(109.0, orderbookArray[1][0][9])

    assertEquals(1000.0, orderbookArray[0][1][0])
    assertEquals(1001.0, orderbookArray[0][1][1])
    assertEquals(1002.0, orderbookArray[0][1][2])
    assertEquals(1003.0, orderbookArray[0][1][3])
    assertEquals(1004.0, orderbookArray[0][1][4])
    assertEquals(1005.0, orderbookArray[0][1][5])
    assertEquals(1006.0, orderbookArray[0][1][6])
    assertEquals(1007.0, orderbookArray[0][1][7])
    assertEquals(1008.0, orderbookArray[0][1][8])
    assertEquals(1009.0, orderbookArray[0][1][9])

    assertEquals(10000.0, orderbookArray[1][1][0])
    assertEquals(10001.0, orderbookArray[1][1][1])
    assertEquals(10002.0, orderbookArray[1][1][2])
    assertEquals(10003.0, orderbookArray[1][1][3])
    assertEquals(10004.0, orderbookArray[1][1][4])
    assertEquals(10005.0, orderbookArray[1][1][5])
    assertEquals(10006.0, orderbookArray[1][1][6])
    assertEquals(10007.0, orderbookArray[1][1][7])
    assertEquals(10008.0, orderbookArray[1][1][8])
    assertEquals(10009.0, orderbookArray[1][1][9])
  }

  void testSetOrderbookArrayWithNullValues() {
    Orderbook orderbook = new Orderbook()
    BigDecimal[][][] orderbookArray = new BigDecimal[2][2][10]
    orderbook.setOrderbookArray(orderbookArray)

    assertNull(orderbook.bid0)
    assertNull(orderbook.bid1)
    assertNull(orderbook.bid2)
    assertNull(orderbook.bid3)
    assertNull(orderbook.bid4)
    assertNull(orderbook.bid5)
    assertNull(orderbook.bid6)
    assertNull(orderbook.bid7)
    assertNull(orderbook.bid8)
    assertNull(orderbook.bid9)

    assertNull(orderbook.ask0)
    assertNull(orderbook.ask1)
    assertNull(orderbook.ask2)
    assertNull(orderbook.ask3)
    assertNull(orderbook.ask4)
    assertNull(orderbook.ask5)
    assertNull(orderbook.ask6)
    assertNull(orderbook.ask7)
    assertNull(orderbook.ask8)
    assertNull(orderbook.ask9)

    assertEquals(0.0, orderbook.bidSize0)
    assertEquals(0.0, orderbook.bidSize1)
    assertEquals(0.0, orderbook.bidSize2)
    assertEquals(0.0, orderbook.bidSize3)
    assertEquals(0.0, orderbook.bidSize4)
    assertEquals(0.0, orderbook.bidSize5)
    assertEquals(0.0, orderbook.bidSize6)
    assertEquals(0.0, orderbook.bidSize7)
    assertEquals(0.0, orderbook.bidSize8)
    assertEquals(0.0, orderbook.bidSize9)

    assertEquals(0.0, orderbook.askSize0)
    assertEquals(0.0, orderbook.askSize1)
    assertEquals(0.0, orderbook.askSize2)
    assertEquals(0.0, orderbook.askSize3)
    assertEquals(0.0, orderbook.askSize4)
    assertEquals(0.0, orderbook.askSize5)
    assertEquals(0.0, orderbook.askSize6)
    assertEquals(0.0, orderbook.askSize7)
    assertEquals(0.0, orderbook.askSize8)
    assertEquals(0.0, orderbook.askSize9)
  }
}
