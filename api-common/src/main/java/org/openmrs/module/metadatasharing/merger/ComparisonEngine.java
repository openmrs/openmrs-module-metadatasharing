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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.springframework.stereotype.Component;

/**
 * Performs comparison of hidden objects which do not match by uuid and should be compared based on
 * business fields.
 */
@Component(MetadataSharingConsts.MODULE_ID + ".ComparisonEngine")
public class ComparisonEngine {
	
	/**
	 * The implementation will change the moment we need to support more hidden types.
	 * 
	 * @param incoming
	 * @param existing
	 * @param incomingToExisting
	 * @return
	 */
	@SuppressWarnings("deprecation")
    public boolean equal(Object incoming, Object existing, Map<Object, Object> incomingToExisting) {
		if (incoming instanceof ConceptMap) {
			ConceptMap incomingMap = (ConceptMap) incoming;
			ConceptMap existingMap = (ConceptMap) existing;
			
			if (incomingMap.getConceptReferenceTerm().getConceptSource() != null && existingMap.getConceptReferenceTerm().getConceptSource() != null) {
				Object existingSource = incomingToExisting.get(incomingMap.getConceptReferenceTerm().getConceptSource());
				if (incomingMap.getConceptReferenceTerm().getConceptSource().equals(existingMap.getConceptReferenceTerm().getConceptSource()) || existingMap.getConceptReferenceTerm().getConceptSource().equals(existingSource)) {
					return StringUtils.equalsIgnoreCase(incomingMap.getConceptReferenceTerm().getCode(), existingMap.getConceptReferenceTerm().getCode());
				}
			} else {
				return false;
			}
		} else if (incoming instanceof ConceptName) {
			ConceptName incomingName = (ConceptName) incoming;
			ConceptName existingName = (ConceptName) existing;
			
			if (StringUtils.equalsIgnoreCase(incomingName.getName(), existingName.getName())) {
				return nullSafeEqual(incomingName.getLocale(), existingName.getLocale());
			}
		}
		
		return false;
	}
	
	private boolean nullSafeEqual(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		} else if (o1 != null) {
			return o1.equals(o2);
		} else {
			return false;
		}
	}
}
