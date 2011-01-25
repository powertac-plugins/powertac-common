
package org.powertac.common

import grails.test.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class TimeServiceTests extends GrailsUnitTestCase 
{
  DateTime theBase
  DateTime theStart
  int theRate
  int theMod
  TimeService ts
  
  protected void setUp() 
  {
    super.setUp()
    theBase = new DateTime(2008, 6, 21, 12, 0, 0, 0, DateTimeZone.UTC)
    theStart = new DateTime(DateTimeZone.UTC)
    theRate = 360
    theMod = 15*60*1000
    ts = new TimeService(base: theBase.millis,
                         start: theStart.millis,
                         rate: theRate,
                         modulo: theMod)
    ts.updateTime()
  }

  protected void tearDown() 
  {
    super.tearDown()
  }

  // set base, start, rate and test, check initial time
  void testTimeConversion() 
  {
    long offset = ts.currentTime.millis - theBase.millis
    assertEquals("${offset} zero", 0, offset)
    //assertTrue("${offset} close to base time", offset < 60*1000) // less than one minute has elapsed
  }

  // set base, start, rate and test, check time after delay
  void testTimePass() 
  {
    Thread.sleep(5000) // 5 seconds
    ts.updateTime()
    long offset = ts.currentTime.millis - theBase.millis
    assertEquals("${offset} is 30 min", 30*60*1000, offset)
    //assertTrue("${offset} less than 31 min", offset < 31*60*1000)
  }
  
  // single action, already due
  void testSingleActionDue()
  {
    def var = 0
    ts.addAction(theBase.toInstant(), { var = 1 })
    ts.updateTime()
    assertEquals("var got set to 1", 1, var)
  }
  
  // single action, in the future
  void testSingleActionFuture()
  {
    def var = 0
    ts.addAction(theBase.toInstant().plus(15*60*1000), { var = 2 })
    ts.updateTime() // not yet
    assertEquals("var unchanged", 0, var)
    Thread.sleep(3000) // 3 seconds -> 18 min sim time
    ts.updateTime()
    assertEquals("var changed", 2, var)
    long offset = ts.currentTime.millis - theBase.millis
    assertEquals("${offset} is 15 min", 15*60*1000, offset)
  }
  
  // simple repeated action
  void testRepeatedActionFuture()
  {
    def var = 0
    def actionCount = 0
    def interval = 15 * 60 * 1000 // one 15-minute tick
    def action = { actionCount += 1; var = 3 * actionCount }
    def add
    add = { ts.addAction(ts.currentTime.plus(interval), { action(); add() }) }
    add()
    ts.updateTime() // not yet
    assertEquals("var unchanged", 0, var)
    Thread.sleep(2500) // 2.5 seconds -> 15 min sim time
    ts.updateTime()
    assertEquals("var changed", 3, var)
    assertEquals("actionCount=1", 1, actionCount)
    Thread.sleep(1000) // 1 second -> 6 min sim time
    assertEquals("var not changed", 3, var)
    assertEquals("actionCount=1", 1, actionCount)
    Thread.sleep(1500) // 1.5 seconds -> 9 min sim time
    ts.updateTime()
    assertEquals("var changed", 6, var)
    assertEquals("actionCount=2", 2, actionCount)
    Thread.sleep(2500) // 2.5 seconds -> 15 min sim time
    ts.updateTime()
    assertEquals("var changed", 9, var)
    assertEquals("actionCount=3", 3, actionCount)
  }
}
