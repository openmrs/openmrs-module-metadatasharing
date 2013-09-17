/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.metadatasharing.smoke;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;


public class Concept19SmokeTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void importConceptsMVP184Small1() throws Exception {
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("packages/Concepts_MVP_1.8.4_small-1.zip");
		
		metadataImporter.loadSerializedPackageStream(inputStream);
		metadataImporter.importPackage();
	}

	@Test
	@SkipBaseSetup
	public void importProgramsAndConcepts() throws Exception {
		executeDataSet(INITIAL_XML_DATASET_PACKAGE_PATH);
		executeDataSet("conceptDatatypes.xml");
		authenticate();

		final String TB_PROGRAM_UUID = "9f144a34-3a4a-44a9-8486-6b7af6cc64f6";

		// Verify that program doesn't exist
		Program program = Context.getProgramWorkflowService().getProgramByUuid(TB_PROGRAM_UUID);
		Assert.assertNull(program);

		// Import package containing program
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		metadataImporter.loadSerializedPackageStream(ClassLoader.getSystemResourceAsStream("packages/Programs_Test-4.zip"));
		metadataImporter.setImportConfig(ImportConfig.valueOf(ImportMode.MIRROR));
		metadataImporter.importPackage();

		// Verify that program now exists and that details are correct from package
		program = Context.getProgramWorkflowService().getProgramByUuid(TB_PROGRAM_UUID);
		Assert.assertNotNull(program);
		Assert.assertEquals("TB Program", program.getName());
		Assert.assertEquals("Treatment for TB patients", program.getDescription());

		// Modify program, save and flush session
		program.setName("XXX");
		program.setDescription("YYY");
		Context.getProgramWorkflowService().saveProgram(program);
		Context.flushSession();
		Context.clearSession();

		// Re-import package containing program
		metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		metadataImporter.loadSerializedPackageStream(ClassLoader.getSystemResourceAsStream("packages/Programs_Test-4.zip"));
		metadataImporter.setImportConfig(ImportConfig.valueOf(ImportMode.MIRROR));
		metadataImporter.importPackage();
		Context.flushSession();
		Context.clearSession();

		// Verify that program details are correct from package
		program = Context.getProgramWorkflowService().getProgramByUuid(TB_PROGRAM_UUID);
		Assert.assertNotNull(program);
		Assert.assertEquals("TB Program", program.getName());  // FAILS !!!
		Assert.assertEquals("Treatment for TB patients", program.getDescription());     // FAILS !!!
	}
}
