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

import org.powertac.common.enumerations.CustomerType
import org.powertac.common.msg.SimStart
import grails.test.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant
import com.thoughtworks.xstream.*

/**
 * Tests for serialization and deserialization of basic admin types, including
 * Competition and CustomerInfo.
 * @author John Collins
 */
class AdminSerializationTestTests extends GroovyTestCase 
{
  def timeService // dependency injection
  
  Broker broker
  CustomerInfo customerInfo
  Timeslot timeslot
  Product product
  XStream xstream
  
  protected void setUp ()
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
    xstream.processAnnotations(Competition.class)
    xstream.processAnnotations(SimStart.class)
    xstream.processAnnotations(CustomerInfo.class)
  }

  protected void tearDown () 
  {
    super.tearDown()
  }

  void testCompetition () 
  {
    Competition comp =
        new Competition(name: "testing", description: "more testing",
                        simulationBaseTime: new DateTime(2010, 1, 10, 0, 0, 0, 0, DateTimeZone.UTC).toInstant())

    comp.parameterMap['tariffPublicationFee'] = 3.45
    comp.parameterMap['distributionRate'] = 0.015
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(comp))
    println serialized.toString()
    def xc = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xc)
    assertTrue("correct type", xc instanceof Competition)
    assertEquals("correct id", comp.id, xc.id)
    assertEquals("correct publication fee", 3.45, xc.parameterMap['tariffPublicationFee'])
  }

  void testCustomerInfo ()
  {
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(customerInfo))
    println serialized.toString()
    def xc = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xc)
    assertTrue("correct type", xc instanceof CustomerInfo)
    assertEquals("correct id", customerInfo.id, xc.id)
  }
  
  void testSimStart ()
  {
    SimStart start = new SimStart(start: new DateTime(2011, 4, 10, 15, 25, 30, 0, DateTimeZone.UTC).toInstant())
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(start))
    println serialized.toString()
    def xs = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xs)
    assertTrue("correct type", xs instanceof SimStart)
    assertEquals("correct id", start.id, xs.id)
  }
}
