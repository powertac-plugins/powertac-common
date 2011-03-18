package org.powertac.common


class ClockDriveJob
{
  //def timeout = 5000l // execute job once in 5 seconds
  def timeService

  // scheduler trigger support
  //static triggers = {simple name:'clockTrigger', group:'clockGroup'}
  
  /**
   * Target for scheduler
   */
  def execute ()
  {
    timeService.updateTime()
  }
}
