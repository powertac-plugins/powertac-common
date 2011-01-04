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

package org.powertac.common.interfaces;

import org.powertac.common.command.WeatherIsReportedCmd;

import java.util.List;

/**
 * Common Interface for the Physical Environment module.
 *
 * @author David Dauer, Carsten Block
 * @version 0.1, January 2nd, 2011
 */
public interface PhysicalEnvironment extends CompetitionBaseEvents {
  /**
   * Generates and returns the real weather data for a given timeslot
   * for customers, brokers, distribution utility
   * <p/>
   * Make sure that the timeslotIsChanged parameter is referenced within the WeatherReadData command
   * so that customers/brokers know which timeslot the data is for
   *
   * @param currentTimeslotId if of the changed (deactivated) timeslot
   * @return The actual weather data for the given timeslotIsChanged parameter
   */
  WeatherIsReportedCmd generateRealWeatherData(String currentTimeslotId);

  /**
   * Generates and returns weather forecasts for every enabled timeslot
   * The physical environment module is responsible for retrieving all enabled timeslots
   * and to compute weather forecasts for each of it from the perspective of the "current timeslot" specified by the given {@code currentTimeslotId}.
   *
   * @param currentTimeslotId the id of the current timeslot
   * @return a list of weather forecast objects
   */
  List<WeatherIsReportedCmd> generateForecastWeatherData(String currentTimeslotId);
}
