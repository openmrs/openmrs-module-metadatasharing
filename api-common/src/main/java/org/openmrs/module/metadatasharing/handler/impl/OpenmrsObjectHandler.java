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
import java.util.List;
import java.util.Map;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.metadatasharing.api.MetadataService;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSaveHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSearchHandler;
import org.openmrs.module.metadatasharing.reflection.OpenmrsClassScanner;
import org.openmrs.module.metadatasharing.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("metadatasharing.OpenmrsObjectHandler")
public class OpenmrsObjectHandler implements MetadataPropertiesHandler<OpenmrsObject>, MetadataSearchHandler<OpenmrsObject>, MetadataSaveHandler<OpenmrsObject> {
	
	@Autowired
	private OpenmrsClassScanner scanner;
	
	@Override
	public int getPriority() {
		return 0;
	}
	
	@Override
	public Integer getId(OpenmrsObject object) {
		return object.getId();
	}
	
	@Override
	public void setId(OpenmrsObject object, Integer id) {
		object.setId(id);
	}
	
	@Override
	public void setUuid(OpenmrsObject object, String uuid) {
		object.setUuid(uuid);
	}
	
	@Override
	public String getUuid(OpenmrsObject object) {
		return object.getUuid();
	}
	
	@Override
	public Boolean getRetired(OpenmrsObject object) {
		return null;
	}
	
	@Override
	public void setRetired(OpenmrsObject object, Boolean retired) {
	}
	
	@Override
	public String getName(OpenmrsObject object) {
		return "";
	}
	
	@Override
	public String getDescription(OpenmrsObject object) {
		return "";
	}
	
	@Override
	public Date getDateChanged(OpenmrsObject object) {
		return DateUtil.getLastDateChanged(object);
	}
	
	@Override
	public Map<String, Object> getProperties(OpenmrsObject object) {
		return Collections.emptyMap();
	}
	
	@Override
	public int getItemsCount(Class<? extends OpenmrsObject> type, boolean includeRetired, String phrase) {
		return Context.getService(MetadataService.class).getItemsCount(type, includeRetired, phrase);
	}
	
	@Override
	public List<OpenmrsObject> getItems(Class<? extends OpenmrsObject> type, boolean includeRetired, String phrase,
	                                    Integer firstResult, Integer maxResults) {
		return Context.getService(MetadataService.class).getItems(type, includeRetired, phrase, firstResult, maxResults);
	}
	
	@Override
	public OpenmrsObject getItemByUuid(Class<? extends OpenmrsObject> type, String uuid) {
		return Context.getService(MetadataService.class).getItem(type, uuid);
	}
	
	@Override
	public OpenmrsObject saveItem(OpenmrsObject item) throws DAOException {
		try {
			return scanner.serviceSaveItem(item);
		}
		catch (Exception e) {
			throw new DAOException("Error saving " + item.getClass().getName() + " [" + item.getUuid() + "]", e);
		}
	}
	
	@Override
	public OpenmrsObject getItemById(Class<? extends OpenmrsObject> type, Integer id) throws DAOException {
		
		return Context.getService(MetadataService.class).getItemById(type, id);
	}
}
