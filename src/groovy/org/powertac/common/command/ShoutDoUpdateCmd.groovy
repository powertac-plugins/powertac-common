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
import org.powertac.common.Competition
import org.powertac.common.Constants
import org.powertac.common.Shout
import org.powertac.common.enumerations.ModReasonCode

/**
 * Command object that can be used by
 * a broker to require the server to change
 * price and / or quantity
 * an already issued shout specific shout;
 *
 * @author Carsten Block
 * @version 1.0 , Date: 01.12.10
 */
@Validateable class ShoutDoUpdateCmd implements Serializable {
  Competition competition
  Broker broker
  String shoutId
  BigDecimal quantity
  BigDecimal limitPrice

  static constraints = {
    competition(nullable: false, validator: {val ->
      if (!val) {
        return [Constants.COMPETITION_NOT_FOUND]
      } else if (!val.current) {
        return [Constants.COMPETITION_INACTIVE]
      } else {
        return true
      }
    })
    broker(nullable: false)
    shoutId(nullable: false, blank: false, validator: {val ->
      def shout = Shout.findByShoutIdAndLatest(val,true)
      if (!shout) {
        return [Constants.SHOUT_NOT_FOUND]
      } else if (shout.modReasonCode == ModReasonCode.DELETIONBYUSER || shout.modReasonCode == ModReasonCode.DELETIONBYSYSTEM) {
        return [Constants.SHOUT_DELETED]
      } else if (shout.modReasonCode == ModReasonCode.EXECUTION) {
        return [Constants.SHOUT_EXECUTED]
      } else {
        return true
      }
    })
    quantity(nullable: true, min: 0.0, scale: Constants.DECIMALS, validator: {val, obj ->
      if (obj.limitPrice == null && val == null) {
        return [Constants.SHOUT_UPDATE_WITHOUT_LIMIT_AND_QUANTITY]
      } else {
        return true
      }
    })
    limitPrice(nullable: true, min: 0.0, scale: Constants.DECIMALS, validator: {val, obj ->
      if (val == null && obj.quantity == null) {
        return [Constants.SHOUT_UPDATE_WITHOUT_LIMIT_AND_QUANTITY]
      } else {
        return true
      }
    })
  }
}
