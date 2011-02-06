package org.powertac.common.command

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant
import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.OrderType
import org.powertac.common.enumerations.ProductType
import org.powertac.common.*

class TariffDoRevokeCmdTests extends GroovyTestCase 
{
  // get ref to TimeService
  def timeService

  Competition competition
  Broker broker
  TariffSpecification tariffSpec

  protected void setUp() {
    super.setUp()
    timeService.setCurrentTime(new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC))
    competition = new Competition(name: "test", current: true)
    assert (competition.validate() && competition.save())
    broker = new Broker(competition: competition, userName: "Bob")
    assert (broker.validate() && broker.save())
    Instant exp = new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
    tariffSpec = new TariffSpecification(brokerId: broker.id, expiration: exp,
                                         minDuration: TimeService.WEEK * 8)
    Rate r1 = new Rate(value: 0.121)
    tariffSpec.addToRates(r1)
    assert (tariffSpec.validate() && tariffSpec.save())
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    TariffDoRevokeCmd cmd = new TariffDoRevokeCmd()
    cmd.id = null
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('id').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('competition').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('tariffId').getCode())
  }

  void testInactiveCompetition() {
    competition.current = false
    competition.save()
    TariffDoRevokeCmd cmd = new TariffDoRevokeCmd(competition: competition)
    assertFalse(cmd.validate())
    assertEquals(Constants.COMPETITION_INACTIVE, cmd.errors.getFieldError('competition').getCode())
  }

  void testTariffIsLegacy() {
    competition.current = true
    competition.save()
    Tariff tariff = new Tariff(tariffSpec: tariffSpec, state: Tariff.State.WITHDRAWN)
    tariff.init()
    if (!tariff.validate()) {
      tariff.errors.each { println it }
      fail("could not validate tariff")
    }
    assert(tariff.save())
    TariffDoRevokeCmd cmd = new TariffDoRevokeCmd(competition: competition, tariffId: tariff.id)
    assertFalse(cmd.validate())
    assertEquals(Constants.TARIFF_OUTDATED, cmd.errors.getFieldError('tariffId').getCode())
  }
}
