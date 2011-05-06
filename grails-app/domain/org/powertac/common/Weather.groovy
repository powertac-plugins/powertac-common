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

/**
 * A weather domain instance provides telemetry data for a particular timeslot, which can
 * be either forecast ({@forecast = true}) or metered ({@forecast=false}).
 * <p>
 * Note that this is an immutable value type, and is therefore not auditable.</p>
 *
 * @author Carsten Block, KIT
 * @version 1.0 - 04/Feb/2011
 */
class Weather implements Serializable {

  String id = IdGenerator.createId()

  /** the target timeslot for which this weather (forecast) is generated. If this timeslot is the same as {@code currentTimeslot} the weather instance consists of metered weather (i.e. forecast error = 0) */
  Timeslot targetTimeslot

  /** the current or reference timeslot from which the weather (forecast) is generated */
  Timeslot currentTimeslot

  /** the metered or forecast temperature */
  BigDecimal temperature

  /** the metered or forecast wind speed */
  BigDecimal windSpeed

  /** the metered or forecast sun intensity */
  BigDecimal sunIntensity

  /** used to indicate of the telemetry data is forecast (i.e. error prone) or metered */
  Boolean forecast

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    targetTimeslot (nullable: false)
    currentTimeslot (nullable: false)
    temperature (nullable: true)
    windSpeed (nullable: true)
    sunIntensity (nullable: true)
    forecast(nullable: false)
  }

  static mapping = {
    id (generator: 'assigned')
  }

  public String toString() {
    return "Weather ${forecast ? 'forecast' : 'report'} from ${currentTimeslot} for ${targetTimeslot}: Temperature: $temperature, Wind Speed: $windSpeed, Sun intensity: $sunIntensity"
  }
}
