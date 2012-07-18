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

import org.openmrs.ConceptName;
import org.openmrs.module.metadatasharing.handler.impl.ConceptMerge17Handler;
import org.openmrs.module.metadatasharing.handler.impl.ObjectHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetadataSharing17Configuration {
	
	@Autowired
	ObjectHandler objectHandler;
	
	public static boolean supportsConceptNameGetConceptNameType() {
		try {
			return ConceptName.class.getDeclaredMethod("getConceptNameType", new Class<?>[0]) != null;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	@Bean(name = "metadatasharing.ConceptMerge17Handler")
	public ConceptMerge17Handler getConceptMerge17Handler() {
		if (supportsConceptNameGetConceptNameType()) {
			return  new ConceptMerge17Handler(objectHandler);
		} else {
			return null;
		}
	}
}
