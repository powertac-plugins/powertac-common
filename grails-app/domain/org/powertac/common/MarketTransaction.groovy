/*
 * Copyright 2009-2011 the original author or authors.
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
import org.powertac.common.enumerations.MarketTransactionType
import org.powertac.common.transformer.ProductConverter
import org.powertac.common.transformer.BrokerConverter
import com.thoughtworks.xstream.annotations.*

/**
 * A MarketTransaction instance represents data commonly
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
 * @author Carsten Block, John Collins
 */
@XStreamAlias("market-tx")
class MarketTransaction //implements Serializable 
{
  // This is a server-generated type - just use the GORM id
  //Integer id

  /** the product for which this information is created */
  @XStreamConverter(ProductConverter)
  Product product // not clear what this means -- JEC

  /** price/mWh of a trade, positive for a buy, negative for a sell */
  @XStreamAsAttribute
  BigDecimal price

  /** quantity of trade in mWh, positive for buy, negative for sell */
  @XStreamAsAttribute
  BigDecimal quantity

  /** trade property: buyer or seller of the products of this trade */
  @XStreamConverter(BrokerConverter)
  Broker broker
  
  /** the timeslot for which this trade or quote information is created */
  Timeslot timeslot
  
  /** when was this trade made */
  Instant postedTime
  
  // explicit version so we can omit it
  @XStreamOmitField
  int version

  static constraints = {
    postedTime (nullable: false)
    broker(nullable: false)
    timeslot(nullable: false)
    product(nullable: true)
    price(nullable: false)
    quantity(nullable: false)
  }

  String toString() {
    return "${timeslot}-${quantity}-${price}"
  }
}
