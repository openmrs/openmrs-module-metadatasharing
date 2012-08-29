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

import org.openmrs.OpenmrsObject;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.reflection.ClassUtil;
import org.openmrs.module.metadatasharing.util.MetadataSharingGlobalPropertyListener;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Prepares OpenmrsObject objects for serialization.
 */
public class OpenmrsObjectConverter extends ReflectionConverter {
	
	public OpenmrsObjectConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
		super(mapper, reflectionProvider);
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		// write out the xml for this object
		super.marshal(source, writer, context);
	}
	
	/**
	 * @param source
	 * @return true if the user has marked this object as "should persist ids" with the global
	 *         property
	 * @see MetadataSharingGlobalPropertyListener
	 * @see MetadataSharingConsts#GP_PERSIST_IDS_FOR_CLASSES
	 */
	private boolean persistIds(Object source) {
		Class<Object> sourceClass = ClassUtil.getDeproxiedClass(source);
		String className = sourceClass.getName();
		
		// first do a quick O(log n) search for the class
		if (MetadataSharingGlobalPropertyListener.getClassNamesToPersistIds().contains(className)) {
			return true;
		}
		
		// if its not there, check all subclasses of the defined classes with something that
		// is slower: O(n) (or worse since its doing object/class comparison)
		for (Class<?> c : MetadataSharingGlobalPropertyListener.getClassesToPersistIds()) {
			// allows for something like Concept to be in classToPersistIds and sourceClass to be ConceptNumeric
			if (c.isAssignableFrom(sourceClass))
				return true;
		}
		
		return false;
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
	 *      com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Object obj = super.unmarshal(reader, context);
		
		if (!persistIds(obj)) {
			// make sure we don't try to persist this id
			Handler.setId(obj, null);
		} else {
			// try to persist the id, but first check to make sure the id doesn't exist
			// if the id exists and the uuid for that object is diff, null out the id so it doesn't overwrite
			Integer id = Handler.getId(obj);
			if (id != null) {
				Object o = Handler.getItemById(obj.getClass(), id);
				if (o != null) {
					// make sure we don't try to persist this id because this object already exists
					// if this is the same object then it will have the same uuid, and the MDS machinery
					// will match up the ids again later anyway
					Handler.setId(obj, null);
				}
			}
		}
		
		return obj;
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return OpenmrsObject.class.isAssignableFrom(type);
	}
	
}
