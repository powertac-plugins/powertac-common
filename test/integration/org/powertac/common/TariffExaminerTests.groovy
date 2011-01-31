/*
 * Copyright (c) 2011 by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powertac.common

import grails.test.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant

class TariffExaminerTests extends GrailsUnitTestCase 
{
  def timeService
  def tariff

  Instant start
  
  protected void setUp () 
  {
    super.setUp()
    start = new DateTime(2011, 1, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
    timeService.setCurrentTime(start)
    DateTime exp = new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC)
    tariff = new Tariff(expiration: new Instant(exp),
                        minDuration: new Duration(TimeService.WEEK * 8))
  }

  protected void tearDown () 
  {
    super.tearDown()
  }

  void testCreate () 
  {
    Rate r1 = new Rate(value: 0.121)
    tariff.addToRates(r1)
    r1.setTariff(tariff)
    TariffExaminer te = new TariffExaminer(tariff: tariff)
    te.init()
    assertNotNull("non-null result", te)
    assertEquals("correct publication time", start, te.offerDate)
    assertEquals("correct Tariff", tariff, te.tariff)
    assertEquals("correct initial realized price", 0.0, te.realizedPrice)
    assertFalse("not yet analyzed", te.analyzed)
  }
  
  void testRealizedPrice ()
  {
    Rate r1 = new Rate(value: 0.121)
    tariff.addToRates(r1)
    r1.setTariff(tariff)
    TariffExaminer te = new TariffExaminer(tariff: tariff)
    te.init()
    te.setTotalUsage 501.2
    te.setTotalCost 99.8
    assertEquals("Correct realized price", 99.8/501.2, te.getRealizedPrice(), 1.0e-6)
  }
  
  void testSimpleRate ()
  {
    Rate r1 = new Rate(value: 0.121)
    tariff.addToRates(r1)
    r1.setTariff(tariff)
    TariffExaminer te = new TariffExaminer(tariff: tariff)
    te.init()
    assertEquals("correct charge, default case", 0.121, te.getUsageCharge())
    assertEquals("correct charge, today", 1.21, te.getUsageCharge(10.0))
  }
}
