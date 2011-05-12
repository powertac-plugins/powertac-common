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

import org.powertac.common.enumerations.BuySellIndicator
import com.thoughtworks.xstream.annotations.*

@XStreamAlias("orderbook-entry")
class OrderbookEntry implements Comparable {

  @XStreamAsAttribute
  BigDecimal limitPrice

  @XStreamAsAttribute
  BigDecimal quantity

  @XStreamAsAttribute
  BuySellIndicator buySellIndicator

  static belongsTo = [orderbook: Orderbook]

  static constraints = {
    limitPrice(nullable: false)
    quantity(nullable: false)
    buySellIndicator(nullable: false)
  }

  int compareTo(Object o) {
    if (!o instanceof OrderbookEntry) return 1
    OrderbookEntry other = (OrderbookEntry) o
    if (buySellIndicator == BuySellIndicator.BUY) {
      return this.limitPrice.equals(other.limitPrice) ? 0 : this.limitPrice < other.limitPrice ? 1 : -1
    } else {
      return this.limitPrice.equals(other.limitPrice) ? 0 : this.limitPrice < other.limitPrice ? -1 : 1
    }

  }
}
