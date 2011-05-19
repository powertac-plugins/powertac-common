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
import org.apache.commons.logging.LogFactory;

class MessageConverter implements org.springframework.beans.factory.InitializingBean
{

  XStream xstream
  private static final log = LogFactory.getLog(this)

  private static final List<Class> classes =
      [Competition, SimStart, CustomerInfo, CashPosition, Timeslot,
       ClearedTrade, MarketPosition, Shout, TariffStatus, TariffTransaction,
       TariffSpecification, Rate, HourlyCharge, TariffUpdate, TariffExpire,
       TariffRevoke, VariableRateUpdate, BankTransaction, CashPosition,
       TimeslotUpdate, PluginConfig, Orderbook]


  void afterPropertiesSet ()
  {
    xstream = new XStream() {
        protected MapperWrapper wrapMapper(final MapperWrapper next) {
          return new HibernateMapper(next);
        }
    };

    for (clazz in classes) {
      xstream.processAnnotations(clazz)
      xstream.omitField(clazz, 'version')
    }

    xstream.registerConverter(new HibernateProxyConverter());
    xstream.registerConverter(new HibernatePersistentCollectionConverter(xstream.getMapper()));
    xstream.registerConverter(new HibernatePersistentMapConverter(xstream.getMapper()));
    xstream.registerConverter(new HibernatePersistentSortedMapConverter(xstream.getMapper()));
    xstream.registerConverter(new HibernatePersistentSortedSetConverter(xstream.getMapper()));

    xstream.autodetectAnnotations(true);
  }

  String toXML(Object message) {
    xstream.toXML(message)
  }

  Object fromXML(String xml) {
    xstream.fromXML(xml)
  }
}
