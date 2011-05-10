/*
 * Copyright (c) 2011 by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.powertac.common

import org.powertac.common.transformer.BrokerConverter
import org.powertac.common.msg.*

import org.hibernate.proxy.HibernateProxy;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

class MessageConverter implements org.springframework.beans.factory.InitializingBean
{

  XStream xstream

  void afterPropertiesSet ()
  {
    xstream = new XStream()
    xstream.processAnnotations(Competition.class)
    xstream.processAnnotations(SimStart.class)
    xstream.processAnnotations(CustomerInfo.class)
    xstream.processAnnotations(CashPosition.class)
    xstream.processAnnotations(Timeslot.class)
    xstream.processAnnotations(ClearedTrade.class)
    xstream.processAnnotations(MarketPosition.class)
    xstream.processAnnotations(MarketTransaction.class)
    xstream.processAnnotations(Shout.class)
    xstream.processAnnotations(TariffStatus.class)
    xstream.processAnnotations(TariffTransaction.class)
    xstream.processAnnotations(TariffSpecification.class)
    xstream.processAnnotations(Rate.class)
    xstream.processAnnotations(HourlyCharge.class)
    xstream.processAnnotations(TariffUpdate.class)
    xstream.processAnnotations(TariffExpire.class)
    xstream.processAnnotations(TariffRevoke.class)
    xstream.processAnnotations(VariableRateUpdate.class)
    xstream.processAnnotations(BankTransaction.class)
    xstream.processAnnotations(CashPosition.class)

    xstream.registerConverter(new HibernateProxyConverter(xstream.getMapper(),new PureJavaReflectionProvider()),XStream.PRIORITY_VERY_HIGH);
  }

  String toXML(Object message) {
    xstream.toXML(message)
  }

  Object fromXML(String xml) {
    xstream.fromXML(xml)
  }

  class HibernateProxyConverter extends ReflectionConverter {
    public HibernateProxyConverter(Mapper arg0, ReflectionProvider arg1) {
      super(arg0, arg1);
    }

    /**
     * be responsible for hibernate proxy
     */
    public boolean canConvert(Class clazz) {
      println("converter says can convert " + clazz + ":"+ HibernateProxy.class.isAssignableFrom(clazz));
      return HibernateProxy.class.isAssignableFrom(clazz);
    }

    public void marshal(Object arg0, HierarchicalStreamWriter arg1, MarshallingContext arg2) {	
      System.err.println("converter marshalls: "  + ((HibernateProxy)arg0).getHibernateLazyInitializer().getImplementation());
      super.marshal(((HibernateProxy)arg0).getHibernateLazyInitializer().getImplementation(), arg1, arg2);
    }
    	
  }
}
