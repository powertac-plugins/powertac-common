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

package org.powertac.common

class AuthenticationServiceTests extends GroovyTestCase {

  def authenticationService
  Competition competition
  Broker broker
  String userName
  String apiKey

  protected void setUp() {
    super.setUp()
    userName = 'testBroker'
    apiKey = 'testApiKey-which-needs-to-be-longer-than-32-characters'
    competition = new Competition(name: "test")
    assert(competition.validate() && competition.save())
    broker = new Broker(competition: competition, userName: userName, apiKey: apiKey)
    assert(broker.validate() && broker.save())
  }

  protected void tearDown() {
    super.tearDown()
  }


  void testAuthenticationServiceNotNull() {
    assertNotNull(authenticationService)
  }

  void testNullAuthentication() {
    assertFalse (authenticationService.authenticateBroker((String) null, null, null))
    assertFalse (authenticationService.authenticateBroker(new Competition(), null, null))
  }

  void testAuthenticateBrokerUsingCompetitionObject() {
    assertTrue (authenticationService.authenticateBroker(competition, userName, apiKey))
    assertFalse (authenticationService.authenticateBroker(competition, "$userName invalid", apiKey))
    assertFalse (authenticationService.authenticateBroker(competition, userName, "$apiKey invalid"))
  }

  void testAuthenticateBrokerUsingCompetitionId() {
    String competionId = competition.id
    assertTrue (authenticationService.authenticateBroker(competionId, userName, apiKey))
    assertFalse (authenticationService.authenticateBroker("$competionId invalid", userName, apiKey))
    assertFalse (authenticationService.authenticateBroker(competition, "$userName invalid", apiKey))
    assertFalse (authenticationService.authenticateBroker(competition, userName, "$apiKey invalid"))
  }
}
