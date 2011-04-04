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

import grails.test.*
import org.joda.time.Instant

/**
 * Unit tests for TariffSpecification
 * @author John Collins
 *
 */
class TariffSpecificationTests extends GrailsUnitTestCase 
{
  protected void setUp() 
  {
    super.setUp()
    mockForConstraintsTests(TariffSpecification)
  }

  protected void tearDown() 
  {
    super.tearDown()
  }

  void testNullableValidationLogic() 
  {
    TariffSpecification ts = new TariffSpecification(powerType: null)
    assertFalse(ts.validate())
    assertEquals('nullable', ts.errors.getFieldError('brokerUsername').getCode())
    assertEquals('nullable', ts.errors.getFieldError('powerType').getCode())
    assertEquals('nullable', ts.errors.getFieldError('rates').getCode())
  }

  void testBlankValidationLogic() 
  {
    TariffSpecification cmd = new TariffSpecification(id: '', tariffId: '', brokerUsername: '')
    assertFalse(cmd.validate())
    assertEquals('blank', cmd.errors.getFieldError('id').getCode())
    assertEquals('blank', cmd.errors.getFieldError('brokerUsername').getCode())
  }

  
  void testValueValidationLogic() 
  {
    TariffSpecification cmd = new TariffSpecification(tariffId: 'abc', brokerUsername: 'def', 
                                                      minDuration: 42l, rates: [])
    assertFalse(cmd.validate())
    cmd.errors.allErrors.each { println it }
    assertEquals('min.notmet', cmd.errors.getFieldError('minDuration').getCode())
    assertEquals('minSize.notmet', cmd.errors.getFieldError('rates').getCode())
  }
}
