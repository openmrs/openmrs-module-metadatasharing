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
package org.openmrs.metadatasharing;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.BaseShareTest;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;

/**
 * Test importing {@link Role}.
 */
public class Role19ShareTest extends BaseShareTest {
	
	@Test
	public void shouldImportNestedRoles() throws Exception {
		PackageImporter importer = MetadataSharing.getInstance().newPackageImporter();
		importer.loadSerializedPackageStream(getClass().getResourceAsStream("/packages/HUM_metadata_part_1-2.zip"));
		
		importer.setImportConfig(ImportConfig.valueOf(ImportMode.PARENT_AND_CHILD));
		
		importer.importPackage();
		
		Context.flushSession();
		Context.clearSession();
		
		UserService userService = Context.getUserService();
		
		Role parentRole = userService.getRole("Provider");
		
		Assert.assertNotNull(parentRole);
		Assert.assertEquals(8, parentRole.getChildRoles().size());
		
		Role childRole = userService.getRole("LacollineProvider");
		
		Assert.assertNotNull(childRole);
		Assert.assertTrue(parentRole.getChildRoles().contains(childRole));
	}
	
	@Test
	public void shouldImportRolesTwice() throws Exception {
		PackageImporter importer = MetadataSharing.getInstance().newPackageImporter();
		importer.loadSerializedPackageStream(getClass().getResourceAsStream("/packages/Roles_and_privileges-3.zip"));
		
		importer.setImportConfig(ImportConfig.valueOf(ImportMode.PARENT_AND_CHILD));
		
		importer.importPackage();
		
		Context.flushSession();
		Context.clearSession();
		
		UserService userService = Context.getUserService();
		
		Role parentRole = userService.getRole("Provider");
		
		Assert.assertNotNull(parentRole);
		Assert.assertEquals(8, parentRole.getChildRoles().size());
		
		Role childRole = userService.getRole("LacollineProvider");
		
		Assert.assertNotNull(childRole);
		Assert.assertTrue(parentRole.getChildRoles().contains(childRole));
		
		importer = MetadataSharing.getInstance().newPackageImporter();
		importer.loadSerializedPackageStream(getClass().getResourceAsStream("/packages/Roles_and_privileges-3.zip"));
		
		importer.setImportConfig(ImportConfig.valueOf(ImportMode.PARENT_AND_CHILD));
		
		importer.importPackage();
		
		parentRole = userService.getRole("Provider");
		
		Assert.assertNotNull(parentRole);
		Assert.assertEquals(8, parentRole.getChildRoles().size());
		
		childRole = userService.getRole("LacollineProvider");
		
		Assert.assertNotNull(childRole);
		Assert.assertTrue(parentRole.getChildRoles().contains(childRole));
	}
}
