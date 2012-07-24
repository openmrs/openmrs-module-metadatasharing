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

import org.openmrs.Concept;
import org.openmrs.module.metadatasharing.mock.ConceptMock;

public class ConceptShareScenarioTest extends BaseShareScenarioTest<Concept> {
	
	/**
	 * @see org.openmrs.module.metadatasharing.BaseShareScenarioTest#prepareItemsToExport()
	 */
	@Override
	public List<Concept> prepareItemsToExport() {
		Concept concept = ConceptMock.newInstance().addPreferredName("Some test concept", Locale.ENGLISH).setDatatype("N/A")
		        .saveConcept().getConcept();
		return Arrays.asList(concept);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.BaseShareScenarioTest#prepareItemsOnImportServer()
	 */
	@Override
	public void prepareItemsOnImportServer() {
		
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.BaseShareScenarioTest#assertImportedCorrectly(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void assertImportedCorrectly() {
		
	}
}
