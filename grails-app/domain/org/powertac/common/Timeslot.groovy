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

class Timeslot {

  String id = IdGenerator.createId()
  Long serialNumber
  Competition competition
  Boolean enabled = false
  Boolean current = false
  LocalDateTime startDateTime = new LocalDateTime()
  LocalDateTime endDateTime = new LocalDateTime()

  SortedSet orderbooks

  static belongsTo = [competition: Competition]

  static hasMany = [meterReadings: MeterReading, orderbooks: Orderbook, transactionLogs: TransactionLog, shouts: Shout]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    serialNumber(nullable: false)
    competition(nullable: false)
    enabled(nullable: false)
    current(nullable: false)
    startDateTime(nullable: false)
    endDateTime(nullable: false)
  }

  static mapping = {
    id (generator: 'assigned')
  }

  public String toString() {
    return "$startDateTime - $endDateTime";
  }

  public Timeslot next() {
    return Timeslot.findByCompetitionAndSerialNumber(this.competition, this.serialNumber + 1l)
  }

  public Timeslot previous() {
    return Timeslot.findByCompetitionAndSerialNumber(this.competition, this.serialNumber - 1l)
  }

}
