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
import org.openmrs.module.metadatasharing.handler.impl.ConceptMerge17Logic;
import org.openmrs.module.metadatasharing.handler.impl.ConceptMergeHandler;
import org.openmrs.module.metadatasharing.handler.impl.ObjectHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetadataSharing17Configuration {
	
	@Autowired
	ObjectHandler objectHandler;
	
	@Autowired
	ConceptMergeHandler conceptMergeHandler;
	
	public static boolean isNot16() {
		try {
			return ConceptName.class.getDeclaredMethod("getConceptNameType", new Class<?>[0]) != null;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	@Bean(name = "metadatasharing.ConceptMerge17Logic")
	public ConceptMerge17Logic getConceptMerge17Logic() {
		if (isNot16()) {
			ConceptMerge17Logic logic = new ConceptMerge17Logic(objectHandler);
			conceptMergeHandler.setConceptMergeLogic(logic);
			return logic;
		} else {
			return null;
		}
	}
}
