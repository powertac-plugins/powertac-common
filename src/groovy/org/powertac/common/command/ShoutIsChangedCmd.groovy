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
import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.ModReasonCode
import org.powertac.common.enumerations.OrderType

 /**
 * Command object that contains all data of a
 * particular shout from the Power TAC wholesale
 * market. It is an "enriched" version of the
 * <code>ShoutDoCreateCmd</code> a broker originially
 * sends to the server and used to report back the current
 * execution status of a shout to the broker.
 *
 * @author Carsten Block
 * @version 1.0, Date: 01.12.10
 */
class ShoutIsChangedCmd {
  String id
  String competitionId
  String brokerId
  String productId
  String timeslotId
  BuySellIndicator buySellIndicator
  BigDecimal quantity
  BigDecimal limitPrice
  BigDecimal executionQuantity
  BigDecimal executionPrice
  OrderType orderType
  LocalDateTime dateCreated
  LocalDateTime dateMod
  ModReasonCode modReasonCode
  Boolean outdated
  String transactionId
  String shoutId
  String comment
  Boolean latest
}
