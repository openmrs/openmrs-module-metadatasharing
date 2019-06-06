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
package org.openmrs.module.metadatasharing.merger;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptSet;

import java.util.HashSet;


/**
 * Builder for {@link ConceptNumeric}.
 */
public class ConceptNumericBuilder {
	
	public static ConceptNumeric valueOf(Concept concept) {
		ConceptNumeric numeric = new ConceptNumeric(concept);
		
		//It is not set in the copy constructor
		numeric.setUuid(concept.getUuid());
		
		// Replace Hibernate collection implementations with basic Java implementations (see Rafal's comment on META-365)
		Concept newConcept = new Concept(); //Needed to retrieve specific implementations
		numeric.setAnswers(newConcept.getAnswers());
		numeric.setConceptMappings(newConcept.getConceptMappings());
		numeric.setConceptSets(newConcept.getConceptSets());
		numeric.setDescriptions(newConcept.getDescriptions());
		numeric.setNames(newConcept.getNames(true));
		
		numeric.getAnswers().addAll(concept.getAnswers());
		for (ConceptAnswer answer : numeric.getAnswers()) {
			answer.setConcept(numeric);
        }
		
		numeric.getConceptMappings().addAll(concept.getConceptMappings());
		for (ConceptMap map : numeric.getConceptMappings()) {
			map.setConcept(numeric);
        }
		
		numeric.getConceptSets().addAll(concept.getConceptSets());
		for (ConceptSet set : numeric.getConceptSets()) {
			set.setConcept(numeric);
        }
		
		numeric.getDescriptions().addAll(concept.getDescriptions());
		for (ConceptDescription description : numeric.getDescriptions()) {
			description.setConcept(numeric);
        }

		numeric.setNames(new HashSet<ConceptName>(concept.getNames(true)));
		for (ConceptName name : numeric.getNames(true)) {
	        name.setConcept(numeric);
        }

		return numeric;
	}
	
}
