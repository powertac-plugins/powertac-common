/*
 * Copyright (c) 2011 by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powertac.common

import grails.test.*
import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.CustomerType
import org.powertac.common.enumerations.ProductType
import org.powertac.common.msg.TimeslotUpdate
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant
import com.thoughtworks.xstream.*

/**
 * Tests for serialization and deserialization of market-related
 * types needed for broker communication.
 * @author John Collins
 */
class MarketSerializationTests extends GroovyTestCase 
{
  def timeService // dependency injection
  Instant now
  
  Broker broker
  CustomerInfo customerInfo
  Timeslot timeslot
  ProductType product
  XStream xstream

  protected void setUp() 
  {
    super.setUp()
    now = new DateTime(2011, 1, 10, 0, 0, 0, 0, DateTimeZone.UTC).toInstant()
    timeService.currentTime = now
    broker = new Broker (username: 'Sally', password: 'testPassword')
    assert broker.save()
    customerInfo = new CustomerInfo(name:"Charley", customerType: CustomerType.CustomerHousehold)
    assert customerInfo.save()
    timeslot = new Timeslot(serialNumber: 1, current: true,
        startInstant: now, endInstant: new Instant(now.millis + TimeService.HOUR))
    assert timeslot.save()
    product = ProductType.Future

    xstream = new XStream()
    xstream.processAnnotations(CashPosition.class)
    xstream.processAnnotations(Timeslot.class)
    xstream.processAnnotations(TimeslotUpdate.class)
    xstream.processAnnotations(ClearedTrade.class)
    xstream.processAnnotations(MarketPosition.class)
    xstream.processAnnotations(MarketTransaction.class)
    xstream.processAnnotations(Shout.class)
    xstream.processAnnotations(Orderbook.class)
    xstream.processAnnotations(OrderbookAsk.class)
    xstream.processAnnotations(OrderbookBid.class)
  }

  protected void tearDown() 
  {
    super.tearDown()
  }

  void testCashPosition() 
  {
    CashPosition position =
      new CashPosition(broker: broker, balance: 42.1)
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(position))
    println serialized.toString()
    def xcp = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xcp)
    assertTrue("correct type", xcp instanceof CashPosition)
    //assertEquals("correct id", position.id, xcp.id)
    assertEquals("correct balance", 42.1, xcp.balance, 1e-6)
  }
  
  void testCashPositionHasMany ()
  {
    CashPosition position =
      new CashPosition(broker: broker, balance: 42.2)
    MarketTransaction mt =
      new MarketTransaction(broker: broker, timeslot: timeslot, product: product,
                            postedTime: timeService.currentTime,
                            quantity: 101.1, price: 12.3)
    if (!mt.validate()) {
      mt.errors.allErrors.each { println it.toString() }
    }
    assert mt.save()
    position.addToMarketTransactions(mt)
    assert position.save()
    
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(position))
    println serialized.toString()
    def xcp = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xcp)
    assertTrue("correct type", xcp instanceof CashPosition)
    //assertEquals("correct id", position.id, xcp.id)
    assertEquals("correct balance", 42.2, xcp.balance, 1e-6)
    assertNull("no transactions", xcp.marketTransactions)
  }
  
  void testTimeslot ()
  {
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(timeslot))
    println serialized.toString()
    def xts = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xts)
    assertTrue("correct type", xts instanceof Timeslot)
    assertEquals("correct sn", timeslot.serialNumber, xts.serialNumber)
    assertEquals("correct start", timeslot.startInstant, xts.startInstant)
  }
  
  void testTimeslotUpdate ()
  {
    Timeslot s2 = new Timeslot(serialNumber: 2, current: false,
        startInstant: new Instant(now.millis + TimeService.HOUR), 
        endInstant: new Instant(now.millis + TimeService.HOUR * 2))
    assert s2.save()
    TimeslotUpdate tu = new TimeslotUpdate(enabled: [s2], disabled: [timeslot])
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(tu))
    println serialized.toString()
    def xtu = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xtu)
    assertTrue("correct type", xtu instanceof TimeslotUpdate)
    assertEquals("correct enabled size", 1, xtu.enabled.size())
    assertEquals("correct enabled sn", 2, xtu.enabled[0].serialNumber)
    assertEquals("correct disabled size", 1, xtu.disabled.size())
    assertEquals("correct disabled sn", 1, xtu.disabled[0].serialNumber)
  }
  
  void testClearedTrade ()
  {
    ClearedTrade ct =
      new ClearedTrade(timeslot: timeslot, product: product,
                       executionQuantity: 33.3,
                       executionPrice: 4.05)

    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(ct))
    println serialized.toString()
    def xct = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xct)
    assertTrue("correct type", xct instanceof ClearedTrade)
    assertEquals("correct timeslot", timeslot.serialNumber, xct.timeslot.serialNumber)
    assertEquals("correct qty", 33.3, xct.executionQuantity, 1e-6)
    assertEquals("correct price", 4.05, xct.executionPrice, 1e-6)
  }
  
  void testMarketPosition ()
  {
    MarketPosition posn =
        new MarketPosition(broker: broker, timeslot: timeslot,
                           product: product, overallBalance: 32.1)
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(posn))
    println serialized.toString()
    def xposn = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xposn)
    assertTrue("correct type", xposn instanceof MarketPosition)
    assertEquals("correct timeslot", timeslot.serialNumber, xposn.timeslot.serialNumber)
    assertEquals("correct qty", 32.1, xposn.overallBalance, 1e-6)
  }
  
  void testMarketTransaction ()
  {
    MarketTransaction mt =
        new MarketTransaction(broker: broker, timeslot: timeslot, product: product,
                              postedTime: timeService.currentTime,
                              quantity: 101.1, price: 12.3)

    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(mt))
    println serialized.toString()
    def xmt = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xmt)
    assertTrue("correct type", xmt instanceof MarketTransaction)
    assertEquals("correct timeslot", timeslot.serialNumber, xmt.timeslot.serialNumber)
    assertEquals("correct qty", 101.1, xmt.quantity, 1e-6)
    assertEquals("correct price", 12.3, xmt.price, 1e-6)
  }
  
  void testMarketTransactionList ()
  {
    MarketTransaction mt1 =
        new MarketTransaction(broker: broker, timeslot: timeslot, product: product,
                              postedTime: timeService.currentTime,
                              quantity: 101.1, price: 12.3)
    MarketTransaction mt2 =
        new MarketTransaction(broker: broker, timeslot: timeslot, product: product,
                              postedTime: timeService.currentTime,
                              quantity: 201.1, price: 22.3)

    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML([mt1, mt2]))
    println serialized.toString()
    def xlist = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xlist)
    assertEquals("list of two items", 2, xlist.size())
    assertTrue("correct type", xlist[0] instanceof MarketTransaction)
    assertEquals("correct timeslot", timeslot.serialNumber, xlist[0].timeslot.serialNumber)
    assertEquals("correct qty", 101.1, xlist[0].quantity, 1e-6)
    assertEquals("correct price", 12.3, xlist[0].price, 1e-6)
    assertEquals("correct qty", 201.1, xlist[1].quantity, 1e-6)
    assertEquals("correct price", 22.3, xlist[1].price, 1e-6)
  }
  
  void testShout ()
  {
    Shout shout =
        new Shout(broker: broker, product: product, timeslot: timeslot,
                  buySellIndicator: BuySellIndicator.BUY,
                  quantity: 500.0, limitPrice: 50.0)

    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(shout))
    println serialized.toString()
    def xs = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xs)
    assertTrue("correct type", xs instanceof Shout)
    assertEquals("correct timeslot", timeslot, xs.timeslot)
  }
  
  void testEmptyOrderbook ()
  {
    Orderbook ob =
        new Orderbook(dateExecuted: timeService.currentTime, transactionId: '2',
                      product: product, timeslot: timeslot)

    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(ob))
    println serialized.toString()
    def xob = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xob)
    assertTrue("correct type", xob instanceof Orderbook)
    assertEquals("correct timeslot", timeslot, xob.timeslot)
    assertEquals("correct transaction", '2', xob.transactionId)
  }
  
  void testOrderbook ()
  {
    Orderbook ob =
        new Orderbook(dateExecuted: timeService.currentTime, transactionId: 'ab',
                      product: product, timeslot: timeslot)
    ob.addToBids(new OrderbookBid(limitPrice: 22.0, quantity: 42.0))
    ob.addToBids(new OrderbookBid(limitPrice: 19.0, quantity: 22.0))
    ob.addToAsks(new OrderbookAsk(limitPrice: 25.0, quantity: 12.0))

    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(ob))
    println serialized.toString()
    def xob = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xob)
    assertTrue("correct type", xob instanceof Orderbook)
    assertEquals("correct timeslot", timeslot, xob.timeslot)
    assertEquals("correct transaction", 'ab', xob.transactionId)
  }
}
