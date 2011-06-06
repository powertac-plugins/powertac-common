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
package org.powertac.common

import org.quartz.Job
import org.quartz.JobExecutionContext

/**
 * Runs the clock. This needs to be scheduled externally to avoid breaking
 * integration tests.
 * @author John Collins
 */
class ClockDriveJob implements Job
{
  def timeService
  def concurrent = false // don't want two copies running

  // scheduler trigger support
  static triggers = {
  //  cron name: 'cronTrigger', cronExpression: '0/5 * * * * ?'
    simple name: 'default', group: 'default'
  }
  
  /**
   * Target for scheduler
   */
  @Override
  void execute (JobExecutionContext context)
  {
    timeService.updateTime()
    log.info("Clock Update " + timeService.getCurrentTime())
  }
}
