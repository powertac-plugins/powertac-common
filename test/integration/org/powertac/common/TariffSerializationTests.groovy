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
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant
import com.thoughtworks.xstream.*

class TariffSerializationTests extends GroovyTestCase 
{
  def timeService // dependency injection
  
  TariffSpecification tariffSpec // instance var

  Instant start
  Instant exp
  Broker broker
  XStream xstream

  protected void setUp() 
  {
    super.setUp()
    start = new DateTime(2011, 1, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
    timeService.setCurrentTime(start)
    broker = new Broker (username: 'Sally', password: 'testPassword')
    assert broker.save()
    exp = new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
    tariffSpec = new TariffSpecification(broker: broker, expiration: exp,
                                         minDuration: TimeService.WEEK * 8)

    xstream = new XStream()
    xstream.processAnnotations(Rate.class)
    xstream.processAnnotations(TariffSpecification.class)
    xstream.processAnnotations(HourlyCharge.class)
  }

  protected void tearDown() 
  {
    super.tearDown()
  }
  
  void testSerializeRate ()
  {
    Rate r1 = new Rate(value: 0.15, dailyBegin: 7, dailyEnd: 17)
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(r1))    
    println serialized.toString()
    def xrate = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xrate)
    assertTrue("correct type", xrate instanceof Rate)
    assertEquals("same ID", r1.id, xrate.id)
    assertEquals("Same minValue", r1.minValue, xrate.minValue)
    assert xrate.save()
  }

  void testSerializeSingleRate() 
  {
    Rate r1 = new Rate(value: 0.15, dailyBegin: 7, dailyEnd: 17)
    tariffSpec.addToRates(r1)
    if (!tariffSpec.validate()) {
      tariffSpec.errors.allErrors.each { println it.toString() }
    }
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(tariffSpec))
    println serialized.toString()
    def xts = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xts)
    assertTrue("correct type", xts instanceof TariffSpecification)
    assertEquals("correct id", tariffSpec.id, xts.id)
    assertEquals("contains 1 rate", 1, xts.rates.size())
    assertTrue("rate type", xts.rates[0] instanceof Rate)
    assertTrue("expiration type", xts.expiration instanceof Instant)
    assertEquals("correct broker", broker, xts.broker)
    assert xts.save()
  }
  
  void testSerializeMultipleRate ()
  {
    Rate r1 = new Rate(value: 0.15, dailyBegin: 7, dailyEnd: 17)
    Rate r2 = new Rate(value: 0.08, dailyBegin: 18, dailyEnd: 6)
    tariffSpec.addToRates(r1)
    tariffSpec.addToRates(r2)

    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(tariffSpec))
    println serialized.toString()
    def xts = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xts)
    assertTrue("correct type", xts instanceof TariffSpecification)
    assertEquals("correct id", tariffSpec.id, xts.id)
    assertEquals("contains 2 rates", 2, xts.rates.size())
    assertTrue("rate type", xts.rates[0] instanceof Rate)
    assertTrue("expiration type", xts.expiration instanceof Instant)
    assert xts.save()
  }
  
  void testSerializeVariableRate ()
  {
    Rate r1 = new Rate(isFixed: false, minValue: 0.05, maxValue: 0.50,
        noticeInterval: 3, expectedMean: 0.10, dailyBegin: 7, dailyEnd: 17)
    Rate r2 = new Rate(value: 0.08, dailyBegin: 18, dailyEnd: 6)
    tariffSpec.addToRates(r1)
    tariffSpec.addToRates(r2)
    r1.addToRateHistory(new HourlyCharge(value: 0.09, atTime: new DateTime(2011, 1, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    r1.addToRateHistory(new HourlyCharge(value: 0.11, atTime: new DateTime(2011, 1, 1, 13, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    r1.addToRateHistory(new HourlyCharge(value: 0.13, atTime: new DateTime(2011, 1, 1, 14, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    r1.addToRateHistory(new HourlyCharge(value: 0.14, atTime: new DateTime(2011, 1, 1, 15, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(tariffSpec))
    println serialized.toString()
    def xts = xstream.fromXML(serialized.toString())
    assertNotNull("deserialized something", xts)
    assertTrue("correct type", xts instanceof TariffSpecification)
    assertEquals("correct id", tariffSpec.id, xts.id)
    assertEquals("contains 2 rates", 2, xts.rates.size())
    assertTrue("rate type", xts.rates[0] instanceof Rate)
    assertTrue("expiration type", xts.expiration instanceof Instant)
    def xr1 = xts.rates[0]
    assertEquals("correct rate", r1.id, xr1.id)
    assertEquals("4 hourly charges", 4, xr1.rateHistory.size())
    assertEquals("correct 1st charge", 0.09, xr1.rateHistory.first().value, 1e-6)
    assert xts.save()
  }
}
