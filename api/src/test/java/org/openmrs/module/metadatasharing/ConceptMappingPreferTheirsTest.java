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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.integration.BaseShareTest;
import org.openmrs.module.metadatasharing.integration.ShareTestHelper;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.springframework.test.annotation.NotTransactional;

/**
 *
 */
public class ConceptMappingPreferTheirsTest extends BaseShareTest {
	
	@Test
	@NotTransactional
	public void preferTheirsShouldNotAddExistingConceptMappings() throws Exception {
		
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
				
				ConceptMap commonMap = new ConceptMap();
				commonMap.setSource(commonSource);
				commonMap.setSourceCode("c");
				c.addConceptMapping(commonMap);
				
				Context.getConceptService().saveConcept(c);
				
				return Collections.nCopies(1, c);
			}
			
			@Override
			public void prepareImportServer() throws Exception {
				
				Concept c = new Concept();
				c.addName(new ConceptName("c", Locale.ENGLISH));
				c.setUuid("c");
				c.setDatatype(Context.getConceptService().getConceptDatatypeByName("N/A"));
				Context.getConceptService().saveConcept(c);
				
				ConceptSource commonSource = new ConceptSource();
				commonSource.setUuid("csOld");
				commonSource.setName("Common");
				Context.getConceptService().saveConceptSource(commonSource);
				
				ConceptMap commonMap = new ConceptMap();
				commonMap.setSource(commonSource);
				commonMap.setSourceCode("c");
				c.addConceptMapping(commonMap);
				
				Context.getConceptService().saveConcept(c);
			}
			
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
				for (ImportedItem importedItem : importer.getImportedItems(0)) {
					if (importedItem.getExisting() == null)
						continue;
					
					Set<String> uuids = new HashSet<String>(Arrays.asList("c", "cs"));
					if (uuids.contains(Handler.getUuid(importedItem.getIncoming()))) {
						importedItem.setImportType(ImportType.PREFER_THEIRS);
					}
				}
			}
			
			@Override
			public void runOnImportServerAfterImport() throws Exception {
				Concept concept = Context.getConceptService().getConceptByUuid("c");
				Assert.assertEquals(2, concept.getConceptMappings().size());
				
				Set<String> uuids = new HashSet<String>(Arrays.asList("csNew", "csOld"));
				for (ConceptMap conceptMap : concept.getConceptMappings()) {
					if (!uuids.contains(conceptMap.getSource().getUuid())) {
						Assert.fail("Must be csNew or csOld");
					}
				}
			}
		});
	}
	
}
