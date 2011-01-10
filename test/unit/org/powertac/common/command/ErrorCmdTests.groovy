package org.powertac.common.command

import grails.test.GrailsUnitTestCase
import org.powertac.common.exceptions.CompetitionNotFoundException
import org.powertac.common.exceptions.ShoutNotFoundException

class ErrorCmdTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testErrorCommandFromException() {
    try {
      throw new CompetitionNotFoundException("Competition not found") //Throw first test excption
    } catch (Exception cnfe) {
      try {
        throw new ShoutNotFoundException("Shout not found",cnfe) //Throw nested 2nd test excpetion
      } catch (ShoutNotFoundException snfe) {
        ErrorCmd cmd = ErrorCmd.fromException(snfe)
        assertEquals('org.powertac.common.exceptions.ShoutNotFoundException', cmd.className)
        assertEquals('Shout not found', cmd.message)
        assertEquals('org.powertac.common.exceptions.CompetitionNotFoundException: Competition not found', cmd.cause)
        assertNotNull('Testtrace', cmd.stackTrace)
      }


    }

  }
}
