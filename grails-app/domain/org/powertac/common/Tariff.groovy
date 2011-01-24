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
  
  /** Last date new subscriptions will be accepted */
  Instant expiration
  
  /** Current state of this Tariff */
  State state = State.OFFERED
  
  /** Minimum contract duration */
  Duration minDuration
  
  /** Type of power covered by this tariff */
  PowerType powerType = PowerType.CONSUMPTION
  
  /**
   * One-time payment for subscribing to tariff, positive for payment
   * from customer, negative for payment to customer.
   */
  BigDecimal signupPayment = 0.0
  
  /**
   * Payment from customer to broker for canceling subscription before
   * minDuration has elapsed.
   */
  BigDecimal earlyWithdrawPayment = 0.0
  
  /** Flat payment per period for two-part tariffs */
  BigDecimal periodicPayment = 0.0

  /** Returns the rate table */
  Set<Rate> rates
  
  /** IDs of tariffs superseded by this Tariff */
  Set<Tariff> supersedes
  Tariff isSupersededBy

  static hasMany = [rates:Rate, supersedes:String]
  static constraints = {
    expiration(nullable: false)
    state(nullable: false)
    powerType(nullable: false)
    rates(nullable: false)
  }
}
