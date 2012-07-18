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

import org.openmrs.Privilege;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.util.DateUtil;
import org.springframework.stereotype.Component;

@Component("metadatasharing.PrivilegeHandler")
public class PrivilegeHandler implements MetadataPropertiesHandler<Privilege> {

	@Override
	public int getPriority() {
	    return 0;
	}
	
	@Override
    public Integer getId(Privilege object) {
		// id is not supported
	    return null;
    }

	@Override
    public void setId(Privilege object, Integer id) {
	    // id is not supported
    }

	@Override
    public String getUuid(Privilege object) {
	    return (object.getUuid() != null) ? object.getUuid() : "";
    }

	@Override
    public void setUuid(Privilege object, String uuid) {
	    object.setUuid(uuid);
    }

	@Override
    public String getName(Privilege object) {
	    return (object.getPrivilege() != null) ? object.getPrivilege() : "";
    }

	@Override
    public String getDescription(Privilege object) {
	    return (object.getDescription() != null) ? object.getDescription() : "";
    }
	
	@Override
	public Boolean getRetired(Privilege object) {
	    return object.isRetired();
	}
	
	@Override
	public void setRetired(Privilege object, Boolean retired) {
		object.setRetired(retired);
	}

	@Override
    public Date getDateChanged(Privilege object) {
	    return DateUtil.getLastDateChanged(object);
    }

	@Override
    public Map<String, Object> getProperties(Privilege object) {
	    return Collections.emptyMap();
    }
}
