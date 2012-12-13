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
package org.openmrs.module.metadatasharing.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component(MetadataSharingConsts.MODULE_ID + ".ReplaceMethodInovker")
public class ReplaceMethodInovker {
	
	public final static String ON_SAVE = "onSave";
	
	public final static String WRITE_REPLACE = "writeReplace";
	
	public final static String GET_DEPENDENCIES = "getPriorityDependenciesForMetadataSharing";
	
	public void callOnSave(Object object, Map<OpenmrsObject, OpenmrsObject> mappings) {
		if (object instanceof OpenmrsObject) {
			Method method = getMethod(object.getClass(), ON_SAVE, Map.class);
			if (method != null) {
				invokeMethod(method, object, mappings);
			}
		}
	}
	
	public OpenmrsObject callWriteReplace(OpenmrsObject object) {
		Method method = getMethod(object.getClass(), WRITE_REPLACE);
		if (method != null) {
			Object result = invokeMethod(method, object);
			return (OpenmrsObject) result;
		}
		return object;
	}
	
	@SuppressWarnings("unchecked")
    public List<Object> callGetPriorityDependenciesForMetadataSharing(Object object) {
		Method method = getMethod(object.getClass(), GET_DEPENDENCIES);
		if (method != null) {
			if (List.class.isAssignableFrom(method.getReturnType())) {
				return (List<Object>) invokeMethod(method, object);
			}
		}
		return Collections.emptyList();
	}
	
	public Object invokeMethod(Method method, Object object, Object... args) {
		try {
			return method.invoke(object, args);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to call " + method.getName(), e);
		}
		catch (InvocationTargetException e) {
			throw new RuntimeException("Failed to call " + method.getName() + "\n\n" + ExceptionUtils.getFullStackTrace(e), e);
		}
	}
	
	public Method getMethod(Class<?> type, String name, Class<?>... parameterTypes) {
		try {
			Method result = type.getDeclaredMethod(name, parameterTypes);
			result.setAccessible(true);
			return result;
		}
		catch (NoSuchMethodException e) {
			return null;
		}
	}
	
}
