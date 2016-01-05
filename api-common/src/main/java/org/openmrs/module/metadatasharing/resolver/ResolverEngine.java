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
package org.openmrs.module.metadatasharing.resolver;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.reflection.ClassUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Processes items before importing with the registered resolvers.
 * <p>
 * Resolvers are registered in <b>moduleApplicationContext.xml</b>.
 * <p>
 * When invoking {@link #resolve(Set, ImportConfig)} importedItems are passed to the registered
 * resolvers.
 * <p>
 * Each importedItem will be passed to consecutive resolvers as long as no exact match is found,
 * therefore first resolver on the list take precedence over the last. If no exact match is found,
 * then we look for a possible match in the same way.
 *
 * @see Resolver
 */
@Component("metadatasharing.ResolverEngine")
public class ResolverEngine {

	private SortedSet<Resolver<?>> resolvers;

	/**
	 * @param resolvers the resolvers to set
	 */
	@Autowired
	public void setResolvers(List<Resolver<?>> resolvers) {
		this.resolvers = new TreeSet<Resolver<?>>(new Comparator<Resolver<?>>() {

			@Override
            public int compare(Resolver<?> o1, Resolver<?> o2) {
	            return new CompareToBuilder().append(o2.getPriority(), o1.getPriority()).append(o2.getClass().toString(), o1.getClass().toString()).toComparison();
            }});

		for (Resolver<?> resolver : resolvers) {
			if (resolver != null) {
				this.resolvers.add(resolver);
			}
        }
	}

	/**
	 * Resolve the given importedItems.
	 *
	 * @param importedItems
	 * @param importConfig
	 */
	public void resolve(Set<ImportedItem> importedItems, ImportConfig importConfig) {
		for (ImportedItem importedItem : importedItems) {
			importedItem.loadExisting();

			//Skip if assessed
			if (importedItem.isAssessed()) {
				continue;
			}

			if (importedItem.isImported()) {
				//Previously matched are now exact matches
				importedItem.setImportType(importConfig.getExactMatch());
				importedItem.setAssessed(!importConfig.isConfirmExactMatch());
			} else {
				//Try to find an exact match
				for (Resolver<?> resolver : resolvers) {
					Class<?> supportedType = ClassUtil.getFirstParameterOfGenericType(resolver.getClass(), Resolver.class);
					if (supportedType.isInstance(importedItem.getIncoming())) {
						@SuppressWarnings("unchecked")
						Object exactMatch = ((Resolver<Object>) resolver).getExactMatch(importedItem.getIncoming());

						if (exactMatch != null) {
							if (exactMatch == importedItem.getIncoming()) {
								throw new IllegalStateException("Resolver " + resolver.getClass()
								        + " failure! Incoming and existing must not be the same: " + exactMatch.toString());
							}
							importedItem.setExisting(exactMatch);
							importedItem.setImportType(importConfig.getExactMatch());
							importedItem.setAssessed(!importConfig.isConfirmExactMatch());
							break;
						}
					}
				}

				if (!importConfig.getPossibleMatch().isOverwriteMine() && importedItem.getExisting() == null) {
					//Try to find a possible match if overwrite was not selected.
					for (Resolver<?> resolver : resolvers) {
						Class<?> supportedType = ClassUtil.getFirstParameterOfGenericType(resolver.getClass(),
						    Resolver.class);
						if (supportedType.isInstance(importedItem.getIncoming())) {
							@SuppressWarnings("unchecked")
							Object possibleMatch = ((Resolver<Object>) resolver)
							        .getPossibleMatch(importedItem.getIncoming());

							if (possibleMatch != null) {
								if (possibleMatch == importedItem.getIncoming()) {
									throw new IllegalStateException("Resolver " + resolver.getClass()
									        + " failure! Incoming and existing must not be the same: "
									        + possibleMatch.toString());
								}
								importedItem.setExisting(possibleMatch);
								importedItem.setImportType(importConfig.getPossibleMatch());
								importedItem.setAssessed(!importConfig.isConfirmPossibleMatch());
								break;
							} else {
								importedItem.setImportType(importConfig.getNoMatch());
								importedItem.setAssessed(!importConfig.isConfirmNoMatch());
							}
						}
					}
				}
			}

			//Assess not yet assessed items if dates equal
			if (!importedItem.isAssessed() && importedItem.getExisting() != null) {
				boolean datesEqual = false;

				if (!importedItem.isImported()) {
					Date incomingChanged = Handler.getDateChanged(importedItem.getIncoming());
					Date existingChanged = Handler.getDateChanged(importedItem.getExisting());

					datesEqual = ignoreTimezoneEquals(incomingChanged, existingChanged);
				} else {
					Date incomingChanged = Handler.getDateChanged(importedItem.getIncoming());
					Date incomingLastChanged = importedItem.getDateChanged();

					datesEqual = ignoreTimezoneEquals(incomingChanged, incomingLastChanged);

					if (datesEqual) {
						//Incoming hasn't changed. We need to check if existing changed since the last import.
						Date existingChanged = Handler.getDateChanged(importedItem.getExisting());
						Date imported = importedItem.getDateImported();

						if (existingChanged != null && imported != null) {
							datesEqual = imported.before(existingChanged);
						} else {
							datesEqual = false;
						}
					}
				}

				importedItem.setAssessed(datesEqual);
			}

			if (importedItem.getExisting() != null) {
				Class<Object> incomingClass = ClassUtil.getDeproxiedClass(importedItem.getIncoming());
				Class<Object> existingClass = ClassUtil.getDeproxiedClass(importedItem.getExisting());
				if (!incomingClass.equals(existingClass)) {
					importedItem.setAssessed(false);
				}
			}
		}
	}

	private boolean ignoreTimezoneEquals(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return false;
		}

		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);

		calendar1.set(Calendar.HOUR, 0);
		calendar2.set(Calendar.HOUR, 0);

		return calendar1.equals(calendar2);
	}

}
