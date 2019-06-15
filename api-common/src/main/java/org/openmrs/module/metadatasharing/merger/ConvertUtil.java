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

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.util.ImportUtil;

/**
 *
 */
public class ConvertUtil {

	protected static final Log log = LogFactory.getLog(ConvertUtil.class);
	
	public static void convert(Collection<ImportedItem> importedItems) {
		for (ImportedItem importedItem : importedItems) {
			if (!(importedItem.getExisting() instanceof Concept)) {
				continue;
			}
			
			if (importedItem.getImportType().isPreferTheirs() || importedItem.getImportType().isOverwriteMine()) {
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
					ConceptNumeric numeric = ConceptNumericBuilder.valueOf(existing);
					
					//Initialize ConceptNameTags before evicting.
					for (ConceptName name : existing.getNames(true)) {
						if (name.getTags() != null) {
							name.getTags().size();
						}
					}
					
					Context.evictFromSession(existing);

					try {
						Context.getConceptService().saveConcept(numeric);
					}
					catch (RuntimeException e) {
						log.error("Error saving Concept Numeric " + numeric + " (" + numeric.getUuid() + ")", e);
						throw e;
					}

					importedItem.setExisting(numeric);
					
					//It's slightly inefficient, but it's the easiest way to get around lazily initialization exceptions.
					//It's needed, because some items may reference the evicted existing instance.
					ImportUtil.reloadExistingItems(importedItems);
				}
			}
		}
	}
}
