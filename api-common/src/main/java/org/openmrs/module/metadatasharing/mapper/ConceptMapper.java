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
import org.openmrs.ConceptSource;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.springframework.stereotype.Component;

@Component(MetadataSharingConsts.MODULE_ID + ".ConceptMapper")
public class ConceptMapper {
	
	/**
	 * @deprecated use ConceptPubSub
	 */
	@Deprecated
	public boolean hasSystemConceptSource() {
		try {
			return getSystemConceptSource() != null;
		}
		catch (APIException e) {
			return false;
		}
	}
	
	/**
	 * @deprecated use {@link MetadataMappingService#getLocalConceptSource()} ()}
	 */
	@Deprecated
	public ConceptSource getSystemConceptSource() throws APIException {
		return Context.getService(MetadataMappingService.class).getLocalConceptSource();
	}
	
	/**
	 * Adds a system mapping to the given concept if necessary.
	 * 
	 * @param concept
	 * @deprecated use {@link MetadataMappingService#addLocalMappingToConcept(Concept)}
	 */
	@Deprecated
	public void addSystemConceptMap(Concept concept) throws APIException {
		Context.getService(MetadataMappingService.class).addLocalMappingToConcept(concept);
	}
}
