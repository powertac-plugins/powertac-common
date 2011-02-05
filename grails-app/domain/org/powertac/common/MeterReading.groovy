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
 *  A {@code MeterReading} instance represents the amount of energy consumed
 * ({@code amount < 0}) or produced {@code amount > 0} by a specific customer
 * in a specific timeslot of a specific competition
 *
 * @author Carsten Block, KIT
 * @version 1.0 - 04/Feb/2011
 */
class MeterReading implements Serializable {

  String id = IdGenerator.createId()

  /** The competition in which this meter reading was generated */
  Competition competition = Competition.currentCompetition()

  /** The customer or more precisely his meter that is being read */
  Customer customer

  /** The timeslot for which this meter reading is generated */
  Timeslot timeslot

  /** The amount of energy consumer (> 0) or produced (<0) as metered */
  BigDecimal amount

  /** flag that marks the latest meter reading for a particular broker in a particular competition and timeslot; used to speed up db queries */
  Boolean latest

  static belongsTo = [competition: Competition, customer: Customer, timeslot: Timeslot]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    competition (nullable: false)
    customer (nullable: false)
    timeslot (nullable: false)
    amount (nullable: false, scale: Constants.DECIMALS)
    latest (nullable: false)
  }

  static mapping = {
    id (generator: 'assigned')
  }

  public String toString() {
    return "$customer-$timeslot-$amount"
  }
}
