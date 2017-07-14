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
package org.openmrs.module.metadatasharing.serializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.serializer.converter.SubscriptionStatusConverter;
import org.openmrs.serialization.OpenmrsSerializer;
import org.springframework.stereotype.Component;

import java.io.Writer;

/**
 * Serializes objects into JSON string. It is used mostly in AJAX pages to serialize response.
 * 
 * @see org.openmrs.serialization.OpenmrsSerializer
 */
@Component(MetadataSharingConsts.MODULE_ID + ".JSONSerializer")
public class JSONSerializer implements OpenmrsSerializer {
	
	private XStream xstream;
	
	/**
	 * Creates serializer instance and registers converters.
	 */
	public JSONSerializer() {
		xstream = new XStream(new JsonHierarchicalStreamDriver() {
			
			@Override
            public HierarchicalStreamWriter createWriter(Writer writer) {
				return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
			}
		});
		xstream.registerConverter(new SubscriptionStatusConverter());
		xstream.autodetectAnnotations(true);
	}
	
	/**
	 * @see org.openmrs.serialization.OpenmrsSerializer#serialize(java.lang.Object)
	 */
	@Override
    public String serialize(Object o) {
		return xstream.toXML(o);
	}
	
	/**
	 * @see org.openmrs.serialization.OpenmrsSerializer#deserialize(java.lang.String,
	 *      java.lang.Class)
	 */
	@Override
    public <T> T deserialize(String serializedObject, Class<? extends T> clazz) {
		@SuppressWarnings("unchecked")
		T object = (T) xstream.fromXML(serializedObject);
		return object;
	}
}
