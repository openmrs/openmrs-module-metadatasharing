package org.openmrs.module.metadatasharing.handler.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
		return 1;
	}

	@Override
	public void merge(Concept existingConcept, Concept incomingConcept, ImportType importType,
					  Map<Object, Object> incomingToExisting) {
		if (existingConcept != null) {
			Set<Locale> locales = new HashSet<Locale>();
			locales.addAll(getLocales(existingConcept.getNames()));
			locales.addAll(getLocales(incomingConcept.getNames()));

			for (Locale locale : locales) {
				if (importType.isPreferTheirs()) {
					//Only one preferred name is allowed for a locale.
					ConceptName preferredName = incomingConcept.getPreferredName(locale);
					if (preferredName != null) {
						for (ConceptName existingName : existingConcept.getNames(locale)) {
							if (!existingName.getName().equalsIgnoreCase(preferredName.getName())) {
								existingName.setLocalePreferred(false);
							}
						}
					}

					//Only one fully specified name is allowed for a locale.
					ConceptName fullySpecifiedName = incomingConcept.getFullySpecifiedName(locale);
					if (fullySpecifiedName != null) {
						for (ConceptName existingName : existingConcept.getNames(locale)) {
							if (!existingName.getName().equalsIgnoreCase(fullySpecifiedName.getName())) {
								if (existingName.isFullySpecifiedName()) {
									existingName.setConceptNameType(null); //make it synonym
								}
							}
						}
					}
				} else if (!importType.isOverwriteMine()) {
					//Only one preferred name is allowed for a locale.
					ConceptName preferredName = existingConcept.getPreferredName(locale);
					if (preferredName != null) {
						for (ConceptName incomingName : incomingConcept.getNames(locale)) {
							incomingName.setLocalePreferred(false);
						}
					}

					//Only one fully specified name is allowed for a locale.
					ConceptName fullySpecifiedName = existingConcept.getFullySpecifiedName(locale);
					if (fullySpecifiedName != null) {
						for (ConceptName incomingName : incomingConcept.getNames(locale)) {
							if (incomingName.isFullySpecifiedName()) {
								incomingName.setConceptNameType(null); //make it synonym
							}
						}
					}
				}
			}
		}

		objectHandler.merge(existingConcept, incomingConcept, importType, incomingToExisting);
	}

	private Set<Locale> getLocales(Collection<ConceptName> names) {
		Set<Locale> locales = new HashSet<Locale>();
		for (ConceptName name : names) {
			locales.add(name.getLocale());
		}
		return locales;
	}
	
}
