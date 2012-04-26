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

import org.openmrs.Privilege;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.handler.BaseHandlersTest;

public class PrivilegeHandlersFunctionalTest extends BaseHandlersTest<Privilege> {
	
	@Override
	public Privilege getNewItem() {
		Privilege privilege = new Privilege();
		privilege.setPrivilege("name");
		privilege.setDescription("description");
		return privilege;
	}
	
	@Override
	public Privilege getExistingItem() {
		Context.getUserService().savePrivilege(getNewItem());
		return Context.getUserService().getAllPrivileges().get(0);
	}
	
	@Override
	public Privilege getUpdatedItem() {
		Privilege privilege = getExistingItem();
		privilege.setRetired(true);
		privilege.setRetireReason("reason");
		return privilege;
	}
	
	@Override
	public void assertExistingSameAsUpdated(Privilege item) {
		Privilege privilege = (Privilege) item;
		Assert.assertTrue(privilege.isRetired());
		Assert.assertEquals("reason", privilege.getRetireReason());
	}
	
	@Override
	public List<Privilege> getAllItems() {
		return Context.getUserService().getAllPrivileges();
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