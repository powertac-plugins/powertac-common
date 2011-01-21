package org.powertac.common

import grails.test.GrailsUnitTestCase
import org.powertac.common.Competition
import org.powertac.common.Rate
//import org.powertac.common.Timeslot
import org.powertac.common.TimeService
import org.joda.time.LocalDateTime

class RateTests extends GrailsUnitTestCase 
{

  Competition competition
  def timeSv

  protected void setUp() 
  {
    super.setUp()
    timeSv = mockFor(TimeService)
    //Mock competition and timeslot methods so that Rate can make use of them
    //This won't work unless/until Timeslot returns the correct time
    //registerMetaClass(Competition)
    //Competition.metaClass.static.currentCompetition = {-> return new Competition(name: 'TestCompetition', enabled: true, current: true)}
    //registerMetaClass(Timeslot)
    //Timeslot.metaClass.static.currentTimeslot = {-> return new Timeslot(serialNumber: 0, current: true, enabled: true)}
  }

  protected void tearDown() 
  {
    super.tearDown()
  }

  void testFixedRate() 
  {
    Rate r = new Rate(value: 0.121, timeService: timeSv.createMock())
    
    timeSv.demand.getCurrentTime { -> return new LocalDateTime(2011,1,10,0,0) }
    assertNotNull("Rate not null", r)
    assertTrue("Rate is fixed", r.isFixed)
    assertEquals("Correct fixed rate", r.value, 0.121)
    assertEquals("Correct notice interval", r.noticeInterval, 0)
  }
  
  // Test a rate that applies between 6:00 and 8:00
  void testDailyRate()
  {
    Rate r = new Rate(value: 0.121, 
                      dailyBegin: new LocalDateTime(2011, 1, 1, 6, 0),
                      dailyEnd: new LocalDateTime(2011, 1, 1, 8, 0),
                      timeService: timeSv.createMock())

    timeSv.demand.getCurrentTime { -> return new LocalDateTime(2011,1,10,5,0) }
    assertTrue("Rate is fixed", r.isFixed)
    assertFalse("Does not apply now", r.applies())
    assertTrue("Applies at 6:00", r.applies(new LocalDateTime(2012, 2, 2, 6, 0)))
    assertTrue("Applies at 7:59", r.applies(new LocalDateTime(2012, 2, 3, 7, 59)))
    assertFalse("Does not apply at 9:00", r.applies(new LocalDateTime(2012, 3, 3, 9, 0)))
    assertFalse("Does not apply at 8:00", r.applies(new LocalDateTime(2012, 1, 3, 8, 0)))
  }
  
  // Test a rate that applies between 22:00 and 5:00
  void testDailyRateOverMidnight()
  {
    Rate r = new Rate(value: 0.121, 
                      dailyBegin: new LocalDateTime(2011, 1, 1, 22, 0),
                      dailyEnd: new LocalDateTime(2011, 1, 2, 5, 0),
                      timeService: timeSv.createMock())

    timeSv.demand.getCurrentTime { -> return new LocalDateTime(2011,1,10,21,0) }
    assertTrue("Rate is fixed", r.isFixed)
    assertFalse("Does not apply now", r.applies())
    assertTrue("Applies at 22:00", r.applies(new LocalDateTime(2012, 2, 2, 22, 0)))
    assertTrue("Applies at 4:59", r.applies(new LocalDateTime(2012, 2, 3, 4, 59)))
    assertFalse("Does not apply at 6:00", r.applies(new LocalDateTime(2012, 3, 3, 6, 0)))
    assertFalse("Does not apply at 5:00", r.applies(new LocalDateTime(2012, 1, 3, 5, 0)))
  }
  
  // Test a weekly rate that applies on Saturday and Sunday (days 6 and 7)
  void testWeeklyRateWeekend()
  {
    Rate r = new Rate(value: 0.121,
                      weeklyBegin: new LocalDateTime(2011, 1, 15, 22, 0),
                      weeklyEnd: new LocalDateTime(2011, 1, 16, 5, 0))

    assertTrue("Rate is fixed", r.isFixed)
    assertFalse("Does not apply on Friday", r.applies(new LocalDateTime(2011, 1, 21, 22, 0)))
    assertTrue("Applies on Saturday", r.applies(new LocalDateTime(2011, 1, 22, 22, 0)))
    assertTrue("Applies on Sunday", r.applies(new LocalDateTime(2011, 1, 23, 4, 59)))
    assertFalse("Does not apply on Monday", r.applies(new LocalDateTime(2011, 1, 24, 6, 0)))
  }
  
  // Test a weekly rate that applies on Sunday only (day 7)
  void testWeeklyRateSun()
  {
    Rate r = new Rate(value: 0.121,
                      weeklyBegin: new LocalDateTime(2011, 1, 16, 22, 0))

    assertTrue("Rate is fixed", r.isFixed)
    assertFalse("Does not apply on Friday", r.applies(new LocalDateTime(2011, 1, 21, 22, 0)))
    assertFalse("Does not apply on Saturday", r.applies(new LocalDateTime(2011, 1, 22, 22, 0)))
    assertTrue("Applies on Sunday", r.applies(new LocalDateTime(2011, 1, 23, 4, 59)))
    assertFalse("Does not apply on Monday", r.applies(new LocalDateTime(2011, 1, 24, 6, 0)))
  }

  // Test a weekly rate that applies on Saturday and Sunday (days 6 and 7)
  void testWeeklyRateSunMon()
  {
    Rate r = new Rate(value: 0.121,
                      weeklyBegin: new LocalDateTime(2011, 1, 16, 22, 0),
                      weeklyEnd: new LocalDateTime(2011, 1, 17, 5, 0))

    assertTrue("Rate is fixed", r.isFixed)
    assertFalse("Does not apply on Saturday", r.applies(new LocalDateTime(2011, 1, 22, 22, 0)))
    assertTrue("Applies on Sunday", r.applies(new LocalDateTime(2011, 1, 23, 4, 59)))
    assertTrue("Applies on Monday", r.applies(new LocalDateTime(2011, 1, 24, 4, 59)))
    assertFalse("Does not apply on Tuesday", r.applies(new LocalDateTime(2011, 1, 25, 6, 0)))
  }
  
  // Test a rate that applies only during the day on weekdays
  void testWeekdayRate()
  {
    Rate r = new Rate(value: 0.121,
                      weeklyBegin: new LocalDateTime(2011, 1, 10, 2, 0), //Monday
                      weeklyEnd: new LocalDateTime(2011, 1, 14, 5, 0),   //Friday
                      dailyBegin: new LocalDateTime(2011, 1, 14, 8, 0),
                      dailyEnd: new LocalDateTime(2011, 1, 14, 17, 0))
    assertFalse("Does not apply on Saturday", r.applies(new LocalDateTime(2011, 1, 22, 12, 0)))
    assertFalse("Does not apply on Sunday", r.applies(new LocalDateTime(2011, 1, 30, 12, 0)))
    assertFalse("Does not apply Mon morning", r.applies(new LocalDateTime(2011, 1, 31, 7, 59)))
    assertTrue("Starts Mon 8:00", r.applies(new LocalDateTime(2011, 1, 31, 8, 0)))
    assertTrue("Applies Mon 16:59", r.applies(new LocalDateTime(2011, 1, 31, 16, 59)))
    assertFalse("Does not apply Mon 17:00", r.applies(new LocalDateTime(2011, 1, 31, 17, 0)))
    assertTrue("Applies Fri 16:59", r.applies(new LocalDateTime(2011, 1, 28, 16, 59)))
  }
  
  // Test a rate that applies between 6:00 and 8:00,
  // tierThreshold = 0
  void testDailyRateT0()
  {
    Rate r = new Rate(value: 0.121, 
                      dailyBegin: new LocalDateTime(2011, 1, 1, 6, 0),
                      dailyEnd: new LocalDateTime(2011, 1, 1, 8, 0),
                      timeService: timeSv.createMock())

    timeSv.demand.getCurrentTime { -> return new LocalDateTime(2011,1,10,7,0) }
    assertTrue("Applies now", r.applies(200.0))
    assertTrue("Applies at 6:00", r.applies(1.0, new LocalDateTime(2012, 2, 2, 6, 0)))
    assertTrue("Applies at 7:59", r.applies(2.0, new LocalDateTime(2012, 2, 3, 7, 59)))
    assertFalse("Does not apply at 9:00", r.applies(2.0, new LocalDateTime(2012, 3, 3, 9, 0)))
  }
  
  // Test a rate that applies between 6:00 and 8:00,
  // tierThreshold = 100
  void testDailyRateT1()
  {
    Rate r = new Rate(value: 0.121, 
                      dailyBegin: new LocalDateTime(2011, 1, 1, 6, 0),
                      dailyEnd: new LocalDateTime(2011, 1, 1, 8, 0),
                      tierThreshold: 100.0,
                      timeService: timeSv.createMock())

    timeSv.demand.getCurrentTime { -> return new LocalDateTime(2011,1,10,7,0) }
    assertFalse("Does not apply at 99", r.applies(99.0))
    assertTrue("Applies at 6:00, 100", r.applies(100.0, new LocalDateTime(2012, 2, 2, 6, 0)))
    assertFalse("Does not apply at 7:00, 80", r.applies(80.0, new LocalDateTime(2012, 3, 3, 7, 0)))
  }
}
