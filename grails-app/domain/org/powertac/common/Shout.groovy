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
import org.powertac.common.enumerations.ModReasonCode
import org.powertac.common.enumerations.OrderType
import org.powertac.common.enumerations.ProductType
import org.powertac.common.transformer.BrokerConverter
import org.powertac.common.transformer.TimeslotConverter
import com.thoughtworks.xstream.annotations.*

/**
 * A shout domain instance represents a market (no price specified) or a limit (min/max
 * price specified) order in the PowerTAC wholesale
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
 */
@XStreamAlias("shout")
class Shout //implements Serializable 
{

  //def timeService
  private getTimeService() {
    ApplicationHolder.application.mainContext.timeService
  }

  @XStreamAsAttribute
  String id = IdGenerator.createId()

  /** the broker who created this shout */
  @XStreamConverter(BrokerConverter)
  Broker broker

  /** the product that should be bought or sold */
  @XStreamAsAttribute
  ProductType product

  /** the timeslot for which the product should be bought or sold */
  @XStreamAsAttribute
  @XStreamConverter(TimeslotConverter)
  Timeslot timeslot

  /** flag that indicates if this shout is a buy or sell order */
  @XStreamAsAttribute
  BuySellIndicator buySellIndicator

  /** the product quantity to buy or sell */
  @XStreamAsAttribute
  BigDecimal quantity

  /** the limit price, i.e. the max. acceptable buy or min acceptable sell price */
  @XStreamAsAttribute
  BigDecimal limitPrice

  /** the last executed quantity (if equal to {@code quantity} the shout is fully executed otherwise it is partially executed */
  @XStreamAsAttribute
  BigDecimal executionQuantity

  /** the last execution price */
  @XStreamAsAttribute
  BigDecimal executionPrice

  /** either MARKET or LIMIT order */
  // not needed - presence of limit price indicates limit order
  //OrderType orderType = OrderType.MARKET

  /** the simulation time when the original shout instance was first created */
  Instant dateCreated = timeService.getCurrentTime()

  /** the latest modification time of the shout */
  Instant dateMod = this.dateCreated

  /** the reason for the latest modifcation to the shout instance */
  @XStreamAsAttribute
  ModReasonCode modReasonCode = ModReasonCode.INSERT

  /** A transactionId is generated during the execution of the shout and marks all domain instances in all domain classes that were created or changed during this single transaction (e.g. corresponding transactionLog, CashUpdate, or MarketPosition instances). Later on this id allows for correlation of the different domain class instances during ex post analysis*/
  String transactionId

  /** optional comment that can be used for example to further describe why a shout was deleted by system (e.g. during deactivaton of a timeslot) */
  String comment
  
  static auditable = true

  static belongsTo = [broker: Broker, timeslot: Timeslot]

  static transients = ['timeService']

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    broker(nullable: false)
    product(nullable: true)
    timeslot(nullable: false)
    buySellIndicator(nullable: false)
    quantity(nullable: false, min: 0.0, scale: Constants.DECIMALS)
    limitPrice(nullable: true, min: 0.0, Scale: Constants.DECIMALS)
    executionQuantity(nullable: true, min: 0.0, scale: Constants.DECIMALS)

    executionPrice(nullable: true, min: 0.0, scale: Constants.DECIMALS)
    //orderType(nullable: true)
    dateCreated(nullable: true)
    dateMod(nullable: true)
    modReasonCode(nullable: true)
    transactionId(nullable: true)
    comment(nullable: true)
  }

  static mapping = {
    cache(true)
    id (generator: 'assigned')
  }

  // JEC -- do we need this at all?
  /**
   * Updates shout instance:
   * 1) updates the modReasonCode field in the cloned instance to the value provided as method param
   * 2) keeps the 'dateCreated' property in the cloned instance unchanged
   * 3) sets 'dateMod' property in the cloned instance to *now* (in simulation time)
   * 4) (does not) sets 'transactionId' property in the cloned instance to null
   *
   * @param newModReasonCode new modReasonCode to use in the cloned shout instance
   * @return cloned shout instance where the cloned instance is changed as described above
   */
  public Shout initModification(ModReasonCode newModReasonCode) {
    this.dateMod = timeService.currentTime
    this.modReasonCode = newModReasonCode
    return this
  }
}
