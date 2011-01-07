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
import org.powertac.common.Shout

/**
 * Command object that can be used by
 * a broker to require the server to delete
 * as specific shout;
 *
 * @author Carsten Block
 * @version 1.0 , Date: 01.12.10
 */
@Validateable class ShoutDoDeleteCmd implements Serializable {
  String competitionId
  String userName
  String apiKey
  Long shoutId

  static constraints = {
    competitionId(nullable: false, blank: false, validator: {val ->
      def competition = Competition.get(val)
      if (!competition) {
        return ['invalid.competition']
      } else if (!competition.current) {
        return ['inactive.competition']
      } else {
        return true
      }
    })
    userName(nullable: false, blank: false)
    apiKey(nullable: false, blank: false, validator: {val, obj ->
      def results = Broker.withCriteria {
        eq('competition.id', obj.competitionId)
        eq('userName', obj.userName)
        eq('apiKey', obj.apiKey)
        cache(true)
      }
      return results.size() == 1 ? true : ['invalid.credentials']
    })
    shoutId(nullable: false, blank: false, validator: {val ->
      def shout = Shout.get(val)
      if (!shout) {
        return ['invalid.shout']
      } else {
        return true
      }
    })
  }

}
