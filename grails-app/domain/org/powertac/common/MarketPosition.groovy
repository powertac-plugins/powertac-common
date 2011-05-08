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

//import org.codehaus.groovy.grails.commons.ApplicationHolder
//import org.joda.time.Instant
import org.powertac.common.enumerations.ProductType
import org.powertac.common.transformer.BrokerConverter
import org.powertac.common.transformer.TimeslotConverter
import com.thoughtworks.xstream.annotations.*

/**
 * A {@code MarketPosition} domain instance represents the current position of a
 * single broker for wholesale power in a given timeslot. The evolution of this
 * position over time is represented by the sequence of MarketTransaction instances
 * for this broker and timeslot.
 *
 * @author Carsten Block, David Dauer, John Collins
 */
@XStreamAlias("market-posn")
class MarketPosition //implements Serializable 
{
  // This is server-generated, there is no need for the fancy ID
  //String id = IdGenerator.createId()
  
  /** the broker this position update belongs to */
  @XStreamConverter(BrokerConverter)
  Broker broker
  
  /** the timeslot this position belongs to */
  @XStreamAsAttribute
  @XStreamConverter(TimeslotConverter)
  Timeslot timeslot
  
  /** The running total position the broker owns (> 0) / owes (< 0) of the specified
   *  product in the specified timeslot */
  @XStreamAlias("bal")
  @XStreamAsAttribute
  BigDecimal overallBalance = 0.0

  /** the product this position update belongs to */
  @XStreamAsAttribute
  ProductType product = ProductType.Future // not sure what this is for -- JEC
  
  // explicit version so we can omit it
  @XStreamOmitField
  int version

  //static auditable = true

  static belongsTo = Broker

  static constraints = {
    //id (nullable: false, blank: false, unique: true)
    timeslot(nullable: false)
    broker(nullable: false)
    product(nullable: true)
    //overallBalance(nullable: false)
  }

  //static mapping = {
  //  id (generator: 'assigned')
  //}

  String toString() {
    return "${broker}-${timeslot}-${overallBalance}"
  }
  
  /**
   * Adds a quantity to the current balance. Positive numbers signify
   * purchased power, negative numbers signify sold power. Returns the
   * resulting total balance
   */
  BigDecimal updateBalance (BigDecimal quantity)
  {
    overallBalance += quantity
    return overallBalance
  }
}
