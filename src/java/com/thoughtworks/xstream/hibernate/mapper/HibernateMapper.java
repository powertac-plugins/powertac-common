/*
 * Copyright (C) 2007, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 11. January 2007 by Konstantin Pribluda
 */
package com.thoughtworks.xstream.hibernate.mapper;

import com.thoughtworks.xstream.mapper.MapperWrapper;
import org.hibernate.collection.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.*;


/**
 * Mapper for Hibernate types. It will map the class names of the Hibernate collections with
 * equivalents of the JDK at serialization time. It will also map the names of the proxy types
 * to the names of the proxies element's type.
 *
 * @author Konstantin Pribluda
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class HibernateMapper extends MapperWrapper
{

  final private Map collectionMap = new HashMap();

  public HibernateMapper (final MapperWrapper mapper)
  {
    super(mapper);
    collectionMap.put(PersistentBag.class, ArrayList.class);
    collectionMap.put(PersistentList.class, ArrayList.class);
    collectionMap.put(PersistentMap.class, HashMap.class);
    collectionMap.put(PersistentSet.class, HashSet.class);
    collectionMap.put(PersistentSortedMap.class, TreeMap.class);
    collectionMap.put(PersistentSortedSet.class, TreeSet.class);
  }

  public Class defaultImplementationOf (final Class clazz)
  {
    if (collectionMap.containsKey(clazz)) {
      return super.defaultImplementationOf((Class) collectionMap.get(clazz));
    }

    return super.defaultImplementationOf(clazz);
  }

  public String serializedClass (final Class clazz)
  {
    // check whether we are Hibernate proxy and substitute real name
    if (HibernateProxy.class.isAssignableFrom(clazz)) {
      return super.serializedClass(clazz.getSuperclass());
    }

    if (collectionMap.containsKey(clazz)) {
      // Pretend this is the underlying collection class and map that instead
      return super.serializedClass((Class) collectionMap.get(clazz));
    }

    return super.serializedClass(clazz);
  }
}
