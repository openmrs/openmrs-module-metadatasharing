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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.custommonkey.xmlunit.XMLAssert;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hibernate.proxy.HibernateProxy;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Tests if MIRROR mode works correctly for Concept.
 */
public class ConceptMirrorTest extends BaseShareTest {
	
	@Autowired
	@Qualifier("conceptService")
	ConceptService conceptService;
	
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
				Concept concept = newConcept(7);
				concept.setUuid(conceptUuid);
				
				Concept concept1 = newConcept(1);
				concept1.setUuid(answer1ConceptUuid);
				ConceptAnswer conceptAnswer1 = newConceptAnswer(1);
				conceptAnswer1.setAnswerConcept(concept1);
				concept.addAnswer(conceptAnswer1);
				
				Concept concept2 = newConcept(2);
				concept2.setUuid(answer2ConceptUuid);
				ConceptAnswer conceptAnswer2 = newConceptAnswer(2);
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
				Concept concept = newConcept(7);
				concept.setUuid(conceptUuid);
				
				Concept concept1 = newConcept(1);
				concept1.setUuid(answer1ConceptUuid);
				ConceptAnswer conceptAnswer1 = newConceptAnswer(1);
				conceptAnswer1.setAnswerConcept(concept1);
				concept.addAnswer(conceptAnswer1);
				
				Concept concept2 = newConcept(2);
				concept2.setUuid(answer2ConceptUuid);
				ConceptAnswer conceptAnswer2 = newConceptAnswer(2);
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
					assertTrue(answer.getAnswerConcept().getUuid() + " missing",
					    expectedAnswers.remove(answer.getAnswerConcept().getUuid()));
				}
			}
		});
	}
	
	@Test
	public void shouldVoidConceptNamesIfNotInIncoming() throws Exception {
		final String conceptUuid = UUID.randomUUID().toString();
		
		runShareTest(new ShareTestHelper() {
			
			@Override
			public List<?> prepareExportServer() throws Exception {
				Concept concept = newConcept(1);
				concept.setUuid(conceptUuid);
				
				concept.addName(new ConceptName("name to add", Locale.ENGLISH));
				
				Context.getConceptService().saveConcept(concept);
				return Arrays.asList(concept);
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#prepareImportServer()
			 */
			@Override
			public void prepareImportServer() throws Exception {
				Concept concept = newConcept(1);
				concept.setUuid(conceptUuid);
				
				concept.addName(new ConceptName("name to void", Locale.ENGLISH));
				
				Context.getConceptService().saveConcept(concept);
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#runOnImportServerBeforeImport(org.openmrs.module.metadatasharing.wrapper.PackageImporter)
			 */
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
			    importer.setImportConfig(ImportConfig.valueOf(ImportMode.MIRROR));
			}
			
			@SuppressWarnings({ "unchecked" })
			@Override
			public void runOnImportServerAfterImport() throws Exception {
				Concept concept = Context.getConceptService().getConceptByUuid(conceptUuid);
				assertThat(concept.getNames(true), containsInAnyOrder(hasName("name to void"), hasName("name to add"),
				        hasName("org.openmrs.Concept_1_name")));
				
				assertThat(concept.getNames(false), containsInAnyOrder(hasName("name to add"), hasName("org.openmrs.Concept_1_name")));
			}
			
		});
		
	}
	
	@Test
	public void shouldExportAndImportConceptNumericConvertingFromConcept() throws Exception {
		final String conceptUuid = UUID.randomUUID().toString();
		
		runShareTest(new ShareTestHelper() {
			
			@Override
			public List<?> prepareExportServer() throws Exception {
				Concept concept = newConcept(1);
				concept.setUuid(conceptUuid);
				
				ConceptNumeric numericConcept = new ConceptNumeric(concept);
				numericConcept.setLowNormal(2.0);
				numericConcept.setHiNormal(2.0);
				numericConcept.setDatatype(conceptService.getConceptDatatypeByName("Numeric"));
				
				conceptService.saveConcept(numericConcept);
				return Arrays.asList(numericConcept);
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#prepareImportServer()
			 */
			@Override
			public void prepareImportServer() throws Exception {
				Concept concept = newConcept(1);
				concept.setUuid(conceptUuid);
				
				conceptService.saveConcept(concept);
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
				ConceptNumeric concept = conceptService.getConceptNumericByUuid(conceptUuid);
				
				assertThat(concept, isA(ConceptNumeric.class));
				assertThat(concept.getLowNormal(), is(2.0));
				assertThat(concept.getHiNormal(), is(2.0));
				assertThat(concept.getDatatype().getName(), is("Numeric"));
			}
			
		});
	}
	
	@Test
	public void shouldExportAndImportRelatedConceptNumericConvertingFromConcept() throws Exception {
		final String conceptUuid = UUID.randomUUID().toString();
		
		runShareTest(new ShareTestHelper() {
			
			@Override
			public List<?> prepareExportServer() throws Exception {
				Concept concept = newConcept(1);
				concept.setUuid(conceptUuid);
				
				ConceptNumeric numericAnswer = newConceptNumeric(2);
				numericAnswer.setLowNormal(2.0);
				numericAnswer.setHiNormal(2.0);
				numericAnswer.setDatatype(conceptService.getConceptDatatypeByName("Numeric"));
				
				ConceptAnswer conceptAnswer = newConceptAnswer(1);
				concept.addAnswer(conceptAnswer);
				conceptAnswer.setAnswerConcept(numericAnswer);
				
				conceptService.saveConcept(concept);
				
				//We want to test here that Hibernate proxies are stripped off properly
				Context.flushSession();
				Context.clearSession();
				
				Concept conceptToExport = conceptService.getConceptByUuid(concept.getUuid());
				
				ConceptAnswer conceptAnswerToExport = conceptToExport.getAnswers().iterator().next();
				
				assertThat(conceptAnswerToExport.getAnswerConcept(), is(instanceOf(HibernateProxy.class)));
				
				return Arrays.asList(conceptToExport);
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#runOnExportServerAfterExport(org.openmrs.module.metadatasharing.ExportedPackage)
			 */
			@Override
			public void runOnExportServerAfterExport(ExportedPackage exportedPackage) throws Exception {
				String xml = exportedPackage.getSerializedPackage().getMetadata()[0];
				
				XMLAssert.assertXpathEvaluatesTo("org.openmrs.ConceptNumeric",
				    "/list/org.openmrs.Concept/answers/org.openmrs.ConceptAnswer/answerConcept/@resolves-to", xml);
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#prepareImportServer()
			 */
			@Override
			public void prepareImportServer() throws Exception {
				Concept concept = newConcept(1);
				concept.setUuid(conceptUuid);
				
				Concept answer = newConcept(2);
				
				ConceptAnswer conceptAnswer = newConceptAnswer(1);
				concept.addAnswer(conceptAnswer);
				conceptAnswer.setAnswerConcept(answer);
				
				conceptService.saveConcept(concept);
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
				ConceptNumeric concept = conceptService.getConceptNumericByUuid(newUuid(Concept.class, 2));
				
				assertThat(concept, isA(ConceptNumeric.class));
				assertThat(concept.getLowNormal(), is(2.0));
				assertThat(concept.getHiNormal(), is(2.0));
				assertThat(concept.getDatatype().getName(), is("Numeric"));
			}
			
		});
	}
	
	private Matcher<ConceptName> hasName(final String name) {
		return new TypeSafeMatcher<ConceptName>() {
			
			@Override
			public void describeTo(Description description) {
				description.appendText("getName should return ").appendValue(name);
			}
			
			@Override
			protected boolean matchesSafely(ConceptName item) {
				return item.getName().equals(name);
			}
		};
	}
	
	private Concept newConcept(Integer id) {
		Concept concept = new Concept();
		populateConceptWithUuidAndNameAndDescription(id, concept);
		return concept;
	}
	
	private Concept populateConceptWithUuidAndNameAndDescription(Integer id, Concept concept) {
		concept.setUuid(newUuid(Concept.class, id));
		ConceptName name = new ConceptName(newName(Concept.class, id), Locale.ENGLISH);
		name.setUuid(newUuid(ConceptName.class, id));
		concept.addName(name);
		ConceptDescription description = new ConceptDescription(newDescription(Concept.class, id), Locale.ENGLISH);
		description.setUuid(newUuid(ConceptDescription.class, id));
		concept.addDescription(description);
		Context.getConceptService().saveConcept(concept);
		return concept;
	}
	
	private ConceptNumeric newConceptNumeric(Integer id) {
		ConceptNumeric concept = new ConceptNumeric();
		populateConceptWithUuidAndNameAndDescription(id, concept);
		return concept;
	}
	
	private ConceptAnswer newConceptAnswer(Integer id) {
		ConceptAnswer conceptAnswer = new ConceptAnswer(id);
		conceptAnswer.setUuid(UUID.randomUUID().toString());
		conceptAnswer.setDateCreated(new Date());
		conceptAnswer.setCreator(Context.getAuthenticatedUser());
		return conceptAnswer;
	}
	
	private String newName(Class<?> type, Integer id) {
		return type.getName() + "_" + id + "_name";
	}
	
	private String newDescription(Class<?> type, Integer id) {
		return type.getName() + "_" + id + "_description";
	}
	
	private String newUuid(Class<?> type, Integer id) {
		return type.getName() + "_" + id + "_uuid";
	}
}
