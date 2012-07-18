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

import org.openmrs.RelationshipType;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.util.DateUtil;
import org.springframework.stereotype.Component;

@Component("metadatasharing.RelationshipTypeHandler")
public class RelationshipTypeHandler implements MetadataPropertiesHandler<RelationshipType> {
	
	@Override
	public int getPriority() {
	    return 0;
	}
	
	@Override
	public Integer getId(RelationshipType object) {
		return object.getId();
	}
	
	@Override
	public void setId(RelationshipType object, Integer id) {
		object.setId(id);
	}
	
	@Override
	public void setUuid(RelationshipType object, String uuid) {
		object.setUuid(uuid);
	}
	
	@Override
	public String getUuid(RelationshipType object) {
		return object.getUuid();
	}
	
	@Override
	public Boolean getRetired(RelationshipType object) {
	    return object.isRetired();
	}
	
	@Override
	public void setRetired(RelationshipType object, Boolean retired) {
		object.setRetired(retired);
	}
	
	@Override
	public String getName(RelationshipType object) {
		return (object.getaIsToB() == null ? "" : object.getaIsToB()) + " / "
		        + (object.getbIsToA() == null ? "" : object.getbIsToA());
	}
	
	@Override
	public String getDescription(RelationshipType object) {
		return (object.getDescription() != null) ? object.getDescription() : "";
	}
	
	@Override
	public Date getDateChanged(RelationshipType object) {
		return DateUtil.getLastDateChanged(object);
	}
	
	@Override
	public Map<String, Object> getProperties(RelationshipType object) {
		return Collections.emptyMap();
	}
	
}
