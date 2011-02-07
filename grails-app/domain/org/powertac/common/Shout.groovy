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

import org.joda.time.DateTime
import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.ModReasonCode
import org.powertac.common.enumerations.OrderType

 /**
 * A shout domain instance represents a market or a limit order in the PowerTAC wholesale
 * market. More precisely it represents a single state of this specific order. Each time a
 * change occurs, the shout object is cloned, the {@code latest} property is set to false
 * for the original object (which remains unchanged otherwise) and all necessary changes
 * are put into the newly cloned shout instance. Like this, each update (e.g. a partial
 * execution, a deletion by system or by user can be tracked by selecting all
 * shout instances from the database that carry the same {@code shoutId}, which is the
 * common identifier for all instances that belong to the same original shout (note that
 * this is not the {@code id} field, which is different (and unique) for each shout
 * instance and servers as primary key for the database.
 *
 * Note: The word "shout" was chosen to avoid db level incompatibilities due to the word
 * "order" being a reserved word in most SQL dialects.
 *
 * @author Carsten Block
 * @version 1.0, Feb, 6 2011
 */
class Shout implements Serializable {

  def timeService

  String id = IdGenerator.createId()

  /** the competition this shout belongs to */
  Competition competition = Competition.currentCompetition()

  /** the broker who created this shout */
  Broker broker

  /** the product that should be bought or sold */
  Product product

  /** the timeslot for which the product should be bought or sold */
  Timeslot timeslot

  /** flag that indicates if this shout is a buy or sell order */
  BuySellIndicator buySellIndicator

  /** the product quantity to buy or sell */
  BigDecimal quantity

  /** the limit price, i.e. the max. acceptable buy or min acceptable sell price */
  BigDecimal limitPrice

  /** the last executed quantity (if equal to {@code quantity} the shout is fully executed otherwise it is partially executed */
  BigDecimal executionQuantity

  /** the last execution price */
  BigDecimal executionPrice

  /** either MARKET or LIMIT order */
  OrderType orderType = OrderType.MARKET

  /** the simulation time when the original shout instance was first created */
  DateTime dateCreated = timeService?.currentTime?.toDateTime()

  /** the latest modification time of the shout */
  DateTime dateMod = this.dateCreated

  /** the reason for the latest modifcation to the shout instance */
  ModReasonCode modReasonCode = ModReasonCode.INSERT

  /** A transactionId is generated during the execution of the shout and marks all domain instances in all domain classes that were created or changed during this single transaction (e.g. corresponding transactionLog, CashUpdate, or PositionUpdate instances). Later on this id allows for correlation of the different domain class instances during ex post analysis*/
  String transactionId

  /** marks all shout instances that belong to the same original shout. On every shout change a new clone of the domain instance is generated and shoutId serves as common identifier among all these clones.*/
  String shoutId

  /** optional comment that can be used for example to further describe why a shout was deleted by system (e.g. during deactivaton of a timeslot) */
  String comment

  /** flag that marks the latest shout instance: Purpose: speed up db queries */
  Boolean latest

  static belongsTo = [broker: Broker, product: Product, timeslot: Timeslot, competition: Competition]

  static transients = ['timeService']

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    competition(nullable: false, validator: {val ->
      val?.current ? true : [Constants.COMPETITION_INACTIVE]
    })
    broker(nullable: false)
    product(nullable: false)
    timeslot(nullable: false)
    buySellIndicator(nullable: false)
    quantity(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    limitPrice(nullable: true, min: 0.0, Scale: Constants.DECIMALS, validator: {val, obj ->
      if (obj.orderType == OrderType.LIMIT && val == null) return [Constants.SHOUT_LIMITORDER_NULL_LIMIT]
      if (obj.orderType == OrderType.MARKET && val != null) return [Constants.SHOUT_MARKETORDER_WITH_LIMIT]
      return true
    })
    executionQuantity(nullable: true, min: 0.0, scale: Constants.DECIMALS)

    executionPrice(nullable: true, min: 0.0, scale: Constants.DECIMALS)
    orderType(nullable: false)
    dateCreated(nullable: false)
    dateMod(nullable: false)
    modReasonCode(nullable: false)
    transactionId(nullable: false)
    shoutId(nullable: false)
    comment(nullable: true)
    latest (nullable: false)
  }

  static mapping = {
    id (generator: 'assigned')
    shoutId (index: 'shout_id_idx,shout_id_latest_idx')
    latest (index: 'shout_id_latest_idx')
  }

  /**
   * Special type of cloning for shout instances. This method clones the shout instance and...
   * 1) updates the modReasonCode field in the cloned instance to the value provided as method param
   * 2) sets the 'latest' property in the original instance to false (and persists the change to the db)
   * 3) sets the 'latest' property in the cloned instance to true
   * 4) keeps the 'dateCreated' property in the cloned instance unchanged
   * 5) sets 'dateMod' property in the cloned instance to *now* (in simulation time)
   * 6) sets 'transactionId' property in the cloned instance to null
   *
   * Note: the original shout instance is saved (in order to persist the
   * updated 'latest' field property while the cloned shout instance *is not saved*!!
   *
   * @param newModReasonCode new modReasonCode to use in the cloned shout instance
   * @return cloned shout instance where the cloned instance is changed as described above
   */
  public Shout initModification(ModReasonCode newModReasonCode) {
    def newShout = new Shout()
    newShout.competition = this.competition
    newShout.broker = this.broker
    newShout.product = this.product
    newShout.timeslot = this.timeslot
    newShout.buySellIndicator = this.buySellIndicator
    newShout.quantity = this.quantity
    newShout.limitPrice = this.limitPrice
    newShout.executionQuantity = this.executionQuantity
    newShout.executionPrice = this.executionPrice
    newShout.orderType = this.orderType
    newShout.dateCreated = this.dateCreated
    newShout.dateMod = timeService.currentTime.toDateTime()
    newShout.modReasonCode = newModReasonCode
    newShout.transactionId = null
    newShout.shoutId = this.shoutId
    newShout.comment = this.comment
    newShout.latest = true
    this.latest = false
    this.save()
    return newShout
  }
}
