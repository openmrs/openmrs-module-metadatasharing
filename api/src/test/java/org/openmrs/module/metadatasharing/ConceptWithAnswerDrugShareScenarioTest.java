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
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;

public class ConceptWithAnswerDrugShareScenarioTest extends BaseShareTest {
	
	@Test
	public void importPackageMappingItemsMatchedByUuids() throws Exception {
		//TODO: create a package with items from the export server and import it to the import server which has them already choosing map for all items with same uuids.
		
		runShareTest(new ShareTestHelper() {
			
			@Override
			public List<?> prepareExportServer() throws Exception {
				//here's a drug concept
				Concept c = new Concept();
				c.setUuid("1");
				c.addName(new ConceptName("DrugAnswerDrugConcept", Locale.US));
				Context.getConceptService().saveConcept(c);
				
				//here's the drug
				Drug drug = new Drug();
				drug.setUuid("2");
				drug.setConcept(c);
				drug.setName("TestAnswerDrug");
				Context.getConceptService().saveDrug(drug);
				
				//and, here'a a concept who's conceptAnswer references the drug through the answerDrug property
				Concept cTest = new Concept();
				cTest.setUuid("3");
				cTest.addName(new ConceptName("TestAnswerDrugConcept", Locale.US));
				
				ConceptAnswer ca = new ConceptAnswer();
				ca.setAnswerConcept(c);
				ca.setAnswerDrug(drug);
				
				cTest.addAnswer(ca);
				Context.getConceptService().saveConcept(cTest);
				return Collections.nCopies(1, cTest);
			}
			
			@Override
			public void prepareImportServer() throws Exception {
				
			}
			
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
				for (ImportedItem importedItem : importer.getImportedItems(0)) {
					if (importedItem.getExisting() == null)
						continue;
					
					if ((Handler.getUuid(importedItem.getExisting()).equals(Handler.getUuid(importedItem.getIncoming())))) {
						importedItem.setImportType(ImportType.PREFER_MINE);
					}
				}
			}
			
			@Override
			public void runOnImportServerAfterImport() throws Exception {
				assertMappedCorrectly();
			}
		});
	}
	
	private void assertMappedCorrectly() {
		Concept concept = Context.getConceptService().getConceptByUuid("1");
		Assert.assertEquals("DrugAnswerDrugConcept", concept.getName().getName());
		Concept concept2 = Context.getConceptService().getConceptByUuid("3");
		Assert.assertEquals("TestAnswerDrugConcept", concept2.getName().getName());
		
		Drug drug = Context.getConceptService().getDrugByUuid("2");
		Assert.assertEquals("TestAnswerDrug", drug.getName());
	}

	/**
     * @see org.openmrs.module.metadatasharing.BaseShareTest#getInitialDataset()
     */
    @Override
    public String getInitialDataset() {
	    return null;
    }
}
