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
  TransactionType transactionType
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
    id (nullable: false, blank: false, unique: true)
    competition(nullable: false)
    product(nullable: false)
    timeslot (nullable: false)
    transactionType(nullable: false)
    dateCreated(nullable: false)
    transactionId(nullable: false)
    latest (nullable: false)

    price(nullable: true, scale: 2, validator: { val, obj ->
      if (obj.transactionType == TransactionType.TRADE && !val) return ['trade.price.null']
      if (obj.transactionType == TransactionType.QUOTE && val) return ['quote.price.notnull']
      return true
    })
    quantity(nullable: true, scale: 2, min: 0.0, validator: { val, obj ->
      if (obj.transactionType == TransactionType.TRADE && !val) return ['trade.quantity.null']
      if (obj.transactionType == TransactionType.QUOTE && val) return ['quote.quantity.notnull']
      return true
    })
    buyer(nullable: true, validator: { val, obj ->
      if (obj.transactionType == TransactionType.QUOTE && val) return ['quote.buyer.notnull']
      return true
    })
    seller(nullable: true, validator: { val, obj ->
      if (obj.transactionType == TransactionType.QUOTE && val) return ['quote.seller.notnull']
      return true
    })
    buySellIndicator(nullable: true, validator: { bsInd, obj ->
      if (obj.transactionType == TransactionType.QUOTE && bsInd) return ['quote.buysellindicator.notnull']
      return true
    })

    bid(nullable: true, scale: 2, validator: { val, obj ->
      if (obj.transactionType == TransactionType.TRADE && val) return ['trade.bid.notnull']
      if (obj.transactionType == TransactionType.QUOTE && !val) return ['quote.bid.null']
      return true
    })

    bidSize(nullable: true, scale: 2, validator: { val, obj ->
      if (obj.transactionType == TransactionType.TRADE && val) return ['trade.bidSize.notnull']
      if (obj.transactionType == TransactionType.QUOTE && !val) return ['quote.bidSize.null']
      return true
    })

    ask(nullable: true, scale: 2, validator: { val, obj ->
      if (obj.transactionType == TransactionType.TRADE && val) return ['trade.ask.notnull']
      if (obj.transactionType == TransactionType.QUOTE && !val) return ['quote.ask.null']
      return true
    })

    askSize(nullable: true, scale: 2, validator: { val, obj ->
      if (obj.transactionType == TransactionType.TRADE && val) return ['trade.askSize.notnull']
      if (obj.transactionType == TransactionType.QUOTE && !val) return ['quote.askSize.null']
      return true
    })
  }

  static mapping = {
    id (generator: 'assigned')
  }



  String toString() {
    return "${dateCreated}-${transactionType}-${product}-${timeslot}"
  }
}
