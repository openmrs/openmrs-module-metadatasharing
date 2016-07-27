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
package org.openmrs.module.metadatasharing.task.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests the {@link ExportPackageTask} class
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, MetadataSharing.class })
public class ExportPackageTaskTest {
	
	/**
	 * @see ExportPackageTask#addLocalMappingIfConcept(Object)
	 * @verifies add local mapping to concept if admin desires
	 */
	@Test
	public void addMappingsToConcepts_shouldAddLocalMappingToConceptIfAdminDesires() throws Exception {
		// set up the mocks for the ConceptSubPubService method
		PowerMockito.mockStatic(Context.class);
		MetadataMappingService service = PowerMockito.mock(MetadataMappingService.class);
		PowerMockito.when(Context.getService(MetadataMappingService.class)).thenReturn(service);
		
		Mockito.when(service.isAddLocalMappingToConceptOnExport()).thenReturn(true); // this is the only difference from test setup below
		
		// do the test
		Concept stubconcept = new Concept();
		stubconcept.setId(1234); // for pre-1.9 .equals comparisons
		stubconcept.setUuid("aaa1234567890"); // for 1.9+ .equals comparisons
		
		new ExportPackageTask(null, false).addLocalMappingIfConcept(stubconcept);
		
		// verify that the service method was called
		
		Mockito.verify(service).addLocalMappingToConcept(stubconcept);
	}
	
	/**
	 * @see ExportPackageTask#addLocalMappingIfConcept(Object)
	 * @verifies not add local mapping to concept if admin desires
	 */
	@Test
	public void addMappingsToConcepts_shouldNotAddLocalMappingToConceptIfAdminDesires() throws Exception {
		// set up the mocks for the ConceptSubPubService method
		PowerMockito.mockStatic(Context.class);
		MetadataMappingService service = PowerMockito.mock(MetadataMappingService.class);
		PowerMockito.when(Context.getService(MetadataMappingService.class)).thenReturn(service);
		
		PowerMockito.mockStatic(MetadataSharing.class);
		MetadataSharing mockInstance = Mockito.mock(MetadataSharing.class);
		PowerMockito.when(MetadataSharing.getInstance()).thenReturn(mockInstance);
		
		Mockito.when(service.isAddLocalMappingToConceptOnExport()).thenReturn(false); // this is the only difference from test setup above
		
		// do the test
		Concept stubconcept = new Concept();
		stubconcept.setId(1234); // for pre-1.9 .equals comparisons
		stubconcept.setUuid("aaa1234567890"); // for 1.9+ .equals comparisons
		
		List<Object> items = new ArrayList<Object>();
		items.add(stubconcept);
		new ExportPackageTask(null, false).addLocalMappingIfConcept(items);
		
		// verify that the service method was called
		
		Mockito.verify(service, Mockito.times(0)).addLocalMappingToConcept((Concept) Mockito.any());
	}
}
