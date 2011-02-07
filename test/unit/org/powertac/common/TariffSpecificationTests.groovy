package org.powertac.common

import grails.test.*
import org.joda.time.Instant

class TariffSpecificationTests extends GrailsUnitTestCase 
{
  protected void setUp() {
    super.setUp()
    mockForConstraintsTests(TariffSpecification)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    TariffSpecification ts = new TariffSpecification(powerType: null)
    assertFalse(ts.validate())
    assertEquals('nullable', ts.errors.getFieldError('brokerId').getCode())
    assertEquals('nullable', ts.errors.getFieldError('powerType').getCode())
    assertEquals('nullable', ts.errors.getFieldError('rates').getCode())
  }

  void testBlankValidationLogic() {
    TariffSpecification cmd = new TariffSpecification(id: '', tariffId: '', brokerId: '')
    assertFalse(cmd.validate())
    assertEquals('blank', cmd.errors.getFieldError('id').getCode())
    assertEquals('blank', cmd.errors.getFieldError('brokerId').getCode())
  }

  
  void testValueValidationLogic() {
    TariffSpecification cmd = new TariffSpecification(tariffId: 'abc', brokerId: 'def', 
                                                      minDuration: 42l, rates: [])
    assertFalse(cmd.validate())
    cmd.errors.allErrors.each { println it }
    assertEquals('min.notmet', cmd.errors.getFieldError('minDuration').getCode())
    assertEquals('minSize.notmet', cmd.errors.getFieldError('rates').getCode())
  }
}
