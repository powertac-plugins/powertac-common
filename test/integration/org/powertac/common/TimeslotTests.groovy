package org.powertac.common

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Instant

class TimeslotTests extends GroovyTestCase 
{
  def timeService

  //Competition competition
  Timeslot timeslot1
  Timeslot timeslot2

  protected void setUp() 
  {
    super.setUp()
    //competition = new Competition(name: "test")
    //assert (competition.save())
    DateTime now = new DateTime(2011, 1, 10, 0, 0, 0, 0, DateTimeZone.UTC)
    timeService.currentTime = now.toInstant()
  }

  protected void tearDown()
  {
    super.tearDown()
  }

  void testNullableConstraints() {
    Timeslot timeslot = new Timeslot(id: null, serialNumber: null, competition: null, enabled: null, current: null, startDateTime: null, endDateTime: null)
    assertFalse(timeslot.validate())
    //assertEquals('nullable', timeslot.errors.getFieldError('id').getCode()) TODO: check null constraint on timeslot id field
    assertEquals('nullable', timeslot.errors.getFieldError('serialNumber').getCode())
    //assertEquals('nullable', timeslot.errors.getFieldError('competition').getCode())
    assertEquals('nullable', timeslot.errors.getFieldError('enabled').getCode())
    assertEquals('nullable', timeslot.errors.getFieldError('current').getCode())
    assertEquals('nullable', timeslot.errors.getFieldError('startDateTime').getCode())
    assertEquals('nullable', timeslot.errors.getFieldError('endDateTime').getCode())
  }

  void testNextAndPrevious() {
    timeslot1 = new Timeslot(serialNumber: 0,
        startDateTime: new DateTime(), endDateTime: new DateTime())
    if (!timeslot1.validate()) println timeslot1.errors.allErrors
    assertTrue(timeslot1.validate() && timeslot1.save())

    timeslot2 = new Timeslot(serialNumber: 1,
        startDateTime: new DateTime(), endDateTime: new DateTime())
    assertTrue(timeslot2.validate() && timeslot2.save())

    assertEquals(timeslot1.id, timeslot2.previous().id)
    assertEquals(timeslot2.id, timeslot1.next().id)
    assertNull(timeslot1.previous())
    assertNull(timeslot2.next())
  }

  void testCurrentTimeslot() {
    //competition.current = true
    //competition.save()
    long now = timeService.currentTime.millis
    timeslot1 = new Timeslot(serialNumber: 0, current: false, 
        startDateTime: new DateTime(now, DateTimeZone.UTC), 
        endDateTime: new DateTime(now + TimeService.HOUR, DateTimeZone.UTC))
    assertTrue("timeslot1 good", timeslot1.validate() && timeslot1.save())

    timeslot2 = new Timeslot(serialNumber: 1, current: true, 
        startDateTime: new DateTime(now + TimeService.HOUR, DateTimeZone.UTC), 
        endDateTime: new DateTime(now + 2 * TimeService.HOUR, DateTimeZone.UTC))
    assertTrue("timeslot2 good", timeslot2.validate() && timeslot2.save())

    assertEquals("timeslot1 is current", timeslot1, Timeslot.currentTimeslot())

    //timeslot2.current = false
    //timeslot2.save()
    timeService.currentTime = new Instant(now + TimeService.HOUR)
    
    //assertNull(Timeslot.currentTimeslot())

    //timeslot2.current = true
    //timeslot2.save()

    assertEquals("timeslot2 is current", timeslot2, Timeslot.currentTimeslot())

    //competition.current = false
    //competition.save()
    //assertNull (Timeslot.currentTimeslot())
  }
}
