/*
* Copyright (c) 2011, John E. Collins
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

import java.util.SortedSet;

import org.joda.time.Instant
import org.joda.time.base.AbstractDateTime

/**
 * This is the simulation time-keeper and event queue. Here's how it works:
 * <ul>
 * <li>You create it with four parameters: (base, start, rate, modulo), defined as
 *   <ul>
 *   <li><strong>base</strong> is the start time of the simulation scenario. So if we
 *     are simulating a period in the summer of 2007, base might be 2007-06-21-12:00.</li>
 *   <li><strong>start</strong> is the start time of the simulation run.</li>
 *   <li><strong>rate</strong> is the time-compression ratio for the simulation. So if we are
 *     running one-hour timeslots every 5 seconds, the rate would be 720.</li>
 *   <li><strong>modulo</strong> controls the values of simulation time values reported. If
 *     we are running one-hour timeslots, then the modulo should be one hour, expressed
 *     in milliseconds. If we are running one-hour timeslots but want to update time every
 *     30 minutes of simulated time, then the modulo would be 30*60*1000. Note that
 *     this will not work correctly unless the calls to updateTime() are made at
 *     modulo/rate intervals. Also note that the reported time is computed as
 *     rawTime - rawTime % modulo, which means it will never be ahead of the raw 
 *     simulation time.</li>
 *   </ul></li>
 * <li>Some process periodically calls updateTime(), at least once every simulated hour. This
 *   sets the currentTime property to the correct scenario time as<br/>
 *   <code>currentTime = base + (systemTime - start) * rate</code><br/>
 *   and runs any simulation actions that are due.</li>
 * <li>If you want something to happen sometime in the future, you add an action by
 *   calling addAction(time, action). Assuming time is not in the past, then the action
 *   (a closure with a single argument, the current time) is added to a time-ordered queue, 
 *   and will be run when its time arrives.</li>
 * </ul>
 * Note that all times are absolute times represented as UTC; timezone offset is 0, and there
 * is no "daylight-saving" time. If we want to represent the effects of DST, we'll have to have
 * our customers wake up earlier in the summertime.
 * @author jcollins
 */
class TimeService 
{
  static transactional = false

  static final long HOUR = 1000l * 60 * 60
  
  // simulation clock parameters
  long base
  long start
  long rate = 720l
  long modulo = 15*60*1000l

  // simulation action queue
  SortedSet<SimulationAction> actions

  // the current time
  Instant currentTime

  /**
   * Updates simulation time when called as specified by clock
   * parameters, then runs any actions that may be due.
   */
  def updateTime () 
  {
    long systemTime = new Instant().getMillis()
    long raw = base + (systemTime - start) * rate
    long cooked = raw - raw % modulo
    currentTime = new Instant(cooked)
    //println "updateTime: sys=${systemTime}, raw=${raw}, cooked=${cooked} => ${currentTime}"
    runActions()
  }
  
  /**
   * Sets current time to a specific value. Intended for testing purposes only.
   */
  protected void setCurrentTime (Instant time)
  {
    currentTime = time
  }
  
  /**
   * Sets current time to a specific value. Intended for testing purposes only.
   */
  protected void setCurrentTime (AbstractDateTime time)
  {
    currentTime = new Instant(time)
  }

  /**
   * Adds an action to the simulation queue, to be triggered at the specified time.
   */
  void addAction (Instant time, act)
  {
    if (actions == null)
      actions = new TreeSet<SimulationAction>()
    actions.add(new SimulationAction(when:time, action:act))
  }
  
  /**
   * Runs any actions that are due at the current simulated time.
   */
  void runActions ()
  {
    if (actions == null)
      return
    while (!actions.isEmpty() && actions.first().when <= currentTime) {
      SimulationAction act = actions.first()
      act.action.call(currentTime)
      actions.remove(act)
    }
  }
}
