package org.powertac.common

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant

class TariffSubscriptionTests extends GroovyTestCase {

  def timeService
  
  Tariff tariff
  Customer customer

  protected void setUp() {
    super.setUp()
    timeService.currentTime = new DateTime(2011, 1, 10, 0, 0, 0, 0, DateTimeZone.UTC).toInstant()
    DateTime exp = new DateTime(2011, 3, 10, 0, 0, 0, 0, DateTimeZone.UTC)
    tariff = new Tariff(expiration: exp.toInstant(),
                        minDuration: TimeService.WEEK * 8)
    tariff.addToRates(new Rate(value: 0.121))
    customer = new Customer(name:"Charley")
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
    assertEquals('nullable', tariffSubscription.errors.getFieldError('customer').getCode())
    assertEquals('nullable', tariffSubscription.errors.getFieldError('tariff').getCode())
  }

  // create a Subscription from a Tariff
  void testSimpleSub ()
  {
    TariffSubscription ts = tariff.subscribe(customer, 3)
    assertNotNull("non-null subscription", ts)
    assertEquals("correct customer", customer, ts.customer)
    assertEquals("correct tariff", tariff, ts.tariff)
    assertEquals("correct customer count", 3, ts.customersCommitted)
  }  
}
