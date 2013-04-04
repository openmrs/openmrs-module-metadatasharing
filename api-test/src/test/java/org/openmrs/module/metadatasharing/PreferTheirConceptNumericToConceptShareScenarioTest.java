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
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;

public class PreferTheirConceptNumericToConceptShareScenarioTest extends BaseShareTest {

	@Test
	public void importPackageMappingItemsMatchedByUuids() throws Exception {
		
		runShareTest(new ShareTestHelper() {
			
			@Override
			public List<?> prepareExportServer() throws Exception {
				
				ConceptNumeric c = new ConceptNumeric();
				c.setDatatype(Context.getConceptService().getConceptDatatypeByName("Numeric"));
				c.addName(new ConceptName("ConceptNumericConcept", Locale.US));
				c.setHiNormal(Double.valueOf(1));
				c.setUuid("23423a2982236623");
				Context.getConceptService().saveConcept(c);
				
				return Collections.nCopies(1, c);
			}
			
			@Override
			public void prepareImportServer() throws Exception {
				
				Concept c = new Concept();
				c.addName(new ConceptName("ConceptNumericConcept", Locale.US));
				c.setUuid("23423a2982236623");
				Context.getConceptService().saveConcept(c);
				
			}
			
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {			
				for (ImportedItem importedItem : importer.getImportedItems(0)) {
					if (importedItem.getExisting() == null)
						continue;
					
					if ((Handler.getUuid(importedItem.getIncoming()).equals("23423a2982236623"))) {
						importedItem.setImportType(ImportType.PREFER_THEIRS);
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
		Concept concept = Context.getConceptService().getConceptByUuid("23423a2982236623");
		if (!(concept instanceof ConceptNumeric)) {
			Assert.fail();
		}
		
		Assert.assertEquals("Numeric", concept.getDatatype().getName());
	}

	/**
     * @see org.openmrs.module.metadatasharing.BaseShareTest#getInitialDataset()
     */
    @Override
    public String getInitialDataset() {
	    return null;
    }
	
}
