package org.powertac.common

import grails.test.*
import org.joda.time.Instant
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.powertac.common.HourlyCharge

class HourlyChargeTests extends GrailsUnitTestCase 
{
  protected void setUp() 
  {
    super.setUp()
  }

  protected void tearDown() 
  {
    super.tearDown()
  }

  void testCreate() 
  {
    def tm = new DateTime(2011, 1, 18, 12, 0, 0, 0, DateTimeZone.UTC)
    def hc = new HourlyCharge(value: 42, atTime: tm.toInstant())
    assertNotNull("object created", hc)
    assertEquals("correct value", 42, hc.getValue())
    assertEquals("correct hour", 12, new DateTime(hc.getAtTime(), DateTimeZone.UTC).getHourOfDay())
  }
}
