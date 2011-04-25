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

/**
 * An orderbook instance captures a snapshot of the PowerTAC wholesale market's orderbook
 * up to a maximum level (depth) of 10 entries on each side. Each level of the orderbook
 * consists of the limit price as well as the aggregated buy / sell quantity on that level.
 * For example bid0 = 10, bidSize0=100 means that the best buy limit price was 10 with an
 * aggregated buy volume of 100 pieces (which can come from 1 or more orders with limit
 * price 10.
 *
 * Note the orderbook is denormalized on purpose to ensure simple and fast database
 * handling. You can use the the {@code setOrderbookArray ( ) and {@getOrderbookArray ( )}* methods to access or manipulate orderbook instances. These are internally mapped to the
 * bid0..9, bidSize0..0, ask0..0, askSize0..9 properties.
 *
 * @author Carsten Block, David Dauer
 * @version 1.1 , 02/27/2011
 */
class Orderbook implements Serializable {

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

  Instant dateExecuted = timeService?.currentTime

  /** the transactionId is generated during the execution of a trade in market and
   * marks all domain instances in all domain classes that were created or changed
   * during this transaction. Like this the orderbookInstance with transactionId=1
   * can be correlated to shout instances with transactionId=1 in ex-post analysis  */
  String transactionId

  /** the product this orderbook is generated for  */
  ProductType product

  /** the timeslot this orderbook is generated for  */
  Timeslot timeslot

  /** bid price at orderbook level 0  */
  BigDecimal bid0 = null
  /** bid price at orderbook level 1  */
  BigDecimal bid1 = null
  /** bid price at orderbook level 2  */
  BigDecimal bid2 = null
  /** bid price at orderbook level 3  */
  BigDecimal bid3 = null
  /** bid price at orderbook level 4  */
  BigDecimal bid4 = null
  /** bid price at orderbook level 5  */
  BigDecimal bid5 = null
  /** bid price at orderbook level 6  */
  BigDecimal bid6 = null
  /** bid price at orderbook level 7  */
  BigDecimal bid7 = null
  /** bid price at orderbook level 8  */
  BigDecimal bid8 = null
  /** bid price at orderbook level 9  */
  BigDecimal bid9 = null
  /** bid quantity at orderbook level 0  */
  BigDecimal bidSize0 = 0.0
  /** bid quantity at orderbook level 1  */
  BigDecimal bidSize1 = 0.0
  /** bid quantity at orderbook level 2  */
  BigDecimal bidSize2 = 0.0
  /** bid quantity at orderbook level 3  */
  BigDecimal bidSize3 = 0.0
  /** bid quantity at orderbook level 4  */
  BigDecimal bidSize4 = 0.0
  /** bid quantity at orderbook level 5  */
  BigDecimal bidSize5 = 0.0
  /** bid quantity at orderbook level 6  */
  BigDecimal bidSize6 = 0.0
  /** bid quantity at orderbook level 7  */
  BigDecimal bidSize7 = 0.0
  /** bid quantity at orderbook level 8  */
  BigDecimal bidSize8 = 0.0
  /** bid quantity at orderbook level 9  */
  BigDecimal bidSize9 = 0.0
  /** ask price at orderbook level 0  */
  BigDecimal ask0 = null
  /** ask price at orderbook level 1  */
  BigDecimal ask1 = null
  /** ask price at orderbook level 2  */
  BigDecimal ask2 = null
  /** ask price at orderbook level 3  */
  BigDecimal ask3 = null
  /** ask price at orderbook level 4  */
  BigDecimal ask4 = null
  /** ask price at orderbook level 5  */
  BigDecimal ask5 = null
  /** ask price at orderbook level 6  */
  BigDecimal ask6 = null
  /** ask price at orderbook level 7  */
  BigDecimal ask7 = null
  /** ask price at orderbook level 8  */
  BigDecimal ask8 = null
  /** ask price at orderbook level 9  */
  BigDecimal ask9 = null
  /** ask quantity at orderbook level 0  */
  BigDecimal askSize0 = 0.0
  /** ask quantity at orderbook level 1  */
  BigDecimal askSize1 = 0.0
  /** ask quantity at orderbook level 2  */
  BigDecimal askSize2 = 0.0
  /** ask quantity at orderbook level 3  */
  BigDecimal askSize3 = 0.0
  /** ask quantity at orderbook level 4  */
  BigDecimal askSize4 = 0.0
  /** ask quantity at orderbook level 5  */
  BigDecimal askSize5 = 0.0
  /** ask quantity at orderbook level 6  */
  BigDecimal askSize6 = 0.0
  /** ask quantity at orderbook level 7  */
  BigDecimal askSize7 = 0.0
  /** ask quantity at orderbook level 8  */
  BigDecimal askSize8 = 0.0
  /** ask quantity at orderbook level 9  */
  BigDecimal askSize9 = 0.0

  static auditable = true
  
  static transients = ['timeService', 'orderbookArray']

  static belongsTo = [timeslot: Timeslot]

  static mapping = {
    id(generator: 'assigned')
    timeslot index: 'Orderbook_Idx_Product_Timeslot'
    product index: 'Orderbook_Idx_Product_Timeslot'
  }

  static constraints = {
    id(nullable: false, blank: false, unique: true)
    dateExecuted(nullable: false)
    transactionId(nullable: false)
    product(nullable: false)
    timeslot(nullable: false)
    bid0(nullable: true, scale: Constants.DECIMALS)
    bid1(nullable: true, scale: Constants.DECIMALS)
    bid2(nullable: true, scale: Constants.DECIMALS)
    bid3(nullable: true, scale: Constants.DECIMALS)
    bid4(nullable: true, scale: Constants.DECIMALS)
    bid5(nullable: true, scale: Constants.DECIMALS)
    bid6(nullable: true, scale: Constants.DECIMALS)
    bid7(nullable: true, scale: Constants.DECIMALS)
    bid8(nullable: true, scale: Constants.DECIMALS)
    bid9(nullable: true, scale: Constants.DECIMALS)
    bidSize0(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    bidSize1(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    bidSize2(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    bidSize3(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    bidSize4(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    bidSize5(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    bidSize6(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    bidSize7(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    bidSize8(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    bidSize9(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    ask0(nullable: true, scale: Constants.DECIMALS)
    ask1(nullable: true, scale: Constants.DECIMALS)
    ask2(nullable: true, scale: Constants.DECIMALS)
    ask3(nullable: true, scale: Constants.DECIMALS)
    ask4(nullable: true, scale: Constants.DECIMALS)
    ask5(nullable: true, scale: Constants.DECIMALS)
    ask6(nullable: true, scale: Constants.DECIMALS)
    ask7(nullable: true, scale: Constants.DECIMALS)
    ask8(nullable: true, scale: Constants.DECIMALS)
    ask9(nullable: true, scale: Constants.DECIMALS)
    askSize0(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    askSize1(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    askSize2(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    askSize3(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    askSize4(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    askSize5(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    askSize6(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    askSize7(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    askSize8(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    askSize9(nullable: false, min: 0.0, scale: Constants.DECIMALS)

  }


  /**
   * Allows updating of the orderbook's {@code bid}, {@code bidSize}, {@code ask}, {@code
   * askSize} fields using a three dimensional array. Internally this array is mapped
   * to the bid1 .. bid10, bidSize1 .. bidSize10, ask1 .. ask10, askSize1 .. askSize10 fields
   * i.e. the array itself is not persisted to the database, i.e. it is transient.
   *
   * Array convention:
   *
   * BigDecimal[0][][] = bid
   * BigDecimal[1][][] = ask
   *
   * BigDecimal[][0][] = price
   * BigDecimal[][1][] = size
   *
   * BigDecimal[][][0] = Level 1
   * BigDecimal[][][1] = Level 2
   * BigDecimal[][][...] = Level ...
   * BigDecimal[][][9] = Level 10
   *
   * @param orderbookArray the three dimensional array that contains the orderbook data to be
   * persisted
   */
  public void setOrderbookArray(BigDecimal[][][] orderbookArray) {

    bid0 = orderbookArray[0][0][0]
    bid1 = orderbookArray[0][0][1]
    bid2 = orderbookArray[0][0][2]
    bid3 = orderbookArray[0][0][3]
    bid4 = orderbookArray[0][0][4]
    bid5 = orderbookArray[0][0][5]
    bid6 = orderbookArray[0][0][6]
    bid7 = orderbookArray[0][0][7]
    bid8 = orderbookArray[0][0][8]
    bid9 = orderbookArray[0][0][9]

    bidSize0 = orderbookArray[0][1][0] == null ? 0.0 : orderbookArray[0][1][0]
    bidSize1 = orderbookArray[0][1][1] == null ? 0.0 : orderbookArray[0][1][1]
    bidSize2 = orderbookArray[0][1][2] == null ? 0.0 : orderbookArray[0][1][2]
    bidSize3 = orderbookArray[0][1][3] == null ? 0.0 : orderbookArray[0][1][3]
    bidSize4 = orderbookArray[0][1][4] == null ? 0.0 : orderbookArray[0][1][4]
    bidSize5 = orderbookArray[0][1][5] == null ? 0.0 : orderbookArray[0][1][5]
    bidSize6 = orderbookArray[0][1][6] == null ? 0.0 : orderbookArray[0][1][6]
    bidSize7 = orderbookArray[0][1][7] == null ? 0.0 : orderbookArray[0][1][7]
    bidSize8 = orderbookArray[0][1][8] == null ? 0.0 : orderbookArray[0][1][8]
    bidSize9 = orderbookArray[0][1][9] == null ? 0.0 : orderbookArray[0][1][9]

    ask0 = orderbookArray[1][0][0]
    ask1 = orderbookArray[1][0][1]
    ask2 = orderbookArray[1][0][2]
    ask3 = orderbookArray[1][0][3]
    ask4 = orderbookArray[1][0][4]
    ask5 = orderbookArray[1][0][5]
    ask6 = orderbookArray[1][0][6]
    ask7 = orderbookArray[1][0][7]
    ask8 = orderbookArray[1][0][8]
    ask9 = orderbookArray[1][0][9]

    askSize0 = orderbookArray[1][1][0] == null ? 0.0 : orderbookArray[1][1][0]
    askSize1 = orderbookArray[1][1][1] == null ? 0.0 : orderbookArray[1][1][1]
    askSize2 = orderbookArray[1][1][2] == null ? 0.0 : orderbookArray[1][1][2]
    askSize3 = orderbookArray[1][1][3] == null ? 0.0 : orderbookArray[1][1][3]
    askSize4 = orderbookArray[1][1][4] == null ? 0.0 : orderbookArray[1][1][4]
    askSize5 = orderbookArray[1][1][5] == null ? 0.0 : orderbookArray[1][1][5]
    askSize6 = orderbookArray[1][1][6] == null ? 0.0 : orderbookArray[1][1][6]
    askSize7 = orderbookArray[1][1][7] == null ? 0.0 : orderbookArray[1][1][7]
    askSize8 = orderbookArray[1][1][8] == null ? 0.0 : orderbookArray[1][1][8]
    askSize9 = orderbookArray[1][1][9] == null ? 0.0 : orderbookArray[1][1][9]
  }

  /**
   * Allows access to the orderbook's {@code bid}, {@code bidSize}, {@code ask}, {@code
   * askSize} fields using a three dimensional array. Internally this array is generated
   * from the bid0 .. bid9, bidSize0 .. bidSize9, ask0 .. ask9, askSize0 .. askSize9
   * fields i.e. the array itself is dynamically generated.
   *
   * Array convention:
   *
   * BigDecimal[0][][] = bid
   * BigDecimal[1][][] = ask
   *
   * BigDecimal[][0][] = price
   * BigDecimal[][1][] = size
   *
   * BigDecimal[][][0] = Level 1
   * BigDecimal[][][1] = Level 2
   * BigDecimal[][][...] = Level ...
   * BigDecimal[][][9] = Level 10
   *
   * @return a three dimensional array representing the orderbook as described above
   */
  public BigDecimal[][][] getOrderbookArray() {

    BigDecimal[][][] orderbookArray = new BigDecimal[2][2][10];

    orderbookArray[0][0][0] = bid0
    orderbookArray[0][0][1] = bid1
    orderbookArray[0][0][2] = bid2
    orderbookArray[0][0][3] = bid3
    orderbookArray[0][0][4] = bid4
    orderbookArray[0][0][5] = bid5
    orderbookArray[0][0][6] = bid6
    orderbookArray[0][0][7] = bid7
    orderbookArray[0][0][8] = bid8
    orderbookArray[0][0][9] = bid9

    orderbookArray[0][1][0] = bidSize0 == null ? 0.0 : bidSize0
    orderbookArray[0][1][1] = bidSize1 == null ? 0.0 : bidSize1
    orderbookArray[0][1][2] = bidSize2 == null ? 0.0 : bidSize2
    orderbookArray[0][1][3] = bidSize3 == null ? 0.0 : bidSize3
    orderbookArray[0][1][4] = bidSize4 == null ? 0.0 : bidSize4
    orderbookArray[0][1][5] = bidSize5 == null ? 0.0 : bidSize5
    orderbookArray[0][1][6] = bidSize6 == null ? 0.0 : bidSize6
    orderbookArray[0][1][7] = bidSize7 == null ? 0.0 : bidSize7
    orderbookArray[0][1][8] = bidSize8 == null ? 0.0 : bidSize8
    orderbookArray[0][1][9] = bidSize9 == null ? 0.0 : bidSize9

    orderbookArray[1][0][0] = ask0
    orderbookArray[1][0][1] = ask1
    orderbookArray[1][0][2] = ask2
    orderbookArray[1][0][3] = ask3
    orderbookArray[1][0][4] = ask4
    orderbookArray[1][0][5] = ask5
    orderbookArray[1][0][6] = ask6
    orderbookArray[1][0][7] = ask7
    orderbookArray[1][0][8] = ask8
    orderbookArray[1][0][9] = ask9

    orderbookArray[1][1][0] = askSize0 == null ? 0.0 : askSize0
    orderbookArray[1][1][1] = askSize1 == null ? 0.0 : askSize1
    orderbookArray[1][1][2] = askSize2 == null ? 0.0 : askSize2
    orderbookArray[1][1][3] = askSize3 == null ? 0.0 : askSize3
    orderbookArray[1][1][4] = askSize4 == null ? 0.0 : askSize4
    orderbookArray[1][1][5] = askSize5 == null ? 0.0 : askSize5
    orderbookArray[1][1][6] = askSize6 == null ? 0.0 : askSize6
    orderbookArray[1][1][7] = askSize7 == null ? 0.0 : askSize7
    orderbookArray[1][1][8] = askSize8 == null ? 0.0 : askSize8
    orderbookArray[1][1][9] = askSize9 == null ? 0.0 : askSize9

    return orderbookArray
  }
}
