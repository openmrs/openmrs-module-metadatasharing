package org.openmrs.module.metadatasharing.handler.impl;

import java.util.Collection;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.handler.MetadataMergeHandler;

public class ConceptMerge17Handler implements MetadataMergeHandler<Concept> {
	
	private final ObjectHandler objectHandler;
	
	public ConceptMerge17Handler(ObjectHandler objectHandler) {
		this.objectHandler = objectHandler;
	}
	
	@Override
	public int getPriority() {
	    return 1;
	}
	
	@Override
	public void merge(Concept existingConcept, Concept incomingConcept, ImportType importType,
	                  Map<Object, Object> incomingToExisting) {
		if (existingConcept != null) {
			//Remove preferred and fully specified tags in existing names if an incoming name is also preferred or fully specified for that locale.
			Collection<ConceptName> existingNames = (existingConcept).getNames();
			for (ConceptName existingName : existingNames) {
				if (existingName.isPreferred()) {
					ConceptName incomingPreferredName = incomingConcept.getPreferredName(existingName.getLocale());
					if (incomingPreferredName != null && !incomingPreferredName.getName().equals(existingName.getName())) {
						if (importType.isPreferTheirs()) {
							existingName.setLocalePreferred(false);
						} else {
							incomingPreferredName.setLocalePreferred(false);
						}
					}
				}
				
				if (ConceptNameType.FULLY_SPECIFIED.equals(existingName.getConceptNameType())) {
					ConceptName incomingFullySpecifiedName = incomingConcept.getFullySpecifiedName(existingName.getLocale());
					if (incomingFullySpecifiedName != null
					        && !incomingFullySpecifiedName.getName().equals(existingName.getName())) {
						if (importType.isPreferTheirs()) {
							existingName.setConceptNameType(ConceptNameType.INDEX_TERM);
						} else {
							incomingFullySpecifiedName.setConceptNameType(ConceptNameType.INDEX_TERM);
						}
					}
				}
			}
		}
		
		objectHandler.merge(existingConcept, incomingConcept, importType, incomingToExisting);
	}
	
}
