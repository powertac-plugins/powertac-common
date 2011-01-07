/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.powertac.common

import org.joda.time.LocalDateTime
import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.ModReasonCode
import org.powertac.common.enumerations.OrderType

class Shout implements Serializable {

  String id = IdGenerator.createId()
  Competition competition
  Broker broker
  Product product
  Timeslot timeslot
  BuySellIndicator buySellIndicator
  BigDecimal quantity
  BigDecimal limitPrice
  BigDecimal executionQuantity
  BigDecimal executionPrice
  OrderType orderType = OrderType.MARKET
  LocalDateTime dateCreated = new LocalDateTime()
  LocalDateTime dateMod = new LocalDateTime()
  ModReasonCode modReasonCode = ModReasonCode.INSERT
  String transactionId
  String shoutId
  String comment
  Boolean latest

  static belongsTo = [broker: Broker, product: Product, timeslot: Timeslot, competition: Competition]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    competition(nullable: false, validator: {val ->
      val?.current ? true : ['inactive.competition']
    })
    broker(nullable: false)
    product(nullable: false)
    buySellIndicator(nullable: false)
    quantity(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    limitPrice(nullable: true, min: 0.0, Scale: Constants.DECIMALS, validator: {val, obj ->
      if (obj.orderType == OrderType.LIMIT && val == null) return ['nullable.limitorder']
      if (obj.orderType == OrderType.MARKET && val != null) return ['nullable.marketorder']
      return true
    })
    executionQuantity(nullable: true, min: 0.0, scale: Constants.DECIMALS)

    executionPrice(nullable: true, min: 0.0, scale: Constants.DECIMALS)
    orderType(nullable: false)
    dateCreated(nullable: false)
    dateMod(nullable: false)
    modReasonCode(nullable: false)
    transactionId(nullable: false)
    shoutId(nullable: false)
    comment(nullable: true)
    latest (nullable: false)
  }

  static mapping = {
    id (generator: 'assigned')
  }

  /**
   * Special type of cloning for shout instances. This method clones the shout instance and...
   * 1) updates the modReasonCode field in the cloned instance to the value provided as method param
   * 2) sets the 'latest' property in the original instance to false (and persists the change to the db)
   * 3) sets the 'latest' property in the cloned instance to true
   * 4) sets 'dataMod' property in the cloned instance to *now*
   * 5) sets 'transactionId' property in the cloned instance to null
   *
   * Note: the original shout instance is saved (in order to persist the
   * updated 'latest' field property while the cloned shout instance *is not saved*!!
   *
   * @param newModReasonCode new modReasonCode to use in the cloned shout instance
   * @return cloned shout instance where the cloned instance is changed as described above
   */
  public Shout initModification(ModReasonCode newModReasonCode) {
    def newShout = new Shout()
    newShout.competition = this.competition
    newShout.broker = this.broker
    newShout.product = this.product
    newShout.timeslot = this.timeslot
    newShout.buySellIndicator = this.buySellIndicator
    newShout.quantity = this.quantity
    newShout.limitPrice = this.limitPrice
    newShout.executionQuantity = this.executionQuantity
    newShout.executionPrice = this.executionPrice
    newShout.orderType = this.orderType
    newShout.dateCreated = this.dateCreated
    newShout.dateMod = new LocalDateTime()
    newShout.modReasonCode = newModReasonCode
    newShout.transactionId = null
    newShout.shoutId = this.shoutId
    newShout.comment = this.comment
    newShout.latest = true
    this.latest = false
    this.save()
    return newShout
  }
}
