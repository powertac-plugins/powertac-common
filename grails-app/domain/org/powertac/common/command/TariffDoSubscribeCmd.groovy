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

import org.joda.time.DateTime
import org.powertac.common.*

 /**
 * Command object that represents a customer's request to subscribe
 * to either a published or an individually negotiated tariff.
 *
 * @author Carsten Block
 * @version 1.0 , Date: 01.12.10
 */
class TariffDoSubscribeCmd implements Serializable {
  String id = IdGenerator.createId()
  Competition competition
  Customer customer
  OldTariff tariff
  DateTime dateCreated = new DateTime()

  static belongsTo = [competition: Competition, customer: Customer, tariff: OldTariff]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    competition(nullable: false, validator: {val ->
      if (!val.current)  return [Constants.COMPETITION_INACTIVE]
      else return true
    })
    customer(nullable: false)
    tariff (nullable: false, validator: {val, obj ->
      if (val.state == Tariff.State.LEGACY) return [Constants.TARIFF_OUTDATED]
      else return true
    })
  }

  static mapping = {
    id (generator: 'assigned')
  }
}
