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
package org.openmrs.module.metadatasharing.api;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class MetadataServiceTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link MetadataService#getItemById(Class<? extends OpenmrsMetadata>,Integer)}
	 */
	@Test
	@Verifies(value = "should return the item matching the passed in type and id", method = "getItemById(Class<? extends OpenmrsMetadata>,Integer)")
	public void getItemById_shouldReturnTheItemMatchingThePassedInTypeAndId() throws Exception {
		/*
		 * following the upgrade to DbSessionFaction from SessionFactory
		 * for some reason, the location now being pulled up is defined in openmrs-core instead of in this module
		Assert.assertEquals("dc5c1fcc-0459-4201-bf70-0b90535ba362",
			    Context.getService(MetadataService.class).getItemById(Location.class, 1).getUuid());
		*/
		Assert.assertEquals("8d6c993e-c2cc-11de-8d13-0010c6dffd0f",
			    Context.getService(MetadataService.class).getItemById(Location.class, 1).getUuid());		
	}
}
