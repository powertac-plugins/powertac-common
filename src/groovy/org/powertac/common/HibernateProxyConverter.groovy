package org.powertac.common

import org.powertac.common.transformer.BrokerConverter
import org.powertac.common.msg.*

import org.hibernate.proxy.HibernateProxy;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentCollectionConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedSetConverter;
//import com.thoughtworks.xstream.hibernate.converter.HibernateProxyConverter;
import com.thoughtworks.xstream.hibernate.mapper.HibernateMapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import org.apache.commons.logging.LogFactory

class HibernateProxyConverter extends ReflectionConverter
{
  public HibernateProxyConverter(Mapper arg0, ReflectionProvider arg1)
  {
    super(arg0, arg1);
  }

  public HibernateProxyConverter()
  {
    super()
  }

  /**
   * be responsible for hibernate proxy
   */
  public boolean canConvert(Class clazz)
  {
    println("converter says can convert " + clazz + ":"+ HibernateProxy.class.isAssignableFrom(clazz));
    return HibernateProxy.class.isAssignableFrom(clazz);
  }

  public void marshal(Object arg0, HierarchicalStreamWriter arg1,
                      MarshallingContext arg2)
  {
    System.err.println("converter marshalls: "  + ((HibernateProxy)arg0).getHibernateLazyInitializer().getImplementation());
    super.marshal(((HibernateProxy)arg0).getHibernateLazyInitializer().getImplementation(), arg1, arg2);
  }
}
