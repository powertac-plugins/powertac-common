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

class Orderbook implements Serializable {

  String id = IdGenerator.createId()
  Competition competition
  LocalDateTime dateExecuted
  String transactionId
  Product product
  Timeslot timeslot
  Boolean latest
  BigDecimal bid1 = null
  BigDecimal bid2 = null
  BigDecimal bid3 = null
  BigDecimal bid4 = null
  BigDecimal bid5 = null
  BigDecimal bid6 = null
  BigDecimal bid7 = null
  BigDecimal bid8 = null
  BigDecimal bid9 = null
  BigDecimal bid10 = null
  BigDecimal bidSize1 = 0
  BigDecimal bidSize2 = 0
  BigDecimal bidSize3 = 0
  BigDecimal bidSize4 = 0
  BigDecimal bidSize5 = 0
  BigDecimal bidSize6 = 0
  BigDecimal bidSize7 = 0
  BigDecimal bidSize8 = 0
  BigDecimal bidSize9 = 0
  BigDecimal bidSize10 = 0
  BigDecimal ask1 = null
  BigDecimal ask2 = null
  BigDecimal ask3 = null
  BigDecimal ask4 = null
  BigDecimal ask5 = null
  BigDecimal ask6 = null
  BigDecimal ask7 = null
  BigDecimal ask8 = null
  BigDecimal ask9 = null
  BigDecimal ask10 = null
  BigDecimal askSize1 = 0
  BigDecimal askSize2 = 0
  BigDecimal askSize3 = 0
  BigDecimal askSize4 = 0
  BigDecimal askSize5 = 0
  BigDecimal askSize6 = 0
  BigDecimal askSize7 = 0
  BigDecimal askSize8 = 0
  BigDecimal askSize9 = 0
  BigDecimal askSize10 = 0

  static belongsTo = [product: Product, timeslot: Timeslot, competition: Competition]

  static mapping = {
    id(generator: 'assigned')
    latest index: 'Orderbook_Idx_Product_Outdated'
    product index: 'Orderbook_Idx_Product_Outdated'
  }

  static constraints = {
    competition(nullable: false)
    dateExecuted(nullable: false)
    transactionId(nullable: false)
    product(nullable: false)
    timeslot(nullable: false)
    ask1(nullable: true, scale: 2)
    ask2(nullable: true, scale: 2)
    ask3(nullable: true, scale: 2)
    ask4(nullable: true, scale: 2)
    ask5(nullable: true, scale: 2)
    ask6(nullable: true, scale: 2)
    ask7(nullable: true, scale: 2)
    ask8(nullable: true, scale: 2)
    ask9(nullable: true, scale: 2)
    ask10(nullable: true, scale: 2)
    bid1(nullable: true, scale: 2)
    bid2(nullable: true, scale: 2)
    bid3(nullable: true, scale: 2)
    bid4(nullable: true, scale: 2)
    bid5(nullable: true, scale: 2)
    bid6(nullable: true, scale: 2)
    bid7(nullable: true, scale: 2)
    bid8(nullable: true, scale: 2)
    bid9(nullable: true, scale: 2)
    bid10(nullable: true, scale: 2)
    latest (nullable: false)
  }

  static transients = ['orderbookArray']

  public setOrderbookArray(BigDecimal[][][] orderbookArray) {
    /*
   BigDecimal[0][][] = bid
   BigDecimal[1][][] = ask

   BigDecimal[][0][] = price
   BigDecimal[][1][] = size

   BigDecimal[][][0] = Level 1
   BigDecimal[][][1] = Level 2
   BigDecimal[][][...] = Level ...
   BigDecimal[][][9] = Level 10
    */


    bid1 = orderbookArray[0][0][0]
    bid2 = orderbookArray[0][0][1]
    bid3 = orderbookArray[0][0][2]
    bid4 = orderbookArray[0][0][3]
    bid5 = orderbookArray[0][0][4]
    bid6 = orderbookArray[0][0][5]
    bid7 = orderbookArray[0][0][6]
    bid8 = orderbookArray[0][0][7]
    bid9 = orderbookArray[0][0][8]
    bid10 = orderbookArray[0][0][9]

    bidSize1 = orderbookArray[0][1][0] == null ? 0 : orderbookArray[0][1][0]
    bidSize2 = orderbookArray[0][1][1] == null ? 0 : orderbookArray[0][1][1]
    bidSize3 = orderbookArray[0][1][2] == null ? 0 : orderbookArray[0][1][2]
    bidSize4 = orderbookArray[0][1][3] == null ? 0 : orderbookArray[0][1][3]
    bidSize5 = orderbookArray[0][1][4] == null ? 0 : orderbookArray[0][1][4]
    bidSize6 = orderbookArray[0][1][5] == null ? 0 : orderbookArray[0][1][5]
    bidSize7 = orderbookArray[0][1][6] == null ? 0 : orderbookArray[0][1][6]
    bidSize8 = orderbookArray[0][1][7] == null ? 0 : orderbookArray[0][1][7]
    bidSize9 = orderbookArray[0][1][8] == null ? 0 : orderbookArray[0][1][8]
    bidSize10 = orderbookArray[0][1][9] == null ? 0 : orderbookArray[0][1][9]

    ask1 = orderbookArray[1][0][0]
    ask2 = orderbookArray[1][0][1]
    ask3 = orderbookArray[1][0][2]
    ask4 = orderbookArray[1][0][3]
    ask5 = orderbookArray[1][0][4]
    ask6 = orderbookArray[1][0][5]
    ask7 = orderbookArray[1][0][6]
    ask8 = orderbookArray[1][0][7]
    ask9 = orderbookArray[1][0][8]
    ask10 = orderbookArray[1][0][9]

    askSize1 = orderbookArray[1][1][0] == null ? 0 : orderbookArray[1][1][0]
    askSize2 = orderbookArray[1][1][1] == null ? 0 : orderbookArray[1][1][1]
    askSize3 = orderbookArray[1][1][2] == null ? 0 : orderbookArray[1][1][2]
    askSize4 = orderbookArray[1][1][3] == null ? 0 : orderbookArray[1][1][3]
    askSize5 = orderbookArray[1][1][4] == null ? 0 : orderbookArray[1][1][4]
    askSize6 = orderbookArray[1][1][5] == null ? 0 : orderbookArray[1][1][5]
    askSize7 = orderbookArray[1][1][6] == null ? 0 : orderbookArray[1][1][6]
    askSize8 = orderbookArray[1][1][7] == null ? 0 : orderbookArray[1][1][7]
    askSize9 = orderbookArray[1][1][8] == null ? 0 : orderbookArray[1][1][8]
    askSize10 = orderbookArray[1][1][9] == null ? 0 : orderbookArray[1][1][9]
  }

  public BigDecimal[][][] getOrderbookArray() {
    /*
   BigDecimal[0][][] = bid
   BigDecimal[1][][] = ask

   BigDecimal[][0][] = price
   BigDecimal[][1][] = size

   BigDecimal[][][0] = Level 1
   BigDecimal[][][1] = Level 2
   BigDecimal[][][...] = Level ...
   BigDecimal[][][9] = Level 10
    */

    BigDecimal[][][] orderbookArray = new BigDecimal[2][2][10];

    orderbookArray[0][0][0] = bid1
    orderbookArray[0][0][1] = bid2
    orderbookArray[0][0][2] = bid3
    orderbookArray[0][0][3] = bid4
    orderbookArray[0][0][4] = bid5
    orderbookArray[0][0][5] = bid6
    orderbookArray[0][0][6] = bid7
    orderbookArray[0][0][7] = bid8
    orderbookArray[0][0][8] = bid9
    orderbookArray[0][0][9] = bid10

    orderbookArray[0][1][0] = bidSize1 == null ? 0 : bidSize1
    orderbookArray[0][1][1] = bidSize2 == null ? 0 : bidSize2
    orderbookArray[0][1][2] = bidSize3 == null ? 0 : bidSize3
    orderbookArray[0][1][3] = bidSize4 == null ? 0 : bidSize4
    orderbookArray[0][1][4] = bidSize5 == null ? 0 : bidSize5
    orderbookArray[0][1][5] = bidSize6 == null ? 0 : bidSize6
    orderbookArray[0][1][6] = bidSize7 == null ? 0 : bidSize7
    orderbookArray[0][1][7] = bidSize8 == null ? 0 : bidSize8
    orderbookArray[0][1][8] = bidSize9 == null ? 0 : bidSize9
    orderbookArray[0][1][9] = bidSize10 == null ? 0 : bidSize10

    orderbookArray[1][0][0] = ask1
    orderbookArray[1][0][1] = ask2
    orderbookArray[1][0][2] = ask3
    orderbookArray[1][0][3] = ask4
    orderbookArray[1][0][4] = ask5
    orderbookArray[1][0][5] = ask6
    orderbookArray[1][0][6] = ask7
    orderbookArray[1][0][7] = ask8
    orderbookArray[1][0][8] = ask9
    orderbookArray[1][0][9] = ask10

    orderbookArray[1][1][0] = askSize1 == null ? 0 : askSize1
    orderbookArray[1][1][1] = askSize2 == null ? 0 : askSize2
    orderbookArray[1][1][2] = askSize3 == null ? 0 : askSize3
    orderbookArray[1][1][3] = askSize4 == null ? 0 : askSize4
    orderbookArray[1][1][4] = askSize5 == null ? 0 : askSize5
    orderbookArray[1][1][5] = askSize6 == null ? 0 : askSize6
    orderbookArray[1][1][6] = askSize7 == null ? 0 : askSize7
    orderbookArray[1][1][7] = askSize8 == null ? 0 : askSize8
    orderbookArray[1][1][8] = askSize9 == null ? 0 : askSize9
    orderbookArray[1][1][9] = askSize10 == null ? 0 : askSize10

    return orderbookArray
  }
}
