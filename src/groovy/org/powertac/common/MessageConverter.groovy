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
import com.thoughtworks.xstream.hibernate.converter.HibernateProxyConverter;
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
import org.powertac.common.command.CustomerBootstrapData
import org.powertac.common.command.CustomerList
import org.powertac.common.command.ErrorCmd
import org.powertac.common.command.LoginRequestCmd
import org.powertac.common.command.SimPause
import org.powertac.common.command.SimResume
import org.powertac.common.command.SimStart
import org.powertac.common.command.SimEnd;

class MessageConverter implements org.springframework.beans.factory.InitializingBean
{
  // injection
  def grailsApplication

  // TODO: retrieve all classes within cmd package programmatically
  def commandClasses = [CustomerBootstrapData, CustomerList, ErrorCmd, SimEnd, SimStart, SimPause, SimResume]

  XStream xstream
  private static final log = LogFactory.getLog(this)

  void afterPropertiesSet ()
  {
    xstream = new XStream() {
        protected MapperWrapper wrapMapper(final MapperWrapper next) {
          return new HibernateMapper(next);
        }
    };

    def classes = grailsApplication.domainClasses*.clazz
//    def classes = grailsApplication.getArtefacts("Domain")*.clazz
    for (clazz in classes) {
      xstream.processAnnotations(clazz)
      xstream.omitField(clazz, 'version')
    }

    for (clazz in commandClasses) {
      xstream.processAnnotations(clazz)
    }

    xstream.registerConverter(new HibernateProxyConverter())
    xstream.registerConverter(new HibernatePersistentCollectionConverter(xstream.getMapper()))
    xstream.registerConverter(new HibernatePersistentMapConverter(xstream.getMapper()))
    xstream.registerConverter(new HibernatePersistentSortedMapConverter(xstream.getMapper()))
    xstream.registerConverter(new HibernatePersistentSortedSetConverter(xstream.getMapper()))
    xstream.autodetectAnnotations(true);
  }

  String toXML(Object message) {
    xstream.toXML(message)
  }

  Object fromXML(String xml) {
    xstream.fromXML(xml)
  }
}
