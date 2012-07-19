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
package org.openmrs.module.metadatasharing.versioner;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.ImportExportTestUtils;
import org.openmrs.module.metadatasharing.Item;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests for {@link PackageVersioner} implementation
 */
public class PackageVersionerTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see PackageVersioner#createNewPackageVersion(Integer)
	 * @verifies set appropriate version
	 */
	@Test
	public void createNewPackageVersion_shouldSetAppropriateVersion() throws Exception {
		ExportedPackage pack = ImportExportTestUtils.exportPackage(1, "EX1");
		ImportExportTestUtils.exportPackage(2, "EX1");
		PackageVersioner versioner = MetadataSharing.getInstance().getPackageVersioner(pack);
		Assert.assertEquals((Integer) 3, versioner.createNewPackageVersion(false).getVersion());
		
		pack = ImportExportTestUtils.exportPackage(7, "EX1");
		ImportExportTestUtils.exportPackage(8, "EX1");
		versioner = MetadataSharing.getInstance().getPackageVersioner(pack);
		Assert.assertEquals((Integer) 9, versioner.createNewPackageVersion(false).getVersion());
	}
	
	/**
	 * @see PackageVersioner#createNewPackageVersion(Integer)
	 * @verifies copy name, description and group
	 */
	@Test
	public void createNewPackageVersion_shouldCopyNameDescriptionAndGroup() throws Exception {
		ExportedPackage pack = ImportExportTestUtils.exportPackage(7, "EX1");
		ImportExportTestUtils.exportPackage(8, "EX1");
		PackageVersioner versioner = MetadataSharing.getInstance().getPackageVersioner(pack);
		ExportedPackage newVersion = versioner.createNewPackageVersion(false);
		Assert.assertEquals(pack.getGroupUuid(), newVersion.getGroupUuid());
		Assert.assertEquals(pack.getDescription(), newVersion.getDescription());
		Assert.assertEquals(pack.getName(), newVersion.getName());
		Assert.assertNotSame(pack.getUuid(), newVersion.getUuid());
	}
	
	/**
	 * @see PackageVersioner#createNewPackageVersion(Integer)
	 * @verifies set published to false
	 */
	@Test
	public void createNewPackageVersion_shouldSetPublishedToFalse() throws Exception {
		ExportedPackage pack = ImportExportTestUtils.exportPackage(1, "EX1", true);
		PackageVersioner versioner = MetadataSharing.getInstance().getPackageVersioner(pack);
		Assert.assertEquals(Boolean.FALSE, versioner.createNewPackageVersion(false).isPublished());
	}
	
	/**
	 * @see PackageVersioner#getMissingClasses()
	 * @verifies return missing classes
	 */
	@Test
	public void getMissingClasses_shouldReturnMissingClasses() throws Exception {
		ExportedPackage pack = ImportExportTestUtils.exportPackage();
		Item summary = new Item();
		pack.getItems().add(summary);
		summary.setClassname("some.missing.class");
		PackageVersioner versioner = MetadataSharing.getInstance().getPackageVersioner(pack);
		List<String> missingClasses = versioner.getMissingClasses();
		Assert.assertEquals(1, missingClasses.size());
		Assert.assertEquals("some.missing.class", missingClasses.get(0));
	}
	
	/**
	 * @see PackageVersioner#getMissingItems()
	 * @verifies return missing items
	 */
	@Test
	@Ignore
	public void getMissingItems_shouldReturnMissingItems() throws Exception {
		executeDataSet("MetadataExporterTest.xml");
		Concept concept = (Concept) Handler.getItemByUuid(Concept.class, "ee3247fb-7ed0-4395-934e-78087ce3086e");
		ExportedPackage pack = ImportExportTestUtils.exportPackage(null, Collections.singletonList(concept));
		pack.getItems().iterator().next().setUuid("NON_EXISTING");
		
		PackageVersioner versioner = MetadataSharing.getInstance().getPackageVersioner(pack);
		Map<String, Set<Item>> missingItems = versioner.getMissingItems();
		Assert.assertEquals(1, missingItems.size());
		Assert.assertEquals(0, versioner.getPackageItems().size());
		Assert.assertEquals(0, versioner.getMissingClasses().size());
		Assert.assertEquals("NON_EXISTING", missingItems.get("Concept").iterator().next().getUuid());
	}
	
	/**
	 * @see PackageVersioner#getPackageItems()
	 * @verifies return package items that are in the database
	 */
	@Test
	@Ignore
	public void getPackageItems_shouldReturnPackageItemsThatAreInTheDatabase() throws Exception {
		executeDataSet("MetadataExporterTest.xml");
		Concept concept = (Concept) Handler.getItemByUuid(Concept.class, "ee3247fb-7ed0-4395-934e-78087ce3086e");
		ExportedPackage pack = ImportExportTestUtils.exportPackage(null, Collections.singletonList(concept));
		
		PackageVersioner versioner = MetadataSharing.getInstance().getPackageVersioner(pack);
		Assert.assertEquals(0, versioner.getMissingItems().size());
		Assert.assertEquals(1, versioner.getPackageItems().size());
		Assert.assertEquals(0, versioner.getMissingClasses().size());
		Assert.assertEquals("ee3247fb-7ed0-4395-934e-78087ce3086e",
		    Handler.getUuid(versioner.getPackageItems().get("Concept").iterator().next()));
	}
}
