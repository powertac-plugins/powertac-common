package org.powertac.common.command

import grails.test.GrailsUnitTestCase
import org.powertac.common.exceptions.CompetitionResetException
import org.powertac.common.exceptions.MeterReadingException

class ErrorCmdTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testErrorCommandFromException() {
    try {
      throw new MeterReadingException("Competition not found") //Throw first test excption
    } catch (Exception cnfe) {
      try {
        throw new CompetitionResetException("Shout not found",cnfe) //Throw nested 2nd test excpetion
      } catch (Exception snfe) {
        ErrorCmd cmd = ErrorCmd.fromException(snfe)
        assertEquals('org.powertac.common.exceptions.CompetitionResetException', cmd.className)
        assertEquals('Shout not found', cmd.message)
        assertEquals('org.powertac.common.exceptions.MeterReadingException: Competition not found', cmd.cause)
        assertNotNull('Testtrace', cmd.stackTrace)
      }


    }

  }
}
