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

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.joda.time.Instant
import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.TransactionType

/**
 * A TransactionLog instance represents data commonly
 * referred to as trade and quote data (TAQ) in financial markets (stock exchanges).
 * One domain instance can (i) represent a trade that happened on the market
 * (price, quantity tuple and - in case of CDA markets - buyer and seller) or (ii)
 * a quote (which occurs if an order was entered into the system that changed the best
 * bid and/or best ask price / quantity but did not causing a clearing / trade).
 *<p>
 * Note: this domain class / table is closely modeled after the Thompson Reuter's TAQ data
 * file format in order to allow ex-post data analysis using the econometrics tools of the
 * Karlsruhe financial markets research group. The denormalization (trade and quote in one
 * domain class) is on purpose as econometrics analysis of market efficiency usually rely
 * on the combined data stream of both information types sorted by time precedence</p>
 * <p>
 * This an immutable value type, and therefore is not auditable.</p>
 *
 * @author Carsten Block
 * @version 1.1, 02/27/2011
 */
class TransactionLog implements Serializable {

  //def timeService
  /**
   * Retrieves the timeService (Singleton) reference from the main application context
   * This is necessary as DI by name (i.e. def timeService) stops working if a class
   * instance is deserialized rather than constructed.
   * Note: In the code below you can can still user timeService.xyzMethod()
   */
  private getTimeService() {
    ApplicationHolder.application.mainContext.timeService
  }

  String id = IdGenerator.createId()

  /** the product for which this trade or quote information is created */
  Product product

  /** the timeslot for which this trade or quote information is created */
  Timeslot timeslot

  /** flag that indicates weather this instance represents a trade or a quote */
  TransactionType transactionType

  /** the simulation date and time this trade or quote was generated */
  Instant dateCreated = timeService.getCurrentTime()

  /** A transactionId is e.g. generated during the execution of a trade in market and
   * marks all domain instances in all domain classes that were created or changed
   * during this single transaction. Later on this id allows for correlation of the
   * different domain class instances during ex post analysis*/
  String transactionId

  /** flag that marks the latest transactionLog instance for a particular product and
   * timeslot in a particular competition: Purpose: speed up db queries */
  Boolean latest

  /** trade property: price of a trade, in a pda this is the common clearing price, in
   * a cda this is the price of the best bid / ask the incoming order is matched
   * against */
  BigDecimal price

  /** trade property: quantity, i.e. number of products exchanged within a single trade */
  BigDecimal quantity

  /** trade property: buyer of the products of this trade - only viable for cda market
   * model */
  Broker buyer

  /** trade property: seller of the products of this trade - only viable for cda market
   * model */
  Broker seller

  /** trade property: flag that indicates if this transaction was triggered by an
   * incoming buy or sell order - only viable for cda market model */
  BuySellIndicator buySellIndicator

  /** quote property: bid price of the best bid in the order book */
  BigDecimal bid

  /** quote property: quantity of products requested in the best bid position of the
   * orderbook */
  BigDecimal bidSize

  /** quote property: ask price of the best ask in the order book */
  BigDecimal ask

  /** quote property: quantity of products offered in the best ask position of the
   * orderbook */
  BigDecimal askSize

  static belongsTo = [product: Product, timeslot: Timeslot]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    product(nullable: false)
    timeslot (nullable: false)
    transactionType(nullable: false)
    dateCreated(nullable: false)
    transactionId(nullable: false)

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
      return true
    })

    bidSize(nullable: true, scale: 2, validator: { val, obj ->
      if (obj.transactionType == TransactionType.TRADE && val) return ['trade.bidSize.notnull']
      return true
    })

    ask(nullable: true, scale: 2, validator: { val, obj ->
      if (obj.transactionType == TransactionType.TRADE && val) return ['trade.ask.notnull']
      return true
    })

    askSize(nullable: true, scale: 2, validator: { val, obj ->
      if (obj.transactionType == TransactionType.TRADE && val) return ['trade.askSize.notnull']
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
