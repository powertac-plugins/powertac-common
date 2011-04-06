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

package org.powertac.common

import org.powertac.common.transformer.BrokerConverter
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.joda.time.Instant
import com.thoughtworks.xstream.annotations.*

/**
 * A {@code CashPosition} domain instance represents the current state of
 * a broker's cash account.
 *
 * @author Carsten Block, David Dauer
 * @version 1.1 - 02/27/2011
 */
@XStreamAlias("cash")
class CashPosition //implements Serializable 
{

  //def timeService
  /**
   * Retrieves the timeService (Singleton) reference from the main application context
   * This is necessary as DI by name (i.e. def timeService) stops working if a class
   * instance is deserialized rather than constructed.
   * Note: In the code below you can can still user timeService.xyzMethod()
   */
  //private getTimeService() {
  //  ApplicationHolder.application.mainContext.timeService
  //}
  // Server-generated type, don't need UUID
  //String id = IdGenerator.createId()

  /** The broker who owns this cash account  */
  @XStreamConverter(BrokerConverter)
  Broker broker

  /** The new running total for the broker's cash account  */
  @XStreamAsAttribute
  BigDecimal balance = 0.0

  @XStreamOmitField
  List<MarketTransaction> marketTransactions
  
  @XStreamOmitField
  List<TariffTransaction> tariffTransactions
  
  // explicit version so we can omit it
  @XStreamOmitField
  int version

  static hasMany = [marketTransactions:MarketTransaction, tariffTransactions:TariffTransaction]
  
  static belongsTo = [broker: Broker]

  static constraints = {
    //id(nullable: false, blank: false, unique: true)
    broker(nullable: false)
    balance(nullable: false, scale: Constants.DECIMALS)
    //lastUpdate(nullable: true)
  }

  //static mapping = {
  //  id(generator: 'assigned')
  //}

  String toString() {
    return "${broker}-${balance}" //-${lastUpdate}"
  }
  
  /**
   * Updates the balance in this account by the specified amount,
   * returns the resulting balance. A withdrawal is negative,
   * deposit is positive.
   */
  BigDecimal deposit (BigDecimal amount)
  {
    balance += amount
    //lastUpdate = timeService.currentTime
    return balance
  }
}
