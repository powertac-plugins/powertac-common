package org.powertac.common.command

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Instant
import org.powertac.common.enumerations.CustomerType;
import org.powertac.common.*

class TariffDoSubscribeCmdTests extends GroovyTestCase 
{
  // get ref to TimeService
  def timeService

  Timeslot timeslot
  Broker broker
  Broker broker2
  CustomerInfo customerInfo
  String userName
  String apiKey
  TariffSpecification tariffSpec

  protected void setUp() 
  {
    super.setUp()
    timeService.setCurrentTime(new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC))
    userName = 'Bill'
    apiKey = 'testApiKey-which-needs-to-be-longer-than-32-characters'
    broker = new Broker(userName: userName, apiKey: apiKey)
    assert (broker.validate() && broker.save())
    broker2 = new Broker(userName: userName + '2', apiKey: apiKey + '2')
    assert (broker2.validate() && broker2.save())
    customerInfo = new CustomerInfo(name: "suburbia", 
                            customerType: CustomerType.CustomerHousehold)
    Instant exp = new DateTime(2011, 3, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
    tariffSpec = new TariffSpecification(brokerId: broker.id, expiration: exp,
                                         minDuration: TimeService.WEEK * 8)
    Rate r1 = new Rate(value: 0.121)
    tariffSpec.addToRates(r1)
    assert (tariffSpec.validate() && tariffSpec.save())
  }

  protected void tearDown() 
  {
    super.tearDown()
  }

  void testNullableValidationLogic() 
  {
    TariffDoSubscribeCmd cmd = new TariffDoSubscribeCmd()
    cmd.id = null
    assertFalse(cmd.validate())
    assertEquals('nullable', cmd.errors.getFieldError('id').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('customer').getCode())
    assertEquals('nullable', cmd.errors.getFieldError('tariffId').getCode())
  }

  void testTariffIsLegacy() 
  {
    Tariff tariff = new Tariff(tariffSpec: tariffSpec, state: Tariff.State.WITHDRAWN)
    tariff.init()
    if (!tariff.validate()) {
      tariff.errors.allErrors.each { println it.toString() }
    }
    assert(tariff.save())
    TariffDoSubscribeCmd cmd = new TariffDoSubscribeCmd(tariffId: tariff.id,
                                                        customerInfo: customerInfo, customerCount: 4)
    assertFalse(cmd.validate())
    assertEquals(Constants.TARIFF_OUTDATED, cmd.errors.getFieldError('tariffId').getCode())
  }
}
