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

import grails.test.GrailsUnitTestCase
import org.powertac.common.Competition

class CompetitionIsChangedCmdTests extends GrailsUnitTestCase {

  Competition competition

  protected void setUp() {
    super.setUp()
    competition = new Competition(name: 'test')
    mockForConstraintsTests(CompetitionIsChangedCmd)
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testTimeslotsOpen() {
    CompetitionIsChangedCmd cmd = new CompetitionIsChangedCmd(timeslotsOpen: 1, timeslotsOverall: 3, deactivateTimeslotsAhead: 3)
    assertFalse(cmd.validate())
    assertEquals('timeslotsOpen.greater.timeslotsAhead', cmd.errors.getFieldError('timeslotsOpen').getCode())
    assertEquals('deactivateTimeslotsAhead.greater.timeslotsOpen', cmd.errors.getFieldError('deactivateTimeslotsAhead').getCode())

    cmd.deactivateTimeslotsAhead = 2
    cmd.validate()
    assertNull(cmd.errors.getFieldError('timeslotsOpen'))
    assertNull(cmd.errors.getFieldError('deactivateTimeslotsAhead'))
  }
}
