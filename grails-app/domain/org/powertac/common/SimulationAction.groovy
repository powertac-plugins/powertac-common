package org.powertac.common

import org.joda.time.Instant

/**
 * Actions run at specified times by the TimeService. This would be a nested
 * class of TimeService if groovy would allow that. These are not serialized
 * or communicated between broker and server.
 * @author jcollins
 */
class SimulationAction implements Comparable
{
  Instant atTime
  def action
  
  static constraints = {
    atTime(nullable:false)
  }

  int compareTo (obj)
  {
    atTime.compareTo(obj.atTime)
  }
}
