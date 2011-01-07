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

import org.powertac.common.enumerations.TariffState

 /**
 * Command object that represents a tariff reply, i.e.
 * a subscription to a <code>PublishTariffCommand</code>
 * or a counter offer in a negotiation process.
 *
 *
 * @author Carsten Block
 * @version 1.0, Date: 01.12.10
 * @see org.powertac.common.command.TariffDoPublishCmd
 */
class TariffDoReplyCmd extends AbstractTariff implements Serializable {
  String authToken
  TariffState tariffState
  String customerId
}
