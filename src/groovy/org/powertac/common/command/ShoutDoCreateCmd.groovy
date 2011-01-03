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

import org.codehaus.groovy.grails.validation.Validateable
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
@Validateable class ShoutDoCreateCmd {
  String competitionId
  String userName
  String apiKey
  String productId
  String timeslotId
  BuySellIndicator buySellIndicator
  BigDecimal quantity
  BigDecimal limitPrice
  OrderType orderType

  static constraints = {
    competitionId(nullable: false, blank: false, validator: {val ->
      def competition = Competition.get(val)
      if (!competition) {
        return ['invalid.competition']
      } else if (!competition.current){
        return ['inactive.competition']
      } else {
        return true
      }
    })
    userName(nullable: false, blank: false)
    apiKey(nullable: false, blank: false, validator: {val, obj ->
      def results = Broker.withCriteria {
        eq('competition.id', obj.competitionId)
        eq('userName', obj.userName)
        eq('apiKey', obj.apiKey)
        cache(true)
      }
      return results.size() == 1 ? true : ['invalid.credentials']
    })
    productId (nullable: false, blank: false, validator: {val->
      def product = Product.get(val)
      if (!product) {
        return ['invalid.product']
      } else {
        return true
      }
    })
    timeslotId (nullable: false, blank: false, validator: {val->
      def timeslot = Timeslot.get(val)
      if (!timeslot) {
        return ['invalid.timeslot']
      } else {
        return true
      }
    })
    buySellIndicator(nullable: false)
    quantity(nullable: false, min: 0.0, Scale: Constants.DECIMALS)
    limitPrice(nullable: true, min: 0.0, Scale: Constants.DECIMALS, validator: {val, obj ->
      if (obj.orderType == OrderType.LIMIT && val == null) return ['nullable.limitorder']
      if (obj.orderType == OrderType.MARKET && val != null) return ['nullable.marketorder']
      return true
    })
    orderType(nullable: false)
  }
}
