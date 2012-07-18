package org.openmrs.module.metadatasharing.handler.impl;

import java.util.Collection;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.handler.MetadataMergeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("metadatasharing.ConceptMergeHandler")
public class ConceptMergeHandler implements MetadataMergeHandler<Concept> {
	
	@Autowired
	ObjectHandler objectHandler;
	
	@Override
	public int getPriority() {
		return 0;
	}
	
	@Override
	public void merge(Concept existingConcept, Concept incomingConcept, ImportType importType,
	                  Map<Object, Object> incomingToExisting) {
		if (existingConcept != null) {
			//Remove preferred and fully specified tags in existing names if an incoming name is also preferred or fully specified for that locale.
			Collection<ConceptName> existingNames = (existingConcept).getNames();
			for (ConceptName existingName : existingNames) {
				if (existingName.getTags() == null) {
					continue;
				}
				
				if (isPreferred(existingName)) {
					ConceptName incomingPreferredName = incomingConcept.getPreferredName(existingName.getLocale());
					if (incomingPreferredName != null && !incomingPreferredName.getName().equals(existingName.getName())) {
						if (importType.isPreferTheirs()) {
							removePreferredTag(existingName);
						} else {
							removePreferredTag(incomingPreferredName);
						}
					}
				}
				
				if (isFullySpecified(existingName)) {
					for (ConceptName incomingName : incomingConcept.getNames()) {
						if (isFullySpecified(incomingName) && existingName.getLocale().equals(incomingName.getLocale())
						        && !existingName.getName().equals(incomingName.getName())) {
							if (importType.isPreferTheirs()) {
								addSynonymTag(existingName);
							} else {
								addSynonymTag(incomingName);
							}
							break;
						}
					}
				}
			}
		}
		
		objectHandler.merge(existingConcept, incomingConcept, importType, incomingToExisting);
	}
	
	public static interface ConceptMergeLogic {
		
		void merge(Concept existingConcept, Concept incomingConcept, ImportType importType,
		           Map<Object, Object> incomingToExisting);
	}
	
	private void removePreferredTag(ConceptName existingName) {
		for (ConceptNameTag tag : existingName.getTags()) {
			if (tag.getTag().startsWith(ConceptNameTag.PREFERRED)) {
				existingName.removeTag(tag);
				break;
			}
		}
	}
	
	private boolean isPreferred(ConceptName name) {
		for (ConceptNameTag tag : name.getTags()) {
			if (tag.getTag().startsWith(ConceptNameTag.PREFERRED)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isFullySpecified(ConceptName name) {
		return name.getTags() == null;
	}
	
	private void addSynonymTag(ConceptName name) {
		name.addTag(ConceptNameTag.SYNONYM);
	}
	
}
