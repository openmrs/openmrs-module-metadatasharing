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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.metadatasharing.reflection.OpenmrsClassScanner;

public class OpenmrsMetadataHandlerTest {
	
	/**
	 * @see OpenmrsMetadataHandler#OpenmrsMetadataHandler(OpenmrsClassScanner)
	 * @verifies support classes with same simple name
	 */
	@Test
	public void OpenmrsMetadataHandler_shouldSupportClassesWithSameSimpleName() throws Exception {
		//given
		OpenmrsClassScanner scanner = Mockito.mock(OpenmrsClassScanner.class);
		List metadataClasses = new ArrayList();
		metadataClasses.add(org.openmrs.module.metadatasharing.handler.impl.Location.class);
		metadataClasses.add(org.openmrs.Location.class);
		
		Mockito.when(scanner.getOpenmrsMetadataClasses()).thenReturn((List<Class<OpenmrsMetadata>>) metadataClasses);
		
		//when
		OpenmrsMetadataHandler handler = new OpenmrsMetadataHandler(scanner);
		
		//then
		Assert.assertEquals("Location.metadatasharing", handler
		        .getTypes().get(Location.class));
		Assert.assertEquals("Location", handler.getTypes().get(org.openmrs.Location.class));
	}
}
