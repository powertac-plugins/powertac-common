package org.powertac.common

import org.joda.time.LocalDateTime

class TimeService {

    static transactional = true
    
    LocalDateTime currentTime

    def updateTime (LocalDateTime time)
    {
      currentTime = time
    }
}
