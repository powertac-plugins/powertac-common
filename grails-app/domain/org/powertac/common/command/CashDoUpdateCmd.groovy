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

package org.powertac.common.command

import org.joda.time.DateTime
import org.powertac.common.Broker
import org.powertac.common.Competition
import org.powertac.common.Constants
import org.powertac.common.IdGenerator

/**
 * Command object that represents an cash transaction
 * (add / deduce money) that should be executed on a
 * specific broker cash account for a given reason.
 *
 * @author Carsten Block
 * @version 1.0 , Date: 02.12.10
 */
class CashDoUpdateCmd implements Serializable {
  String id = IdGenerator.createId()
  Competition competition
  Broker broker
  BigDecimal relativeChange
  String transactionId
  String reason
  String origin
  DateTime dateCreated = new DateTime()

  static belongsTo = [competition: Competition, broker: Broker]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    competition(nullable: false, validator: {val ->
      if (!val.current) return [Constants.COMPETITION_INACTIVE]
      else return true
    })
    broker(nullable: false)
    relativeChange(nullable: false, scale: Constants.DECIMALS)
    transactionId(nullable: true)
    reason(nullable: true)
    origin(nullable: true)
  }

  static mapping = {
    id (generator: 'assigned')
  }
}
