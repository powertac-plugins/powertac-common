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

import org.joda.time.DateTime
import org.powertac.common.*

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
class TariffDoRevokeCmd implements Serializable {
  String id = IdGenerator.createId()
  String tariffId
  DateTime dateCreated = new DateTime()

  //static belongsTo = [broker: Broker, tariff: Tariff]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    tariffId (nullable: false, validator: {val, obj ->
      Tariff tf = Tariff.get(val)
      if (tf == null) return [Constants.NOT_FOUND]
      else if (tf.state == Tariff.State.WITHDRAWN) return [Constants.TARIFF_OUTDATED]
      else return true
    })
  }

  static mapping = {
    id (generator: 'assigned')
  }
}
