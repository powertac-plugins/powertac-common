package org.powertac.common.transformer

import com.thoughtworks.xstream.converters.SingleValueConverter
import org.powertac.common.Broker

class BrokerConverter implements SingleValueConverter
{

  @Override
  public boolean canConvert (Class type)
  {
    if (Broker.class.isAssignableFrom(type)) {
      return true
    }
    else {
      println "BrokerConverter cannot convert ${type}" 
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
