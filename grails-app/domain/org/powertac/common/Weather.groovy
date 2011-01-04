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

class Weather {

  String id = IdGenerator.createId()
  Competition competition
  Timeslot targetTimeslot
  Timeslot currentTimeslot
  BigDecimal temperature
  BigDecimal windSpeed
  BigDecimal sunIntensity
  Boolean forecast

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    competition (nullable: false)
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
