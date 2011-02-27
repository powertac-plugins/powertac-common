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

/**
 * A {@code CashUpdate} domain instance represents a single cash transaction
 * in a broker's cash account. Each update thus carries a {@code relativeChange}
 * that describes the concrete addition or subtraction of money as part of the
 * respective cash transaction as well as an {@code overallBalance}, which represents
 * the running total of the broker's cash account.
 *
 * @author Carsten Block, David Dauer
 * @version 1.1 - 02/27/2011
 */
class CashUpdate implements Serializable {

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

  /** A transactionId is e.g. generated during the execution of a trade in market and marks all domain instances in all domain classes that were created or changed during this transaction.  */
  String transactionId

  /** The competition this cash update belongs to  */
  //Competition competition = Competition.currentCompetition()

  /** The broker who owns the cash account in which this cash update takes place  */
  Broker broker

  /** The amount added to or deduced from the broker's cash account  */
  BigDecimal relativeChange

  /** The new running total for the broker's cash account  */
  BigDecimal overallBalance

  /** The reason why this cash transaction took place  */
  String reason

  /** The originator of this cash transaction, e.g. pda market, tax authority, or distribution utility  */
  String origin

  /** creation date of this cash update in local competition time  */
  Instant dateCreated = timeService?.getCurrentTime()

  static auditable = true
  
  static belongsTo = [broker: Broker]

  static constraints = {
    id(nullable: false, blank: false, unique: true)
    broker(nullable: false)
    relativeChange(nullable: false, scale: Constants.DECIMALS)
    overallBalance(nullable: false, scale: Constants.DECIMALS)
    reason(nullable: true)
    origin(nullable: true)
    dateCreated(nullable: false)
    transactionId(nullable: false, blank: false)
  }

  static mapping = {
    id(generator: 'assigned')
  }

  public String toString() {
    return "${broker}-${relativeChange}-${reason}-${dateCreated}"
  }
}
