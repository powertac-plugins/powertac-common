/*
 * Copyright 2009-2011 the original author or authors.
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

import grails.test.GrailsUnitTestCase

class TariffTests extends GrailsUnitTestCase {
  protected void setUp() {
    super.setUp()
    mockForConstraintsTests(Tariff)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    Tariff tariff = new Tariff(id: null, dateCreated: null, latest: null)
    assertFalse(tariff.validate())
    assertEquals('nullable', tariff.errors.getFieldError('id').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('transactionId').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('competition').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('broker').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('tariffState').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('isDynamic').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('isNegotiable').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('dateCreated').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('latest').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice0').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice1').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice2').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice3').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice4').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice5').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice6').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice7').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice8').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice9').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice10').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice11').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice12').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice13').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice14').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice15').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice16').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice17').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice18').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice19').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice20').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice21').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice22').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerConsumptionPrice23').getCode())

    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice0').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice1').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice2').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice3').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice4').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice5').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice6').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice7').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice8').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice9').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice10').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice11').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice12').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice13').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice14').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice15').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice16').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice17').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice18').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice19').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice20').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice21').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice22').getCode())
    assertEquals('nullable', tariff.errors.getFieldError('powerProductionPrice23').getCode())
  }

  void testSetAndGetPowerConsumptionPrices() {
    def price = 15.0
    Tariff tariff = new Tariff()
    tariff.setFlatPowerConsumptionPrice(price)
    BigDecimal[] array = tariff.getPowerConsumptionPrices()
    assertEquals(24, array.length)
    for (i in 0..23) {
      assertEquals(price, tariff."powerConsumptionPrice$i")
    }
  }

  void testSetAndGetPowerProductionPrices() {
    def price = 15.0
    Tariff tariff = new Tariff()
    tariff.setFlatPowerProductionPrice(price)
    BigDecimal[] array = tariff.getPowerProductionPrices()
    assertEquals(24, array.length)
    for (i in 0..23) {
      assertEquals(price, tariff."powerProductionPrice$i")
    }
  }

  void testSetPowerConsumptionPricesWithOverlyLongArray() {

    Tariff tariff1 = new Tariff()
    BigDecimal[] array1 = new BigDecimal[30]
    array1.eachWithIndex {val, i -> array1[i] = i as BigDecimal}
    tariff1.setPowerConsumptionPrices(array1)

    BigDecimal[] resultArray = tariff1.getPowerConsumptionPrices()
    assertEquals(24, resultArray.length)
    for (i in 0..23) {
      assertEquals(i as BigDecimal, resultArray[i])
    }
  }

  void testSetPowerConsumptionPricesWithShortArray() {

    Tariff tariff1 = new Tariff()
    BigDecimal[] array1 = new BigDecimal[5]
    array1.eachWithIndex {val, i -> array1[i] = i as BigDecimal}
    tariff1.setPowerConsumptionPrices(array1)

    BigDecimal[] resultArray = tariff1.getPowerConsumptionPrices()
    assertEquals(24, resultArray.length)
    for (i in 0..4) {
      assertEquals(i as BigDecimal, resultArray[i])
    }
    for (i in 5..23) {
      assertNull(resultArray[i])
    }
  }


  void testSetPowerProductionPricesWithOverlyLongArray() {

    Tariff tariff1 = new Tariff()
    BigDecimal[] array1 = new BigDecimal[30]
    array1.eachWithIndex {val, i -> array1[i] = i as BigDecimal}
    tariff1.setPowerProductionPrices(array1)

    BigDecimal[] resultArray = tariff1.getPowerProductionPrices()
    assertEquals(24, resultArray.length)
    for (i in 0..23) {
      assertEquals(i as BigDecimal, resultArray[i])
    }
  }

  void testSetPowerProductionPricesWithShortArray() {

    Tariff tariff1 = new Tariff()
    BigDecimal[] array1 = new BigDecimal[5]
    array1.eachWithIndex {val, i -> array1[i] = i as BigDecimal}
    tariff1.setPowerProductionPrices(array1)

    BigDecimal[] resultArray = tariff1.getPowerProductionPrices()
    assertEquals(24, resultArray.length)
    for (i in 0..4) {
      assertEquals(i as BigDecimal, resultArray[i])
    }
    for (i in 5..23) {
      assertNull(resultArray[i])
    }
  }

  void testSetPowerConsumptionPriceNullArray() {
    Tariff tariff1 = new Tariff()
    tariff1.setPowerConsumptionPrices(null)
    def resultArray = tariff1.getPowerConsumptionPrices()
    assertEquals(24, resultArray.length)
    for (i in 0..23) {
      assertNull(resultArray[i])
    }
  }

  void testSetPowerProductionPriceNullArray() {
    Tariff tariff1 = new Tariff()
    tariff1.setPowerProductionPrices(null)
    def resultArray = tariff1.getPowerProductionPrices()
    assertEquals(24, resultArray.length)
    for (i in 0..23) {
      assertNull(resultArray[i])
    }
  }

  void testSetGetPowerConsumptionPriceFor() {
    Tariff tariff1 = new Tariff()
    def price = 5.0
    tariff1.setPowerConsumptionPriceFor(-1, price)
    tariff1.setPowerConsumptionPriceFor(30, price)
    def resultArray = tariff1.getPowerConsumptionPrices()
    assertEquals(24, resultArray.length)
    for (i in 0..23) {
      assertNull(resultArray[i])
    }
    tariff1.setPowerConsumptionPriceFor(1, price)
    assertEquals (price, tariff1.powerConsumptionPrice1)
    assertEquals (price, tariff1.getPowerConsumptionPriceFor(1))
    assertNull (tariff1.getPowerConsumptionPriceFor(-1))
    assertNull (tariff1.getPowerConsumptionPriceFor(30))
  }

  void testSetGetPowerProductionPriceFor() {
    Tariff tariff1 = new Tariff()
    def price = 5.0
    tariff1.setPowerProductionPriceFor(-1, price)
    tariff1.setPowerProductionPriceFor(30, price)
    def resultArray = tariff1.getPowerProductionPrices()
    assertEquals(24, resultArray.length)
    for (i in 0..23) {
      assertNull(resultArray[i])
    }
    tariff1.setPowerProductionPriceFor(1, price)
    assertEquals (price, tariff1.powerProductionPrice1)
    assertEquals (price, tariff1.getPowerProductionPriceFor(1))
    assertNull (tariff1.getPowerProductionPriceFor(-1))
    assertNull (tariff1.getPowerProductionPriceFor(30))
  }

  void testSetGetPowerConsumptionPriceForTooLargeRange() {
    Tariff tariff1 = new Tariff()
    def price = 5.0
    tariff1.setPowerConsumptionPriceForRange(-1, 30, price)
    def resultArray = tariff1.getPowerConsumptionPrices()
    assertEquals(24, resultArray.length)
    for (i in 0..23) {
      assertEquals(price, resultArray[i])
    }
  }

  void testSetGetPowerConsumptionPriceForSubRange() {
    Tariff tariff1 = new Tariff()
    def price = 5.0
    tariff1.setPowerConsumptionPriceForRange(5, 10, price)
    def resultArray = tariff1.getPowerConsumptionPrices()
    assertEquals(24, resultArray.length)
    for (i in 0..4) {
      assertNull(resultArray[i])
    }
    for (i in 5..10) {
      assertEquals(price, resultArray[i])
    }
    for (i in 11..23) {
      assertNull(resultArray[i])
    }
  }


  void testSetGetPowerProductionPriceForTooLargeRange() {
    Tariff tariff1 = new Tariff()
    def price = 5.0
    tariff1.setPowerProductionPriceForRange(-1, 30, price)
    def resultArray = tariff1.getPowerProductionPrices()
    assertEquals(24, resultArray.length)
    for (i in 0..23) {
      assertEquals(price, resultArray[i])
    }
  }

   void testSetGetPowerProductionPriceForReverseRange() {
    Tariff tariff1 = new Tariff()
    def price = 5.0
    tariff1.setPowerProductionPriceForRange(35, 10, price)
     //this should result in first timeslot to be set = 23 and last timeslot to be set = 23
    def resultArray = tariff1.getPowerProductionPrices()
    assertEquals(24, resultArray.length)
    for (i in 0..22) {
      assertNull(resultArray[i])
    }
    assertEquals (price, resultArray[23])
  }

  void testSetGetPowerConsumptionPriceForReverseRange() {
    Tariff tariff1 = new Tariff()
    def price = 5.0
    tariff1.setPowerConsumptionPriceForRange(35, 10, price)
     //this should result in first timeslot to be set = 23 and last timeslot to be set = 23
    def resultArray = tariff1.getPowerConsumptionPrices()
    assertEquals(24, resultArray.length)
    for (i in 0..22) {
      assertNull(resultArray[i])
    }
    assertEquals (price, resultArray[23])
  }

  void testSetGetPowerProductionPriceForSubRange() {
    Tariff tariff1 = new Tariff()
    def price = 5.0
    tariff1.setPowerProductionPriceForRange(5, 10, price)
    def resultArray = tariff1.getPowerProductionPrices()
    assertEquals(24, resultArray.length)
    for (i in 0..4) {
      assertNull(resultArray[i])
    }
    for (i in 5..10) {
      assertEquals(price, resultArray[i])
    }
    for (i in 11..23) {
      assertNull(resultArray[i])
    }
  }

}
