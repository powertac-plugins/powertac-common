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

import org.powertac.common.enumerations.ProductType

/**
 * A product instance describes a product that is tradeable in the powertac wholesale market.
 * Currently we only support Futures (see {@link ProductType}), which are binding energy
 * consumption or production commitments at a defined price for a particular {@link
 * Timeslot} sometimes in the future. In later versions of PowerTAC one might implement
 * Options as another product tradeable in the wholesale mareket, which grants the option
 * holder the right (but comes with no obligation) to consume or produce energy at a certain
 * price for a defined timeslot.
 *
 * @author Carsten Block
 * @version 1.0, Feb 6, 2011
 */
class Product implements Serializable {

  String id = IdGenerator.createId()

  /** the product type, one of Future or Option - currently only Future is supported */
  ProductType productType

  static hasMany = [orderbooks: Orderbook, transactionLogs: MarketTransaction]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    productType(nullable: false)
  }

  static mapping = {
    id (generator: 'assigned')
  }

  public String toString() {
    return productType.toString()
  }
}
