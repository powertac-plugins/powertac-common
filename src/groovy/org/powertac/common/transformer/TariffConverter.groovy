package org.powertac.common.transformer

import com.thoughtworks.xstream.converters.SingleValueConverter
import org.powertac.common.Tariff

class TariffConverter implements SingleValueConverter
{

  @Override
  public boolean canConvert (Class type)
  {
    return Tariff.class.isAssignableFrom(type)
  }

  @Override
  public Object fromString (String id)
  {
    return Tariff.get(id)
  }

  @Override
  public String toString (Object tariff)
  {
    return tariff.id
  }

}
