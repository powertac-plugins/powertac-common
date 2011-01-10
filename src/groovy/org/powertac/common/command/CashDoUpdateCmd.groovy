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

package org.powertac.common.command

import org.codehaus.groovy.grails.validation.Validateable
import org.powertac.common.Broker
import org.powertac.common.Competition
import org.powertac.common.Constants

/**
 * Command object that represents an cash transaction
 * (add / deduce money) that should be executed on a
 * specific broker cash account for a given reason.
 *
 * @author Carsten Block
 * @version 1.0 , Date: 02.12.10
 */
@Validateable class CashDoUpdateCmd implements Serializable {
  Competition competition
  Broker broker
  BigDecimal relativeChange
  String reason
  String origin

  static constraints = {
    competition(nullable: false)
    broker(nullable: false)
    relativeChange(nullable: false, scale: Constants.DECIMALS)
    reason(nullable: true)
    origin(nullable: true)
  }
}
