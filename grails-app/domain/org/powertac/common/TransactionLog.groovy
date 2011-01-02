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
import org.powertac.common.enumerations.TransactionType

class TransactionLog implements Serializable {

  String id = IdGenerator.createId()
  Competition competition
  Product product
  Timeslot timeslot
  TransactionType type
  LocalDateTime dateCreated = new LocalDateTime()
  String transactionId
  Boolean latest

  //Trade properties
  BigDecimal price
  BigDecimal quantity
  Broker buyer
  Broker seller
  BuySellIndicator buySellIndicator

  //Quote properties
  BigDecimal bid
  BigDecimal bidSize
  BigDecimal ask
  BigDecimal askSize

  static belongsTo = [competition: Competition, product: Product, timeslot: Timeslot]

  static constraints = {
    competition(nullable: false)
    product(nullable: false)
    timeslot (nullable: false)
    type(nullable: false)
    dateCreated(nullable: false)
    transactionId(nullable: false)
    latest (nullable: false)

    price(nullable: true, scale: 2, validator: { price, tl ->
      (tl.type == TransactionType.QUOTE && !price) || (tl.type == TransactionType.TRADE && price)
    })
    quantity(nullable: true, scale: 2, min: 0.0, validator: { quantity, tl ->
      (tl.type == TransactionType.QUOTE && !quantity) || (tl.type == TransactionType.TRADE && quantity)
    })
    buyer(nullable: true, validator: { buyerId, tl ->
      (tl.type == TransactionType.QUOTE && !buyerId) || (tl.type == TransactionType.TRADE && buyerId)
    })
    seller(nullable: true, validator: { sellerId, tl ->
      (tl.type == TransactionType.QUOTE && !sellerId) || (tl.type == TransactionType.TRADE && sellerId)
    })
    buySellIndicator(nullable: true, validator: { bsInd, tl ->
      (tl.type == TransactionType.QUOTE && !bsInd) || (tl.type == TransactionType.TRADE && bsInd)
    })

    bid(nullable: true, scale: 2, validator: { bid, tl ->
      (tl.type == TransactionType.TRADE && !bid) || TransactionType.QUOTE
    })
    bidSize(nullable: true, scale: 2, validator: { bidSize, tl ->
      (tl.type == TransactionType.TRADE && !bidSize) || TransactionType.QUOTE
    })
    ask(nullable: true, scale: 2, validator: { ask, tl ->
      (tl.type == TransactionType.TRADE && !ask) || TransactionType.QUOTE
    })
    askSize(nullable: true, scale: 2, validator: { askSize, tl ->
      (tl.type == TransactionType.TRADE && !askSize) || TransactionType.QUOTE
    })
  }

  static mapping = {
    id (generator: 'assigned')
  }



  String toString() {
    return "${dateCreated}-${type}-${product}-${timeslot}"
  }
}
