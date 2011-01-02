package org.powertac.common

import grails.test.*

class IdGeneratorTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testIdGenerator() {

      String myId1 = IdGenerator.createId()
      String myId2 = IdGenerator.createId()
      assertNotNull myId1
      assertNotNull myId2
      assertTrue (!myId1.equals(myId2))
    }
}
