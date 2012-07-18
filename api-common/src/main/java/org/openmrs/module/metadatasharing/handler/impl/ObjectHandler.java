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
package org.openmrs.module.metadatasharing.handler.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.User;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.handler.MetadataMergeHandler;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.openmrs.module.metadatasharing.merger.ComparisonEngine;
import org.openmrs.module.metadatasharing.visitor.ObjectVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Makes sure that priority dependencies returned by the getDependencies method (if exists) on the
 * give object are saved first.
 */
@Component("metadatasharing.ObjectHandler")
public class ObjectHandler implements MetadataPriorityDependenciesHandler<Object>, MetadataMergeHandler<Object> {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private ObjectVisitor visitor;
	
	@Autowired
	private ComparisonEngine comparisonEngine;
	
	@Override
	public int getPriority() {
	    return 0;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler#getPriorityDependencies(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getPriorityDependencies(Object object) {
		List<Object> dependencies = new ArrayList<Object>();
		try {
			Method method = object.getClass().getMethod("getDependencies");
			if (method != null) {
				Collection<Object> items = (Collection<Object>) method.invoke(object);
				if (items != null) {
					dependencies.addAll(items);
				}
			}	
		}
		catch (NoSuchMethodException e) {
			log.debug("No priority dependencies for " + object.toString());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return dependencies;
	}

	@Override
    public void merge(final Object existing, final Object incoming, final ImportType importType, final Map<Object, Object> incomingToExisting) {
		if (existing == null) {
			//Replace incoming object's fields with existing objects
			visitor.visitFields(incoming, false, new ObjectVisitor.FieldVisitor() {
				
				@Override
				public void visit(String fieldName, Class<?> type, Class<?> definedIn, Object incomingField) {
					if (incomingField instanceof OpenmrsObject) {
						Object existing = incomingToExisting.get(incomingField);
						if (existing != null) {
							visitor.writeField(incoming, fieldName, existing, definedIn);
						}
					} else if (incomingField instanceof Collection) {
						@SuppressWarnings("unchecked")
						Collection<Object> collection = (Collection<Object>) incomingField;
						Iterator<Object> it = collection.iterator();
						
						Collection<Object> replacements = new ArrayList<Object>();
						while (it.hasNext()) {
							incomingField = it.next();
							Object existing = incomingToExisting.get(incomingField);
							if (existing != null) {
								replacements.add(existing);
								it.remove();
							}
						}
						collection.addAll(replacements);
					}
				}
			});
		} else {
			if (importType.isPreferTheirs() || importType.isPreferMine()) {
				//Copy properties from the incoming object to the existing object				
				Integer id = Handler.getId(existing);
				
				visitor.visitFields(incoming, false, new ObjectVisitor.FieldVisitor() {
					
					@Override
					public void visit(String fieldName, Class<?> type, Class<?> definedIn, Object incomingField) {
						if (incomingField instanceof Collection) {
							Object existingField = visitor.readField(existing, fieldName, definedIn);
							if (existingField instanceof Collection) {
								@SuppressWarnings("unchecked")
								Collection<Object> existingCollection = (Collection<Object>) existingField;
								@SuppressWarnings("unchecked")
								Collection<Object> incomingCollection = (Collection<Object>) incomingField;
								
								for (Object incomingElement : incomingCollection) {
									Object existing = incomingToExisting.get(incomingElement);
									
									if (existing == null) {
										boolean missing = true;
										for (Object existingElement : existingCollection) {
											if (comparisonEngine.equal(incomingElement, existingElement, incomingToExisting)) {
												missing = false;
												break;
											}
										}
										
										if (missing) {
											existingCollection.add(incomingElement);
										}
									}
								}
							}
						} else if (!importType.isPreferMine()) {
							if (incomingField instanceof OpenmrsObject) {
								Object existingField = incomingToExisting.get(incomingField);
								if (existingField != null) {
									visitor.writeField(existing, fieldName, existingField, definedIn);
								} else {
									visitor.writeField(existing, fieldName, incomingField, definedIn);
								}
							} else {
								if (!fieldName.equals("uuid") && !(incomingField instanceof User)) {
									visitor.writeField(existing, fieldName, incomingField, definedIn);
								}
							}
						}
					}
				});
				
				if (id != null) {
					Handler.setId(existing, id);
				}
			}
		}
    }
	
}
