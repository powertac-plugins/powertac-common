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

import org.joda.time.LocalDateTime

class CashUpdate implements Serializable {

  String id = IdGenerator.createId()
  String transactionId
  Competition competition
  Broker broker
  BigDecimal relativeChange
  BigDecimal overallBalance
  String reason
  String origin
  Boolean latest
  LocalDateTime dateCreated = new LocalDateTime()

  static belongsTo = [broker: Broker, competition: Competition]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    competition(nullable: false)
    broker(nullable: false)
    relativeChange(nullable: false, scale: Constants.DECIMALS)
    overallBalance(nullable: false, scale: Constants.DECIMALS)
    reason(nullable: true)
    origin(nullable: true)
    dateCreated(nullable: false)
    transactionId(nullable: false, blank: false)
    latest (nullable: false)
  }

  static mapping = {
    id (generator: 'assigned')
    competition(index:'cu_competition_broker_latest_idx')
    broker(index:'cu_competition_broker_latest_idx')
    latest(index:'cu_competition_broker_latest_idx')
  }

  public String toString() {
    return "${broker}-${relativeChange}-${reason}-${dateCreated}"
  }
}
