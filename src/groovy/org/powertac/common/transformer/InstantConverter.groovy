package org.powertac.common.transformer

import org.simpleframework.xml.convert.Converter
import org.simpleframework.xml.stream.InputNode
import org.simpleframework.xml.stream.OutputNode
import org.joda.time.Instant

class InstantConverter implements Converter<Instant>
{

  @Override
  public Instant read (InputNode node) throws Exception
  {
    return new Instant(node.getAttribute('ms'))
  }

  @Override
  public void write (OutputNode node, Instant inst) throws Exception
  {
    node.setAttribute('ms', Long.toString(inst.millis))
  }
}
