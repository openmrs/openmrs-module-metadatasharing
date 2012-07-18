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
package org.openmrs.module.metadatasharing.handler.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSaveHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSearchHandler;
import org.openmrs.module.metadatasharing.handler.MetadataTypesHandler;
import org.openmrs.module.metadatasharing.util.DateUtil;
import org.springframework.stereotype.Component;

/**
 * Adds support for GlobalProperties.
 */
@Component("metadatasharing.GlobalPropertiesHandler")
public class GlobalPropertiesHandler implements MetadataTypesHandler<GlobalProperty>, MetadataSearchHandler<GlobalProperty>, MetadataSaveHandler<GlobalProperty>, MetadataPropertiesHandler<GlobalProperty> {
	
	private final Map<Class<? extends GlobalProperty>, String> types;
	
	public GlobalPropertiesHandler() {
		Map<Class<? extends GlobalProperty>, String> tmpTypes = new HashMap<Class<? extends GlobalProperty>, String>();
		tmpTypes.put(GlobalProperty.class, "Global Property");
		types = Collections.unmodifiableMap(tmpTypes);
	}
	
	@Override
	public int getPriority() {
	    return 0;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataTypesHandler#getTypes()
	 */
	@Override
	public Map<Class<? extends GlobalProperty>, String> getTypes() {
		return types;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataSearchHandler#getItemsCount(java.lang.Class,
	 *      boolean, java.lang.String)
	 */
	@Override
	public int getItemsCount(Class<? extends GlobalProperty> type, boolean includeRetired, String phrase)
	    throws DAOException {
		return getItems(type, includeRetired, phrase, null, null).size();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataSearchHandler#getItems(java.lang.Class,
	 *      boolean, java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<GlobalProperty> getItems(Class<? extends GlobalProperty> type, boolean includeRetired, String phrase,
	                                     Integer firstResult, Integer maxResults) throws DAOException {
		List<GlobalProperty> properties;
		if (phrase == null) {
			properties = Context.getAdministrationService().getAllGlobalProperties();
		} else {
			properties = Context.getAdministrationService().getGlobalPropertiesByPrefix(phrase);
		}
		
		if (firstResult == null) {
			firstResult = 0;
		}
		
		if (maxResults == null || firstResult + maxResults > properties.size()) {
			maxResults = properties.size() - firstResult;
		}
		
		return properties.subList(firstResult, firstResult + maxResults);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataSearchHandler#getItemByUuid(java.lang.Class,
	 *      java.lang.String)
	 */
	@Override
	public GlobalProperty getItemByUuid(Class<? extends GlobalProperty> type, String uuid) throws DAOException {
		return Context.getAdministrationService().getGlobalPropertyByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataSaveHandler#saveItem(java.lang.Object)
	 */
	@Override
	public GlobalProperty saveItem(GlobalProperty item) throws DAOException {
		return Context.getAdministrationService().saveGlobalProperty(item);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#getId(java.lang.Object)
	 */
	@Override
	public Integer getId(GlobalProperty object) {
		return null;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#setId(java.lang.Object,
	 *      java.lang.Integer)
	 */
	@Override
	public void setId(GlobalProperty object, Integer id) {
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#getUuid(java.lang.Object)
	 */
	@Override
	public String getUuid(GlobalProperty object) {
		return object.getUuid();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#setUuid(java.lang.Object,
	 *      java.lang.String)
	 */
	@Override
	public void setUuid(GlobalProperty object, String uuid) {
		object.setUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#getRetired(java.lang.Object)
	 */
	@Override
	public Boolean getRetired(GlobalProperty object) {
	    return null;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#setRetired(java.lang.Object, boolean)
	 */
	@Override
	public void setRetired(GlobalProperty object, Boolean retired) {   
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#getName(java.lang.Object)
	 */
	@Override
	public String getName(GlobalProperty object) {
		return object.getProperty();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#getDescription(java.lang.Object)
	 */
	@Override
	public String getDescription(GlobalProperty object) {
		return object.getDescription();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#getDateChanged(java.lang.Object)
	 */
	@Override
	public Date getDateChanged(GlobalProperty object) {
		return DateUtil.getLastDateChanged(object);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#getProperties(java.lang.Object)
	 */
	@Override
	public Map<String, Object> getProperties(GlobalProperty object) {
		return Collections.emptyMap();
	}

	@Override
	public GlobalProperty getItemById(Class<? extends GlobalProperty> type,
			Integer propertyName) throws DAOException {
		return null;
	}
	
}
