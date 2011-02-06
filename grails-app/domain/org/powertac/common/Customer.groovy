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

class Customer implements Serializable {

  String id = IdGenerator.createId()
  Competition competition
  String name
  CustomerType customerType //gives a "rough" classification what type of customer to expect based on an enumeration, i.e. a fixed set of customer types
  Boolean multiContracting = false // describes whether or not this customer engages in multiple contracts at the same time
  Boolean canNegotiate =false // describes whether or not this customer negotiates over contracts
  BigDecimal upperPowerCap = 10.0 // >0: max power consumption (think consumer with fuse limit); <0: min power production (think nuclear power plant with min output)
  BigDecimal lowerPowerCap = 0.0 // >0: min power consumption (think refrigerator); <0: max power production (think power plant with max capacity)
  BigDecimal carbonEmissionRate = 0.0 // >=0 - gram CO2 per kW/h
  BigDecimal windToPowerConversion = 0.0 // measures how wind changes translate into load / generation changes of the customer
  BigDecimal tempToPowerConversion = 0.0 // measures how temperature changes translate into load / generation changes of the customer
  BigDecimal sunToPowerConversion = 0.0 // measures how sun intensity changes translate into load /generation changes of the customer

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
