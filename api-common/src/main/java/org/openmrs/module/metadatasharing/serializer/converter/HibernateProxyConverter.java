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

import org.hibernate.proxy.HibernateProxy;
import org.openmrs.module.metadatasharing.serializer.mapper.HibernateProxyMapper;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Strips Hibernate proxies by retrieving the underlying objects that are next marshalled.
 * <p>
 * WARNING: We don't use this, because it breaks handling subclass entities properly. See also
 * {@link HibernateProxyMapper}.
 * <p>
 * This converter only takes care of delivering the underlying objects while
 * {@link HibernateProxyMapper} takes care of the naming.
 * <p>
 * Reference: <a
 * href="http://jira.codehaus.org/browse/XSTR-226">http://jira.codehaus.org/browse/XSTR-226</a>
 * 
 * @see org.openmrs.module.metadatasharing.serializer.mapper.HibernateProxyMapper
 * @see org.hibernate.proxy.HibernateProxy
 */
public class HibernateProxyConverter implements Converter {
	
	/**
	 * @see com.thoughtworks.xstream.converters.reflection.ReflectionConverter#canConvert(Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return HibernateProxy.class.isAssignableFrom(clazz);
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.reflection.ReflectionConverter#marshal(Object,
	 *      HierarchicalStreamWriter, MarshallingContext)
	 */
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Object impl = ((HibernateProxy) source).getHibernateLazyInitializer().getImplementation();
		context.convertAnother(impl);
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
	    return null;
	}
	
}
