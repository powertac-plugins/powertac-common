package org.powertac.common

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant

import org.powertac.common.enumerations.CustomerType

class TariffSubscriptionTests extends GroovyTestCase {

  def timeService
  
  Tariff tariff
  Broker broker
  Competition competition
  Timeslot timeslot
  CustomerInfo customerInfo
  DateTime now

  protected void setUp() {
    super.setUp()
    competition = new Competition(name: "test", current: true)
    competition.save()
    broker = new Broker(competition: competition, userName: "Joe")
    broker.save()
    TariffTransaction.list()*.delete()
    now = new DateTime(2011, 1, 10, 0, 0, 0, 0, DateTimeZone.UTC)
    timeService.currentTime = now.toInstant()
    timeslot = new Timeslot(serialNumber: 0, 
        startDateTime: new DateTime(now.millis, DateTimeZone.UTC), 
        endDateTime: new DateTime(now.millis + TimeService.HOUR, DateTimeZone.UTC))
    timeslot.save()
    DateTime exp = new DateTime(now.millis + TimeService.WEEK * 10, DateTimeZone.UTC)
    TariffSpecification tariffSpec = 
        new TariffSpecification(brokerId: broker.getId(),
                                expiration: exp.toInstant(),
                                minDuration: TimeService.WEEK * 8)
    tariffSpec.addToRates(new Rate(value: 0.121))
    tariffSpec.save()
    tariff = new Tariff(tariffSpec: tariffSpec)
    tariff.init()
    assert(tariff.save())
    customerInfo = new CustomerInfo(name:"Charley", customerType: CustomerType.CustomerHousehold)
    if (!customerInfo.validate()) {
      customerInfo.errors.each { println it.toString() }
      fail("Could not save customerInfo")
    }
    assert(customerInfo.save())
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    TariffSubscription tariffSubscription = new TariffSubscription()
    tariffSubscription.id = null
    assertFalse(tariffSubscription.validate())
    assertEquals('nullable', tariffSubscription.errors.getFieldError('id').getCode())
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
  
  // subscription with non-zero signup bonus
  void testSignupBonus ()
  {
    DateTime exp = new DateTime(now.millis + TimeService.WEEK * 10, DateTimeZone.UTC)
    TariffSpecification tariffSpec =
        new TariffSpecification(brokerId: broker.getId(),
                                expiration: exp.toInstant(),
                                minDuration: TimeService.WEEK * 4,
                                signupPayment: -33.2)
    tariffSpec.addToRates(new Rate(value: 0.121))
    tariffSpec.save()
    tariff = new Tariff(tariffSpec: tariffSpec)
    tariff.init()
    tariff.save()

    TariffSubscription tsub = tariff.subscribe(customerInfo, 5)
    assertTrue("subscription saves", tsub.validate() && tsub.save())
    assertNotNull("non-null subscription", tsub)
    assertEquals("one expiration record", 1, tsub.expirations.size())
    assertEquals("correct timeslot", timeslot, Timeslot.currentTimeslot())
    def txs = Timeslot.currentTimeslot().tariffTx
    assertEquals("one transaction exists", 1, TariffTransaction.count())
    assertNotNull("transactions present", txs)
    assertEquals("one transaction", 1, txs.size())
  }
}
