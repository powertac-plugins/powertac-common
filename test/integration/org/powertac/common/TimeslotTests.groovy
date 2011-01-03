package org.powertac.common

class TimeslotTests extends GroovyTestCase {

  Competition competition
  Timeslot timeslot1
  Timeslot timeslot2

  protected void setUp() {
    super.setUp()
    competition = new Competition(name: "test")
    assert (competition.save())
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableConstraints() {
    Timeslot timeslot = new Timeslot(id: null, serialNumber: null, competition: null, enabled: null, current: null, startDateTime: null, endDateTime: null)
    assertFalse(timeslot.validate())
    //assertEquals('nullable', timeslot.errors.getFieldError('id').getCode()) TODO: check null constraint on timeslot id field
    assertEquals('nullable', timeslot.errors.getFieldError('serialNumber').getCode())
    assertEquals('nullable', timeslot.errors.getFieldError('competition').getCode())
    assertEquals('nullable', timeslot.errors.getFieldError('enabled').getCode())
    assertEquals('nullable', timeslot.errors.getFieldError('current').getCode())
    assertEquals('nullable', timeslot.errors.getFieldError('startDateTime').getCode())
    assertEquals('nullable', timeslot.errors.getFieldError('endDateTime').getCode())
  }

  void testNextAndPrevious() {
    timeslot1 = new Timeslot(competition: competition, serialNumber: 0)
    assertTrue(timeslot1.validate() && timeslot1.save())

    timeslot2 = new Timeslot(competition: competition, serialNumber: 1)
    assertTrue (timeslot2.validate() && timeslot2.save())

    assertEquals (timeslot1.id, timeslot2.previous().id)
    assertEquals (timeslot2.id, timeslot1.next().id)
    assertNull (timeslot1.previous())
    assertNull (timeslot2.next())
  }
}
