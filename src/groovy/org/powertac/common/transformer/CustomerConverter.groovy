package org.powertac.common.transformer

import com.thoughtworks.xstream.converters.SingleValueConverter
import org.powertac.common.CustomerInfo

class CustomerConverter implements SingleValueConverter
{

  @Override
  public boolean canConvert (Class type)
  {
    return CustomerInfo.class.isAssignableFrom(type)
  }

  @Override
  public Object fromString (String id)
  {
    return CustomerInfo.get(id)
  }

  @Override
  public String toString (Object customer)
  {
    return customer.id
  }
}
