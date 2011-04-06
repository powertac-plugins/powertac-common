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
import org.powertac.common.enumerations.CustomerType
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
  
  Broker broker
  CustomerInfo customerInfo
  Timeslot timeslot
  XStream xstream

  protected void setUp() 
  {
    super.setUp()
    Instant now = new DateTime(2011, 1, 10, 0, 0, 0, 0, DateTimeZone.UTC).toInstant()
    timeService.currentTime = now
    broker = new Broker (username: 'Sally', password: 'testPassword')
    assert broker.save()
    customerInfo = new CustomerInfo(name:"Charley", customerType: CustomerType.CustomerHousehold)
    assert customerInfo.save()
    timeslot = new Timeslot(serialNumber: 1, current: true,
        startInstant: now, endInstant: new Instant(now.millis + TimeService.HOUR))
    assert timeslot.save()

    xstream = new XStream()
    xstream.processAnnotations(CashPosition.class)

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
    assertEquals("correct id", position.id, xcp.id)
    assertEquals("correct balance", 42.1, xcp.balance, 1e-6)
  }
  
  void testCashPositionHasMany ()
  {
    CashPosition position =
      new CashPosition(broker: broker, balance: 42.2)
    MarketTransaction mt =
      new MarketTransaction(broker: broker, timeslot: timeslot,
                            postedTime: timeService.currentTime,
                            quantity: 101.1, price: 12.3)
    assert mt.save()
    position.addToMarketTransactions(mt)
    assert position.save()
    
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(position))
    println serialized.toString()
    def xcp = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xcp)
    assertTrue("correct type", xcp instanceof CashPosition)
    assertEquals("correct id", position.id, xcp.id)
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
}
