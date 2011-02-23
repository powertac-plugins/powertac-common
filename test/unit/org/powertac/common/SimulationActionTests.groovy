package org.powertac.common

import grails.test.*
import org.joda.time.Instant

class SimulationActionTests extends GrailsUnitTestCase
{
  protected void setUp()
  {
    super.setUp()
  }

  protected void tearDown() 
  {
    super.tearDown()
  }

  void testCreateDummy () 
  {
    SimulationAction sa = new SimulationAction(atTime: new Instant(10000), action: 42)
    assertNotNull("Not null", sa)
    assertEquals("correct time", 10000, sa.atTime.millis)
    assertEquals("correct content", 42, sa.action)
  }
  
  void testCreateAction ()
  {
    def var = 0
    SimulationAction sa = new SimulationAction(atTime: new Instant(20000), action: { var = 1 })
    assertNotNull("Not null", sa)
    assertEquals("correct time", 20000, sa.atTime.millis)
    sa.action.call()
    assertEquals("correct action", 1, var)
  }
}
