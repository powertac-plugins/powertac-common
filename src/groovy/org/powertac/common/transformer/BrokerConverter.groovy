package org.powertac.common.transformer

import com.thoughtworks.xstream.converters.SingleValueConverter
import org.powertac.common.Broker
import org.apache.commons.logging.LogFactory

class BrokerConverter implements SingleValueConverter
{
  private static final log = LogFactory.getLog(this)

  @Override
  public boolean canConvert (Class type)
  {
    if (Broker.class.isAssignableFrom(type)) {
      return true
    }
    else {
      log.info "BrokerConverter cannot convert ${type}"
      return false
    }
  }

  @Override
  public Object fromString (String username)
  {
    return Broker.findByUsername(username)
  }

  @Override
  public String toString (Object broker)
  {
    return broker.username
  }
}
