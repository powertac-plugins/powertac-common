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

class Shout {

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
  Boolean outdated = false
  String transactionId
  String shoutId
  String comment
  Boolean latest

  static belongsTo = [broker: Broker, product: Product, timeslot: Timeslot, competition: Competition]

  static constraints = {
    competition(nullable: false)
    broker(nullable: false,
        validator: { person ->
          person.enabled
        })
    product(nullable: false)
    buySellIndicator(nullable: false)
    quantity(nullable: false, min: 0.0, scale: 2)
    limitPrice(nullable: true, min: 0.0, scale: 2, validator: { limitPrice, shoutInstance ->
      (
      (shoutInstance.orderType == OrderType.MARKET && limitPrice == null)
          || (shoutInstance.orderType == OrderType.LIMIT && limitPrice != null)
      )
    })
    executionQuantity(nullable: true, min: 0.0, scale: 2)

    executionPrice(nullable: true, min: 0.0, scale: 2)
    orderType(nullable: false)
    dateCreated(nullable: false)
    dateMod(nullable: false)
    modReasonCode(nullable: false)
    outdated(nullable: false)
    transactionId(nullable: false, min: 0L)
    shoutId(nullable: false, min: 0L)
    comment(nullable: true)
    latest (nullable: false)
  }

  static mapping = {
    id (generator: 'assigned')
  }

  public Shout initModification(ModReasonCode newModReasonCode) {
    def newShout = new Shout()
    newShout.competition = this.competition
    newShout.broker = this.broker
    newShout.product = this.product
    newShout.buySellIndicator = this.buySellIndicator
    newShout.quantity = this.quantity
    newShout.limitPrice = this.limitPrice
    newShout.executionQuantity = null
    newShout.executionPrice = null
    newShout.orderType = this.orderType
    newShout.dateCreated = this.dateCreated
    newShout.dateMod = new LocalDateTime()
    newShout.modReasonCode = newModReasonCode
    newShout.outdated = this.outdated
    newShout.transactionId = null
    newShout.shoutId = this.shoutId
    newShout.comment = this.comment
    return newShout
  }
}
