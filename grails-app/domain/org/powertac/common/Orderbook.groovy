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
import org.powertac.common.transformer.TimeslotConverter

/**
 * An orderbook instance captures a snapshot of the PowerTAC wholesale market's orderbook
 * Each entry of the orderbook consists of the limit price, as well as the aggregated buy / sell quantity on that level.
 *
 *
 * @author Daniel Schnurr
 * @version 1.2 , 05/02/2011
 */
@XStreamAlias("orderbook-entry")
class Orderbook {

  //def timeService
  /**
   * Retrieves the timeService (Singleton) reference from the main application context
   * This is necessary as DI by name (i.e. def timeService) stops working if a class
   * instance is deserialized rather than constructed.
   * Note: In the code below you can can still user timeService.xyzMethod()
   */
  /* DSC commented out since timeService seems to crash integration tests
  private getTimeService() {
    ApplicationHolder.application.mainContext.timeService
  }

  @XStreamAsAttribute
  Instant dateExecuted = timeService?.currentTime
  */
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

  /** midpoint between bids and asks; if there is no bid (ask) the min ask (max bid) will be returned*/
  @XStreamOmitField
  BigDecimal midPoint

  /** sorted set of OrderbookEntries with buySellIndicator = buy (descending)*/
  SortedSet<OrderbookEntry> bids = new TreeSet<OrderbookEntry>()

  /** sorted set of OrderbookEntries with buySellIndicator = sell (ascending)*/
  SortedSet<OrderbookEntry> asks = new TreeSet<OrderbookEntry>()

  static auditable = true
  
  static transients = ['timeService', 'midPoint']

  static belongsTo = [timeslot: Timeslot]

  static hasMany = [bids: OrderbookEntry, asks: OrderbookEntry]

  static constraints = {
    dateExecuted(nullable: false)
    transactionId(nullable: false)
    product(nullable: false)
    timeslot(nullable: false)
    asks(nullable: true)
    bids(nullable: true)
  }

  private void setMidPoint(BigDecimal executableVolume) {
    //do nothing, method just prevents generation of a public setter
  }

  public BigDecimal getMidPoint() {
    OrderbookEntry bestBid = bids.first()
    OrderbookEntry bestAsk = asks.first()

    if (bestBid && bestAsk) {
      return ((bestBid.limitPrice+bestAsk.limitPrice)/2)
    } else {
      if (bestBid && !bestAsk) return bestBid.limitPrice
      if (bestAsk && !bestBid) return bestAsk.limitPrice
      return null
    }
  }
}