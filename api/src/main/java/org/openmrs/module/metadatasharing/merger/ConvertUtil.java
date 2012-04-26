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
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ImportedItem;

/**
 *
 */
public class ConvertUtil {
	
	public static void convert(ImportedItem importedItem) {
		if (importedItem.getImportType().isPreferTheirs() && importedItem.getIncoming() instanceof Concept) {
			Concept incoming = (Concept) importedItem.getIncoming();
			Concept existing = (Concept) importedItem.getExisting();
			
			if (!incoming.isSet() && existing.isSet()) {
				if (existing.getConceptSets() != null) {
					existing.getConceptSets().clear();
				}
			}
			if (incoming.getDatatype() != null && !incoming.getDatatype().isCoded()) {
				if (existing.getAnswers(true) != null) {
					existing.getAnswers(true).clear();
				}
			}
			
			if (incoming.isNumeric() && !existing.isNumeric()) {
				//Initialize collections
				if (existing.getNames(true) != null) {
					for (ConceptName name : existing.getNames(true)) {
						if (name.getTags() != null) {
							name.getTags().size();
						}
					}
				}
				if (existing.getDescriptions() != null) {
					existing.getDescriptions().size();
				}
				if (existing.getConceptMappings() != null) {
					existing.getConceptMappings().size();
				}
				if (existing.getConceptSets() != null) {
					existing.getConceptSets().size();
				}
				if (existing.getAnswers(true) != null) {
					existing.getAnswers(true).size();
				}
				
				ConceptNumeric concept = new ConceptNumeric(existing);
				concept.setUuid(existing.getUuid()); //The ConceptNumeric constructor had bug so we need to set it manually
				
				Context.evictFromSession(existing);
				Context.getConceptService().saveConcept(concept);
				importedItem.setExisting(concept);
			}
		}
	}
}
