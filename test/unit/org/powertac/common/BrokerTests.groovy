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

import grails.test.GrailsUnitTestCase

class BrokerTests extends GrailsUnitTestCase {

  Competition competition
  Broker broker
  String userName = 'testName'
  
  protected void setUp() {
    super.setUp()
    competition = new Competition(name: 'testCompetition')
    registerMetaClass(Competition)
    Competition.metaClass.'static'.currentCompetition = {-> return competition }
    broker = new Broker(competition: competition, userName: userName)
    mockForConstraintsTests(Broker, [broker])
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testNullableValidationLogic() {
    Broker broker1 = new Broker(id: null, apiKey: null)
    broker1.competition = null
    assertFalse(broker1.validate())
    assertEquals('nullable', broker1.errors.getFieldError('id').getCode())
    assertEquals('nullable', broker1.errors.getFieldError('competition').getCode())
    assertEquals('nullable', broker1.errors.getFieldError('userName').getCode())
    assertEquals('nullable', broker1.errors.getFieldError('apiKey').getCode())
  }

  void testBlankValidationLogic() {
    Broker broker1 = new Broker(id: '', userName: '', apiKey: '')
    assertFalse(broker1.validate())
    assertEquals('blank', broker1.errors.getFieldError('id').getCode())
    assertEquals('blank', broker1.errors.getFieldError('userName').getCode())
    assertEquals('blank', broker1.errors.getFieldError('apiKey').getCode())
  }

  void testIdUniqueness() {
    Broker broker1 = new Broker(id: broker.id)
    assertFalse(broker1.validate())
    assertEquals('unique', broker1.errors.getFieldError('id').getCode())
  }

  void testUsernameSpecialCharConstraints() {
    Broker broker1 = new Broker (userName: 'SpecialChars_!?')
    assertFalse(broker1.validate())
    assertEquals('matches.invalid', broker1.errors.getFieldError('userName').getCode())
  }

  void testUsernameTooShortConstraint() {
    Broker broker1 = new Broker (userName: '1')
    assertFalse(broker1.validate())
    assertEquals('minSize.notmet', broker1.errors.getFieldError('userName').getCode())
  }
}
