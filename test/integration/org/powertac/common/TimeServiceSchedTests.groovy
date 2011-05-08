/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.powertac.common

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

import grails.test.*

/**
 * Scheduling tests for the TimeService 
 * @author John Collins
 */
class TimeServiceSchedTests extends GroovyTestCase
{
  def clockDriveJob    // import the clock-drive job
  def quartzScheduler  // we also need the job scheduler
  
  DateTime theBase
  DateTime theStart
  int theRate
  int theMod
  def timeService

  protected void setUp() 
  {
    super.setUp()
    quartzScheduler.start()
    theBase = new DateTime(2007, 6, 21, 12, 0, 0, 0, DateTimeZone.UTC)
    theStart = new DateTime(DateTimeZone.UTC)
    theRate = 360
    theMod = 15*60*1000
    timeService.base = theBase.millis
    timeService.start = theStart.millis
    timeService.rate = theRate
    timeService.modulo = theMod
    timeService.updateTime()
  }

  protected void tearDown() 
  {
    super.tearDown()
  }

  void testSimpleRepeat() 
  {
    Object monitor = new Object() // dummy object for synchronization
    def times = []
    def interval = 15 * 60 * 1000 // one 15-minute tick
    def action = { synchronized(monitor){ times.add(timeService.currentTime)
                                          println "Action: time = ${timeService.currentTime} times length = ${times.size()}"
                                          monitor.notifyAll() } }
    def add
    add = { timeService.addAction(timeService.currentTime.plus(interval), { action(); add() }) }
    add()

    ClockDriveJob.schedule(2500l, 3) // run 3 times, including once immediately
    
    synchronized(monitor) {
      monitor.wait()
    }
    // should wake up after the first action
    assertEquals("one event", 1, times.size())
    assertEquals("15 min after base", theBase.millis + interval, times[0].millis)
    
    synchronized(monitor) {
      monitor.wait()
    }
    // should wake up after the second action
    assertEquals("two events", 2, times.size())
    assertEquals("30 min after base", theBase.millis + interval * 2, times[1].millis)
    
    synchronized(monitor) {
      monitor.wait()
    }
    // should wake up after the third action
    assertEquals("three events", 3, times.size())
    assertEquals("30 min after base", theBase.millis + interval * 3, times[2].millis)
  }
}
