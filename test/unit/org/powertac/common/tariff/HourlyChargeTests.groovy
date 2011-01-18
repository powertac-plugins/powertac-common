package org.powertac.common.tariff

import grails.test.*
import org.joda.time.LocalDateTime

class HourlyChargeTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testCreate() {
    def tm = new LocalDateTime(2011, 1, 18, 12, 0)
    def hc = new HourlyCharge(value: 42, when: tm)
    assertNotNull("object created", hc)
    assertEquals("correct value", hc.getValue(), 42)
    assertEquals("correct hour", hc.getWhen().getHourOfDay(), 12)
  }
}
