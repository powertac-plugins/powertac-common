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

import org.codehaus.groovy.grails.validation.Validateable

/**
 * Represents the forecast or real metered weather data
 * for timeslot {@code targetTimeslotId} as forecast / reported
 * from the perspective of {@code currentTimeslotId}
 *
 * @author Carsten Block
 * @version 1.0, Date: 02.01.11
 */
@Validateable class WeatherIsReportedCmd implements Serializable {
  String id
  String competitionId
  String targetTimeslotId
  String currentTimeslotId
  BigDecimal temperature
  BigDecimal windSpeed
  BigDecimal sunIntensity
  Boolean forecast

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    competitionId (nullable: false, blank: false)
    targetTimeslotId (nullable: false, blank: false)
    currentTimeslotId (nullable: false, blank: false)
    temperature (nullable: true)
    windSpeed (nullable: true)
    sunIntensity (nullable: true)
    forecast(nullable: false)
  }
}
