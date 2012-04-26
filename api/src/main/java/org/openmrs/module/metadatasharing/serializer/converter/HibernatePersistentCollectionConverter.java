/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.metadatasharing.serializer.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentList;
import org.hibernate.collection.PersistentMap;
import org.hibernate.collection.PersistentSet;
import org.hibernate.collection.PersistentSortedMap;
import org.hibernate.collection.PersistentSortedSet;
import org.openmrs.module.metadatasharing.serializer.mapper.HibernatePersistentCollectionMapper;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Strips Hibernate 3 collections by retrieving the underlying collection which is then processed by
 * the delegated converter.
 * <p>
 * This converter only takes care of the values inside the collections while
 * {@link HibernatePersistentCollectionMapper} takes care of the collections naming.
 * <p>
 * Reference: <a
 * href="http://jira.codehaus.org/browse/XSTR-226">http://jira.codehaus.org/browse/XSTR-226</a>
 * 
 * @see org.openmrs.module.metadatasharing.serializer.mapper.HibernatePersistentCollectionMapper
 * @see com.thoughtworks.xstream.converters.collections.CollectionConverter
 * @see org.hibernate.collection.PersistentCollection
 */
public class HibernatePersistentCollectionConverter implements Converter {
	
	private final ConverterLookup converterLookup;
	
	public HibernatePersistentCollectionConverter(ConverterLookup converterLookup) {
		this.converterLookup = converterLookup;
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.Converter#canConvert(Class)
	 */
	@Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return PersistentCollection.class.isAssignableFrom(type);
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		if (source instanceof PersistentList) {
			source = new ArrayList((Collection) source);
		} else if (source instanceof PersistentMap) {
			source = new HashMap((Map) source);
		} else if (source instanceof PersistentSortedMap) {
			source = new TreeMap((SortedMap) source);
		} else if (source instanceof PersistentSortedSet) {
			source = new TreeSet((SortedSet) source);
		} else if (source instanceof PersistentSet) {
			source = new HashSet((Set) source);
		}
		
		// delegate the collection to the approapriate converter
		converterLookup.lookupConverterForType(source.getClass()).marshal(source, writer, context);
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
	 *      com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	@Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return null;
	}
}
