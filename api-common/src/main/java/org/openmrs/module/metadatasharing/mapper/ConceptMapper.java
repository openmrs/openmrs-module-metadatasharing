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
package org.openmrs.module.metadatasharing.mapper;

import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptSource;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.springframework.stereotype.Component;

@Component(MetadataSharingConsts.MODULE_ID + ".ConceptMapper")
public class ConceptMapper {
	
	public boolean hasSystemConceptSource() {
		String conceptSourceUuid = Context.getAdministrationService().getGlobalProperty(
		    MetadataSharingConsts.GP_SYSTEM_CONCEPT_SOURCE);
		if (conceptSourceUuid != null) {
			ConceptSource conceptSource = Context.getConceptService().getConceptSourceByUuid(conceptSourceUuid);
			if (conceptSource != null) {
				return true;
			}
		}
		return false;
	}
	
	public ConceptSource getSystemConceptSource() throws APIException {
		String conceptSourceUuid = Context.getAdministrationService().getGlobalProperty(
		    MetadataSharingConsts.GP_SYSTEM_CONCEPT_SOURCE);
		if (conceptSourceUuid == null) {
			throw new APIException("The global property " + MetadataSharingConsts.GP_SYSTEM_CONCEPT_SOURCE
			        + " was not found!");
		}
		
		ConceptSource conceptSource = Context.getConceptService().getConceptSourceByUuid(conceptSourceUuid);
		if (conceptSource == null) {
			throw new APIException("The concept source uuid (" + conceptSourceUuid + ") specified in the global property "
			        + MetadataSharingConsts.GP_SYSTEM_CONCEPT_SOURCE + " was not found!");
		}
		
		return conceptSource;
	}
	
	/**
	 * Adds a system mapping to the given concept if necessary.
	 * 
	 * @param concept
	 */
	public void addSystemConceptMap(Concept concept) throws APIException {
		if (concept.getId() == null) {
			//The concept is not saved in the db.
			return;
		}
		
		ConceptSource systemConceptSource = getSystemConceptSource();
		
		boolean found = false;
		for (ConceptMap map : concept.getConceptMappings()) {
			if (map.getSource().equals(systemConceptSource)) {
				if (map.getSourceCode().equals(concept.getId().toString())) {
					found = true;
					break;
				}
			}
		}
		
		if (!found) {
			ConceptMap mapping = new ConceptMap();
			mapping.setConcept(concept);
			mapping.setSource(systemConceptSource);
			mapping.setSourceCode(concept.getId().toString());
			
			concept.addConceptMapping(mapping);
			Context.getConceptService().saveConcept(concept);
		}
	}
}
