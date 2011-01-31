package org.powertac.common

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration

class TariffSubscriptionTests extends GroovyTestCase {

  def timeService

  protected void setUp() {
    super.setUp()
    timeService.currentTime = new DateTime(2011, 1, 10, 0, 0, 0, 0, DateTimeZone.UTC).toInstant()
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    TariffSubscription tariffSubscription = new TariffSubscription()
    tariffSubscription.id = null
    tariffSubscription.customerShareCommitted= null
    tariffSubscription.tariffStartDateTime=null
    tariffSubscription.tariffEndDateTime=null
    assertFalse(tariffSubscription.validate())
    assertEquals('nullable', tariffSubscription.errors.getFieldError('id').getCode())
    assertEquals('nullable', tariffSubscription.errors.getFieldError('competition').getCode())
    assertEquals('nullable', tariffSubscription.errors.getFieldError('broker').getCode())
    assertEquals('nullable', tariffSubscription.errors.getFieldError('customer').getCode())
    assertEquals('nullable', tariffSubscription.errors.getFieldError('tariff').getCode())
    assertEquals('nullable', tariffSubscription.errors.getFieldError('customerShareCommitted').getCode())
    assertEquals('nullable', tariffSubscription.errors.getFieldError('tariffStartDateTime').getCode())
    assertEquals('nullable', tariffSubscription.errors.getFieldError('tariffEndDateTime').getCode())
  }

  void testEndDateTimeGreaterThanStartDateTimeConstraint() {
    DateTime startDateTime = new DateTime()
    DateTime endDateTime = startDateTime - Duration.standardSeconds(1)
    TariffSubscription tariffSubscription = new TariffSubscription(tariffStartDateTime: startDateTime, tariffEndDateTime: endDateTime)
    assertFalse(tariffSubscription.validate())
    assertEquals(Constants.TARIFF_SUBSCRIPTION_START_AFTER_END, tariffSubscription.errors.getFieldError('tariffStartDateTime').getCode())
    assertEquals(Constants.TARIFF_SUBSCRIPTION_START_AFTER_END, tariffSubscription.errors.getFieldError('tariffEndDateTime').getCode())
  }

  void testMinMaxCustomerShareCommitted() {
    TariffSubscription tariffSubscription = new TariffSubscription()
    tariffSubscription.customerShareCommitted = -0.1
    assertFalse(tariffSubscription.validate())
    assertEquals('min.notmet', tariffSubscription.errors.getFieldError('customerShareCommitted').getCode())

    tariffSubscription.customerShareCommitted = 1.1
    assertFalse(tariffSubscription.validate())
    assertEquals('max.exceeded', tariffSubscription.errors.getFieldError('customerShareCommitted').getCode())
  }


}
