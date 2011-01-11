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

class PositionUpdate implements Serializable {

  String id = IdGenerator.createId()
  Competition competition
  Product product
  Timeslot timeslot
  Broker broker
  BigDecimal relativeChange
  BigDecimal overallBalance
  String reason
  String origin
  LocalDateTime dateCreated = new LocalDateTime();
  String transactionId
  Boolean latest

  static belongsTo = [broker: Broker, product: Product, timeslot: Timeslot, competition: Competition]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    competition(nullable: false)
    product(nullable: false)
    timeslot(nullable: false)
    broker(nullable: false)
    relativeChange(nullable: false)
    overallBalance(nullable: false)
    reason(nullable: true)
    origin(nullable: true)
    dateCreated(nullable: false)
    transactionId(nullable: false)
    latest (nullable: false)
  }

  static mapping = {
    id (generator: 'assigned')
    competition(index:'pu_competition_product_timeslot_broker_latest_idx')
    product(index:'pu_competition_product_timeslot_broker_latest_idx')
    timeslot(index:'pu_competition_product_timeslot_broker_latest_idx')
    broker(index:'pu_competition_product_timeslot_broker_latest_idx')
    latest(index:'pu_competition_product_timeslot_broker_latest_idx')
  }

  public String toString() {
    return "${broker}-${product}-${relativeChange}-${reason}-${dateCreated}"
  }
}
