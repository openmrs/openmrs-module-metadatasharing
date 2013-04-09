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
package org.openmrs.module.metadatasharing.serializer.mapper;

import org.hibernate.proxy.HibernateProxy;
import org.openmrs.module.metadatasharing.visitor.impl.OpenmrsObjectVisitor;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Removes proxy marker from classnames managed by Hibernate.
 * <p>
 * WARNING: It doesn't work correctly for subclassed entities, because proxyClass.getSuperclass() returns
 * superclasses e.g. the org.openmrs.ConceptNumeric proxy returns org.openmrs.Concept. For
 * that reason we always serialize previously deproxied objects. See {@link OpenmrsObjectVisitor},
 * which is used before serialization. We rely on the "resolves-to" attribute instead, which displays
 * the correct thing.
 * <p>
 * Reference: <a
 * href="http://jira.codehaus.org/browse/XSTR-226">http://jira.codehaus.org/browse/XSTR-226</a>
 * 
 * @see org.openmrs.module.metadatasharing.serializer.mapper.HibernateProxyMapper
 * @see org.hibernate.proxy.HibernateProxy
 */
public class HibernateProxyMapper extends MapperWrapper {
	
	/**
	 * @see com.thoughtworks.xstream.mapper.MapperWrapper#MapperWrapper(Mapper)
	 */
	public HibernateProxyMapper(Mapper wrapped) {
		super(wrapped);
	}
	
	/**
	 * @see com.thoughtworks.xstream.mapper.MapperWrapper#serializedClass(java.lang.Class)
	 */
	@Override
	public String serializedClass(@SuppressWarnings("rawtypes") Class type) {
		if (HibernateProxy.class.isAssignableFrom(type)) {
			type = type.getSuperclass();
		}
		return super.serializedClass(type);
	}
	
	/**
	 * @see com.thoughtworks.xstream.mapper.MapperWrapper#isImmutableValueType(java.lang.Class)
	 */
	@Override
	public boolean isImmutableValueType(@SuppressWarnings("rawtypes") Class type) {
		if (HibernateProxy.class.isAssignableFrom(type)) {
			type = type.getSuperclass();
		}
		return super.isImmutableValueType(type);
	}
}
