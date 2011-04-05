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
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.strategy.Strategy

class TariffSerializationTests extends GroovyTestCase 
{
  def timeService // dependency injection
  
  TariffSpecification tariffSpec // instance var

  Instant start
  Instant exp
  Broker broker

  protected void setUp() 
  {
    super.setUp()
    start = new DateTime(2011, 1, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
    timeService.setCurrentTime(start)
    broker = new Broker (username: 'Sally', password: 'testPassword')
    assert broker.save()
    exp = new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
    tariffSpec = new TariffSpecification(brokerUsername: broker.username, expiration: exp,
                                         minDuration: TimeService.WEEK * 8)
  }

  protected void tearDown() 
  {
    super.tearDown()
  }
  
  void testSerializeRate ()
  {
    Rate r1 = new Rate(value: 0.15, dailyBegin: 7, dailyEnd: 17)
    assert r1.save()

    StringWriter serialized = new StringWriter ()
    Serializer serializer = new Persister()
    //File result = new File("example.xml");
    serializer.write(r1, serialized)
    println serialized.toString()
    StringReader input = new StringReader(serialized.toString())
    Rate xrate = serializer.read(Rate.class, input)
    assertNotNull("deserialized something", xrate)
    assertEquals("same ID", r1.id, xrate.id)
    assertEquals("Same minValue", r1.minValue, xrate.minValue)
  }

  void testSerializeSingleRate() 
  {
    Rate r1 = new Rate(value: 0.15, dailyBegin: 7, dailyEnd: 17)
    assert r1.save()
    tariffSpec.addToRates(r1)
    if (!tariffSpec.validate()) {
      tariffSpec.errors.allErrors.each { println it.toString() }
    }
    assert tariffSpec.save()

    StringWriter serialized = new StringWriter ()
    Strategy strategy = new AnnotationStrategy()
    Serializer serializer = new Persister(strategy)
    serializer.write(tariffSpec, serialized)
    println serialized.toString()
  }
  
  void testSerializeMultipleRate ()
  {
    Rate r1 = new Rate(value: 0.15, dailyBegin: 7, dailyEnd: 17)
    assert r1.save()
    Rate r2 = new Rate(value: 0.08, dailyBegin: 18, dailyEnd: 6)
    assert r2.save()
    tariffSpec.addToRates(r1)
    tariffSpec.addToRates(r2)
    //assert tariffSpec.save()

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
    assert r1.save()
    assert r2.save()
    //assert tariffSpec.save()
    
  }
}
