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

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.FormField;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.User;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataTypesHandler;
import org.openmrs.module.metadatasharing.reflection.OpenmrsClassScanner;
import org.openmrs.module.metadatasharing.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("metadatasharing.OpenmrsMetadataHandler")
public class OpenmrsMetadataHandler implements MetadataTypesHandler<OpenmrsMetadata>, MetadataPropertiesHandler<OpenmrsMetadata> {
	
	private final Map<Class<? extends OpenmrsMetadata>, String> types;
	
	@Autowired
	public OpenmrsMetadataHandler(OpenmrsClassScanner scanner) throws IOException {
		Map<Class<? extends OpenmrsMetadata>, String> tmpTypes = new HashMap<Class<? extends OpenmrsMetadata>, String>();
		for (Class<OpenmrsMetadata> clazz : scanner.getOpenmrsMetadataClasses()) {
			tmpTypes.put(clazz, clazz.getSimpleName());
		}
		tmpTypes.remove(User.class);
		tmpTypes.remove(FormField.class);
		types = Collections.unmodifiableMap(tmpTypes);
	}
	
	@Override
	public Map<Class<? extends OpenmrsMetadata>, String> getTypes() {
		return types;
	}
	
	@Override
	public Integer getId(OpenmrsMetadata object) {
		return object.getId();
	}
	
	@Override
	public void setId(OpenmrsMetadata object, Integer id) {
		object.setId(id);
	}
	
	@Override
	public void setUuid(OpenmrsMetadata object, String uuid) {
		object.setUuid(uuid);
	}
	
	@Override
	public String getUuid(OpenmrsMetadata object) {
		return (object.getUuid() != null) ? object.getUuid() : "";
	}
	
	@Override
	public Boolean getRetired(OpenmrsMetadata object) {
	    return object.isRetired();
	}
	
	@Override
	public void setRetired(OpenmrsMetadata object, Boolean retired) {
		object.setRetired(retired);
	}
	
	@Override
	public String getName(OpenmrsMetadata object) {
		return (object.getName() != null) ? object.getName() : "";
	}
	
	@Override
	public String getDescription(OpenmrsMetadata object) {
		return (object.getDescription() != null) ? object.getDescription() : "";
	}
	
	@Override
	public Date getDateChanged(OpenmrsMetadata object) {
		return DateUtil.getLastDateChanged(object);
	}
	
	@Override
	public Map<String, Object> getProperties(OpenmrsMetadata object) {
		return Collections.emptyMap();
	}
}
