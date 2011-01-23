package org.powertac.common

import org.joda.time.LocalDateTime

class TimeService {

    static transactional = true
    
    static final long HOUR = 1000l * 60 * 60
    
    LocalDateTime currentTime

    def updateTime (LocalDateTime time)
    {
      currentTime = time
    }
}
