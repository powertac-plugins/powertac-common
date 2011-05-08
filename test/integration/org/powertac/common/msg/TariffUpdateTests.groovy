package org.powertac.common.msg

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Instant
import org.powertac.common.*
import org.powertac.common.msg.TariffUpdate;

class TariffUpdateTests extends GroovyTestCase
{
  // get ref to TimeService
  def timeService

  Broker broker
  TariffSpecification tariffSpec

  protected void setUp() {
    super.setUp()
    timeService.setCurrentTime(new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC))
    broker = new Broker(username: "Bob", password: "password")
    assert (broker.validate() && broker.save())
    Instant exp = new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
    tariffSpec = new TariffSpecification(broker: broker, expiration: exp,
                                         minDuration: TimeService.WEEK * 8)
    Rate r1 = new Rate(value: 0.121)
    tariffSpec.addToRates(r1)
    assert (tariffSpec.validate() && tariffSpec.save())
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testTariffIsLegacy() {
    Tariff tariff = new Tariff(tariffSpec: tariffSpec, state: Tariff.State.WITHDRAWN)
    tariff.init()
    if (!tariff.validate()) {
      tariff.errors.each { println it }
      fail("could not validate tariff")
    }
    assert(tariff.save())
    TariffUpdate cmd = new TariffUpdate(tariffId: tariff.id)
    assertFalse(cmd.validate())
    //assertEquals(Constants.TARIFF_OUTDATED, cmd.errors.getFieldError('tariffId').getCode())
  }
}
