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

import org.powertac.common.enumerations.CustomerType

/**
 * A {@code Customer} instance represents a customer model (i.e. a consumer or a producer)
 * within a specific competition. The customer data stored is published to all brokers in
 * the respective competition in order to provide them with an brief overview on what type
 * of customers participate in the specific competition.
 *
 * @author Carsten Block, KIT
 * @version 1.0 - 04/Feb/2011
 */
class Customer implements Serializable {

  String id = IdGenerator.createId()

  /** Competition this customer is defined for */
  Competition competition = Competition.currentCompetition()

  /** Name of the customer model */
  String name

  /** gives a "rough" classification what type of customer to expect based on an enumeration, i.e. a fixed set of customer types */
  CustomerType customerType

  /** describes whether or not this customer engages in multiple contracts at the same time */
  Boolean multiContracting

  /** describes whether or not this customer negotiates over contracts */
  Boolean canNegotiate

  /** >0: max power consumption (think consumer with fuse limit); <0: min power production (think nuclear power plant with min output) */
  BigDecimal upperPowerCap

  /** >0: min power consumption (think refrigerator); <0: max power production (think power plant with max capacity) */
  BigDecimal lowerPowerCap

  /** >=0 - gram CO2 per kW/h */
  BigDecimal carbonEmissionRate

  /** measures how wind changes translate into load / generation changes of the customer */
  BigDecimal windToPowerConversion

  /** measures how temperature changes translate into load / generation changes of the customer */
  BigDecimal tempToPowerConversion

  /** measures how sun intensity changes translate into load /generation changes of the customer */
  BigDecimal sunToPowerConversion

  //TODO: Possibly add parameters as the ones below that provide descriptive statistical information on historic power consumption / production of the customer
  /*
  BigDecimal annualPowerAvg // >0: customer is on average a consumer; <0 customer is on average a producer
  private BigDecimal minResponsiveness // define factor characterizing minimal responsiveness to price signals, i.e. "elasticity"

  private BigDecimal maxResponsiveness;   // define factor characterizing max responsiveness to price signals, i.e. "elasticity"
  */

  static belongsTo = [competition: Competition]

  static hasMany = [meterReadings: MeterReading] //, tariffs: Tariff]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    competition(nullable: false)
    name (blank: false, unique: true)
    customerType(nullable: false)
    multiContracting (nullable: false)
    canNegotiate (nullable: false)
    upperPowerCap (nullable: false, scale: Constants.DECIMALS)
    lowerPowerCap (nullable: false, scale: Constants.DECIMALS)
    carbonEmissionRate (nullable: false, scale: Constants.DECIMALS)
    windToPowerConversion (nullable: false, scale: Constants.DECIMALS)
    tempToPowerConversion (nullable: false, scale: Constants.DECIMALS)
    sunToPowerConversion (nullable: false, scale: Constants.DECIMALS)
  }

  static mapping = {
    id (generator: 'assigned')
  }

  public String toString() {
    return name
  }
}
