package org.powertac.common

import groovy.util.GroovyTestCase;
import org.joda.time.Duration
import org.joda.time.LocalDateTime

class VariableRateTests extends GroovyTestCase
{
  // get ref to TimeService
  def timeService
  
  protected void setUp()
  {
    super.setUp()
    timeService.updateTime(new LocalDateTime(2011, 1, 26, 12, 0))
  }
  
  protected void tearDown()
  {
    super.tearDown()
  }
  
  void testFixedRate ()
  {
    Tariff t1 = new Tariff(expiration: new LocalDateTime(2011, 3, 1, 12, 0),
                           minDuration: new Duration(1000l*60*60*25*30))
    Rate r1 = new Rate(value: 0.121)
    t1.addToRates(r1)
    if (!t1.save()) {
      t1.errors.each { println it }
      fail("Could not save Tariff")
    }
    r1.setTariff(t1)
    assertTrue("Validate", r1.validate())
    assertNotNull("Save", r1.save())
    assertTrue("Rate is fixed", r1.isFixed)
    assertEquals("Correct fixed rate", 0.121, r1.getValue())
  }

  //variable rates, minimal setup  
  void testVariableRate ()
  {
    Tariff t1 = new Tariff(expiration: new LocalDateTime(2011, 3, 1, 12, 0),
                           minDuration: new Duration(1000l*60*60*25*30))
    Rate r1 = new Rate(isFixed: false, minValue: 0.05, maxValue: 0.50,
                       noticeInterval: 0, expectedMean: 0.10)
    t1.addToRates(r1)
    if (!t1.save()) {
      t1.errors.each { println it }
      fail("Could not save Tariff")
    }
    r1.setTariff(t1)
    r1.addToRateHistory(new HourlyCharge(value: 0.08, when: new LocalDateTime(2011, 1, 26, 12, 0)))
    r1.addToRateHistory(new HourlyCharge(value: 0.07, when: new LocalDateTime(2011, 1, 26, 11, 0)))
    assertTrue("Validate", r1.validate())
    assertNotNull("Save", r1.save())
    assertFalse("Rate is variable", r1.isFixed)
    assertEquals("Correct rate at 11:00", 0.07, r1.getValue(new LocalDateTime(2011, 1, 26, 11, 0)))
    assertEquals("Correct rate at 12:00", 0.08, r1.getValue(new LocalDateTime(2011, 1, 26, 12, 0)))
    assertEquals("Default rate at 10:00", 0.10, r1.getValue(new LocalDateTime(2011, 1, 26, 10, 0)))
    assertEquals("Default rate at 13:00", 0.10, r1.getValue(new LocalDateTime(2011, 1, 26, 13, 0)))
  }
  
  // variable rates with gaps
  void testVariableRateGap ()
  {
    Tariff t1 = new Tariff(expiration: new LocalDateTime(2011, 3, 1, 12, 0),
                           minDuration: new Duration(1000l*60*60*25*30))
    Rate r1 = new Rate(isFixed: false, minValue: 0.05, maxValue: 0.50,
                       noticeInterval: 0, expectedMean: 0.10)
    t1.addToRates(r1)
    if (!t1.save()) {
      t1.errors.each { println it }
      fail("Could not save Tariff")
    }
    r1.setTariff(t1)
    r1.addToRateHistory(new HourlyCharge(value: 0.08, when: new LocalDateTime(2011, 1, 26, 12, 0)))
    r1.addToRateHistory(new HourlyCharge(value: 0.07, when: new LocalDateTime(2011, 1, 26, 11, 0)))
    r1.addToRateHistory(new HourlyCharge(value: 0.06, when: new LocalDateTime(2011, 1, 26, 9, 0)))
    assertTrue("Validate", r1.validate())
    assertNotNull("Save", r1.save())
    assertFalse("Rate is variable", r1.isFixed)
    assertEquals("Correct rate at 9:00", 0.06, r1.getValue(new LocalDateTime(2011, 1, 26, 9, 0)))
    assertEquals("Correct rate at 10:00", 0.06, r1.getValue(new LocalDateTime(2011, 1, 26, 10, 0)))
    assertEquals("Correct rate at 11:00", 0.07, r1.getValue(new LocalDateTime(2011, 1, 26, 11, 0)))
    assertEquals("Correct rate at 12:00", 0.08, r1.getValue(new LocalDateTime(2011, 1, 26, 12, 0)))
    assertEquals("Default rate at 8:00", 0.10, r1.getValue(new LocalDateTime(2011, 1, 26, 8, 0)))
    assertEquals("Default rate at 13:00", 0.10, r1.getValue(new LocalDateTime(2011, 1, 26, 13, 0)))
  }
  
  // variable rates with 3-hour notification interval
  void testVariableRateN3 ()
  {
    Tariff t1 = new Tariff(expiration: new LocalDateTime(2011, 3, 1, 12, 0),
                           minDuration: new Duration(1000l*60*60*25*30))
    Rate r1 = new Rate(isFixed: false, minValue: 0.05, maxValue: 0.50,
                       noticeInterval: 3, expectedMean: 0.10)
    t1.addToRates(r1)
    if (!t1.save()) {
      t1.errors.each { println it }
      fail("Could not save Tariff")
    }
    r1.setTariff(t1)
    r1.addToRateHistory(new HourlyCharge(value: 0.06, when: new LocalDateTime(2011, 1, 26, 9, 0)))
    r1.addToRateHistory(new HourlyCharge(value: 0.07, when: new LocalDateTime(2011, 1, 26, 11, 0)))
    r1.addToRateHistory(new HourlyCharge(value: 0.08, when: new LocalDateTime(2011, 1, 26, 12, 0)))
    r1.addToRateHistory(new HourlyCharge(value: 0.09, when: new LocalDateTime(2011, 1, 26, 13, 0)))
    r1.addToRateHistory(new HourlyCharge(value: 0.11, when: new LocalDateTime(2011, 1, 26, 14, 0)))
    r1.addToRateHistory(new HourlyCharge(value: 0.12, when: new LocalDateTime(2011, 1, 26, 15, 0)))
    assertTrue("Validate", r1.validate())
    assertNotNull("Save", r1.save())
    assertFalse("Rate is variable", r1.isFixed)
    assertEquals("Default rate at 8:00", 0.10, r1.getValue(new LocalDateTime(2011, 1, 26, 8, 0)))
    assertEquals("Correct rate at 9:00", 0.06, r1.getValue(new LocalDateTime(2011, 1, 26, 9, 0)))
    assertEquals("Correct rate at 10:00", 0.06, r1.getValue(new LocalDateTime(2011, 1, 26, 10, 0)))
    assertEquals("Correct rate at 11:00", 0.07, r1.getValue(new LocalDateTime(2011, 1, 26, 11, 0)))
    assertEquals("Correct rate at 12:00", 0.08, r1.getValue(new LocalDateTime(2011, 1, 26, 12, 0)))
    assertEquals("Correct rate at 13:00", 0.09, r1.getValue(new LocalDateTime(2011, 1, 26, 13, 0)))
    assertEquals("Correct rate at 14:00", 0.11, r1.getValue(new LocalDateTime(2011, 1, 26, 14, 0)))
    assertEquals("Correct rate at 15:00", 0.12, r1.getValue(new LocalDateTime(2011, 1, 26, 15, 0)))
    assertEquals("Default rate at 16:00", 0.10, r1.getValue(new LocalDateTime(2011, 1, 26, 16, 0)))
  }
}
