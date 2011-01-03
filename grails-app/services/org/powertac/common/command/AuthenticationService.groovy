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

import org.powertac.common.Broker
import org.powertac.common.Competition

/**
 * Service provides authentication methods to validate brokers based userName, apiKey and competitionId as credentials
 */
class AuthenticationService {

  static transactional = false

  /**
   * Authenticates broker credentials based on competitionId, userName, and apiKey as credentials and uses db query caching to speed up the lookup process
   *
   * @param competitionId the competitionId for which to authenticate the broker
   * @param userName the broker's username
   * @param apiKey the broker's apiKey
   * @return true if a broker instance with matching userName and apiKey is found in the for the given competition or false otherwise
   */
  public boolean authenticateBroker(String competitionId, String userName, String apiKey) {
    if (!competitionId || !userName || !apiKey) return false
    def results = Broker.withCriteria {
      eq('competition.id', competitionId)
      eq('userName', userName)
      eq('apiKey', apiKey)
      cache (true)
    }
    return results.size() == 1
  }

  /**
   * Authenticates broker credentials based on competition, userName, and apiKey as credentials
   * @param competition the competition for which to authenticate the broker
   * @param userName the broker's username
   * @param apiKey the broker's apiKey
   * @return true if a broker instance with matching userName and apiKey is found in the for the given competition or false otherwise
   */
  public boolean authenticateBroker(Competition competition, String userName, String apiKey) {
    return authenticateBroker(competition?.id, userName, apiKey)
  }
}
