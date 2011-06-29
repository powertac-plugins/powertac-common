package org.powertac.common.transformer

import com.thoughtworks.xstream.converters.SingleValueConverter
import org.powertac.common.TariffSpecification

class TariffConverter implements SingleValueConverter
{

  @Override
  public boolean canConvert (Class type)
  {
    return TariffSpecification.class.isAssignableFrom(type)
  }

  @Override
  public Object fromString (String id)
  {
    return TariffSpecification.get(id)
  }

  @Override
  public String toString (Object tariffSpec)
  {
    return tariffSpec.id
  }

}
