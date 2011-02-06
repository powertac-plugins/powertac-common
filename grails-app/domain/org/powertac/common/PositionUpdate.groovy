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

/**
 * A {@code PositionUpdate} domain instance represents a single position change
 * in a broker's depot / portfolio. Each update thus carries a {@code relativeChange}
 * that describes the concrete addition or subtraction of a certain product in a certain
 * timeslot as part of the respective position transaction as well as an
 * {@code overallBalance}, which represents the running total of the broker's position of
 * the product for the timeslot.
 *
 * To determine the total amount of a product a certain broker owns for a certain product
 * in a certain competition, one simply searches for the instance marked as {@code
 * latest=true} discriminating by competition, product, timeslot, and broker (this is an
 * indexed search and thus fast).
 *
 * @author Carsten Block, KIT
 * @version 1.0 - 04/Feb/2011
 */

class PositionUpdate implements Serializable {

  def timeService

  String id = IdGenerator.createId()

  /** the competition this position update belongs to */
  Competition competition = Competition.currentCompetition()

  /** the product this position update belongs to */
  Product product

  /** the timeslot this position update belongs to */
  Timeslot timeslot

  /** the broker this position update belongs to */
  Broker broker

  /** The amount added (> 0) to or deduced (<0) from the broker's overall position for the specified product in the specified timeslot*/
  BigDecimal relativeChange

  /** The running total position the broker owns (>0) / owes (< 0) of the specified product in the specified timeslot */
  BigDecimal overallBalance


  /** The reason why this change took place */
  String reason


  /** The originator of this position change, e.g. pda market, tax authority, or distribution utility */
  String origin

  /** creation date of this cash update in local competition time */
  DateTime dateCreated = timeService?.currentTime?.toDateTime()

  /** A transactionId is e.g. generated during the execution of a trade in the market and marks all domain instances in all domain classes that were created or changed during this transaction. For example the execution of a shout lead to an addition of 100 units of product 1 to broker X portfolio. Then the shout instance that was matched is marked with the same transactionId as this positionUpdate so that both domain instances can be correlated during ex-post analysis */
  String transactionId

  /** flag that marks the latest position update for the specified product, timeslot and broker in the specified competition and that is used to speed up db queries */
  Boolean latest


  static transients = ['timeService']

  static belongsTo = [broker: Broker, product: Product, timeslot: Timeslot, competition: Competition]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    competition(nullable: false)
    product(nullable: false)
    timeslot(nullable: false)
    broker(nullable: false)
    relativeChange(nullable: false)
    overallBalance(nullable: false)
    reason(nullable: true)
    origin(nullable: true)
    dateCreated(nullable: false)
    transactionId(nullable: false)
    latest (nullable: false)
  }

  static mapping = {
    id (generator: 'assigned')
    competition(index:'pu_competition_product_timeslot_broker_latest_idx')
    product(index:'pu_competition_product_timeslot_broker_latest_idx')
    timeslot(index:'pu_competition_product_timeslot_broker_latest_idx')
    broker(index:'pu_competition_product_timeslot_broker_latest_idx')
    latest(index:'pu_competition_product_timeslot_broker_latest_idx')
  }

  public String toString() {
    return "${broker}-${product}-${relativeChange}-${reason}-${dateCreated}"
  }
}
