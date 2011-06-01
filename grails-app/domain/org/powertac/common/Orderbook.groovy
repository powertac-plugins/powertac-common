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
import org.powertac.common.enumerations.ProductType
import com.thoughtworks.xstream.annotations.*
import com.thoughtworks.xstream.converters.collections.TreeSetConverter
import org.powertac.common.transformer.TimeslotConverter

/**
 * An orderbook instance captures a snapshot of the PowerTAC wholesale market's orderbook
 * (the un-cleared bids and asks remaining after the market is cleared). 
 * Each OrderbookEntry contained in the orderbook contains a limit price and
 * total un-cleared buy / sell quantity at that price.
 * Each time the market clears, one orderbook is created and sent to brokers for each
 * timeslot being traded during that clearing event.
 *
 * @author Daniel Schnurr
 * @version 1.2 , 05/02/2011
 */
@XStreamAlias("orderbook")
class Orderbook {

  /**
   * Retrieves the timeService (Singleton) reference from the main application context
   * This is necessary as DI by name (i.e. def timeService) stops working if a class
   * instance is deserialized rather than constructed.
   * Note: In the code below you can can still user timeService.xyzMethod()
   */

  //@XStreamAsAttribute
  Instant dateExecuted

  /** the transactionId is generated during the execution of a trade in market and
   * marks all domain instances in all domain classes that were created or changed
   * during this transaction. Like this the orderbookInstance with transactionId=1
   * can be correlated to shout instances with transactionId=1 in ex-post analysis  */
  @XStreamAsAttribute
  String transactionId

  /** the product this orderbook is generated for  */
  @XStreamAsAttribute
  ProductType product

  /** the timeslot this orderbook is generated for  */
  @XStreamConverter(TimeslotConverter)
  Timeslot timeslot

  /** last clearing price; if there is no last clearing price the min ask (max bid) will be returned*/
  @XStreamAsAttribute
  BigDecimal clearingPrice


  /** sorted set of OrderbookEntries with buySellIndicator = buy (descending)*/
  SortedSet<OrderbookEntry> bids = new TreeSet<OrderbookEntry>()

  /** sorted set of OrderbookEntries with buySellIndicator = sell (ascending)*/
  SortedSet<OrderbookEntry> asks = new TreeSet<OrderbookEntry>()

  static auditable = true
  
  static transients = ['timeService']

  static belongsTo = [timeslot: Timeslot]

  static hasMany = [bids: OrderbookEntry, asks: OrderbookEntry]

  static constraints = {
    dateExecuted(nullable: false)
    transactionId(nullable: false)
    product(nullable: false)
    timeslot(nullable: false)
    clearingPrice(nullable: true)
    asks(nullable: true)
    bids(nullable: true)
  }

  public BigDecimal determineClearingPrice() {
    OrderbookEntry bestBid
    OrderbookEntry bestAsk
    if (this.bids?.size() != 0) bestBid = this.bids?.first()
    if (this.asks?.size() != 0) bestAsk = this.asks?.first()

    if (this.clearingPrice) {
      return this.clearingPrice
    } else {
      if (bestBid && !bestAsk) return bestBid.limitPrice
      if (bestAsk && !bestBid) return bestAsk.limitPrice
      return null
    }
  }
}