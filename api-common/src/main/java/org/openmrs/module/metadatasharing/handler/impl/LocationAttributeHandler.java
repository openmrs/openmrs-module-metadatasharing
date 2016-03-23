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
import java.util.List;

import org.openmrs.LocationAttribute;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.springframework.stereotype.Component;

@Component("metadatasharing.LocationAttributeHandler")
public class LocationAttributeHandler implements MetadataPriorityDependenciesHandler<LocationAttribute> {

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public List<Object> getPriorityDependencies(LocationAttribute object) {
		List<Object> dependencies = new ArrayList<Object>();
		dependencies.add(object.getAttributeType());
		return dependencies;
	}

}
