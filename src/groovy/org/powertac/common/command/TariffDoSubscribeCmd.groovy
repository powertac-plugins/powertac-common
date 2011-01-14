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
import org.powertac.common.Constants
import org.powertac.common.Customer
import org.powertac.common.Tariff
import org.powertac.common.enumerations.TariffState

/**
 * Command object that represents a customer's request to subscribe
 * to either a published or an individually negotiated tariff.
 *
 * @author Carsten Block
 * @version 1.0, Date: 01.12.10
 */
@Validateable class TariffDoSubscribeCmd implements Serializable {
  Customer customer
  Tariff tariff

  static constraints = {
    customer(nullable: false)
    tariff (nullable: false, validator: {val->
      if (!val?.latest) return [Constants.TARIFF_OUTDATED]
      if (val?.parent && val?.tariffState != TariffState.InNegotiation) return [Constants.TARIFF_INVALID_STATE]
      if (val?.parent == null && val?.tariffState != TariffState.Published) return [Constants.TARIFF_INVALID_STATE]
      return true
    })
  }


}
