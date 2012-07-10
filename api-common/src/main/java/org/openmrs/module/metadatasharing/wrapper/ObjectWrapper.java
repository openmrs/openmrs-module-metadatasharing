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
package org.openmrs.module.metadatasharing.wrapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.reflection.ClassUtil;

/**
 * Wraps {@link Object} to provide {@link #equals(Object)}, {@link #hashCode()} based on UUIDs and
 * classnames. UUIDs must not be null.
 */
public class ObjectWrapper<T> {
	
	private final T object;
	
	public ObjectWrapper(T object) {
		this.object = object;
		if (object == null) {
			throw new IllegalArgumentException("The argument must not be null");
		}
		if (Handler.getUuid(object) == null) {
			throw new IllegalArgumentException("UUID of " + object + " must not be null");
		}
	}
	
	/**
	 * @return the object
	 */
	public T getObject() {
		return object;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Handler.getUuid(object).hashCode();
		return result;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ObjectWrapper))
			return false;
		
		@SuppressWarnings("unchecked")
		ObjectWrapper<T> other = (ObjectWrapper<T>) obj;
		if (object == other.object)
			return true;
		
		if (!Handler.getUuid(object).equals(Handler.getUuid(other.object)))
			return false;
		
		Class<?> objectClass = ClassUtil.getDeproxiedClass(object);
		Class<?> otherObjectClass = ClassUtil.getDeproxiedClass(other.object);
		
		if (!objectClass.equals(otherObjectClass))
			return false;
		
		return true;
	}
	
	public static <T extends OpenmrsObject> Set<ObjectWrapper<T>> asSet(Collection<? extends T> objects) {
		if (objects == null) {
			return null;
		}
		
		Set<ObjectWrapper<T>> set = new HashSet<ObjectWrapper<T>>();
		for (T object : objects) {
			set.add(new ObjectWrapper<T>(object));
		}
		return set;
	}
	
}
