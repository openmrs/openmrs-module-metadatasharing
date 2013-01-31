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

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSource;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.wrapper.PackageExporter;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.serialization.SerializationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Functional tests on {@link Concept}s.
 */
public class SharingConceptsFunctionalTest extends BaseModuleContextSensitiveTest {
	
	private ConceptService conceptService;
	
	private AdministrationService administrationService;
	
	public static final String CONCEPT_SOURCE_UUID = UUID.randomUUID().toString();
	
	@Before
	public void before() throws Exception {
		conceptService = Context.getConceptService();
		
		ConceptSource conceptSource = new ConceptSource();
		conceptSource.setUuid(CONCEPT_SOURCE_UUID);
		conceptSource.setName(CONCEPT_SOURCE_UUID);
		conceptService.saveConceptSource(conceptSource);
		
		administrationService = Context.getAdministrationService();
		GlobalProperty gp = new GlobalProperty(MetadataSharingConsts.GP_SYSTEM_CONCEPT_SOURCE, CONCEPT_SOURCE_UUID);
		administrationService.saveGlobalProperty(gp);
	}
	
	@Test
	public void shouldExportConceptsCorrectly() throws Exception {
		//given
		Concept concept1 = newConcept(1);
		Concept concept2 = newConcept(2);
		ConceptAnswer answer2 = new ConceptAnswer(concept1);
		answer2.setUuid(newUuid(ConceptAnswer.class, 2));
		concept2.addAnswer(answer2);
		conceptService.saveConcept(concept2);
		
		//when
		Concept concept = conceptService.getConceptByUuid(newUuid(Concept.class, 2));
		ExportedPackage exportedPackage = newExportedPackage(1, concept);
		
		//then
		
		String header = exportedPackage.getSerializedPackage().getHeader();
		XMLAssert.assertXpathEvaluatesTo(newName(Package.class, 1), "/package/name", header);
		XMLAssert.assertXpathEvaluatesTo(newDescription(Package.class, 1), "/package/description", header);
		
		String metadata = exportedPackage.getSerializedPackage().getMetadata()[0];
		XMLAssert.assertXpathEvaluatesTo(newUuid(Concept.class, 2), "/list/org.openmrs.Concept/@uuid", metadata);
		XMLAssert.assertXpathEvaluatesTo(newName(Concept.class, 2),
		    "/list/org.openmrs.Concept/names/org.openmrs.ConceptName/name", metadata);
		XMLAssert.assertXpathEvaluatesTo(newUuid(Concept.class, 1),
		    "/list/org.openmrs.Concept/answers/org.openmrs.ConceptAnswer/answerConcept/@uuid", metadata);
		XMLAssert.assertXpathEvaluatesTo(newName(Concept.class, 1),
		    "/list/org.openmrs.Concept/answers/org.openmrs.ConceptAnswer/answerConcept/names/org.openmrs.ConceptName/name",
		    metadata);
	}
	
	@Test
	public void shouldImportConceptsCorrectly() throws Exception {
		//given
		Concept concept1 = newConcept(1);
		Concept concept2 = newConcept(2);
		ConceptAnswer answer2 = new ConceptAnswer(concept1);
		answer2.setUuid(newUuid(ConceptAnswer.class, 2));
		concept2.addAnswer(answer2);
		conceptService.saveConcept(concept2);
		
		ExportedPackage exportedPackage = newExportedPackage(1, concept2);
		
		//when
		newImportedPackage(exportedPackage);
		
		//then	
		concept1 = conceptService.getConceptByUuid(newUuid(Concept.class, 1));
		concept2 = conceptService.getConceptByUuid(newUuid(Concept.class, 2));
		
		Assert.assertNotNull("concept1 not null", concept1);
		Assert.assertNotNull("concept2 not null", concept2);
		answer2 = concept2.getAnswers().iterator().next();
		Assert.assertEquals("concept1 answer concept2", concept1, answer2.getAnswerConcept());
	}
	
	private Concept newConcept(Integer id) {
		Concept concept = new Concept();
		concept.setUuid(newUuid(Concept.class, id));
		ConceptName name = new ConceptName(newName(Concept.class, id), Locale.ENGLISH);
		name.setUuid(newUuid(ConceptName.class, id));
		concept.addName(name);
		ConceptDescription description = new ConceptDescription(newDescription(Concept.class, id), Locale.ENGLISH);
		description.setUuid(newUuid(ConceptDescription.class, id));
		concept.addDescription(description);
		conceptService.saveConcept(concept);
		return concept;
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
	
	private ExportedPackage newExportedPackage(Integer id, Concept... concepts) throws Exception {
		PackageExporter exporter = MetadataSharing.getInstance().newPackageExporter();
		exporter.getExportedPackage().setName(newName(Package.class, id));
		exporter.getExportedPackage().setDescription(newDescription(Package.class, id));
		
		for (Concept concept : concepts) {
			exporter.addItem(concept);
		}
		
		exporter.exportPackage();
		
		return exporter.getExportedPackage();
	}
	
	private ImportedPackage newImportedPackage(ExportedPackage exportedPackage) throws IOException, SerializationException {
		PackageImporter importer = MetadataSharing.getInstance().newPackageImporter();
		importer.loadSerializedPackageStream(exportedPackage.getSerializedPackageStream());
		importer.importPackage();
		ImportedPackage importedPackage = importer.getImportedPackage();
		return importedPackage;
	}
}
