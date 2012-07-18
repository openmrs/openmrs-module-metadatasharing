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

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.integration.BaseShareTest;
import org.openmrs.module.metadatasharing.integration.ShareTestHelper;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;

public class ConceptShareTest extends BaseShareTest {
	
	@Override
	public boolean insertInitialDataSet() {
		return false;
	}
	
	@Test
	public void shouldImportConceptWithMappingToEmptyServer() throws Exception {
		
		runShareTest(new ShareTestHelper() {
			
			@Override
			public List<?> prepareExportServer() throws Exception {
				
				Concept c = new Concept();
				c.addName(new ConceptName("c", Locale.ENGLISH));
				c.setUuid("c");
				c.setDatatype(Context.getConceptService().getConceptDatatypeByName("N/A"));
				Context.getConceptService().saveConcept(c);
				
				ConceptSource newSource = new ConceptSource();
				newSource.setUuid("csNew");
				newSource.setName("New");
				Context.getConceptService().saveConceptSource(newSource);
				
				ConceptSource commonSource = new ConceptSource();
				commonSource.setUuid("cs");
				commonSource.setName("Common");
				Context.getConceptService().saveConceptSource(commonSource);
				
				ConceptMap newMap = new ConceptMap();
				newMap.setSource(newSource);
				newMap.setSourceCode("c");
				c.addConceptMapping(newMap);
				
				Context.getConceptService().saveConcept(c);
				
				ConceptMap commonMap = new ConceptMap();
				commonMap.setSource(commonSource);
				commonMap.setSourceCode("cd");
				c.addConceptMapping(commonMap);
				
				Context.getConceptService().saveConcept(c);
				
				return Collections.nCopies(1, c);
			}
			
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
			}
			
			@Override
			public void runOnImportServerAfterImport() throws Exception {
				Concept concept = Context.getConceptService().getConceptByUuid("c");
				Assert.assertEquals("c", concept.getUuid());
				Assert.assertEquals(2, concept.getConceptMappings().size());
				
				for (ConceptMap conceptMap : concept.getConceptMappings()) {
					if (conceptMap.getSourceCode().equals("c")) {
						Assert.assertEquals(conceptMap.getSource().getName(), "New");
					} else if (conceptMap.getSourceCode().equals("cd")) {
						Assert.assertEquals(conceptMap.getSource().getName(), "Common");
					} else {
						Assert.fail();
					}
                }
			}
		});
	}
}
