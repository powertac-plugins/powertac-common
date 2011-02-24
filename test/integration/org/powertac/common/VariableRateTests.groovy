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

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant

/** 
 * Primarily tests Rate applicability logic.
 * @author John Collins 
 */
class VariableRateTests extends GroovyTestCase
{
  // get ref to TimeService
  def timeService
  def broker

  protected void setUp()
  {
    super.setUp()
    timeService.setCurrentTime(new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC))
  }
  
  protected void tearDown()
  {
    super.tearDown()
  }
  
  void testFixedRate ()
  {
    DateTime exp = new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC)

    TariffSpecification t1 =
        new TariffSpecification(brokerId: "abc", expiration: new Instant(exp),
                                minDuration: TimeService.WEEK * 4)
    Rate r1 = new Rate(value: 0.121)
    t1.addToRates(r1)
    if (!t1.save()) {
      t1.errors.each { println it }
      fail("Could not save Tariff")
    }
    assertTrue("Validate", r1.validate())
    assertNotNull("Save", r1.save())
    assertTrue("Rate is fixed", r1.isFixed)
    assertEquals("Correct fixed rate", 0.121, r1.getValue())
  }

  //variable rates, minimal setup  
  void testVariableRate ()
  {
    TariffSpecification t1 = 
        new TariffSpecification(brokerId: "def", expiration: new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant(),
                                minDuration: TimeService.WEEK * 4)
    Rate r1 = new Rate(isFixed: false, minValue: 0.05, maxValue: 0.50,
                       noticeInterval: 0, expectedMean: 0.10)
    t1.addToRates(r1)
    if (!t1.save()) {
      t1.errors.each { println it }
      fail("Could not save Tariff")
    }
    r1.addToRateHistory(new HourlyCharge(value: 0.08, atTime: new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    r1.addToRateHistory(new HourlyCharge(value: 0.07, atTime: new DateTime(2011, 1, 26, 11, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    assertTrue("Validate", r1.validate())
    assertNotNull("Save", r1.save())
    assertFalse("Rate is variable", r1.isFixed)
    assertEquals("Correct rate at 11:00", 0.07, r1.getValue(new DateTime(2011, 1, 26, 11, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Correct rate at 12:00", 0.08, r1.getValue(new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Default rate at 10:00", 0.10, r1.getValue(new DateTime(2011, 1, 26, 10, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Default rate at 13:00", 0.10, r1.getValue(new DateTime(2011, 1, 26, 13, 0, 0, 0, DateTimeZone.UTC)))
  }
  
  // variable rates with gaps
  void testVariableRateGap ()
  {
    DateTime exp = new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC)
    TariffSpecification t1 = 
        new TariffSpecification(brokerId: "123", expiration: new Instant(exp),
                                minDuration: TimeService.WEEK * 4)
    Rate r1 = new Rate(isFixed: false, minValue: 0.05, maxValue: 0.50,
                       noticeInterval: 0, expectedMean: 0.10)
    t1.addToRates(r1)
    if (!t1.save()) {
      t1.errors.each { println it }
      fail("Could not save Tariff")
    }
    r1.addToRateHistory(new HourlyCharge(value: 0.08, atTime: new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    r1.addToRateHistory(new HourlyCharge(value: 0.07, atTime: new DateTime(2011, 1, 26, 11, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    r1.addToRateHistory(new HourlyCharge(value: 0.06, atTime: new DateTime(2011, 1, 26, 9, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    assertTrue("Validate", r1.validate())
    assertNotNull("Save", r1.save())
    assertFalse("Rate is variable", r1.isFixed)
    assertEquals("Correct rate at 9:00", 0.06, r1.getValue(new DateTime(2011, 1, 26, 9, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Correct rate at 10:00", 0.06, r1.getValue(new DateTime(2011, 1, 26, 10, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Correct rate at 11:00", 0.07, r1.getValue(new DateTime(2011, 1, 26, 11, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Correct rate at 12:00", 0.08, r1.getValue(new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Default rate at 8:00", 0.10, r1.getValue(new DateTime(2011, 1, 26, 8, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Default rate at 13:00", 0.10, r1.getValue(new DateTime(2011, 1, 26, 13, 0, 0, 0, DateTimeZone.UTC)))
  }
  
  // variable rates with 3-hour notification interval
  void testVariableRateN3 ()
  {
    TariffSpecification t1 = 
        new TariffSpecification(brokerId: "456", expiration: new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant(),
                                minDuration: TimeService.WEEK * 4)
    Rate r1 = new Rate(isFixed: false, minValue: 0.05, maxValue: 0.50,
                       noticeInterval: 3, expectedMean: 0.10)
    t1.addToRates(r1)
    if (!t1.save()) {
      t1.errors.each { println it }
      fail("Could not save Tariff")
    }
    r1.addToRateHistory(new HourlyCharge(value: 0.06, atTime: new DateTime(2011, 1, 26, 9, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    r1.addToRateHistory(new HourlyCharge(value: 0.07, atTime: new DateTime(2011, 1, 26, 11, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    r1.addToRateHistory(new HourlyCharge(value: 0.08, atTime: new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    r1.addToRateHistory(new HourlyCharge(value: 0.09, atTime: new DateTime(2011, 1, 26, 13, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    r1.addToRateHistory(new HourlyCharge(value: 0.11, atTime: new DateTime(2011, 1, 26, 14, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    r1.addToRateHistory(new HourlyCharge(value: 0.12, atTime: new DateTime(2011, 1, 26, 15, 0, 0, 0, DateTimeZone.UTC).toInstant()))
    assertTrue("Validate", r1.validate())
    assertNotNull("Save", r1.save())
    assertFalse("Rate is variable", r1.isFixed)
    assertEquals("Default rate at 8:00", 0.10, r1.getValue(new DateTime(2011, 1, 26, 8, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Correct rate at 9:00", 0.06, r1.getValue(new DateTime(2011, 1, 26, 9, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Correct rate at 10:00", 0.06, r1.getValue(new DateTime(2011, 1, 26, 10, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Correct rate at 11:00", 0.07, r1.getValue(new DateTime(2011, 1, 26, 11, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Correct rate at 12:00", 0.08, r1.getValue(new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Correct rate at 13:00", 0.09, r1.getValue(new DateTime(2011, 1, 26, 13, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Correct rate at 14:00", 0.11, r1.getValue(new DateTime(2011, 1, 26, 14, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Correct rate at 15:00", 0.12, r1.getValue(new DateTime(2011, 1, 26, 15, 0, 0, 0, DateTimeZone.UTC)))
    assertEquals("Default rate at 16:00", 0.10, r1.getValue(new DateTime(2011, 1, 26, 16, 0, 0, 0, DateTimeZone.UTC)))
  }
}
