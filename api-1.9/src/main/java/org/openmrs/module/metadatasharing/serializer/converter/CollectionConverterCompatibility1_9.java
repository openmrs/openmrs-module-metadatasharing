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
import org.openmrs.annotation.OpenmrsProfile;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@OpenmrsProfile(openmrsPlatformVersion = "1.9.9 - 1.12.*")
public class CollectionConverterCompatibility1_9 implements CollectionConverterCompatibility {

	@Override
	public boolean canConvert(Class type) {
		return PersistentCollection.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context, ConverterLookup converterLookup) {
		
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
}
