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

package org.powertac.common.msg

import org.joda.time.Instant
import org.powertac.common.*
import org.powertac.common.transformer.BrokerConverter
import com.thoughtworks.xstream.annotations.*

 /**
 * Command object that represents a broker's request to update a tariff, either
 * to change its expiration date or to revoke it.
 * <p>
 * Note: Revoking a tariff causes all existing subscriptions to be switched either
 * to the superseding tariff (if any) or to the default tariff.</p>
 *
 * @author Carsten Block, John Collins
 */
@XStreamAlias("tariff-up")
class TariffUpdate //implements Serializable 
{
  @XStreamAsAttribute
  String id = IdGenerator.createId()
  @XStreamAsAttribute
  String tariffId
  @XStreamConverter(BrokerConverter)
  Broker broker
  
  //no constraints with subclasses...
  
  static mapping = {
    id (generator: 'assigned')
  }
}
