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

package org.powertac.common

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Instant
import com.thoughtworks.xstream.annotations.*
import org.apache.commons.logging.LogFactory

/**
 * A competition instance represents a single PowerTAC competition and
 * at the same time serves as the place for all competition properties that can be
 * adjusted during competition setup (i.e. during server runtime but before competition start).
 * This is an immutable value type, and most parameters are included in the
 * parameterMap, rather than in individual fields.
 * @author Carsten Block, KIT; John Collins, U of Minnesota
 */
@XStreamAlias("competition")
class Competition //implements Serializable 
{
  private static final log = LogFactory.getLog(this)

  @XStreamAsAttribute
  String id = IdGenerator.createId()

  /** The competition's name */
  @XStreamAsAttribute
  String name

  /** Optional text that further describes the competition    */
  String description

  /** length of a timeslot in simulation minutes    */
  @XStreamAsAttribute
  Integer timeslotLength = 60

  /** Minimum number of timeslots, aka competition length    */
  @XStreamAsAttribute
  Integer minimumTimeslotCount = 96
  
  @XStreamAsAttribute
  Integer expectedTimeslotCount = 105

  /** concurrently open timeslots, i.e. time window in which broker actions like trading are allowed   */
  @XStreamAsAttribute
  Integer timeslotsOpen = 23

  /** # timeslots a timeslot gets deactivated ahead of the now timeslot (default: 1 timeslot, which (given default length of 60 min) means that e.g. trading is disabled 60 minutes ahead of time    */
  @XStreamAsAttribute
  Integer deactivateTimeslotsAhead = 1

  /** the start time of the simulation scenario, in sim time. */
  @XStreamAsAttribute
  Instant simulationBaseTime = new DateTime(2010, 6, 21, 0, 0, 0, 0, DateTimeZone.UTC).toInstant()

  /** the time-compression ratio for the simulation. So if we are running one-hour timeslots every 5 seconds, the rate would be 720 (=default).    */
  @XStreamAsAttribute
  Long simulationRate = 300l

  /** controls the values of simulation time values reported. If
   *  we are running one-hour timeslots, then the modulo should be one hour, expressed
   *  in milliseconds. If we are running one-hour timeslots but want to update time every
   *  30 minutes of simulated time, then the modulo would be 30*60*1000. Note that
   *  this will not work correctly unless the calls to updateTime() are made at
   *  modulo/rate intervals. Also note that the reported time is computed as
   *  rawTime - rawTime % modulo, which means it will never be ahead of the raw
   *  simulation time. Note that values other than the length of a timeslot have
   *  not been tested. */
  @XStreamAsAttribute
  Long simulationModulo = 60*60*1000
  
  // include the list of broker usernames
  @XStreamImplicit(itemFieldName = 'broker')
  List<String> brokers

  static hasMany = [plugins: PluginConfig, brokers: String]

  static constraints = {
    id(nullable: false, unique: true, blank: false)
    name(nullable: false, unique: true, blank: false)
    description(nullable: true)
    timeslotLength(nullable: false, min: 1)
    minimumTimeslotCount(nullable: false, min: 1)
    expectedTimeslotCount(nullable: false, validator: { etc, competition ->
      etc >= competition.minimumTimeslotCount ? true : ['expectedTimeslotCount.less.minimumTimeslotCount']
    })
    timeslotsOpen(nullable: false, min: 1, validator: { timeslotsOpen, competition ->
      timeslotsOpen <= (competition.minimumTimeslotCount - competition.deactivateTimeslotsAhead) ? true : ['timeslotsOpen.greater.timeslotsAhead']
    })
    deactivateTimeslotsAhead(nullable: false, min: 0, validator: {deactivateTimeslotsAhead, competition ->
      deactivateTimeslotsAhead <= (competition.minimumTimeslotCount- competition.timeslotsOpen) ? true : ['deactivateTimeslotsAhead.greater.timeslotsOpen']
    })
    simulationBaseTime(nullable: false)
    simulationModulo (nullable: false)
    simulationRate (nullable: false)
  }

  static mapping = {
    cache true
    id(generator: 'assigned')
    plugins lazy: false
  }

  public static currentCompetition() {
    def competitionList = Competition.list()
    return competitionList.size() > 0 ? competitionList.first() : null
  }

  public String toString() 
  {
    if (name == null)
      log.info "competition name is null"
    return name
  }
}
