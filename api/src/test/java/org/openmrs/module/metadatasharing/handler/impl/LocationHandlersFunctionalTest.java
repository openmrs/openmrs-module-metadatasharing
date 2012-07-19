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

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.handler.BaseHandlersTest;


public class LocationHandlersFunctionalTest extends BaseHandlersTest<Location> {

	@Override
	public Location getNewItem(){
		Location location = new Location();
		location.setName("New location");
		location.setDescription("New location description");
		return location;
	}
	
	@Override
	public Location getExistingItem(){
		Location location = new Location();
		location.setName("Existing location");
		location.setDescription("Existing location description");
		return Context.getLocationService().saveLocation(location);
	}
	
	@Override
	public Location getUpdatedItem(){
		Location location = getExistingItem();
		location.setRetired(true);
		location.setRetireReason("reason");
		return location;
	}
	
	@Override
	public void assertExistingSameAsUpdated(Location item){
		Location location = (Location)item;
		Assert.assertTrue(location.isRetired());
		Assert.assertEquals("reason", location.getRetireReason());
	}
	
	@Override
	public List<Location> getAllItems(){
		return Context.getLocationService().getAllLocations();
	}
}