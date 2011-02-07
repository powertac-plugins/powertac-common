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

//import org.powertac.common.Timeslot


import grails.test.GrailsUnitTestCase
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class RateTests extends GrailsUnitTestCase
{

  def timeService

  protected void setUp() 
  {
    super.setUp()
    timeService = new TimeService()
    timeService.setCurrentTime(new DateTime())
    registerMetaClass(Rate)
    Rate.metaClass.getTimeService = {-> return timeService}
  }

  protected void tearDown() 
  {
    super.tearDown()
  }

  void testFixedRate() 
  {
    //timeSv.demand.getCurrentTime { -> return new DateTime(2011,1,10,0,0,0,0,DateTimeZone.UTC) }
    timeService.setCurrentTime(new DateTime(2011,1,10,0,0,0,0,DateTimeZone.UTC))
    Rate r = new Rate(value: 0.121)

    assertNotNull("Rate not null", r)
    assertTrue("Rate is fixed", r.isFixed)
    assertEquals("Correct fixed rate", r.value, 0.121)
    assertEquals("Correct notice interval", r.noticeInterval, 0)
  }
  
  // Test a rate that applies between 6:00 and 8:00
  void testDailyRate()
  {
    timeService.setCurrentTime(new DateTime(2011,1,10,5,0, 0, 0, DateTimeZone.UTC))
    Rate r = new Rate(value: 0.121,
                      dailyBegin: new DateTime(2011, 1, 1, 6, 0, 0, 0, DateTimeZone.UTC),
                      dailyEnd: new DateTime(2011, 1, 1, 8, 0, 0, 0, DateTimeZone.UTC))

    assertTrue("Rate is fixed", r.isFixed)
    assertFalse("Does not apply now", r.applies())
    assertTrue("Applies at 6:00", r.applies(new DateTime(2012, 2, 2, 6, 0, 0, 0, DateTimeZone.UTC)))
    assertTrue("Applies at 7:59", r.applies(new DateTime(2012, 2, 3, 7, 59, 0, 0, DateTimeZone.UTC)))
    assertFalse("Does not apply at 9:00", r.applies(new DateTime(2012, 3, 3, 9, 0, 0, 0, DateTimeZone.UTC)))
    assertFalse("Does not apply at 8:00", r.applies(new DateTime(2012, 1, 3, 8, 0, 0, 0, DateTimeZone.UTC)))
  }
  
  // Test a rate that applies between 22:00 and 5:00
  void testDailyRateOverMidnight()
  {
    timeService.setCurrentTime(new DateTime(2011,1,10,21,0,0,0,DateTimeZone.UTC))
    Rate r = new Rate(value: 0.121, 
                      dailyBegin: new DateTime(2011, 1, 1, 22, 0, 0, 0, DateTimeZone.UTC),
                      dailyEnd: new DateTime(2011, 1, 2, 5, 0, 0, 0, DateTimeZone.UTC))

    assertTrue("Rate is fixed", r.isFixed)
    assertFalse("Does not apply now", r.applies())
    assertTrue("Applies at 22:00", r.applies(new DateTime(2012, 2, 2, 22, 0, 0, 0, DateTimeZone.UTC)))
    assertTrue("Applies at 4:59", r.applies(new DateTime(2012, 2, 3, 4, 59, 0, 0, DateTimeZone.UTC)))
    assertFalse("Does not apply at 6:00", r.applies(new DateTime(2012, 3, 3, 6, 0, 0, 0, DateTimeZone.UTC)))
    assertFalse("Does not apply at 5:00", r.applies(new DateTime(2012, 1, 3, 5, 0, 0, 0, DateTimeZone.UTC)))
  }
  
  // Test a weekly rate that applies on Saturday and Sunday (days 6 and 7)
  void testWeeklyRateWeekend()
  {
    Rate r = new Rate(value: 0.121,
                      weeklyBegin: new DateTime(2011, 1, 15, 22, 0, 0, 0, DateTimeZone.UTC),
                      weeklyEnd: new DateTime(2011, 1, 16, 5, 0, 0, 0, DateTimeZone.UTC))

    assertTrue("Rate is fixed", r.isFixed)
    assertFalse("Does not apply on Friday", r.applies(new DateTime(2011, 1, 21, 22, 0, 0, 0, DateTimeZone.UTC)))
    assertTrue("Applies on Saturday", r.applies(new DateTime(2011, 1, 22, 22, 0, 0, 0, DateTimeZone.UTC)))
    assertTrue("Applies on Sunday", r.applies(new DateTime(2011, 1, 23, 4, 59, 0, 0, DateTimeZone.UTC)))
    assertFalse("Does not apply on Monday", r.applies(new DateTime(2011, 1, 24, 6, 0, 0, 0, DateTimeZone.UTC)))
  }
  
  // Test a weekly rate that applies on Sunday only (day 7)
  void testWeeklyRateSun()
  {
    Rate r = new Rate(value: 0.121,
                      weeklyBegin: new DateTime(2011, 1, 16, 22, 0, 0, 0, DateTimeZone.UTC))

    assertTrue("Rate is fixed", r.isFixed)
    assertFalse("Does not apply on Friday", r.applies(new DateTime(2011, 1, 21, 22, 0, 0, 0, DateTimeZone.UTC)))
    assertFalse("Does not apply on Saturday", r.applies(new DateTime(2011, 1, 22, 22, 0, 0, 0, DateTimeZone.UTC)))
    assertTrue("Applies on Sunday", r.applies(new DateTime(2011, 1, 23, 4, 59, 0, 0, DateTimeZone.UTC)))
    assertFalse("Does not apply on Monday", r.applies(new DateTime(2011, 1, 24, 6, 0, 0, 0, DateTimeZone.UTC)))
  }

  // Test a weekly rate that applies on Saturday and Sunday (days 6 and 7)
  void testWeeklyRateSunMon()
  {
    Rate r = new Rate(value: 0.121,
                      weeklyBegin: new DateTime(2011, 1, 16, 22, 0, 0, 0, DateTimeZone.UTC),
                      weeklyEnd: new DateTime(2011, 1, 17, 5, 0, 0, 0, DateTimeZone.UTC))

    assertTrue("Rate is fixed", r.isFixed)
    assertFalse("Does not apply on Saturday", r.applies(new DateTime(2011, 1, 22, 22, 0, 0, 0, DateTimeZone.UTC)))
    assertTrue("Applies on Sunday", r.applies(new DateTime(2011, 1, 23, 4, 59, 0, 0, DateTimeZone.UTC)))
    assertTrue("Applies on Monday", r.applies(new DateTime(2011, 1, 24, 4, 59, 0, 0, DateTimeZone.UTC)))
    assertFalse("Does not apply on Tuesday", r.applies(new DateTime(2011, 1, 25, 6, 0, 0, 0, DateTimeZone.UTC)))
  }
  
  // Test a rate that applies only during the day on weekdays
  void testWeekdayRate()
  {
    Rate r = new Rate(value: 0.121,
                      weeklyBegin: new DateTime(2011, 1, 10, 2, 0, 0, 0, DateTimeZone.UTC), //Monday
                      weeklyEnd: new DateTime(2011, 1, 14, 5, 0, 0, 0, DateTimeZone.UTC),   //Friday
                      dailyBegin: new DateTime(2011, 1, 14, 8, 0, 0, 0, DateTimeZone.UTC),
                      dailyEnd: new DateTime(2011, 1, 14, 17, 0, 0, 0, DateTimeZone.UTC))
    assertFalse("Does not apply on Saturday", r.applies(new DateTime(2011, 1, 22, 12, 0, 0, 0, DateTimeZone.UTC)))
    assertFalse("Does not apply on Sunday", r.applies(new DateTime(2011, 1, 30, 12, 0, 0, 0, DateTimeZone.UTC)))
    assertFalse("Does not apply Mon morning", r.applies(new DateTime(2011, 1, 31, 7, 59, 0, 0, DateTimeZone.UTC)))
    assertTrue("Starts Mon 8:00", r.applies(new DateTime(2011, 1, 31, 8, 0, 0, 0, DateTimeZone.UTC)))
    assertTrue("Applies Mon 16:59", r.applies(new DateTime(2011, 1, 31, 16, 59, 0, 0, DateTimeZone.UTC)))
    assertFalse("Does not apply Mon 17:00", r.applies(new DateTime(2011, 1, 31, 17, 0, 0, 0, DateTimeZone.UTC)))
    assertTrue("Applies Fri 16:59", r.applies(new DateTime(2011, 1, 28, 16, 59, 0, 0, DateTimeZone.UTC)))
  }
  
  // Test a rate that applies between 6:00 and 8:00,
  // tierThreshold = 0
  void testDailyRateT0()
  {
    timeService.setCurrentTime(new DateTime(2011,1,10,7,0, 0, 0, DateTimeZone.UTC))
    Rate r = new Rate(value: 0.121, 
                      dailyBegin: new DateTime(2011, 1, 1, 6, 0, 0, 0, DateTimeZone.UTC),
                      dailyEnd: new DateTime(2011, 1, 1, 8, 0, 0, 0, DateTimeZone.UTC))

    assertTrue("Applies now", r.applies(200.0))
    assertTrue("Applies at 6:00", r.applies(1.0, new DateTime(2012, 2, 2, 6, 0, 0, 0, DateTimeZone.UTC)))
    assertTrue("Applies at 7:59", r.applies(2.0, new DateTime(2012, 2, 3, 7, 59, 0, 0, DateTimeZone.UTC)))
    assertFalse("Does not apply at 9:00", r.applies(2.0, new DateTime(2012, 3, 3, 9, 0, 0, 0, DateTimeZone.UTC)))
  }
  
  // Test a rate that applies between 6:00 and 8:00,
  // tierThreshold = 100
  void testDailyRateT1()
  {
    timeService.setCurrentTime(new DateTime(2011,1,10,7,0, 0, 0, DateTimeZone.UTC))
    Rate r = new Rate(value: 0.121, 
                      dailyBegin: new DateTime(2011, 1, 1, 6, 0, 0, 0, DateTimeZone.UTC),
                      dailyEnd: new DateTime(2011, 1, 1, 8, 0, 0, 0, DateTimeZone.UTC),
                      tierThreshold: 100.0)

    assertFalse("Does not apply at 99", r.applies(99.0))
    assertTrue("Applies at 6:00, 100", r.applies(100.0, new DateTime(2012, 2, 2, 6, 0, 0, 0, DateTimeZone.UTC)))
    assertFalse("Does not apply at 7:00, 80", r.applies(80.0, new DateTime(2012, 3, 3, 7, 0, 0, 0, DateTimeZone.UTC)))
  }
}
