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

/**
 *  A {@code TariffTransaction} instance represents the amount of energy consumed
 * ({@code amount < 0}) or produced {@code amount > 0} by some members of a 
 * specific customer model, in a specific timeslot, under a particular tariff.
 * Note that this is an immutable type, and therefore is not auditable.
 *
 * @author Carsten Block, KIT
 * @version 1.0 - 04/Feb/2011
 */
class TariffTransaction implements Serializable {
  
  enum TxType { PRODUCTION, CONSUMPTION, PERIODIC, SIGNUP, WITHDRAW }

  String id = IdGenerator.createId()
  
  /** Purpose of this transaction */
  TxType txType = TxType.CONSUMPTION

  /** The customerInfo or more precisely his meter that is being read */
  CustomerInfo customerInfo
  
  /** Number of individual customers involved */
  int customerCount = 0
  
  /** The Tariff that applies to this billing */
  Tariff tariff

  /** The timeslot for which this meter reading is generated */
  Timeslot timeslot

  /** The amount of energy consumer (> 0) or produced (<0) as metered */
  BigDecimal amount = 0.0
  
  /** The charge for this reading, according to the tariff:
   *  positive for credit to broker, negative for debit from broker */
  BigDecimal charge = 0.0

  static belongsTo = Timeslot

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    customerInfo (nullable: false)
    tariff (nullable: false)
    timeslot (nullable: false)
    //amount (nullable: false, scale: Constants.DECIMALS)
    //charge (nullable: false, scale: Constants.DECIMALS)
  }

  static mapping = {
    id (generator: 'assigned')
  }

  public String toString() {
    return "$customerInfo-$timeslot-$txType-$amount"
  }
}
