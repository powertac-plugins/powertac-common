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
import org.powertac.common.msg.TariffUpdate
import org.powertac.common.msg.TariffExpire
import org.powertac.common.msg.TariffRevoke
import org.powertac.common.msg.VariableRateUpdate
import org.powertac.common.msg.TariffStatus
import org.powertac.common.enumerations.CustomerType
import com.thoughtworks.xstream.*

/**
 * Tests for serialization and deserialization of Tariff-related
 * types needed for broker communication.
 * @author John Collins
 */
class TariffSerializationTests extends GroovyTestCase 
{
  def timeService // dependency injection
  
  TariffSpecification tariffSpec // instance var

  Instant start
  Instant exp
  Broker broker
  CustomerInfo customerInfo
  XStream xstream

  protected void setUp() 
  {
    super.setUp()
    start = new DateTime(2011, 1, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
    timeService.setCurrentTime(start)
    broker = new Broker (username: 'Sally', password: 'testPassword')
    assert broker.save()
    customerInfo = new CustomerInfo(name:"Charley", customerType: CustomerType.CustomerHousehold)
    assert customerInfo.save()
    exp = new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
    tariffSpec = new TariffSpecification(broker: broker, expiration: exp,
                                         minDuration: TimeService.WEEK * 8)

    xstream = new XStream()
    xstream.processAnnotations(Rate.class)
    xstream.processAnnotations(TariffSpecification.class)
    xstream.processAnnotations(HourlyCharge.class)
    xstream.processAnnotations(TariffStatus.class)
    xstream.processAnnotations(TariffTransaction.class)
    xstream.processAnnotations(TariffUpdate.class)
    xstream.processAnnotations(TariffExpire.class)
    xstream.processAnnotations(TariffRevoke.class)
    xstream.processAnnotations(VariableRateUpdate.class)
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
  
  void testSerializeStatus ()
  {
    TariffStatus status = 
        new TariffStatus(broker: broker, tariffId: "xyz", updateId: "abc",
                         status: TariffStatus.Status.success,
                         message: "This is a dummy tariff status instance")
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(status))
    println serialized.toString()
    def xts = xstream.fromXML(serialized.toString())
    assertNotNull("got something", xts)
    assertTrue("correct type", xts instanceof TariffStatus)
    assertEquals("correct ID", status.id, xts.id)
    assertEquals("correct tariff ID", 'xyz', xts.tariffId)
  }
  
  void testSerializeTransaction ()
  {
    Rate r1 = new Rate(value: 0.15, dailyBegin: 7, dailyEnd: 17)
    tariffSpec.addToRates(r1)
    tariffSpec.save()
    Tariff tariff = new Tariff(tariffSpec: tariffSpec)
    tariff.init()
    tariff.save()

    def txTime = new DateTime(2011, 1, 1, 14, 0, 0, 0, DateTimeZone.UTC).toInstant()
    TariffTransaction ttx =
        new TariffTransaction(broker: broker, customerInfo:customerInfo,
                              customerCount: 4, postedTime: txTime,
                              quantity: 56.7, charge: 67.8, tariff: tariff)
    assert ttx.save()
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(ttx))
    println serialized.toString()
    def xttx = xstream.fromXML(serialized.toString())
    assertNotNull("got something", xttx)
    assertTrue("correct type", xttx instanceof TariffTransaction)
    //assertEquals("correct ID", 3, xttx.id)
    assertEquals("correct tariff", tariff, xttx.tariff)
    assertEquals("correct broker", broker, xttx.broker)
    assertEquals("correct customer", customerInfo, xttx.customerInfo)
    assertEquals("correct quantity", 56.7, xttx.quantity, 1e-6)
  }
  
  void testTariffExpire ()
  {
    def newExp = new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
    TariffExpire tex = 
      new TariffExpire(tariffId: tariffSpec.id, broker: broker,
                       newExpiration: newExp)
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(tex))
    println serialized.toString()
    def xtex = xstream.fromXML(serialized.toString())
    assertNotNull("got something", xtex)
    assertTrue("correct type", xtex instanceof TariffExpire)
    assertEquals("correct tariff ID", tariffSpec.id, xtex.tariffId)
    assertEquals("correct broker", broker, xtex.broker)
    assertEquals("correct time", newExp, xtex.newExpiration)
  }
  
  void testTariffRevoke ()
  {
    TariffRevoke trv = 
      new TariffRevoke(tariffId: tariffSpec.id, broker: broker)
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(trv))
    println serialized.toString()
    def xtrv = xstream.fromXML(serialized.toString())
    assertNotNull("got something", xtrv)
    assertTrue("correct type", xtrv instanceof TariffRevoke)
    assertEquals("correct tariff ID", tariffSpec.id, xtrv.tariffId)
    assertEquals("correct broker", broker, xtrv.broker)
  }
  
  void testVariableRateUpdate ()
  {
    Rate r1 = new Rate(value: 0.15, dailyBegin: 7, dailyEnd: 17)
    tariffSpec.addToRates(r1)
    tariffSpec.save()
    HourlyCharge hc = 
      new HourlyCharge(value: 0.09, 
                       atTime: new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC).toInstant())
    VariableRateUpdate vru = 
      new VariableRateUpdate(tariffId: tariffSpec.id, broker: broker,
                             payload: hc, rateId: r1.id)
    StringWriter serialized = new StringWriter ()
    serialized.write(xstream.toXML(vru))
    println serialized.toString()
    def xvru = xstream.fromXML(serialized.toString())
    assertNotNull("got something", xvru)
    assertTrue("correct type", xvru instanceof VariableRateUpdate)
    assertEquals("correct tariff ID", tariffSpec.id, xvru.tariffId)
    assertEquals("correct broker", broker, xvru.broker)
    assertEquals("correct value", 0.09, xvru.payload.value, 1e-6)
  }
}
