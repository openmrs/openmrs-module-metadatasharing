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

import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.resolver.Resolver;
import org.springframework.stereotype.Component;

/**
 * This resolver handles concept to find matching concepts by concept mappings
 */
@Component(MetadataSharingConsts.MODULE_ID + ".ConceptByMappingResolver")
public class ConceptByMappingResolver extends Resolver<Concept> {
	
	/**
	 * @see org.openmrs.module.metadatasharing.resolver.Resolver#getPriority()
	 */
	@Override
	public int getPriority() {
	    return 20;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.resolver.Resolver#getExactMatch(java.lang.Object)
	 */
	@Override
	public Concept getExactMatch(Concept incoming) {
		Set<String> preferredSourceNames = MetadataSharing.getService().getPreferredSourceNames();
		
		for (ConceptMap map : incoming.getConceptMappings()) {
			if (map.getConceptReferenceTerm().getCode() == null || map.getConceptReferenceTerm().getConceptSource() == null || map.getConceptReferenceTerm().getConceptSource().getName() == null) {
				continue;
			}
			
			if (preferredSourceNames.contains(map.getConceptReferenceTerm().getConceptSource().getName())) {
				ConceptSource source = Context.getConceptService().getConceptSourceByName(map.getConceptReferenceTerm().getConceptSource().getName());
				
				for (ConceptMap m : Context.getConceptService().getConceptMappingsToSource(source)) {
					if (m.getConceptReferenceTerm().getCode().equals(map.getConceptReferenceTerm().getCode())) {
						Concept existing = m.getConcept();
						
						if (existing == null || existing.getDatatype() == null || existing.getDatatype().getName() == null
						        || existing.getConceptClass() == null || existing.getConceptClass().getName() == null) {
							continue;
						}
						
						if (incoming == null || incoming.getDatatype() == null || incoming.getDatatype().getName() == null
						        || incoming.getConceptClass() == null || incoming.getConceptClass().getName() == null) {
							continue;
						}
						
						if (incoming.getDatatype().getName().equalsIgnoreCase(existing.getDatatype().getName())
						        && incoming.getConceptClass().getName()
						                .equalsIgnoreCase(existing.getConceptClass().getName())) {
							return existing;
						}
						
						if (incoming.getDatatype().getName().equalsIgnoreCase(existing.getDatatype().getName())
						        && incoming.getConceptClass().getName()
						                .equalsIgnoreCase(existing.getConceptClass().getName())) {
							return existing;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.resolver.Resolver#getPossibleMatch(java.lang.Object)
	 */
	@Override
	public Concept getPossibleMatch(Concept incoming) {
		for (ConceptMap map : incoming.getConceptMappings()) {
			if (map.getConceptReferenceTerm().getCode() == null || map.getConceptReferenceTerm().getConceptSource() == null || map.getConceptReferenceTerm().getConceptSource().getName() == null) {
				continue;
			}
			
			ConceptSource source = Context.getConceptService().getConceptSourceByName(map.getConceptReferenceTerm().getConceptSource().getName());
			for (ConceptMap m : Context.getConceptService().getConceptMappingsToSource(source)) {
				if (m.getConceptReferenceTerm().getCode().equals(map.getConceptReferenceTerm().getCode())) {
					Concept existing = m.getConcept();
					
					if (existing == null || existing.getDatatype() == null || existing.getDatatype().getName() == null
					        || existing.getConceptClass() == null || existing.getConceptClass().getName() == null) {
						continue;
					}
					
					if (incoming == null || incoming.getDatatype() == null || incoming.getDatatype().getName() == null
					        || incoming.getConceptClass() == null || incoming.getConceptClass().getName() == null) {
						continue;
					}
					
					if (incoming.getDatatype().getName().equalsIgnoreCase(existing.getDatatype().getName())
					        && incoming.getConceptClass().getName().equalsIgnoreCase(existing.getConceptClass().getName())) {
						return existing;
					}
				}
			}
			
		}
		
		return null;
	}
}
