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

import org.joda.time.LocalDateTime

/**
 * TODO: Add Description
 *
 * @author Carsten Block
 * @version 1.0, Date: 02.01.11
 */
abstract class AbstractTariff {
  String id
  String tariffId
  String competitionId
  String brokerId

  Boolean isDynamic
  Boolean isNegotiable

  BigDecimal signupFee //one-time fee (>0) / reward (<0) charged / paid for contract signup
  BigDecimal earlyExitFee //payed by the customer if he leaves the contract before its regular end
  BigDecimal baseFee //daily base Fee (>0) / reward (<0) charged at the end of each day
  Integer changeLeadTime //time in timeslots a tariff change has to be announced in advance

  /*
    * These attributes allow a broker to specify minimum and maximum
    * contract runtimes, e.g. min one month
    */
  LocalDateTime contractStartDate //defines a specific contract start Date, may be specified by customer or broker
  LocalDateTime contractEndDate //defines a specific contract end Date, may be specified by customer or broker
  Integer minimumContractRuntime //null or min days; has to be consistent with contractEndTime - contractStartTime
  Integer maximumContractRuntime //null or max days; has to be consistent with contractEndTime - contractStartTime

  /*
   * These attributes allow modeling a two-part tariff
   */
  BigDecimal powerConsumptionThreshold   // >0: threshold consumption level; consumption exceeding this threshold leads to a surcharge (see below) being added to the time dependent powerConsumptionPrice
  BigDecimal powerConsumptionSurcharge   // % fee added to hourly powerConsumptionPrice if consumption exceeds threshold (see above)

  BigDecimal powerProductionThreshold    // >0: threshold production level; production exceeding this threshold leads to a surcharge (see below) being added to the time dependent powerProductionPrice
  BigDecimal powerProductionSurcharge    // % fee added to hourly powerProductionPrice if production exceeds threshold (see above)

  BigDecimal powerConsumptionPrice0
  BigDecimal powerConsumptionPrice1
  BigDecimal powerConsumptionPrice2
  BigDecimal powerConsumptionPrice3
  BigDecimal powerConsumptionPrice4
  BigDecimal powerConsumptionPrice5
  BigDecimal powerConsumptionPrice6
  BigDecimal powerConsumptionPrice7
  BigDecimal powerConsumptionPrice8
  BigDecimal powerConsumptionPrice9
  BigDecimal powerConsumptionPrice10
  BigDecimal powerConsumptionPrice11
  BigDecimal powerConsumptionPrice12
  BigDecimal powerConsumptionPrice13
  BigDecimal powerConsumptionPrice14
  BigDecimal powerConsumptionPrice15
  BigDecimal powerConsumptionPrice16
  BigDecimal powerConsumptionPrice17
  BigDecimal powerConsumptionPrice18
  BigDecimal powerConsumptionPrice19
  BigDecimal powerConsumptionPrice20
  BigDecimal powerConsumptionPrice21
  BigDecimal powerConsumptionPrice22
  BigDecimal powerConsumptionPrice23

  BigDecimal powerProductionPrice0
  BigDecimal powerProductionPrice1
  BigDecimal powerProductionPrice2
  BigDecimal powerProductionPrice3
  BigDecimal powerProductionPrice4
  BigDecimal powerProductionPrice5
  BigDecimal powerProductionPrice6
  BigDecimal powerProductionPrice7
  BigDecimal powerProductionPrice8
  BigDecimal powerProductionPrice9
  BigDecimal powerProductionPrice10
  BigDecimal powerProductionPrice11
  BigDecimal powerProductionPrice12
  BigDecimal powerProductionPrice13
  BigDecimal powerProductionPrice14
  BigDecimal powerProductionPrice15
  BigDecimal powerProductionPrice16
  BigDecimal powerProductionPrice17
  BigDecimal powerProductionPrice18
  BigDecimal powerProductionPrice19
  BigDecimal powerProductionPrice20
  BigDecimal powerProductionPrice21
  BigDecimal powerProductionPrice22
  BigDecimal powerProductionPrice23
}
