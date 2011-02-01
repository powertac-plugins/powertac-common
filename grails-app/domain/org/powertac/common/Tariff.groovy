package org.powertac.common

import org.joda.time.Duration
import org.joda.time.Instant

/**
 * Represents a Tariff offered by a Broker to customers. A Tariff specifies
 * prices for energy in various circumstances, along with upfront and periodic
 * payments and basic constraints. This class is simply a serializable,
 * immutable data structure. You need a TariffBuilder to create one, and a
 * TariffExaminer to make serious use of one.
 */
class Tariff 
{
  // ----------- Power-type and State enumerations --------------
  enum PowerType
  {
    CONSUMPTION, INTERRUPTIBLE_CONSUMPTION,
    PRODUCTION, SOLAR_PRODUCTION, WIND_PRODUCTION,
    BATTERY_STORAGE, THERMAL_STORAGE
  }
  
  enum State
  {
    OFFERED, ACTIVE, LEGACY
  }

  def timeService // connection to simulation time service
  
  /** The broker who offers the tariff */
  Broker broker

  /** Last date new subscriptions will be accepted */
  Instant expiration
  
  /** Current state of this Tariff */
  State state = State.OFFERED
  
  /** Minimum contract duration (in milliseconds) */
  long minDuration = 0
  
  /** Type of power covered by this tariff */
  PowerType powerType = PowerType.CONSUMPTION
  
  /** One-time payment for subscribing to tariff, positive for payment
   *  from customer, negative for payment to customer. */
  BigDecimal signupPayment = 0.0
  
  /** Payment from customer to broker for canceling subscription before
   *  minDuration has elapsed. */
  BigDecimal earlyWithdrawPayment = 0.0
  
  /** Flat payment per period for two-part tariffs */
  BigDecimal periodicPayment = 0.0

  /** Returns the rate table */
  //def rates = []
  
  /** The TariffExaminer associated with this Tariff */
  TariffExaminer examiner
  
  /** Subscriptions to this Tariff, indexed by Customer ID */
  //def subscriptions = []
  
  /** IDs of tariffs superseded by this Tariff */
  //def supersedes = []
  Tariff isSupersededBy

  //static belongsTo = [broker:Broker]
  static hasMany = [rates:Rate, subscriptions:TariffSubscription]
  static constraints = {
    broker(nullable:false)
    //minDuration(nullable:true)
    expiration(nullable: true)
    state(nullable: false)
    powerType(nullable: false)
    rates(nullable: false)
    examiner(nullable: true)
    isSupersededBy(nullable: true)
  }
  
  static transients = ['timeService', 'expired']

  /**
   * Returns the TariffExaminer associated with this Tariff.
   */
  TariffExaminer getTariffExaminer ()
  {
    if (examiner == null) {
      examiner = new TariffExaminer(tariff: this)
      examiner.init()
    }
    return examiner
  }
  
  // GORM wants a setter
  void setTariffExaminer (TariffExaminer ex)
  {
    examiner = ex
  }
  
  /**
   * Subscribes a block of Customers from a single Customer model to
   * this Tariff, as long as this Tariff has not expired. If the
   * subscription succeeds, then the TariffSubscription instance is
   * return, otherwise null.
   */
  TariffSubscription subscribe (Customer customer, int customerCount)
  {
    if (isExpired())
      return null
    
    TariffSubscription sub = subscriptions?.findByCustomer(customer)
    if (sub == null) {
      sub = new TariffSubscription(customer: customer,
                                   tariff: this,
                                   tariffExaminer: this.getTariffExaminer())
    }
    sub.subscribe(customerCount)
    return sub
  }
  
  /**
   * True just in case the current time is past the expiration date
   * of this Tariff.
   */
  boolean isExpired ()
  {
    return timeService.currentTime.millis > expiration.millis
  }
}
