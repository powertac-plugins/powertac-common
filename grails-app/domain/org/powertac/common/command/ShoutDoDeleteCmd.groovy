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

import org.joda.time.Instant
import org.powertac.common.enumerations.ModReasonCode
import org.powertac.common.*

/**
 * Command object that can be used by
 * a broker to require the server to delete
 * as specific shout;
 *
 * @author Carsten Block
 * @version 1.0 , Date: 01.12.10
 */
class ShoutDoDeleteCmd implements Serializable {
  String id = IdGenerator.createId()
  Broker broker
  Shout shout
  Instant dateCreated = new Instant()

  static belongsTo = [broker: Broker, shout: Shout]

  static constraints = {
    id(nullable: false, blank: false, unique: true)
    broker (nullable: false, validator: {val, obj ->
      if (obj?.shout?.broker?.id != obj?.broker?.id) return [Constants.SHOUT_WRONG_BROKER]
      return true
    })
    shout(nullable: false, validator: {shout ->
      if (!shout.latest) {
        return [Constants.SHOUT_OUTDATED]
      } else if (shout.modReasonCode == ModReasonCode.DELETIONBYUSER || shout.modReasonCode == ModReasonCode.DELETIONBYSYSTEM) {
        return [Constants.SHOUT_DELETED]
      } else if (shout.modReasonCode == ModReasonCode.EXECUTION) {
        return [Constants.SHOUT_EXECUTED]
      } else {
        return true
      }
    })
  }

  static mapping = {
    id(generator: 'assigned')
  }
}
