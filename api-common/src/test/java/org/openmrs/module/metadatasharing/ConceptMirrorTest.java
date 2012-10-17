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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptDescription;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;

/**
 * Test if ConceptAnswers are overwritten correctly
 */
public class ConceptMirrorTest extends BaseShareTest {
	
	@Override
	public boolean insertInitialDataSet() {
		return false;
	}
	
	@Test
	public void shouldCorrectlyOverwriteConceptAnswersInMirrorMode() throws Exception {
		final String conceptUuid = UUID.randomUUID().toString();
		final String answer1ConceptUuid = UUID.randomUUID().toString();
		final String answer2ConceptUuid = UUID.randomUUID().toString();
		
		runShareTest(new ShareTestHelper() {
			
			@Override
			public List<?> prepareExportServer() throws Exception {
				Concept concept = new Concept();
				concept.setUuid(conceptUuid);
				
				Concept concept1 = new Concept(1);
				concept1.setUuid(answer1ConceptUuid);
				concept1.setDescriptions(new HashSet<ConceptDescription>());
				ConceptAnswer conceptAnswer1 = new ConceptAnswer(1);
				conceptAnswer1.setAnswerConcept(concept1);
				concept.addAnswer(conceptAnswer1);
				
				Concept concept2 = new Concept(2);
				concept2.setUuid(answer2ConceptUuid);
				concept2.setDescriptions(new HashSet<ConceptDescription>());
				ConceptAnswer conceptAnswer2 = new ConceptAnswer(2);
				conceptAnswer2.setAnswerConcept(concept2);
				concept.addAnswer(conceptAnswer2);
				
				Context.getConceptService().saveConcept(concept);
				return Arrays.asList(concept);
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#prepareImportServer()
			 */
			@Override
			public void prepareImportServer() throws Exception {
				Concept concept = new Concept();
				concept.setUuid(conceptUuid);
				
				Concept concept1 = new Concept(1);
				concept1.setUuid(answer1ConceptUuid);
				concept1.setDescriptions(new HashSet<ConceptDescription>());
				ConceptAnswer conceptAnswer1 = new ConceptAnswer(1);
				conceptAnswer1.setAnswerConcept(concept1);
				concept.addAnswer(conceptAnswer1);
				
				Concept concept2 = new Concept(2);
				concept2.setUuid(answer2ConceptUuid);
				concept2.setDescriptions(new HashSet<ConceptDescription>());
				ConceptAnswer conceptAnswer2 = new ConceptAnswer(2);
				conceptAnswer2.setAnswerConcept(concept2);
				concept.addAnswer(conceptAnswer2);
				
				Context.getConceptService().saveConcept(concept);
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#runOnImportServerBeforeImport(org.openmrs.module.metadatasharing.wrapper.PackageImporter)
			 */
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
				importer.setImportConfig(ImportConfig.valueOf(ImportMode.MIRROR));
			}
			
			@Override
			public void runOnImportServerAfterImport() throws Exception {
				Concept concept = Context.getConceptService().getConceptByUuid(conceptUuid);
				
				Collection<ConceptAnswer> answers = concept.getAnswers();
				Assert.assertEquals(2, answers.size());
				
				Set<String> expectedAnswers = new HashSet<String>();
				expectedAnswers.addAll(Arrays.asList(answer1ConceptUuid, answer2ConceptUuid));
				for (ConceptAnswer answer : answers) {
					assertTrue(answer.getAnswerConcept().getUuid() + " missing", expectedAnswers.remove(answer.getAnswerConcept().getUuid()));
				}
			}
		});
	}
}
