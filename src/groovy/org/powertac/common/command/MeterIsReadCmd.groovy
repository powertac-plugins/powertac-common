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

/**
 * Command object that represents a meter reading
 *
 * @author Carsten Block
 * @version 1.0 , Date: 02.01.11
 */
@Validateable class MeterIsReadCmd {
  String id
  String competitionId
  String customerId
  String timeslotId
  BigDecimal amount

  static constraints = {
    id(nullable: false, blank: false)
    competitionId(nullable: false, blank: false)
    customerId(nullable: false, blank: false)
    timeslotId(nullable: false, blank: false)
    amount(nullable: false, scale: Constants.DECIMALS)
  }
}
