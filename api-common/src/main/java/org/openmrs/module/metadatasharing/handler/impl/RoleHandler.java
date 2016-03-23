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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Role;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.handler.MetadataMergeHandler;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("metadatasharing.RoleHandler")
public class RoleHandler implements MetadataPropertiesHandler<Role>, MetadataPriorityDependenciesHandler<Role>, MetadataMergeHandler<Role> {

	@Autowired
	ObjectHandler objectHandler;

	@Override
	public int getPriority() {
	    return 0;
	}
	
	@Override
	public Integer getId(Role object) {
		// id is not supported
		return null;
	}
	
	@Override
	public void setId(Role object, Integer id) {
		// id is not supported
	}
	
	@Override
	public String getUuid(Role object) {
		return (object.getUuid() != null) ? object.getUuid() : "";
	}
	
	@Override
	public void setUuid(Role object, String uuid) {
		object.setUuid(uuid);
	}
	
	@Override
	public Boolean getRetired(Role object) {
	    return object.isRetired();
	}
	
	@Override
	public void setRetired(Role object, Boolean retired) {
		object.setRetired(retired);
	}
	
	@Override
	public String getName(Role object) {
		return (object.getRole() != null) ? object.getRole() : "";
	}
	
	@Override
	public String getDescription(Role object) {
		return (object.getDescription() != null) ? object.getDescription() : "";
	}
	
	@Override
	public Date getDateChanged(Role object) {
		return DateUtil.getLastDateChanged(object);
	}
	
	@Override
	public Map<String, Object> getProperties(Role object) {
		return Collections.emptyMap();
	}

	/**
     * @see org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler#getPriorityDependencies(java.lang.Object)
     */
    @Override
    public List<Object> getPriorityDependencies(Role object) {
    	if (object.getInheritedRoles() != null && !object.getInheritedRoles().isEmpty()) {
    		return new ArrayList<Object>(object.getInheritedRoles());
    	}
	    return Collections.emptyList();
    }

	/**
	 * New nested roles must be added only to inheritedRoles and not both inheritedRoles and
	 * childRoles.
	 *
	 * @see org.openmrs.module.metadatasharing.handler.MetadataMergeHandler#merge(java.lang.Object,
	 *      java.lang.Object, org.openmrs.module.metadatasharing.ImportType, java.util.Map)
	 */
	@Override
	public void merge(Role existing, Role incoming, ImportType importType, Map<Object, Object> incomingToExisting) {
		if (incoming.getChildRoles() != null) {
			incoming.setChildRoles(null);
		}

		objectHandler.merge(existing, incoming, importType, incomingToExisting);
	}


}
