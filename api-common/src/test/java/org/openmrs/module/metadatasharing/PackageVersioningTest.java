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
package org.openmrs.module.metadatasharing;

import java.util.List;

import junit.framework.Assert;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * This class tests all features related to package versioning.
 */
public class PackageVersioningTest extends BaseModuleContextSensitiveTest {
	
	protected MetadataSharingService service = null;
	
	@Before
	public void before() {
		service = Context.getService(MetadataSharingService.class);
	}
	
	/**
	 * Tests if after adding new fields to ExportedPackage class saving to database and
	 * serialization works properly.
	 */
	@Test
	public void saveVersionedPackageAndGenerateXML() throws Exception {
		ExportedPackage pack = ImportExportTestUtils.exportPackage(1, "some_uuid");
		String headerXml = pack.getSerializedPackage().getHeader();
		
		XMLAssert.assertXpathExists("/package/version", headerXml);
		XMLAssert.assertXpathExists("/package/groupUuid", headerXml);
	}
	
	/**
	 * Checks whether importing versioned packages respects version and group
	 */
	@Test
	public void importVersionedPackage() throws Exception {
		ImportedPackage pack = ImportExportTestUtils.importPackage(4, "UUID4");
		Assert.assertEquals((Integer) 4, pack.getVersion());
		Assert.assertEquals("UUID4", pack.getGroupUuid());
	}
	
	/**
	 * Tests if importing package twice doesn't cause errors
	 */
	@Test
	public void importVersionedPackageTwice() throws Exception {
		ImportExportTestUtils.importPackage(4, "GR1", "non_uniqie_uuid");
		ImportExportTestUtils.importPackage(4, "GR1", "non_uniqie_uuid");
	}
	
	/**
	 * Checks whether imported packages are saved in database properly
	 */
	@Test
	public void importDifferentPackageVersions() throws Exception {
		Assert.assertNull(service.getImportedPackageByGroup("UUID5"));
		ImportExportTestUtils.importPackage(5, "UUID5");
		Assert.assertEquals((Integer) 5, service.getImportedPackageByGroup("UUID5").getVersion());
		
		ImportExportTestUtils.importPackage(6, "UUID5");
		Assert.assertEquals((Integer) 6, service.getImportedPackageByGroup("UUID5").getVersion());
		
		ImportExportTestUtils.importPackage(4, "UUID5");
		Assert.assertEquals((Integer) 4, service.getImportedPackageByGroup("UUID5").getVersion());
		
		ImportExportTestUtils.importPackage(10, "UUID6");
		Assert.assertEquals((Integer) 4, service.getImportedPackageByGroup("UUID5").getVersion());
		Assert.assertEquals((Integer) 10, service.getImportedPackageByGroup("UUID6").getVersion());
	}
	
	/**
	 * @see MetadataSharingService#getExportedPackagesByGroup(String)
	 */
	@Test
	public void metadataSharingService_getExportedPackageGroup() throws Exception {
		ImportExportTestUtils.exportPackage(4, "group1");
		ImportExportTestUtils.exportPackage(2, "group1");
		ImportExportTestUtils.exportPackage(5, "group1");
		
		ImportExportTestUtils.exportPackage(1, "group2");
		ImportExportTestUtils.exportPackage(2, "group2");
		ImportExportTestUtils.exportPackage(1, "group1");
		ImportExportTestUtils.exportPackage(3, "group1");
		List<ExportedPackage> list = service.getExportedPackagesByGroup("group1");
		Assert.assertEquals(5, list.size());
		
		for (int i = 0; i < list.size(); i++) {
			Assert.assertEquals((Integer) (list.size() - i), list.get(i).getVersion());
		}
	}
	
	/**
	 * Tests if getting latest version of an exported package from database works properly.
	 * 
	 * @see {@link MetadataSharingService#getLatestExportedPackageByGroup(String)}
	 */
	@Test
	public void metadataSharingService_getLatestExportedPackage() throws Exception {
		Assert.assertNull(service.getLatestExportedPackageByGroup("UUID_G"));
		ImportExportTestUtils.exportPackage(7, "UUID_G");
		Assert.assertEquals((Integer) 7, service.getLatestExportedPackageByGroup("UUID_G").getVersion());
		
		ImportExportTestUtils.exportPackage(8, "UUID_G");
		Assert.assertEquals((Integer) 8, service.getLatestExportedPackageByGroup("UUID_G").getVersion());
		
		ImportExportTestUtils.exportPackage(9, "UUID_G");
		Assert.assertEquals((Integer) 9, service.getLatestExportedPackageByGroup("UUID_G").getVersion());
	}
}
