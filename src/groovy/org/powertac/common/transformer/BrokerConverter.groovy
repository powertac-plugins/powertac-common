package org.powertac.common.transformer

import com.thoughtworks.xstream.converters.SingleValueConverter
import org.powertac.common.Broker

class BrokerConverter implements SingleValueConverter
{

  @Override
  public boolean canConvert (Class type)
  {
    return type.equals(Broker.class)
  }

  @Override
  public Object fromString (String id)
  {
    return Broker.get(id)
  }

  @Override
  public String toString (Object broker)
  {
    return broker.id
  }
}