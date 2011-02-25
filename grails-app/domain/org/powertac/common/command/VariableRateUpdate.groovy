/*
 * Copyright (c) 2011 by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powertac.common.command

import org.powertac.common.HourlyCharge

/**
 * Conveys an HourlyCharge instance, labeled by its Tariff and
 * Rate. When received by the server, the HourlyCharge simply
 * needs to be added to its Rate.
 * @author jcollins
 */
class VariableRateUpdate 
{
  HourlyCharge payload
  String tariffId
  String rateId

  static constraints = {
    payload(nullable: false)
    tariffId(nullable: false)
    rateId(nullable: false)
  }
}
