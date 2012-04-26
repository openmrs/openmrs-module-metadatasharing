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

import java.util.List;

import junit.framework.Assert;

import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.handler.BaseHandlersTest;

public class RoleHandlersFunctionalTest extends BaseHandlersTest<Role> {
	
	@Override
	public Role getNewItem() {
		Role role = new Role();
		role.setRole("name");
		role.setDescription("description");
		return role;
	}
	
	@Override
	public Role getExistingItem() {
		return Context.getUserService().getRole("Provider");
	}
	
	@Override
	public Role getUpdatedItem() {
		Role role = getExistingItem();
		role.setRetired(true);
		role.setRetireReason("reason");
		return role;
	}
	
	@Override
	public void assertExistingSameAsUpdated(Role item) {
		Role role = (Role) item;
		Assert.assertTrue(role.isRetired());
		Assert.assertEquals("reason", role.getRetireReason());
	}
	
	@Override
	public List<Role> getAllItems() {
		return Context.getUserService().getAllRoles();
	}
	
	@Override
	public boolean hasIdProperty() {
		return false;
	}
	
	@Override
	public boolean hasPriorityDependenciesHandler() {
		return false;
	}
}
