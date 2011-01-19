package org.powertac.common.tariff

import grails.test.*
import org.joda.time.LocalDateTime
import org.powertac.common.TimeService

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
    def timeSv = mockFor(TimeService)
    timeSv.demand.getCurrentTime(1..1) { -> return new LocalDateTime(2011,1,10,0,0) }
    Rate r = new Rate (value:0.121)
    r.timeService = timeSv.createMock()
    
    assertNotNull("Rate not null", r)
    assertTrue("Rate is fixed", r.isFixed)
    assertEquals("Correct fixed rate", r.value, 0.121)
  }
}
