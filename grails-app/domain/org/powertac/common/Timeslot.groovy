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
import org.joda.time.Instant
import com.thoughtworks.xstream.annotations.*

 /**
 * A timeslot instance describes a duration in time (slot). Time slots are used (i) to
 * correlate tradeable products (energy futures) and trades in the market with a future time
 * interval where settlement (i.e. delivery / consumption) has to take place, (ii) to
 * correlate meter readings with a duration in time, (iii) to  allow tariffs to define
 * different consumption / production prices for different times of a day. Timeslots are
 * represented in server-broker communications by serial number and start time.
 *
 * @author Carsten Block
 * @version 1.0 - Feb,6,2011
 */
@XStreamAlias("slot")
class Timeslot //implements Serializable 
{
  static TimeService getTimeService()
  {
    ApplicationHolder.application.mainContext.timeService
  }
  
  @XStreamOmitField
  String id = IdGenerator.createId()

  /**
   * used to find succeeding / preceding timeslot instances
   * @see {@code Timeslot.next()} {@code Timeslot.previous()}
   */
  @XStreamAsAttribute
  Integer serialNumber

  /** flag that determines enabled state of the slot. 
   * E.g. in the market only orders for enabled timeslots 
   * are accepted. */
  @XStreamOmitField
  Boolean enabled = false

  /** start date and time of the timeslot */
  Instant startInstant

  /** end date and time of the timeslot */
  @XStreamOmitField
  Instant endInstant
  
  @XStreamOmitField
  List<Orderbook> orderbooks
  
  // explicit version so we can omit it
  @XStreamOmitField
  int version

  static auditable = true
  
  static transients = ['timeService']

  static hasMany = [orderbooks: Orderbook]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    serialNumber(nullable: false)
    enabled(nullable: false)
    startInstant(nullable: false)
    endInstant(nullable: false)
  }

  static mapping = {
    id (generator: 'assigned')
    enabled(index:'ts_enabled_idx')
  }

  public String toString() {
    return "${serialNumber}: ${startInstant} - ${endInstant} (${enabled})";
  }

  /**
   * Note that this scheme for finding the current timeslot relies on the
   * time granularity reported by the timeService being the same as the length
   * of a timeslot.
   */
  public static Timeslot currentTimeslot () 
  {
    return Timeslot.findByStartInstant(timeService.currentTime)
  }
  
  public static List<Timeslot> enabledTimeslots ()
  {
    return Timeslot.findAllByEnabled(true)
  }

  public Timeslot next() {
    return Timeslot.findBySerialNumber(this.serialNumber + 1)
  }

  public Timeslot previous() {
    return Timeslot.findBySerialNumber(this.serialNumber - 1)
  }

}
