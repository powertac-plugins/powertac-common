package org.powertac.common

import grails.test.GrailsUnitTestCase
import org.powertac.common.Competition
import org.powertac.common.Rate
import org.powertac.common.Timeslot

class RateTests extends GrailsUnitTestCase {

  Competition competition
  Timeslot timeslot

  protected void setUp() {
    super.setUp()
    //Mock competition and timeslot methods so that Rate can make use of them
    registerMetaClass(Competition)
    Competition.metaClass.static.currentCompetition = {-> return new Competition(name: 'TestCompetition', enabled: true, current: true)}

    registerMetaClass(Timeslot)
    Timeslot.metaClass.static.currentTimeslot = {-> return new Timeslot(serialNumber: 0, current: true, enabled: true)}
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testFixedRate() {
    Rate r = new Rate(value: 0.121)
    assertNotNull("Rate not null", r)
    assertTrue("Rate is fixed", r.isFixed)
    assertEquals("Correct fixed rate", r.value, 0.121)
  }
}
