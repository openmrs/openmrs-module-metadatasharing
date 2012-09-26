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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openmrs.Role;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.handler.MetadataMergeHandler;

/**
 *
 */
public class Role18Handler implements MetadataMergeHandler<Role> {
	
	private final ObjectHandler objectHandler;
	
	public Role18Handler(ObjectHandler objectHandler) {
		this.objectHandler = objectHandler;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataHandler#getPriority()
	 */
	@Override
	public int getPriority() {
		return 0;
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
		Set<Role> existingChildRoles = new HashSet<Role>();
		if (existing != null && existing.getChildRoles() != null) {
			existingChildRoles.addAll(existing.getChildRoles());
		}
		
		objectHandler.merge(existing, incoming, importType, incomingToExisting);
		
		if (existing != null && existing.getChildRoles() != null) {
			existing.getChildRoles().retainAll(existingChildRoles);
		}
		if (incoming != null && incoming.getChildRoles() != null) {
			incoming.getChildRoles().retainAll(existingChildRoles);
		}
	}
	
}
