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
package org.openmrs.module.metadatasharing.mock;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import junit.framework.Assert;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSource;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.handler.Handler;

public class ConceptMock {
	
	private final Concept concept;
	
	public static Class<? extends ConceptMock> conceptBuilderClass = ConceptMock.class;
	
	public ConceptMock() {
		concept = new Concept();
		concept.setUuid(UUID.randomUUID().toString());
	}
	
	public ConceptMock(Concept concept) {
		this.concept = concept;
	}
	
	public static ConceptMock newInstance() {
		try {
			return conceptBuilderClass.newInstance();
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static ConceptMock newInstance(Concept concept) {
		try {
			return conceptBuilderClass.getConstructor(Concept.class).newInstance(concept);
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public ConceptMock setUuid(String uuid) {
		concept.setUuid(uuid);
		return this;
	}
	
	public ConceptMock addName(String name, Locale locale) {
		ConceptName conceptName = new ConceptName(name, locale);
		conceptName.setUuid(UUID.randomUUID().toString());
		concept.addName(conceptName);
		return this;
	}
	
	public ConceptMock setPreferredName(String name, Locale locale) {
		for (ConceptName conceptName : concept.getNames()) {
			if (conceptName.getName().equals(name) && conceptName.getLocale().equals(locale)) {
				concept.setPreferredName(locale, conceptName);
				return this;
			}
		}
		throw new IllegalStateException("Name '" + name + "' in locale '" + locale + "' must be added before");
	}
	
	public ConceptMock setShortName(String name, Locale locale) {
		for (ConceptName conceptName : concept.getNames()) {
			if (conceptName.getName().equals(name) && conceptName.getLocale().equals(locale)) {
				concept.setShortName(locale, conceptName);
				return this;
			}
		}
		throw new IllegalStateException("Name '" + name + "' in locale '" + locale + "' must be added before");
	}
	
	public ConceptMock addPreferredName(String name, Locale locale) {
		ConceptName conceptName = new ConceptName(name, locale);
		conceptName.setUuid(UUID.randomUUID().toString());
		concept.setPreferredName(locale, conceptName);
		return this;
	}
	
	public ConceptMock addShortName(String name, Locale locale) {
		ConceptName conceptName = new ConceptName(name, locale);
		conceptName.setUuid(UUID.randomUUID().toString());
		concept.setShortName(locale, conceptName);
		return this;
	}
	
	public ConceptMock addDescription(String description, Locale locale) {
		ConceptDescription conceptDescription = new ConceptDescription(description, locale);
		conceptDescription.setUuid(UUID.randomUUID().toString());
		concept.addDescription(conceptDescription);
		return this;
	}
	
	public ConceptMock addMapping(String code, String source) {
		ConceptSource conceptSource = Context.getConceptService().getConceptSourceByName(source);
		if (conceptSource == null) {
			conceptSource = new ConceptSource();
			conceptSource.setUuid(UUID.randomUUID().toString());
			conceptSource.setName(source);
			conceptSource.setHl7Code(source);
		}
		ConceptMap conceptMap = new ConceptMap();
		conceptMap.setSource(conceptSource);
		conceptMap.setSourceCode(code);
		concept.addConceptMapping(conceptMap);
		return this;
	}
	
	public ConceptMock addClass(String clazz) {
		ConceptClass conceptClass = Context.getConceptService().getConceptClassByName(clazz);
		if (conceptClass == null) {
			conceptClass = new ConceptClass();
			conceptClass.setUuid(UUID.randomUUID().toString());
			conceptClass.setName(clazz);
		}
		concept.setConceptClass(conceptClass);
		return this;
	}
	
	public ConceptMock setDatatype(String datatype) {
		ConceptDatatype conceptDatatype = Context.getConceptService().getConceptDatatypeByName(datatype);
		if (conceptDatatype == null) {
			conceptDatatype = new ConceptDatatype();
			conceptDatatype.setUuid(UUID.randomUUID().toString());
			conceptDatatype.setName(datatype);
		}
		concept.setDatatype(conceptDatatype);
		return this;
	}
	
	public ConceptMock saveConcept() {
		saveObjectWithDependencies(concept);
		return this;
	}
	
	private void saveObjectWithDependencies(Object object) {
		List<Object> priorityDependencies = Handler.getPriorityDependencies(object);
		for (Object priorityDependecy : priorityDependencies) {
			saveObjectWithDependencies(priorityDependecy);
		}
		Handler.saveItem(object);
	}
	
	public Concept getConcept() {
		return concept;
	}
	
	protected boolean assertBothOrNoneNull(String message, Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return true;
		} else if (o1 == null || o2 == null) {
			Assert.fail(message);
			return false;
		} else {
			return false;
		}
	}
	
	public ConceptMock assertEquals(Concept expected, boolean ignoreUuids) {
		if (!ignoreUuids) {
			Assert.assertEquals("uuid", expected.getUuid(), concept.getUuid());
		}
		if (!assertBothOrNoneNull("datatype", expected.getDatatype(), concept.getDatatype())) {
			if (!ignoreUuids) {
				Assert.assertEquals("datatype uuid", expected.getDatatype().getUuid(), concept.getDatatype().getUuid());
			}
			Assert.assertEquals("datatype name", expected.getDatatype().getName(), concept.getDatatype().getName());
		}
		if (!assertBothOrNoneNull("class", expected.getConceptClass(), concept.getConceptClass())) {
			if (!ignoreUuids) {
				Assert.assertEquals("class uuid", expected.getConceptClass().getUuid(), concept.getConceptClass().getUuid());
			}
			Assert.assertEquals("class name", expected.getConceptClass().getName(), concept.getConceptClass().getName());
		}
		if (!assertBothOrNoneNull("maps", expected.getConceptMappings(), concept.getConceptMappings())) {
			Assert.assertEquals("map size", expected.getConceptMappings().size(), concept.getConceptMappings().size());
			for (ConceptMap conceptMap : concept.getConceptMappings()) {
				boolean found = false;
				for (ConceptMap expectedMap : expected.getConceptMappings()) {
					if (expectedMap.getSourceCode().equals(conceptMap.getSourceCode())) {
						found = true;
						if (!assertBothOrNoneNull("map source", expectedMap.getSource(), conceptMap.getSource())) {
							if (!ignoreUuids) {
								Assert.assertEquals("map source uuid", expectedMap.getSource().getUuid(), conceptMap
								        .getSource().getUuid());
							}
							Assert.assertEquals("map source name", expectedMap.getSource().getName(), conceptMap.getSource()
							        .getName());
						}
					}
				}
				Assert.assertTrue("map code not found", found);
			}
		}
		
		if (!assertBothOrNoneNull("names", expected.getNames(), concept.getNames())) {
			Assert.assertEquals("names size", expected.getNames().size(), concept.getNames().size());
			for (ConceptName conceptName : concept.getNames()) {
				boolean found = false;
				for (ConceptName expectedName : expected.getNames()) {
					if (expectedName.getName().equals(conceptName.getName())) {
						found = true;
						if (!ignoreUuids) {
							Assert.assertEquals("name uuid", expectedName.getUuid(), conceptName.getUuid());
						}
					}
				}
				Assert.assertTrue("name not found: " + conceptName.getName(), found);
			}
			
			for (ConceptName conceptName : concept.getNames()) {
	            ConceptName preferredName = concept.getName(conceptName.getLocale()); 
	            ConceptName expectedPreferredName = expected.getName(conceptName.getLocale());
	            if (!assertBothOrNoneNull("preferred name", expectedPreferredName, preferredName)) {
	            	Assert.assertEquals("preferred name value", expectedPreferredName.getName(), preferredName.getName());
	            }
            }
		}
		return this;
	}

	public ConceptMock addDrugAnswer(String drug, Concept concept) {		
		Drug answerDrug = Context.getConceptService().getDrug(drug);
		if (answerDrug == null) {
			answerDrug = new Drug();
			answerDrug.setUuid(UUID.randomUUID().toString());
			answerDrug.setName(drug);
		}
		answerDrug.setConcept(concept);
		
		ConceptAnswer conceptAnswer = new ConceptAnswer();
		conceptAnswer.setUuid(UUID.randomUUID().toString());
		conceptAnswer.setAnswerDrug(answerDrug);
		conceptAnswer.setAnswerConcept(concept);
		this.concept.addAnswer(conceptAnswer);
	    return this;
    }
}
