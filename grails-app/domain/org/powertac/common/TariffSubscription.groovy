package org.powertac.common

import org.joda.time.DateTime
import org.joda.time.Duration

class TariffSubscription {

  def timeService

  String id = IdGenerator.createId()
  Competition competition
  Broker broker
  Customer customer
  Tariff tariff
  BigDecimal customerShareCommitted = 1.0 //describes how much percentage demand /supply of a multi-contracting customer is committed to this tariff subscription
  DateTime tariffStartDateTime = timeService?.currentTime?.toDateTime()
  DateTime tariffEndDateTime = timeService?.currentTime ? timeService.currentTime.toDateTime() + Duration.standardDays(1) : null

  static transients = ['timeService']

  static constraints = {
    id(nullable: false, blank: false, unique: true)
    competition(nullable: false)
    broker(nullable: false)
    customer(nullable: false)
    tariff(nullable: false)
    customerShareCommitted(nullable: false, min: 0.001, max: 1.0)
    tariffStartDateTime(nullable: false, validator: {val, obj ->
      return obj.tariffStartDateTime < obj.tariffEndDateTime ? true : [Constants.TARIFF_SUBSCRIPTION_START_AFTER_END]
    })
    tariffEndDateTime(nullable: false, validator: {val, obj ->
      return obj.tariffStartDateTime < obj.tariffEndDateTime ? true : [Constants.TARIFF_SUBSCRIPTION_START_AFTER_END]
    })
  }
  static mapping = {
    id(generator: 'assigned')
  }
}
