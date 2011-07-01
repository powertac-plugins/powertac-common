/*
* Copyright 2011 the original author or authors.
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

import org.powertac.common.transformer.TimeslotConverter
import com.thoughtworks.xstream.annotations.*

/**
* A weather forecast report instance that describes the weather data
*
* @author Erik Onarheim, Josh Edeen
*
* @version 1.0 - 03/Jun/2011
*/
@XStreamAlias("weather-report")
class WeatherForecastReport implements Serializable {

  /** the current or reference timeslot from which the weather forecast is generated */
  @XStreamAsAttribute
  @XStreamConverter(TimeslotConverter)
  Timeslot currentTimeslot
  
  /** the current timeslot's temperature*/
  @XStreamAsAttribute
  BigDecimal temperature
  
  /**  the current timeslot's windSpeed*/
  @XStreamAsAttribute
  BigDecimal windSpeed
  
  /** the current timeslot's windDirection*/
  @XStreamAsAttribute
  BigDecimal windDirection
  
  /** the current timeslot's cloudCover*/
  @XStreamAsAttribute
  BigDecimal cloudCover
  
  /** Explicit version so we can omit*/
  @XStreamOmitField
  int version
  
    
    static constraints = {
      currentTimeslot(nullable: true)
      temperature (nullable: true)
      windSpeed (nullable: true)
      windDirection (nullable: true)
      cloudCover (nullable: true)
      
    }
}