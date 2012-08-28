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
import java.util.Iterator;
import java.util.List;

import org.openmrs.module.metadatasharing.handler.MetadataDeserializer;
import org.openmrs.module.metadatasharing.serializer.MetadataSerializer;
import org.openmrs.util.OpenmrsConstants;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class XStreamMetadataDeserializer extends MetadataDeserializer {
	
	private final HierarchicalStreamReader reader;
	
	private final UnmarshallingContext context;
	
	public XStreamMetadataDeserializer(HierarchicalStreamReader reader, UnmarshallingContext context) {
		this.reader = reader;
		this.context = context;
	}
	
	@Override
	public Collection<String> getAttributeNames() {
		List<String> list = new ArrayList<String>();
		
		@SuppressWarnings("unchecked")
		Iterator<String> attributeNames = reader.getAttributeNames();
		while (attributeNames.hasNext()) {
			list.add(attributeNames.next());
		}
		
		return list;
	}
	
	@Override
	public String getAttribute(String name) {
		return reader.getAttribute(name);
	}
	
	@Override
	public boolean hasMoreChildren() {
		return reader.hasMoreChildren();
	}
	
	@Override
	public void moveDown() {
		reader.moveDown();
	}
	
	@Override
	public void moveUp() {
		reader.moveUp();
	}
	
	@Override
	public String getNodeName() {
		return reader.getNodeName();
	}
	
	@Override
	public String getValue() {
		return reader.getValue();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object current, Class<? extends T> type) {
		return (T) context.convertAnother(current, type);
	}
	
	@Override
	public Class<?> getType() {
		return context.getRequiredType();
	}
	
	@Override
	public String getFromVersion() {
		Object object = context.get(MetadataSerializer.FROM_VERSION_CONTEXT);
		if (object instanceof String) {
			return (String) object;
		} else {
			return null;
		}
	}
	
	@Override
	public String getToVersion() {
		return OpenmrsConstants.OPENMRS_VERSION;
	}

	@Override
    public void putReference(Object value) {
		context.put(reader.getAttribute("id"), value);
    }

	@Override
    public Object getReference() {
	    return context.get(reader.getAttribute("id"));
    }
	
}
