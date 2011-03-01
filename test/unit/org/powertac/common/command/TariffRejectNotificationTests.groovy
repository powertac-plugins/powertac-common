package org.powertac.common.command

import grails.test.*

class TariffRejectNotificationTests extends GrailsUnitTestCase 
{
  protected void setUp() 
  {
    super.setUp()
    mockForConstraintsTests(TariffRejectNotification)
  }

  protected void tearDown() 
  {
    super.tearDown()
  }

  void testValidation() 
  {
    TariffRejectNotification notice = new TariffRejectNotification()
    assertFalse("Should not be valid", notice.validate())
    assertEquals("broker field not nullable",
                 'nullable',
                 notice.errors.getFieldError('brokerId').getCode())
    assertEquals("tariff id field not nullable",
                 'nullable',
                 notice.errors.getFieldError('tariffId').getCode())
  }
}
