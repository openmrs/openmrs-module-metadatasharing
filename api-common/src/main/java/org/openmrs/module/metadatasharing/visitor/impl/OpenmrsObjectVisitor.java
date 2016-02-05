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
package org.openmrs.module.metadatasharing.visitor.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.proxy.HibernateProxy;
import org.openmrs.OpenmrsObject;
import org.openmrs.User;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.reflection.ReplaceMethodInovker;
import org.openmrs.module.metadatasharing.visitor.ObjectVisitor;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;

/**
 * The pure java reflection implementation of {@link ObjectVisitor} for Openmrs.
 */
@Component(MetadataSharingConsts.MODULE_ID + ".OpenmrsObjectVisitor")
public class OpenmrsObjectVisitor implements ObjectVisitor {

	private PureJavaReflectionProvider reflectionProvider = new PureJavaReflectionProvider();

	private ReplaceMethodInovker methodInvoker = new ReplaceMethodInovker();

	/**
	 * @see org.openmrs.module.metadatasharing.visitor.ObjectVisitor#writeField(java.lang.Object,
	 *      java.lang.String, java.lang.Object, java.lang.Class)
	 */
	@Override
	public void writeField(Object object, String fieldName, Object value, Class<?> definedIn) {
		reflectionProvider.writeField(object, fieldName, value, definedIn);
	}

	/**
	 * @see org.openmrs.module.metadatasharing.visitor.ObjectVisitor#readField(java.lang.Object,
	 *      java.lang.String, java.lang.Class)
	 */
	@Override
	public Object readField(Object object, String fieldName, Class<?> definedIn) {
		Field field = reflectionProvider.getField(definedIn, fieldName);
		field.setAccessible(true);
		Object result;
		try {
			result = field.get(object);
		}
		catch (IllegalAccessException e) {
			throw new ObjectAccessException("Cannot access field " + object.getClass().getName() + "." + field.getName());
		}
		return result;
	}

	/**
	 * @see org.openmrs.module.metadatasharing.visitor.ObjectVisitor#visitFields(java.lang.Object,
	 *      boolean, org.openmrs.module.metadatasharing.visitor.ObjectVisitor.FieldVisitor)
	 */
	@Override
	public void visitFields(Object object, final boolean writeReplace, final FieldVisitor visitor) {
		if (object instanceof User) {
			return;
		}

		if (object instanceof HibernateProxy) {
			try {
				object = ((HibernateProxy) object).getHibernateLazyInitializer().getImplementation();
			} catch (ObjectNotFoundException e) {
				String objectDescription = object.getClass().getSimpleName();
				if (object instanceof OpenmrsObject) {
					OpenmrsObject openmrsObject = (OpenmrsObject) object;
					objectDescription += "[id:" + openmrsObject.getId() + " uuid:" + openmrsObject.getUuid() + "]";
				}
				throw new RuntimeException("Failed resolving " + objectDescription + " due to " + e.getMessage(), e);
			}
		}

		if (writeReplace && object instanceof OpenmrsObject) {
			object = methodInvoker.callWriteReplace((OpenmrsObject) object);
		}

		final Object toVisit = object;
		reflectionProvider.visitSerializableFields(toVisit, new ReflectionProvider.Visitor() {

			@Override
			public void visit(String name, @SuppressWarnings("rawtypes") Class type,
			                  @SuppressWarnings("rawtypes") Class definedIn, Object value) {
				if (value instanceof User) {
					return;
				}

				if (value instanceof HibernateProxy) {
					try {
						value = ((HibernateProxy) value).getHibernateLazyInitializer().getImplementation();
					} catch (ObjectNotFoundException e) {
						String objectDescription = toVisit.getClass().getSimpleName();
						if (toVisit instanceof OpenmrsObject) {
							OpenmrsObject openmrsObject = (OpenmrsObject) toVisit;
							objectDescription += "[id:" + openmrsObject.getId() + " uuid:" + openmrsObject.getUuid() + "]";
						}
						throw new RuntimeException("Failed resolving " + objectDescription + " due to " + e.getMessage(), e);
					}
				}

				if (writeReplace && value instanceof OpenmrsObject) {
					value = methodInvoker.callWriteReplace((OpenmrsObject) value);
				}

				visitor.visit(name, type, definedIn, value);
			}

		});
	}

	/**
	 * @see org.openmrs.module.metadatasharing.visitor.ObjectVisitor#copyFields(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void copyFields(final Object source, final Object target) {
		if (source == target) {
			throw new IllegalArgumentException("Source (" + source.getClass() + ") and target (" + target.getClass()
			        + ") cannot be the same objects");
		}

		visitFields(source, false, new ObjectVisitor.FieldVisitor() {

			@Override
			public void visit(String fieldName, Class<?> type, Class<?> definedIn, Object value) {
				if (!(value instanceof Collection || value instanceof Map)) {
					writeField(target, fieldName, value, definedIn);
				}
			}
		});
	}
}
