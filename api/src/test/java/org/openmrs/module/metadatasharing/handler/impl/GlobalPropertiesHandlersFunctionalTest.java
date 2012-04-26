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

import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.handler.BaseHandlersTest;


public class GlobalPropertiesHandlersFunctionalTest extends BaseHandlersTest<GlobalProperty> {

	@Override
	public GlobalProperty getNewItem(){
		GlobalProperty globalProperty = new GlobalProperty();
		globalProperty.setProperty("name");
		globalProperty.setDescription("description");
		return globalProperty;
	}
	
	@Override
	public GlobalProperty getExistingItem(){
		GlobalProperty globalProperty = Context.getAdministrationService().getAllGlobalProperties().get(0);
		globalProperty.setDescription("description");
		return globalProperty;
	}
	
	@Override
	public GlobalProperty getUpdatedItem(){
		GlobalProperty globalProperty = getExistingItem();
		globalProperty.setPropertyValue("thechangedvalue");
		return globalProperty;
	}
	
	@Override
	public void assertExistingSameAsUpdated(GlobalProperty item){
		GlobalProperty globalProperty = (GlobalProperty)item;
		Assert.assertEquals("thechangedvalue", globalProperty.getPropertyValue());
	}
	
	@Override
	public List<GlobalProperty> getAllItems(){
		return Context.getAdministrationService().getAllGlobalProperties();
	}
	
	@Override
	public boolean hasIdProperty() {
		return false;
	}
	
	@Override
	public boolean hasPriorityDependenciesHandler() {
		return false;
	}
	
	@Override
	public boolean hasRetiredProperty() {
		return false;
	}
}