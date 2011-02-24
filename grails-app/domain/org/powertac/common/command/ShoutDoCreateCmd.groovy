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

package org.powertac.common.command

import org.joda.time.DateTime
import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.OrderType
import org.powertac.common.*

/**
 * Command object that represents a new (incoming) shout from a broker that should be
 * matched in the power tac wholesale market
 *
 * @author Carsten Block
 * @version 1.0, Date: 01.12.10
 */
class ShoutDoCreateCmd implements Serializable {
  String id = IdGenerator.createId()
  Broker broker
  Product product
  Timeslot timeslot
  BuySellIndicator buySellIndicator
  BigDecimal quantity
  BigDecimal limitPrice
  OrderType orderType
  DateTime dateCreated = new DateTime()

  static belongsTo = [broker: Broker, product: Product, timeslot: Timeslot]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    broker(nullable: false)
    product (nullable: false)
    timeslot(nullable: false, validator: {val ->
      if (!val.enabled) {
        return [Constants.TIMESLOT_INACTIVE]
      } else {
        return true
      }
    })
    buySellIndicator(nullable: false)
    quantity(nullable: false, min: 0.0, Scale: Constants.DECIMALS)
    limitPrice(nullable: true, min: 0.0, Scale: Constants.DECIMALS, validator: {val, obj ->
      if (obj.orderType == OrderType.LIMIT && val == null) return [Constants.SHOUT_LIMITORDER_NULL_LIMIT]
      if (obj.orderType == OrderType.MARKET && val != null) return [Constants.SHOUT_MARKETORDER_WITH_LIMIT]
      return true
    })
    orderType(nullable: false)
  }

  static mapping = {
    id (generator: 'assigned')
  }
}
