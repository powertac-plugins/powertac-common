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
import org.powertac.common.Broker
import org.powertac.common.Constants
import org.powertac.common.Tariff

/**
 * Command object that represents a broker's request to revoke
 * a particular tariff so that it is no longer subscribable by new customers.
 * <p/>
 * Note: Revoking a tariff does not influence running tariff contracts. These
 * remain valid until the end of the agreed runtime or until the customer decides
 * to exit the contract ahead of time.
 *
 * @author Carsten Block
 * @version 1.0 , Date: 02.01.11
 */
@Validateable class TariffDoRevokeCmd implements Serializable {
  Broker broker
  Tariff tariff

  static constraints = {
    broker (nullable: false, validator: {val, obj ->
      if (obj?.tariff?.broker?.id != obj?.broker?.id) return [Constants.TARIFF_WRONG_BROKER]
      return true
    })
    tariff (nullable: false, validator: {val, obj ->
      if (val?.parent) return [Constants.TARIFF_HAS_PARENT]
      if (!val?.latest) return [Constants.TARIFF_OUTDATED]
      if (obj?.tariff?.broker?.id != obj?.broker?.id) return [Constants.TARIFF_WRONG_BROKER]
      return true
    })
  }
}
