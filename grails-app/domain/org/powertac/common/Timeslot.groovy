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

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.joda.time.DateTime
import org.joda.time.Instant

 /**
 * A timeslot instance describes a duration in time (slot). Time slots are used (i) to
 * correlate tradeable products (energy futures) and trades in the market with a future time
 * interval where settlement (i.e. delivery / consumption) has to take place, (ii) to
 * correlate meter readings with a duration in time, (iii) to  allow tariffs to define
 * different consumption / production prices for different times of a day
 *
 * @author Carsten Block
 * @version 1.0 - Feb,6,2011
 */
class Timeslot implements Serializable 
{
  static TimeService getTimeService()
  {
    ApplicationHolder.application.mainContext.timeService
  }

  String id = IdGenerator.createId()

  /**
   * used to find succeeding / preceding timeslot instances
   * @see {@code Timeslot.next()} {@code Timeslot.previous()}
   */
  Integer serialNumber

  /** competition for which this timeslot is valid */
  Competition competition = Competition.currentCompetition()

  /** flag that determines enabled state of the slot. E.g. in the market only orders for enabled timeslots are accepted. */
  Boolean enabled = false

  /** indicates that this timeslot is the present / now() timeslot in the competition */
  Boolean current = false

  /** start date and time of the timeslot */
  DateTime startDateTime

  /** end date and time of the timeslot */
  DateTime endDateTime
  
  static transients = ['timeService']

  static belongsTo = [competition: Competition]

  static hasMany = [tariffTx: TariffTransaction, orderbooks: Orderbook, transactionLogs: TransactionLog, shouts: Shout]

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
    competition(index:'ts_competition_current_idx')
    current(index:'ts_competition_current_idx')
  }

  public String toString() {
    return "$startDateTime - $endDateTime";
  }

  /**
   * Note that this scheme for finding the current timeslot relies on the
   * time granularity reported by the timeService being the same as the length
   * of a timeslot.
   */
  public static Timeslot currentTimeslot() 
  {
    return Timeslot.findByStartDateTime(timeService.currentDateTime)
  }

  public Timeslot next() {
    return Timeslot.findByCompetitionAndSerialNumber(this.competition, this.serialNumber + 1)
  }

  public Timeslot previous() {
    return Timeslot.findByCompetitionAndSerialNumber(this.competition, this.serialNumber - 1)
  }

}
