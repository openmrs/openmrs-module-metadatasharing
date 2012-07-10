/**
 * The contents of this file are subjsect to the OpenMRS Public License
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
package org.openmrs.module.metadatasharing.handler;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.api.db.DAOException;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.reflection.ClassUtil;

/**
 * Shortcut to {@link HandlerEngine} methods.
 */
public abstract class Handler {
	
	private static HandlerEngine getHandlerEngine() {
		return MetadataSharing.getInstance().getHandlerEngine();
	}
	
	/**
	 * @see MetadataSaveHandler#saveItem(Object)
	 */
	public static Object saveItem(Object object) {
		return getHandlerEngine().getSaveHandler(object).saveItem(object);
	}
	
	/**
	 * @see MetadataSearchHandler#getItemById(Class, Integer)
	 */
	public static Object getItemById(Class<?> type, Integer id) throws DAOException {
		type = ClassUtil.getDeproxiedClass(type);
		
		Object item = getHandlerEngine().getSearchHandler(type).getItemById(type, id);
		if (item != null) {
			Handler.initObject(item);
		}
		return item;
	}
	
	/**
	 * @see MetadataSearchHandler#getItemByUuid(Class, String)
	 */
	public static Object getItemByUuid(Class<?> type, String uuid) throws DAOException {
		type = ClassUtil.getDeproxiedClass(type);
		
		Object item = getHandlerEngine().getSearchHandler(type).getItemByUuid(type, uuid);
		if (item != null) {
			Handler.initObject(item);
		}
		return item;
	}
	
	/**
	 * @see MetadataSearchHandler#getItemsCount(Class, boolean, String)
	 */
	public static int getItemsCount(Class<?> type, boolean includeRetired, String phrase) throws DAOException {
		type = ClassUtil.getDeproxiedClass(type);
		
		return getHandlerEngine().getSearchHandler(type).getItemsCount(type, includeRetired, phrase);
	}
	
	/**
	 * @see MetadataSearchHandler#getItems(Class, boolean, String, Integer, Integer)
	 */
	public static List<Object> getItems(Class<?> type, boolean includeRetired, String phrase, Integer firstResult,
	                                    Integer maxResults) throws DAOException {
		type = ClassUtil.getDeproxiedClass(type);
		
		return getHandlerEngine().getSearchHandler(type).getItems(type, includeRetired, phrase,
		    firstResult, maxResults);
	}
	
	/**
	 * @see MetadataPriorityDependenciesHandler#getPriorityDependencies(Object)
	 */
	public static List<Object> getPriorityDependencies(Object object) {
		List<Object> priorityDependencies = null;
		if (getHandlerEngine().hasPriorityDependenciesHandler(object)) {
			priorityDependencies = getHandlerEngine().getPriorityDependenciesHandler(object).getPriorityDependencies(object);
		}
		
		if (priorityDependencies == null) {
			priorityDependencies = Collections.emptyList();
		}
		return priorityDependencies;
	}
	
	/**
	 * @see HandlerEngine#getRegisteredClass(String)
	 */
	public static Class<?> getRegisteredClass(String type) {
		return getHandlerEngine().getRegisteredClass(type);
	}
	
	/**
	 * @see HandlerEngine#getRegisteredClass(Class)
	 */
	public static Class<?> getRegisteredClass(Class<?> clazz) {
		return getHandlerEngine().getRegisteredClass(clazz);
	}
	
	/**
	 * @see HandlerEngine#getRegisteredClass(Object)
	 */
	public static Class<?> getRegisteredClass(Object object) {
		return getHandlerEngine().getRegisteredClass(object);
	}
	
	/**
	 * @see HandlerEngine#getRegisteredTypes()
	 */
	public static Map<String, Class<?>> getRegisteredTypes() {
		return getHandlerEngine().getRegisteredTypes();
	}
	
	/**
	 * @see HandlerEngine#getRegisteredType(Class)
	 */
	public static String getRegisteredType(Class<?> clazz) {
		return getHandlerEngine().getRegisteredType(clazz);
	}
	
	/**
	 * @see HandlerEngine#getRegisteredType(Object)
	 */
	public static String getRegisteredType(Object object) {
		return getHandlerEngine().getRegisteredType(object);
	}
	
	/**
	 * @see HandlerEngine#isHidden(Object)
	 */
	public static boolean isHidden(Object object) {
		return getHandlerEngine().isHidden(object);
	}
	
	/**
	 * @see HandlerEngine#initObject(Object)
	 */
	public static void initObject(Object object) {
		getHandlerEngine().initObject(object);
	}
	
	/**
	 * @see MetadataPropertiesHandler#getId(Object)
	 */
	public static Integer getId(Object object) {
		return getHandlerEngine().getPropertiesHandler(object).getId(object);
	}
	
	/**
	 * @see MetadataPropertiesHandler#setId(Object, Integer)
	 */
	public static void setId(Object object, Integer id) {
		getHandlerEngine().getPropertiesHandler(object).setId(object, id);
	}
	
	/**
	 * @see MetadataPropertiesHandler#getUuid(Object)
	 */
	public static String getUuid(Object object) {
		return getHandlerEngine().getPropertiesHandler(object).getUuid(object);
	}
	
	/**
	 * @see MetadataPropertiesHandler#setUuid(Object, String)
	 */
	public static void setUuid(Object object, String uuid) {
		getHandlerEngine().getPropertiesHandler(object).setUuid(object, uuid);
	}
	
	/**
	 * @see MetadataPropertiesHandler#getRetired(Object)
	 */
	public static Boolean getRetired(Object object) {
		return getHandlerEngine().getPropertiesHandler(object).getRetired(object);
	}
	
	/**
	 * @see MetadataPropertiesHandler#getRetired(Object)
	 */
	public static boolean isRetired(Object object) {
		Boolean retired = getRetired(object);
		return (retired != null) ? retired : false;
	}
	
	/**
	 * @see MetadataPropertiesHandler#setRetired(Object, Boolean)
	 */
	public static void setRetired(Object object, Boolean retired) {
		getHandlerEngine().getPropertiesHandler(object).setRetired(object, retired);
	}
	
	/**
	 * @see MetadataPropertiesHandler#getName(Object)
	 * @return the name or empty string
	 */
	public static String getName(Object object) {
		String name = getHandlerEngine().getPropertiesHandler(object).getName(object);
		
		return (name != null) ? name : "";
	}
	
	/**
	 * @see MetadataPropertiesHandler#getDescription(Object)
	 * @return the description or empty string
	 */
	public static String getDescription(Object object) {
		String description = getHandlerEngine().getPropertiesHandler(object).getDescription(object);
		
		return (description != null) ? description : "";
	}
	
	/**
	 * @see MetadataPropertiesHandler#getDateChanged(Object)
	 */
	public static Date getDateChanged(Object object) {
		return getHandlerEngine().getPropertiesHandler(object).getDateChanged(object);
	}
	
	/**
	 * @see MetadataPropertiesHandler#getProperties(Object)
	 * @return the properties or empty map
	 */
	public static Map<String, Object> getProperties(Object object) {
		Map<String, Object> properties = getHandlerEngine().getPropertiesHandler(object).getProperties(object);
		if (properties == null) {
			properties = Collections.emptyMap();
		}
		return properties;
	}
	
}
