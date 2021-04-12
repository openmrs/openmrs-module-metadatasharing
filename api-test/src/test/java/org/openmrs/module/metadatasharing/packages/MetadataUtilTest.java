/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.metadatasharing.packages;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.packages.MetadataUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class MetadataUtilTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void testSetupStandardMetadataFromAliasedPackageFile() throws Exception {
		String aliasedPackage = "packages-aliased.xml";
		assertThat(Context.getVisitService().getVisitTypeByUuid("86b3d7bc-d91f-4ce2-991c-f71bba0b31e4"), nullValue());
		
		boolean anyChanges = MetadataUtil.setupStandardMetadata(getClass().getClassLoader(),aliasedPackage);
		
		assertTrue(anyChanges);
		assertThat(Context.getVisitService().getVisitTypeByUuid("86b3d7bc-d91f-4ce2-991c-f71bba0b31e4").getName(),
		    is("Clinic or Hospital Visit"));
	}
	
	@Test
	public void testSetupStandardMetadataFromExistingpackageWithEmrapiPackageName() throws Exception {
		String deprecatedPackage = "packages-deprecated.xml";
		assertThat(Context.getVisitService().getVisitTypeByUuid("86b3d7bc-d91f-4ce2-991c-f71bba0b31e4"), nullValue());
		
		boolean anyChanges = MetadataUtil.setupStandardMetadata(getClass().getClassLoader(),deprecatedPackage);
		
		assertTrue(anyChanges);
		assertThat(Context.getVisitService().getVisitTypeByUuid("86b3d7bc-d91f-4ce2-991c-f71bba0b31e4").getName(),
		    is("Clinic or Hospital Visit"));
	}
}
