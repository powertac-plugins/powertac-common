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

import org.powertac.common.enumerations.CustomerType
import org.powertac.common.enumerations.PowerType
import com.thoughtworks.xstream.annotations.*

/**
 * A {@code CustomerInfo} instance represents a customer model (i.e. a consumer or a producer)
 * within a specific competition. The customer data stored is published to all brokers in
 * the respective competition in order to provide them with an brief overview on what type
 * of customers participate in the specific competition.
 *
 * @author Carsten Block, KIT; John Collins, U of Minnesota
 */
@XStreamAlias("cust-info")
class CustomerInfo //implements Serializable 
{

  @XStreamAsAttribute
  String id = IdGenerator.createId()

  /** Name of the customer model */
  String name

  /** gives a "rough" classification what type of customer to expect based on an enumeration, i.e. a fixed set of customer types */
  @XStreamAsAttribute
  CustomerType customerType

  /** gives the power classification of the customer */
  // this makes no sense - a customer model potentially has all types - JEC
  //PowerType powerType
  
  /** describes whether or not this customer engages in multiple contracts at the same time */
  @XStreamAsAttribute
  Boolean multiContracting = false

  /** describes whether or not this customer negotiates over contracts */
  @XStreamAsAttribute
  Boolean canNegotiate = false

  // explicit version so we can ignore it
  @XStreamOmitField
  int version
   
  static auditable = true

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    name (blank: false, unique: true)
    customerType(nullable: false)
    //powerType(nullable: true)
    multiContracting (nullable: false)
    canNegotiate (nullable: false)
 //   upperPowerCap (nullable: false, scale: Constants.DECIMALS)
 //   lowerPowerCap (nullable: false, scale: Constants.DECIMALS)
 //   carbonEmissionRate (nullable: false, scale: Constants.DECIMALS)
 //   windToPowerConversion (nullable: false, scale: Constants.DECIMALS)
 //   tempToPowerConversion (nullable: false, scale: Constants.DECIMALS)
 //   sunToPowerConversion (nullable: false, scale: Constants.DECIMALS)
  }

  static mapping = {
    id (generator: 'assigned')
  }

  public String toString() {
    return name
  }
}
