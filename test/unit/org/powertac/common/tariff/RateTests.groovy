package org.powertac.common.tariff

import grails.test.*

class RateTests extends GrailsUnitTestCase {
  protected void setUp()
  {
    super.setUp()
  }

  protected void tearDown() 
  {
    super.tearDown()
  }

  void testFixedRate() 
  {
    Rate r = new Rate (value:0.121)
    assertNotNull("Rate not null", r)
    assertTrue("Rate is fixed", r.isFixed)
    assertEquals("Correct fixed rate", r.value, 0.121)
  }
}
