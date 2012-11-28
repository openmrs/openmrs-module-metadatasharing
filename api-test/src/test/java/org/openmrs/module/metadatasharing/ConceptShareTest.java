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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.mock.ConceptMock;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;

public class ConceptShareTest extends BaseShareTest {
	
	@Test
	public void shouldImportConceptWithMappingToEmptyServer() throws Exception {
		
		runShareTest(new ShareTestHelper() {
			
			private Concept exportedConcept;
			
			@Override
			public List<?> prepareExportServer() throws Exception {
				exportedConcept = ConceptMock.newInstance().addPreferredName("Yes Yes", Locale.ENGLISH)
				        .addMapping("373066001", "SNOMED CT").addMapping("1065", "AMPATH").setDatatype("N/A").saveConcept()
				        .getConcept();
				
				return Arrays.asList(exportedConcept);
			}
			
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
			}
			
			@Override
			public void runOnImportServerAfterImport() throws Exception {
				Concept concept = Context.getConceptService().getConceptByName("Yes Yes");
				
				ConceptMock.newInstance(concept).assertEquals(exportedConcept, false);
			}
		});
	}
	
	@Test
	public void shouldUpdateConceptByAddingNewMapping() throws Exception {
		
		runShareTest(new ShareTestHelper() {
			
			private Concept exportedConcept;
			
			@Override
			public List<?> prepareExportServer() throws Exception {
				exportedConcept = ConceptMock.newInstance().addPreferredName("Yes Yes", Locale.ENGLISH)
				        .addMapping("373066001", "SNOMED CT").addMapping("1065", "AMPATH").setDatatype("N/A").saveConcept()
				        .getConcept();
				
				return Arrays.asList(exportedConcept);
			}
			
			@Override
			public void prepareImportServer() throws Exception {
				ConceptMock.newInstance().addPreferredName("Yes Yes", Locale.ENGLISH).addMapping("373066001", "SNOMED CT")
				        .setDatatype("N/A").saveConcept();
			}
			
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
			}
			
			@Override
			public void runOnImportServerAfterImport() throws Exception {
				Concept concept = Context.getConceptService().getConceptByName("Yes Yes");
				
				ConceptMock.newInstance(concept).assertEquals(exportedConcept, true);
			}
		});
	}
	
	@Test
	public void shouldMergeConceptNamesIgnoringCaseInP2PMode() throws Exception {
		
		runShareTest(new ShareTestHelper() {
			
			@Override
			public List<?> prepareExportServer() throws Exception {
				Concept concept = ConceptMock.newInstance().addPreferredName("Yes Yes", Locale.ENGLISH).saveConcept()
				        .getConcept();
				
				return Arrays.asList(concept);
			}
			
			@Override
			public void prepareImportServer() throws Exception {
				ConceptMock.newInstance().addName("YES YES", Locale.ENGLISH).addPreferredName("Yes!", Locale.ENGLISH)
				        .saveConcept();
			}
			
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
				importer.setImportConfig(ImportConfig.valueOf(ImportMode.PEER_TO_PEER));
			}
			
			@Override
			public void runOnImportServerAfterImport() throws Exception {
				Concept concept = Context.getConceptService().getConceptByName("Yes!");
				
				Concept expectedConcept = ConceptMock.newInstance().addName("YES YES", Locale.ENGLISH)
				        .addPreferredName("Yes!", Locale.ENGLISH).getConcept();
				
				ConceptMock.newInstance(concept).assertEquals(expectedConcept, true);
			}
		});
	}
}
