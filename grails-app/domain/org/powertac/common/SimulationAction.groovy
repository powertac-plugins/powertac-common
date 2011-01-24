package org.powertac.common

import org.joda.time.Instant

/**
 * Actions run at specified times by the TimeService. This would be a nested
 * class of TimeService if groovy would allow that.
 * @author jcollins
 */
class SimulationAction implements Comparable
{
  Instant when
  def action
  
  static constraints = {
    when(nullable:false)
  }

  int compareTo (obj)
  {
    when.compareTo(obj.when)
  }
}
