package org.powertac.common

import grails.test.*
import org.powertac.common.enumerations.ProductType
import org.joda.time.Instant

class ClearedTradeTests extends GrailsUnitTestCase {

  ClearedTrade ct

  protected void setUp() {
    super.setUp()
    ct = new ClearedTrade()
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testPriceValidator() {
    ct.executionQuantity = 10.0
    mockForConstraintsTests(ClearedTrade, [ct])
    assertFalse(ct.validate())

    assertEquals('validator.invalid', ct.errors.getFieldError('executionPrice').getCode())
  }

  void testQuantityValidator() {
    ct.executionPrice = 10.0
    mockForConstraintsTests(ClearedTrade, [ct])
    assertFalse(ct.validate())

    assertEquals('validator.invalid', ct.errors.getFieldError('executionQuantity').getCode())
  }

  void testNullableConstraint() {
    ct.product = ProductType.Future
    ct.timeslot = new Timeslot(serialNumber: 1, startInstant: new Instant(), endInstant: new Instant())
    mockForConstraintsTests(ClearedTrade, [ct])
    assert ct.validate()

    ct.executionQuantity = 10.0
    ct.executionPrice = 20.0
    assert ct.validate()
  }
}

