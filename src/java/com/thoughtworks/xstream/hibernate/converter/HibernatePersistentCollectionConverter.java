/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 19. April 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.hibernate.converter;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import org.hibernate.collection.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import org.hibernate.collection.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * A converter for Hibernate's {@link PersistentBag}, {@link PersistentList} and
 * {@link PersistentSet}. The converter will drop any reference to the Hibernate
 * collection and emit at serialization time an equivalent JDK collection
 * instead.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class HibernatePersistentCollectionConverter extends CollectionConverter
{

  /**
   * Construct a HibernatePersistentCollectionConverter.
   * 
   * @param mapper
   * @since upcoming
   */
  public HibernatePersistentCollectionConverter (final Mapper mapper)
  {
    super(mapper);
  }

  public Object unmarshal (final HierarchicalStreamReader reader,
                           final UnmarshallingContext context)
  {
    throw new ConversionException("Cannot deserialize Hibernate collection");
  }

  @SuppressWarnings("unchecked")
  public void marshal (Object source, HierarchicalStreamWriter writer,
                       MarshallingContext context)
  {
    Object collection = source;

    if (source instanceof PersistentCollection) {
      PersistentCollection col = (PersistentCollection) source;
      col.forceInitialization();
      collection = col.getStoredSnapshot();
    }

    if (source instanceof PersistentSet) {
      collection = new HashSet(((HashMap) collection).values());
    }

    super.marshal(collection, writer, context);
  }
}
