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
 * A {@code CashUpdate} domain instance represents a single cash transaction
 * in a broker's cash account. Each update thus carries a {@code relativeChange}
 * that describes the concrete addition or subtraction of money as part of the
 * respective cash transaction as well as an {@code overallBalance}, which represents
 * the running total of the broker's cash account.
 *
 * @author Carsten Block, KIT
 * @version 1.0 - 04/Feb/2011
 */
class CashUpdate implements Serializable {

  def timeService

  String id = IdGenerator.createId()

  /** A transactionId is e.g. generated during the execution of a trade in market and marks all domain instances in all domain classes that were created or changed during this transaction. */
  String transactionId

  /** The competition this cash update belongs to */
  Competition competition = Competition.currentCompetition()

  /** The broker who owns the cash account in which this cash update takes place */
  Broker broker

  /** The amount added to or deduced from the broker's cash account */
  BigDecimal relativeChange

  /** The new running total for the broker's cash account */
  BigDecimal overallBalance

  /** The reason why this cash transaction took place */
  String reason

  /** The originator of this cash transaction, e.g. pda market, tax authority, or distribution utility */
  String origin

  /** flag that marks the latest cash transaction for a particular broker in a particular competition and that is used to speed up db queries */
  Boolean latest

  /** creation date of this cash update in local competition time */
  DateTime dateCreated = timeService.currentTime.toDateTime()

  static belongsTo = [broker: Broker, competition: Competition]

  static transients = ['timeService']

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    competition(nullable: false)
    broker(nullable: false)
    relativeChange(nullable: false, scale: Constants.DECIMALS)
    overallBalance(nullable: false, scale: Constants.DECIMALS)
    reason(nullable: true)
    origin(nullable: true)
    dateCreated(nullable: false)
    transactionId(nullable: false, blank: false)
    latest (nullable: false)
  }

  static mapping = {
    id (generator: 'assigned')
    competition(index:'cu_competition_broker_latest_idx')
    broker(index:'cu_competition_broker_latest_idx')
    latest(index:'cu_competition_broker_latest_idx')
  }

  public String toString() {
    return "${broker}-${relativeChange}-${reason}-${dateCreated}"
  }
}
