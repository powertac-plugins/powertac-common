package org.powertac.common

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Instant

class TimeslotTests extends GroovyTestCase 
{
  def timeService

  Timeslot timeslot1
  Timeslot timeslot2

  protected void setUp() 
  {
    super.setUp()
    DateTime now = new DateTime(2011, 1, 10, 0, 0, 0, 0, DateTimeZone.UTC)
    timeService.currentTime = now.toInstant()
  }

  protected void tearDown()
  {
    super.tearDown()
  }

  void testNullableConstraints() {
    Timeslot timeslot = new Timeslot(id: null, serialNumber: null, enabled: null, startInstant: null, endInstant: null)
    assertFalse(timeslot.validate())
    //assertEquals('nullable', timeslot.errors.getFieldError('id').getCode()) TODO: check null constraint on timeslot id field
    assertEquals('nullable', timeslot.errors.getFieldError('serialNumber').getCode())
    //assertEquals('nullable', timeslot.errors.getFieldError('competition').getCode())
    assertEquals('nullable', timeslot.errors.getFieldError('enabled').getCode())
    //assertEquals('nullable', timeslot.errors.getFieldError('current').getCode())
    assertEquals('nullable', timeslot.errors.getFieldError('startInstant').getCode())
    assertEquals('nullable', timeslot.errors.getFieldError('endInstant').getCode())
  }

  void testNextAndPrevious() {
    timeslot1 = new Timeslot(serialNumber: 0,
        startInstant: new Instant(), endInstant: new Instant())
    if (!timeslot1.validate()) println timeslot1.errors.allErrors
    assertTrue(timeslot1.validate() && timeslot1.save())

    timeslot2 = new Timeslot(serialNumber: 1,
        startInstant: new Instant(), endInstant: new Instant())
    assertTrue(timeslot2.validate() && timeslot2.save())

    assertEquals(timeslot1.id, timeslot2.previous().id)
    assertEquals(timeslot2.id, timeslot1.next().id)
    assertNull(timeslot1.previous())
    assertNull(timeslot2.next())
  }

  void testCurrentTimeslot() {
    long now = timeService.currentTime.millis
    timeslot1 = new Timeslot(serialNumber: 0, current: false, 
        startInstant: new Instant(now), 
        endInstant: new Instant(now + TimeService.HOUR))
    assertTrue("timeslot1 good", timeslot1.validate() && timeslot1.save())

    timeslot2 = new Timeslot(serialNumber: 1, current: true, 
        startInstant: new Instant(now + TimeService.HOUR), 
        endInstant: new Instant(now + 2 * TimeService.HOUR))
    assertTrue("timeslot2 good", timeslot2.validate() && timeslot2.save())

    assertEquals("timeslot1 is current", timeslot1, Timeslot.currentTimeslot())

    //timeslot2.current = false
    //timeslot2.save()
    timeService.currentTime = new Instant(now + TimeService.HOUR)
    
    //assertNull(Timeslot.currentTimeslot())

    //timeslot2.current = true
    //timeslot2.save()

    assertEquals("timeslot2 is current", timeslot2, Timeslot.currentTimeslot())

    //assertNull (Timeslot.currentTimeslot())
  }

  void testEnabledTimeslots() {
    long now = timeService.currentTime.millis
    timeslot1 = new Timeslot(serialNumber: 0, current: false, 
        startInstant: new Instant(now), 
        endInstant: new Instant(now + TimeService.HOUR))
    assertTrue("timeslot1 good", timeslot1.validate() && timeslot1.save())

    timeslot2 = new Timeslot(serialNumber: 1, current: true, 
        startInstant: new Instant(now + TimeService.HOUR), 
        endInstant: new Instant(now + 2 * TimeService.HOUR))
    assertTrue("timeslot2 good", timeslot2.validate() && timeslot2.save())
    
    Timeslot timeslot3 = new Timeslot(serialNumber: 2, current: true,
        startInstant: new Instant(now + 2 * TimeService.HOUR),
        endInstant: new Instant(now + 3 * TimeService.HOUR))
    assertTrue("timeslot3 good", timeslot3.validate() && timeslot3.save())
    
    Timeslot timeslot4 = new Timeslot(serialNumber: 3, current: true,
        startInstant: new Instant(now + 3 * TimeService.HOUR),
        endInstant: new Instant(now + 4 * TimeService.HOUR))
    assertTrue("timeslot4 good", timeslot4.validate() && timeslot4.save())

    assertEquals("timeslot1 is current", timeslot1, Timeslot.currentTimeslot())
    assertEquals("no enabled timeslots", 0, Timeslot.enabledTimeslots().size())
    
    // enable timeslots 2 and 3
    timeslot3.enabled = true
    timeslot3.save()
    timeslot2.enabled = true
    timeslot2.save()
    List<Timeslot> enabledSlots = Timeslot.enabledTimeslots()
    assertEquals("two enabled timeslots", 2, enabledSlots.size())
    assertNotNull("contains timeslot 3", enabledSlots.find { it == timeslot3 })
    assertNotNull("contains timeslot 2", enabledSlots.find { it == timeslot2 })
    assertNull("does not contain timeslot 4", enabledSlots.find { it == timeslot4 })
    
    timeService.currentTime = new Instant(now + TimeService.HOUR)

    assertEquals("timeslot2 is current", timeslot2, Timeslot.currentTimeslot())

    // Disable timeslot 2 and enable timeslot 4
    timeslot2.enabled = false
    timeslot2.save()
    timeslot4.enabled = true
    timeslot4.save()
    enabledSlots = Timeslot.enabledTimeslots()
    assertEquals("two enabled timeslots", 2, enabledSlots.size())
    assertNotNull("contains timeslot 3", enabledSlots.find { it == timeslot3 })
    assertNotNull("contains timeslot 4", enabledSlots.find { it == timeslot4 })
    assertNull("does not contain timeslot 2", enabledSlots.find { it == timeslot2 })
  }
}
