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

import org.joda.time.DateTime
import org.joda.time.DateTimeZone;
import org.powertac.common.enumerations.CompetitionStatus

 /**
 * A competition instance represents a single PowerTAC competition and
 * at the same time serves as the place for all competition properties that can be
 * adjusted during competition setup (i.e. during server runtime but before competition start).
 *
 * @author Carsten Block, KIT
 * @version 1.0 - 04/Feb/2011
 */
class Competition implements Serializable {

  String id = IdGenerator.createId()

  /** The competition's name    */
  String name

  /** enabled / disabled state of the competition    */
  Boolean enabled = false

  /** The function {@code Competition.currentCompetition ( )} returns the first competition instance that has set current flag to true - make sure only one competition per server is market current=true    */
  Boolean current = false

  /** Lifecycle state of the competition    */
  CompetitionStatus competitionStatus = CompetitionStatus.Created

  /** Optional text that further describes the competition    */
  String description

  /** seconds before and initial deactivation / activation cylce is started (default: 60)    */
  Long initialDuration = 60l

  /** seconds between deactivation of oldest and activation of newest product (default: 60)    */
  Long durationBetweenShifts = 60l

  /** length of a timeslot in minutes (default: 60)    */
  Integer timeslotLength = 60

  /** Overall timeslots in a competition, aka competition length    */
  Integer timeslotsOverall = 24

  /** concurrently open timeslots, i.e. time window in which broker actions like trading are allowed   */
  Integer timeslotsOpen = 12

  /** # timeslots a timeslot gets deactivated ahead of the now timeslot (default: 1 timeslot, which (given default length of 60 min) means that e.g. trading is disabled 60 minutes ahead of time    */
  Integer deactivateTimeslotsAhead = 1

  /** the start time of the simulation scenario. So if we are simulating a period in the summer of 2007, base might be 2007-06-21-12:00.    */
  DateTime simulationStartTime = new DateTime(DateTimeZone.UTC)

  /** the start time of the simulation run.    */
  DateTime simulationBaseTime = new DateTime(DateTimeZone.UTC)

  /** the time-compression ratio for the simulation. So if we are running one-hour timeslots every 5 seconds, the rate would be 720 (=default).    */
  Long simulationRate = 720l

/** controls the values of simulation time values reported. If
 *     we are running one-hour timeslots, then the modulo should be one hour, expressed
 *     in milliseconds. If we are running one-hour timeslots but want to update time every
 *     30 minutes of simulated time, then the modulo would be 30*60*1000. Note that
 *     this will not work correctly unless the calls to updateTime() are made at
 *     modulo/rate intervals. Also note that the reported time is computed as
 *     rawTime - rawTime % modulo, which means it will never be ahead of the raw
 *     simulation time (default: 1800000).
 */
  Long simulationModulo = 1800000l

  /** the (real-world) date time this competition instance was initially created  */
  DateTime dateCreated = new DateTime(DateTimeZone.UTC)

  /** the (real-world) date time this competition instance was last updated  */
  DateTime lastUpdated = new DateTime(DateTimeZone.UTC)

  /** cost for imbalances caused by too much energy in a broker portfolio */
  BigDecimal balancingCostOver = 0
  /** cost for imbalances caused by too little energy in a broker portfolio */
  BigDecimal balancingCostUnder = 0

  /** switch to enable or disable liquidity provider service */
  Boolean liquidityProviderEnabled = true

  /** effectively defines the spread width, i.e. the difference between generated bid and ask shout prices */
  BigDecimal premium = 0.01

  /** offset added by the liquidity provider to the national energy market prices */
  BigDecimal fixCost = 0

  /** REST base url for retrieving market data - default to the REST interface of the IISM dato store */
  String profileUrl = 'http://ibwmarkets.iw.uni-karlsruhe.de/ps/rest'

  /** Name of the time series in the data profile to be used */
  String timeseriesName = 'Average Price'

  /** defines the profile to use for market data retrieval; default 2007 EEX data profile */
  Long profileId = 82

  /** auth token to use for authentication against the iism data store; the default token belongs to a special PowerTAC server account defined in the IISM data store*/
  String authToken = 'FYtlyOAmeCmfdVbgkm37oQJsyZ0U1d2u'

  /** regular line capacity at the point of common coupling the liquidity provider can use to bring in energy from outside (i.e. national energy market) into the simulated region or to transfer outside */
  Integer regularCapacity = Integer.MAX_VALUE - 1

  /** max technical line capacity*/
  Integer maxCapacity = Integer.MAX_VALUE

  /** percentage price increase (premium) that the liquidity provider will charge in situations where demand exeeds {@code regularCapacity} of the line (default: 5%)*/
  BigDecimal percentPriceIncrease = 0.05

  /** scaling factor to convert between prices stored in the data store and local prices */
  BigDecimal priceScaling = 1

  static hasMany = [brokers: Broker, customers: CustomerInfo, orderbooks: Orderbook, positionUpdates: PositionUpdate, products: Product, shouts: Shout, tariffs: Tariff, timeslots: Timeslot, transactionLogs: TransactionLog]

  static constraints = {
    id(nullable: false, unique: true, blank: false)
    name(nullable: false, unique: true, blank: false)
    enabled(nullable: false)
    current(nullable: false)
    competitionStatus(nullable: false)
    description(nullable: true)
    initialDuration(nullable: false, min: 1l)
    durationBetweenShifts(nullable: false, min: 1l)
    timeslotLength(nullable: false, min: 1)
    timeslotsOverall(nullable: false, min: 1)
    timeslotsOpen(nullable: false, min: 1, validator: { timeslotsOpen, competition ->
      timeslotsOpen <= (competition.timeslotsOverall - competition.deactivateTimeslotsAhead) ? true : ['timeslotsOpen.greater.timeslotsAhead']
    })
    deactivateTimeslotsAhead(nullable: false, min: 0, validator: {deactivateTimeslotsAhead, competition ->
      deactivateTimeslotsAhead <= (competition.timeslotsOverall - competition.timeslotsOpen) ? true : ['deactivateTimeslotsAhead.greater.timeslotsOpen']
    })
    simulationStartTime(nullable: false)
    simulationBaseTime(nullable: false)
    simulationModulo (nullable: false)
    simulationRate (nullable: false)
    dateCreated(nullable: false)
    lastUpdated(nullable: false)
    balancingCostOver(nullable: false, scale: Constants.DECIMALS)
    balancingCostUnder(nullable: false, scale: Constants.DECIMALS)
    liquidityProviderEnabled(nullable: false)
    premium(nullable: false)
    fixCost(nullable: false)
    profileUrl(nullable: false)
    timeseriesName(nullable: false)
    profileId(nullable: false)
    authToken(nullable: false)
    regularCapacity(nullable: false)
    percentPriceIncrease(nullable: false)
    priceScaling(nullable: false)
  }

  static mapping = {
    cache true
    id(generator: 'assigned')
  }

  public static currentCompetition() {return Competition.findByCurrent(true, [cache: true])}

  public String toString() 
  {
    if (name == null)
      println "competition name is null"
    return name
  }
}
