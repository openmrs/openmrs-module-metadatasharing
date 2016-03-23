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
package org.openmrs.module.metadatasharing.resolver.impl;

import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.resolver.Resolver;
import org.openmrs.module.metadatasharing.resolver.impl.ObjectByNameResolver;
import org.openmrs.module.metadatasharing.resolver.impl.ObjectByUuidResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 */
@Component(MetadataSharingConsts.MODULE_ID + ".ConceptReferenceTermResolver")
public class ConceptReferenceTermResolver extends Resolver<ConceptReferenceTerm> {

	@Autowired
	ObjectByNameResolver objectByName;

	@Autowired
	ObjectByUuidResolver objectByUuid;
	
	/**
	 * @see org.openmrs.module.metadatasharing.resolver.Resolver#getPriority()
	 */
	@Override
	public int getPriority() {
	    return 10;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.resolver.Resolver#getExactMatch(java.lang.Object)
	 */
	@Override
	public ConceptReferenceTerm getExactMatch(ConceptReferenceTerm incoming) {
		Object exactMatch = objectByUuid.getExactMatch(incoming.getConceptSource());
		if (exactMatch != null) {
			ConceptSource conceptSource = (ConceptSource) exactMatch;
			
			ConceptReferenceTerm term = Context.getConceptService().getConceptReferenceTermByCode(incoming.getCode(),
			    conceptSource);
			return term;
		} else {
			return null;
		}
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.resolver.Resolver#getPossibleMatch(java.lang.Object)
	 */
	@Override
	public ConceptReferenceTerm getPossibleMatch(ConceptReferenceTerm incoming) {
		Object possibleMatch = objectByName.getPossibleMatch(incoming.getConceptSource());
		if (possibleMatch != null) {
			ConceptSource conceptSource = (ConceptSource) possibleMatch;
			
			ConceptReferenceTerm term = Context.getConceptService().getConceptReferenceTermByCode(incoming.getCode(),
			    conceptSource);
			return term;
		} else {
			return null;
		}
	}
	
}
