package org.powertac.common

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant

import org.powertac.common.enumerations.CustomerType
import org.powertac.common.enumerations.PowerType

class TariffSubscriptionTests extends GroovyTestCase 
{

  def timeService
  
  Tariff tariff
  Broker broker
  //Competition competition
  Timeslot timeslot
  CustomerInfo customerInfo
  DateTime now

  protected void setUp() 
  {
    super.setUp()
    //competition = new Competition(name: "test", current: true)
    //competition.save()
    broker = new Broker(userName: "Joe")
    broker.save()
    TariffTransaction.list()*.delete()
    Timeslot.list()*.delete()
    now = new DateTime(2011, 1, 10, 0, 0, 0, 0, DateTimeZone.UTC)
    timeService.currentTime = now.toInstant()
    timeslot = new Timeslot(serialNumber: 0, 
        startDateTime: new DateTime(now.millis, DateTimeZone.UTC), 
        endDateTime: new DateTime(now.millis + TimeService.HOUR, DateTimeZone.UTC))
    timeslot.save()
    Instant exp = new Instant(now.millis + TimeService.WEEK * 10)
    TariffSpecification tariffSpec = 
        new TariffSpecification(brokerId: broker.getId(),
                                expiration: exp,
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

  protected void tearDown() 
  {
    super.tearDown()
  }

  void testNullableValidationLogic() 
  {
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
    Instant exp = new Instant(now.millis + TimeService.WEEK * 10)
    TariffSpecification tariffSpec =
        new TariffSpecification(brokerId: broker.getId(),
                                expiration: exp,
                                minDuration: TimeService.WEEK * 4,
                                signupPayment: -33.2)
    tariffSpec.addToRates(new Rate(value: 0.121))
    tariffSpec.save()
    tariff = new Tariff(tariffSpec: tariffSpec)
    tariff.init()
    tariff.save()

    TariffSubscription tsub = tariff.subscribe(customerInfo, 5)
    assertNotNull("non-null subscription", tsub)
    assertTrue("subscription saves", tsub.validate() && tsub.save())
    assertEquals("five customers committed", 5, tsub.customersCommitted)
    assertEquals("one expiration record", 1, tsub.expirations.size())
    Instant ex = new Instant(now.millis + TimeService.WEEK * 4)
    assertEquals("correct contract interval", ex, tsub.expirations[0][0])
    assertEquals("correct timeslot", timeslot, Timeslot.currentTimeslot())
    def txs = Timeslot.currentTimeslot().tariffTx
    assertEquals("one transaction exists", 1, TariffTransaction.count())
    assertNotNull("transactions present", txs)
    assertEquals("one transaction", 1, txs.size())
    def txArray = txs.toArray()
    assertEquals("correct txType", TariffTransaction.TxType.SIGNUP, txArray[0].txType)
    assertEquals("correct charge", -33.2*5, txArray[0].charge)
  }
  
  // subscription withdrawal without and with penalty
  void testEarlyWithdraw ()
  {
    DateTime exp = new DateTime(now.millis + TimeService.WEEK * 10, DateTimeZone.UTC)
    TariffSpecification tariffSpec =
        new TariffSpecification(brokerId: broker.getId(),
                                expiration: exp.toInstant(),
                                minDuration: TimeService.WEEK * 4,
                                signupPayment: -33.2,
                                earlyWithdrawPayment: 42.1)
    tariffSpec.addToRates(new Rate(value: 0.121))
    tariffSpec.save()
    tariff = new Tariff(tariffSpec: tariffSpec)
    tariff.init()
    tariff.save()
    TariffSubscription tsub = tariff.subscribe(customerInfo, 5)
    assertTrue("subscription saves", tsub.validate() && tsub.save())

    // move time forward 2 weeks, withdraw 2 customers
    Instant wk2 = new Instant(now.millis + TimeService.WEEK * 2)
    timeService.currentTime = wk2
    timeslot = new Timeslot(serialNumber: 3,
        startDateTime: new DateTime(wk2.millis, DateTimeZone.UTC),
        endDateTime: new DateTime(wk2.millis + TimeService.HOUR, DateTimeZone.UTC))
    timeslot.save()
    tsub.unsubscribe(2)
    def txs = Timeslot.currentTimeslot().tariffTx
    assertEquals("one transaction", 1, txs.size())
    def txArray = txs.toArray()
    assertEquals("correct txType", TariffTransaction.TxType.WITHDRAW, txArray[0].txType)
    assertEquals("correct charge", 42.1*2, txArray[0].charge)
    assertEquals("three customers committed", 3, tsub.customersCommitted)
    
    // move time forward another week, add 4 customers and drop 1
    Instant wk3 = new Instant(now.millis + TimeService.WEEK * 2 + TimeService.HOUR * 6)
    timeService.currentTime = wk3
    timeslot = new Timeslot(serialNumber: 7,
        startDateTime: new DateTime(wk3.millis, DateTimeZone.UTC),
        endDateTime: new DateTime(wk3.millis + TimeService.HOUR, DateTimeZone.UTC))
    timeslot.save()
    TariffSubscription tsub1 = tariff.subscribe(customerInfo, 4)
    assertEquals("same subscription", tsub, tsub1)
    tsub1.unsubscribe(1)
    txs = Timeslot.currentTimeslot().tariffTx
    assertEquals("two transactions", 2, txs.size())
    TariffTransaction ttx = TariffTransaction.findByTimeslotAndTxType(Timeslot.currentTimeslot(),
                                                                      TariffTransaction.TxType.SIGNUP)
    assertNotNull("found signup tx", ttx)
    assertEquals("correct charge", -33.2 * 4, ttx.charge)
    ttx = TariffTransaction.findByTimeslotAndTxType(Timeslot.currentTimeslot(),
                                                    TariffTransaction.TxType.WITHDRAW)
    assertNotNull("found withdraw tx", ttx)
    assertEquals("correct charge", 42.1, ttx.charge)
    assertEquals("six customers committed", 6, tsub1.customersCommitted)
    assertEquals("two expiration records", 2, tsub.expirations.size())
  }
  
  // Check consumption transactions
  void testConsumption ()
  {
    Instant exp = new Instant(now.millis + TimeService.WEEK * 10)
    TariffSpecification tariffSpec =
        new TariffSpecification(brokerId: broker.getId(),
                                expiration: exp,
                                minDuration: TimeService.WEEK * 4,
                                signupPayment: -33.2)
    tariffSpec.addToRates(new Rate(value: 0.121))
    tariffSpec.save()
    tariff = new Tariff(tariffSpec: tariffSpec)
    tariff.init()
    tariff.save()

    // subscribe and consume in the first timeslot
    TariffSubscription tsub = tariff.subscribe(customerInfo, 4)
    assertTrue("subscription saved", tsub.validate() && tsub.save())
    assertEquals("four customers committed", 4, tsub.customersCommitted)
    tsub.usePower(24.4) // consumption
    assertEquals("correct total usage", 24.4 / 4, tsub.totalUsage)
    assertEquals("correct realized price", 0.121, tariff.realizedPrice, 1e-6)
    Timeslot current = Timeslot.currentTimeslot() 
    assertEquals("two transactions", 2, current.tariffTx.size())
    TariffTransaction ttx = TariffTransaction.findByTimeslotAndTxType(current, TariffTransaction.TxType.SIGNUP)
    assertNotNull("found signup tx", ttx)
    assertEquals("correct charge", -33.2 * 4, ttx.charge, 1e-6)
    ttx = TariffTransaction.findByTimeslotAndTxType(current, TariffTransaction.TxType.CONSUMPTION)
    assertNotNull("found consumption tx", ttx)
    assertEquals("correct amount", 24.4, ttx.amount)
    assertEquals("correct charge", 0.121 * 24.4, ttx.charge, 1e-6)

    // just consume in the second timeslot
    Instant hour = new Instant(now.millis + TimeService.HOUR)
    timeService.currentTime = hour
    timeslot = new Timeslot(serialNumber: 1,
        startDateTime: new DateTime(hour.millis, DateTimeZone.UTC),
        endDateTime: new DateTime(hour.millis + TimeService.HOUR, DateTimeZone.UTC))
    timeslot.save()
    tsub.usePower(32.8) // consumption
    assertEquals("correct total usage", (24.4 + 32.8) / 4, tsub.totalUsage, 1e-6)
    assertEquals("correct realized price", 0.121, tariff.realizedPrice, 1e-6)
    current = Timeslot.currentTimeslot() 
    assertEquals("one transaction", 1, current.tariffTx.size())
    ttx = TariffTransaction.findByTimeslotAndTxType(current, TariffTransaction.TxType.CONSUMPTION)
    assertNotNull("found consumption tx", ttx)
    assertEquals("correct amount", 32.8, ttx.amount)
    assertEquals("correct charge", 0.121 * 32.8, ttx.charge, 1e-6)
  }
  
  // Check two-part tariff
  void testTwoPart()
  {
    Instant exp = new Instant(now.millis + TimeService.WEEK * 10)
    TariffSpecification tariffSpec =
        new TariffSpecification(brokerId: broker.getId(),
                                expiration: exp,
                                minDuration: TimeService.WEEK * 4,
                                signupPayment: -31.2,
                                periodicPayment: 1.3)
    tariffSpec.addToRates(new Rate(value: 0.112))
    tariffSpec.save()
    tariff = new Tariff(tariffSpec: tariffSpec)
    tariff.init()
    tariff.save()

    // subscribe and consume in the first timeslot
    TariffSubscription tsub = tariff.subscribe(customerInfo, 6)
    assertTrue("subscription saved", tsub.validate() && tsub.save())
    assertEquals("six customers committed", 6, tsub.customersCommitted)
    tsub.usePower(28.8) // consumption
    assertEquals("correct total usage", 28.8 / 6, tsub.totalUsage)
    assertEquals("correct realized price", (0.112 * 28.8 + 6 * 1.3) / 28.8, tariff.realizedPrice, 1e-6)
    Timeslot current = Timeslot.currentTimeslot() 
    assertEquals("two transactions", 3, current.tariffTx.size())
    TariffTransaction ttx = TariffTransaction.findByTimeslotAndTxType(current, TariffTransaction.TxType.SIGNUP)
    assertNotNull("found signup tx", ttx)
    assertEquals("correct charge", -31.2 * 6, ttx.charge, 1e-6)
    ttx = TariffTransaction.findByTimeslotAndTxType(current, TariffTransaction.TxType.CONSUMPTION)
    assertNotNull("found consumption tx", ttx)
    assertEquals("correct amount", 28.8, ttx.amount)
    assertEquals("correct charge", 0.112 * 28.8, ttx.charge, 1e-6)
    ttx = TariffTransaction.findByTimeslotAndTxType(current, TariffTransaction.TxType.PERIODIC)
    assertNotNull("found periodoc tx", ttx)
    assertEquals("correct charge", 6 * 1.3, ttx.charge, 1e-6)
  }
    
  // Check production transactions
  void testProduction ()
  {
    Instant exp = new Instant(now.millis + TimeService.WEEK * 10)
    TariffSpecification tariffSpec =
        new TariffSpecification(brokerId: broker.getId(),
                                powerType: PowerType.PRODUCTION,
                                expiration: exp,
                                minDuration: TimeService.WEEK * 4,
                                signupPayment: -34.2,
                                earlyWithdrawPayment: 35.0)
    tariffSpec.addToRates(new Rate(value: 0.102))
    tariffSpec.save()
    tariff = new Tariff(tariffSpec: tariffSpec)
    tariff.init()
    tariff.save()

    // subscribe and consume in the first timeslot
    TariffSubscription tsub = tariff.subscribe(customerInfo, 4)
    assertTrue("subscription saved", tsub.validate() && tsub.save())
    assertEquals("four customers committed", 4, tsub.customersCommitted)
    tsub.usePower(-244.6) // production
    assertEquals("correct total usage", -244.6 / 4, tsub.totalUsage)
    assertEquals("correct realized price", 0.102, tariff.realizedPrice, 1e-6)
    Timeslot current = Timeslot.currentTimeslot() 
    assertEquals("two transactions", 2, current.tariffTx.size())
    TariffTransaction ttx = TariffTransaction.findByTimeslotAndTxType(current, TariffTransaction.TxType.SIGNUP)
    assertNotNull("found signup tx", ttx)
    assertEquals("correct charge", -34.2 * 4, ttx.charge, 1e-6)
    ttx = TariffTransaction.findByTimeslotAndTxType(current, TariffTransaction.TxType.PRODUCTION)
    assertNotNull("found production tx", ttx)
    assertEquals("correct amount", -244.6, ttx.amount)
    assertEquals("correct charge", -0.102 * 244.6, ttx.charge, 1e-6)
  }
}
