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

/**
 * Represents a weather forecast
 * for a future timeslot {@code targetTimeslotId}
 * as of timeslot {@currentTimeslotId}
 *
 * @author Carsten Block
 * @version 1.0, Date: 02.01.11
 */
class WeatherForecastIsUpdatedCmd {
  String competitionId
  String targetTimeslotId
  String currentTimeslotId
  BigDecimal temperature
  BigDecimal windSpeed
  BigDecimal sunIntensity
}