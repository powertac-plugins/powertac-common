package org.powertac.common.transformer

import org.powertac.common.Timeslot
import com.thoughtworks.xstream.converters.SingleValueConverter;

class TimeslotConverter implements SingleValueConverter
{

  @Override
  public boolean canConvert (Class type)
  {
    return Timeslot.class.isAssignableFrom(type)
  }

  @Override
  public Object fromString (String serial)
  {
    return Timeslot.findBySerialNumber(serial.toInteger())
  }

  @Override
  public String toString (Object timeslot)
  {
    return Integer.toString(timeslot.serialNumber)
  }

}
