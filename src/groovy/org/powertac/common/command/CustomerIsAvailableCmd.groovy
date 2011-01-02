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

package org.powertac.common.command

import org.powertac.common.enumerations.CustomerType
import org.powertac.common.Competition

/**
 * Command object that provides information about
 * a specific customer instance configured in a
 * competition
 *
 * @author Carsten Block
 * @version 1.0 , Date: 02.01.11
 */
class CustomerIsAvailableCmd {
  String id
  Competition competition
  String name
  CustomerType customerType //gives a "rough" classification what type of customer to expect based on an enumeration, i.e. a fixed set of customer types
  Boolean multiContracting // describes whether or not this customer engages in multiple contracts at the same time
  Boolean canNegotiate // describes whether or not this customer negotiates over contracts
  BigDecimal upperPowerCap // >0: max power consumption (think consumer with fuse limit); <0: min power production (think nuclear power plant with min output)
  BigDecimal lowerPowerCap // >0: min power consumption (think refrigerator); <0: max power production (think power plant with max capacity)
  BigDecimal carbonEmissionRate // >=0 - gram CO2 per kW/h
  BigDecimal windToPowerConversion // measures how wind changes translate into load / generation changes of the customer
  BigDecimal tempToPowerConversion // measures how temperature changes translate into load / generation changes of the customer
  BigDecimal sunToPowerConersion // measures how sun intensity changes translate into load /generation changes of the customer
}
