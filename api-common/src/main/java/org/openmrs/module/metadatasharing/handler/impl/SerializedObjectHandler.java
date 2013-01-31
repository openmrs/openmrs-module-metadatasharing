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
import java.util.Map;

import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.metadatasharing.api.MetadataService;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSaveHandler;
import org.openmrs.module.metadatasharing.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Adds an ability to import {@link SerializedObject}.
 */
@Component("metadatasharing.SerializedObjectHandler")
public class SerializedObjectHandler implements MetadataSaveHandler<SerializedObject>, MetadataPropertiesHandler<SerializedObject> {
	
	@Autowired
	MetadataService service;
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataHandler#getPriority()
	 */
	@Override
	public int getPriority() {
		return 0;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataSaveHandler#saveItem(java.lang.Object)
	 */
	@Override
	public SerializedObject saveItem(SerializedObject item) throws DAOException {
		return service.saveItem(item);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#getId(java.lang.Object)
	 */
	@Override
	public Integer getId(SerializedObject object) {
		return object.getId();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#setId(java.lang.Object,
	 *      java.lang.Integer)
	 */
	@Override
	public void setId(SerializedObject object, Integer id) {
		object.setId(id);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#getUuid(java.lang.Object)
	 */
	@Override
	public String getUuid(SerializedObject object) {
		return (object.getUuid() != null) ? object.getUuid() : "";
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#setUuid(java.lang.Object,
	 *      java.lang.String)
	 */
	@Override
	public void setUuid(SerializedObject object, String uuid) {
		object.setUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#getRetired(java.lang.Object)
	 */
	@Override
	public Boolean getRetired(SerializedObject object) {
		return object.isRetired();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#setRetired(java.lang.Object,
	 *      java.lang.Boolean)
	 */
	@Override
	public void setRetired(SerializedObject object, Boolean retired) {
		object.setRetired(retired);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#getName(java.lang.Object)
	 */
	@Override
	public String getName(SerializedObject object) {
		return (object.getName() != null) ? object.getName() : "";
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#getDescription(java.lang.Object)
	 */
	@Override
	public String getDescription(SerializedObject object) {
		String description = "";
		if (object.getType() != null) {
			description += "[" + object.getType().substring(object.getType().lastIndexOf(".") + 1) + "]";
		}
		if (object.getDescription() != null) {
			description += " " + object.getDescription();
		}
		return description;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#getDateChanged(java.lang.Object)
	 */
	@Override
	public Date getDateChanged(SerializedObject object) {
		return DateUtil.getLastDateChanged(object);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler#getProperties(java.lang.Object)
	 */
	@Override
	public Map<String, Object> getProperties(SerializedObject object) {
		return Collections.emptyMap();
	}
	
}
