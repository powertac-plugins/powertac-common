package org.powertac.common

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant

class TariffSubscriptionTests extends GroovyTestCase {

  def timeService
  
  Tariff tariff
  Broker broker
  Competition competition
  CustomerInfo customerInfo

  protected void setUp() {
    super.setUp()
    competition = new Competition(name: "test", current: true)
    competition.save()
    broker = new Broker(competition: competition, userName: "Joe")
    broker.save()
    timeService.currentTime = new DateTime(2011, 1, 10, 0, 0, 0, 0, DateTimeZone.UTC).toInstant()
    DateTime exp = new DateTime(2011, 3, 10, 0, 0, 0, 0, DateTimeZone.UTC)
    TariffSpecification tariffSpec = 
        new TariffSpecification(brokerId: broker.getId(),
                                expiration: exp.toInstant(),
                                minDuration: TimeService.WEEK * 8)
    tariffSpec.addToRates(new Rate(value: 0.121))
    tariffSpec.save()
    tariff = new Tariff(tariffSpec: tariffSpec)
    tariff.init()
    tariff.save()
    customerInfo = new CustomerInfo(name:"Charley")
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    TariffSubscription tariffSubscription = new TariffSubscription()
    tariffSubscription.id = null
    assertFalse(tariffSubscription.validate())
    assertEquals('nullable', tariffSubscription.errors.getFieldError('id').getCode())
    assertEquals('nullable', tariffSubscription.errors.getFieldError('competition').getCode())
    assertEquals('nullable', tariffSubscription.errors.getFieldError('customerInfo').getCode())
    assertEquals('nullable', tariffSubscription.errors.getFieldError('tariff').getCode())
  }

  // create a Subscription from a Tariff
  void testSimpleSub ()
  {
    TariffSubscription ts = tariff.subscribe(customerInfo, 3)
    assertNotNull("non-null subscription", ts)
    assertEquals("correct customer", customerInfo, ts.customerInfo)
    assertEquals("correct tariff", tariff, ts.tariff)
    assertEquals("correct customer count", 3, ts.customersCommitted)
  }  
}
