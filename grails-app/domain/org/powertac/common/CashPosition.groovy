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
 * A {@code CashPosition} domain instance represents the current state of
 * a broker's cash account.
 *
 * @author Carsten Block, David Dauer
 * @version 1.1 - 02/27/2011
 */
class CashPosition implements Serializable {

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

  /** The broker who owns this cash account  */
  Broker broker

  /** The new running total for the broker's cash account  */
  BigDecimal overallBalance

  /** creation date of this cash update in local competition time  */
  Instant lastUpdate = timeService?.getCurrentTime()

  //static auditable = true
  
  static hasMany = [marketTransactions:MarketTransaction, tariffTransactions:TariffTransaction]
  
  static belongsTo = [broker: Broker]

  static constraints = {
    id(nullable: false, blank: false, unique: true)
    broker(nullable: false)
    overallBalance(nullable: false, scale: Constants.DECIMALS)
  }

  static mapping = {
    id(generator: 'assigned')
  }

  public String toString() {
    return "${broker}-${overallBalance}-${lastUpdate}"
  }
}
