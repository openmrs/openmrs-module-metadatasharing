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

import org.junit.Test;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.test.BaseModuleContextSensitiveTest;


public class Concept19SmokeTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void importConceptsMVP184Small1() throws Exception {
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("packages/Concepts_MVP_1.8.4_small-1.zip");
		
		metadataImporter.loadSerializedPackageStream(inputStream);
		metadataImporter.importPackage();
	}
}
