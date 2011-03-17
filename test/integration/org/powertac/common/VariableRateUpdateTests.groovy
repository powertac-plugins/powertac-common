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
import org.powertac.common.msg.VariableRateUpdate

class VariableRateUpdateTests extends GrailsUnitTestCase 
{
  // get ref to TimeService
  def timeService
  def broker

    protected void setUp() {
    super.setUp()
    broker = new Broker (username: 'testBroker', password: 'testPassword')
    assert broker.save()
    timeService.setCurrentTime(new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC))
  }

  protected void tearDown() {
    super.tearDown()
  }

  // simple correct addition of an hourly rate
  void testZero() {
    TariffSpecification t1 =
          new TariffSpecification(brokerId: broker.id, expiration: new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant(),
                                  minDuration: TimeService.WEEK * 4)
    Rate r1 = new Rate(isFixed: false, minValue: 0.05, maxValue: 0.50,
                       noticeInterval: 0, expectedMean: 0.10)
    t1.addToRates(r1)
    if (!t1.save()) {
      t1.errors.each { println it.toString() }
      fail("Could not save TariffSpecification")
    }
    r1.addToRateHistory(new HourlyCharge(value: 0.07, atTime: new DateTime(2011, 1, 26, 11, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    assertEquals("Initial rate at 11:00", 0.07, r1.getValue(new DateTime(2011, 1, 26, 11, 0, 0, 0, DateTimeZone.UTC)))
    assert r1.validate()
    assert r1.save()
    Tariff tf = new Tariff(tariffSpec: t1)
    tf.init()
    if (!tf.save()) {
      tf.errors.each { println it.toString() }
      fail("Could not save Tariff")
    }
    HourlyCharge hc = new HourlyCharge(value: 0.09, atTime: new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC).toInstant())
    if (!hc.save()) {
      hc.errors.each { println it.toString() }
      fail("Could not save VariableRateUpdate")
    }
    VariableRateUpdate vru = new VariableRateUpdate(payload: hc, brokerId: broker.id, tariffId: tf.id, rateId: r1.id)
    if (!vru.save()) {
      vru.errors.each { println it.toString() }
      fail("Could not save VariableRateUpdate")
    }
    assertTrue("Successful add", Tariff.get(vru.tariffId).addHourlyCharge(vru.payload, vru.rateId))
    assertEquals("tariff", tf, Tariff.get(vru.tariffId))
    assertEquals("rate", r1, Rate.get(vru.rateId))
    assertEquals("Correct rate at 11:00", 0.07, r1.getValue(new DateTime(2011, 1, 26, 11, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Correct rate at 12:00", 0.09, r1.getValue(new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC)))
  }
  
  // attempt to add hourly rate in the past.
  void testPast() {
    TariffSpecification t1 =
          new TariffSpecification(brokerId: broker.id, expiration: new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant(),
                                  minDuration: TimeService.WEEK * 4)
    Rate r1 = new Rate(isFixed: false, minValue: 0.05, maxValue: 0.50,
                       noticeInterval: 0, expectedMean: 0.10)
    t1.addToRates(r1)
    if (!t1.save()) {
      t1.errors.each { println it.toString() }
      fail("Could not save TariffSpecification")
    }
    r1.addToRateHistory(new HourlyCharge(value: 0.07, atTime: new DateTime(2011, 1, 26, 11, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    assertEquals("Initial rate at 11:00", 0.07, r1.getValue(new DateTime(2011, 1, 26, 11, 0, 0, 0, DateTimeZone.UTC)))
    assert r1.validate()
    assert r1.save()
    Tariff tf = new Tariff(tariffSpec: t1)
    tf.init()
    if (!tf.save()) {
      tf.errors.each { println it.toString() }
      fail("Could not save Tariff")
    }
    HourlyCharge hc = new HourlyCharge(value: 0.09, atTime: new DateTime(2011, 1, 26, 10, 0, 0, 0, DateTimeZone.UTC).toInstant())
    if (!hc.save()) {
      hc.errors.each { println it.toString() }
      fail("Could not save VariableRateUpdate")
    }
    VariableRateUpdate vru = new VariableRateUpdate(payload: hc, brokerId: broker.id, tariffId: tf.id, rateId: r1.id)
    if (!vru.save()) {
      vru.errors.each { println it.toString() }
      fail("Could not save VariableRateUpdate")
    }
    assertFalse("Reject", Tariff.get(vru.tariffId).addHourlyCharge(vru.payload, vru.rateId))
  }

  // attempt to add future hourly rate, but not far enough in
  // the future.
  
  // overwrite an existing hourly rate
  void testOverwrite() {
    TariffSpecification t1 =
          new TariffSpecification(brokerId: broker.id, expiration: new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant(),
                                  minDuration: TimeService.WEEK * 4)
    Rate r1 = new Rate(isFixed: false, minValue: 0.05, maxValue: 0.50,
                       noticeInterval: 0, expectedMean: 0.10)
    t1.addToRates(r1)
    if (!t1.save()) {
      t1.errors.each { println it.toString() }
      fail("Could not save TariffSpecification")
    }
    r1.addToRateHistory(new HourlyCharge(value: 0.07, atTime: new DateTime(2011, 1, 26, 11, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    r1.addToRateHistory(new HourlyCharge(value: 0.08, atTime: new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    assertEquals("Initial rate at 11:00", 0.07, r1.getValue(new DateTime(2011, 1, 26, 11, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Initial rate at 12:00", 0.08, r1.getValue(new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC)))
    assert r1.validate()
    assert r1.save()
    Tariff tf = new Tariff(tariffSpec: t1)
    tf.init()
    if (!tf.save()) {
      tf.errors.each { println it.toString() }
      fail("Could not save Tariff")
    }
    HourlyCharge hc = new HourlyCharge(value: 0.09, atTime: new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC).toInstant())
    if (!hc.save()) {
      hc.errors.each { println it.toString() }
      fail("Could not save VariableRateUpdate")
    }
    VariableRateUpdate vru = new VariableRateUpdate(payload: hc, brokerId: broker.id, tariffId: tf.id, rateId: r1.id)
    if (!vru.save()) {
      vru.errors.each { println it.toString() }
      fail("Could not save VariableRateUpdate")
    }
    assertTrue("Successful add", Tariff.get(vru.tariffId).addHourlyCharge(vru.payload, vru.rateId))
    assertEquals("tariff", tf, Tariff.get(vru.tariffId))
    assertEquals("rate", r1, Rate.get(vru.rateId))
    assertEquals("Correct rate at 11:00", 0.07, r1.getValue(new DateTime(2011, 1, 26, 11, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Correct rate at 12:00", 0.09, r1.getValue(new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC)))
  }  
}
