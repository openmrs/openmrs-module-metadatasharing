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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptName;
import org.openmrs.OpenmrsObject;
import org.openmrs.User;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.handler.MetadataMergeHandler;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.openmrs.module.metadatasharing.merger.ComparisonEngine;
import org.openmrs.module.metadatasharing.reflection.ReplaceMethodInovker;
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

	public ObjectHandler() {
	}

	/**
	 * For use in unit tests when we can't autowire
	 * @param visitor
	 * @param comparisonEngine
	 */
	public ObjectHandler(ObjectVisitor visitor, ComparisonEngine comparisonEngine) {
		this.visitor = visitor;
		this.comparisonEngine = comparisonEngine;
	}

	@Override
	public int getPriority() {
	    return 0;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler#getPriorityDependencies(java.lang.Object)
	 */
	@Override
	public List<Object> getPriorityDependencies(Object object) {
		List<Object> dependencies = new ArrayList<Object>();
		try {
			List<Object> items = new ReplaceMethodInovker().callGetPriorityDependenciesForMetadataSharing(object);
			if (items != null) {
				dependencies.addAll(items);
			}
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
			if (importType.isPreferTheirs() || importType.isPreferMine() || importType.isOverwriteMine()) {
				//Copy properties from the incoming object to the existing object				
				Integer id = Handler.getId(existing);

				visitor.visitFields(incoming, false, new ObjectVisitor.FieldVisitor() {

					@Override
					public void visit(String fieldName, Class<?> type, Class<?> definedIn, Object incomingField) {
						if (Collection.class.isAssignableFrom(type)) {
							//If the collection field is null then do nothing
							if (incomingField == null) {
								return;
							}

							Object existingField = visitor.readField(existing, fieldName, definedIn);
							if (existingField instanceof Collection) {
								@SuppressWarnings("unchecked")
								Collection<Object> existingCollection = (Collection<Object>) existingField;
								@SuppressWarnings("unchecked")
								Collection<Object> incomingCollection = (Collection<Object>) incomingField;

								if (importType.isOverwriteMine()) {
									//We'll be skipping incoming elements if they exist.
									List<Object> incomingDiffCollection = new LinkedList<Object>(incomingCollection);

									Iterator<Object> existingCollectionIt = existingCollection.iterator();
									while(existingCollectionIt.hasNext()) {
										Object existingElement = existingCollectionIt.next();

										// note that modifications to collection members will be applied when handling the member itself as an import item
										boolean existingMissing = findAndRemoveMatchingElementByUuid(incomingDiffCollection, existingElement) == null;

										if (existingMissing) {
											if (existingElement instanceof ConceptName) {
												//ConceptNames must not be purged, but voided. See META-324.
												((ConceptName) existingElement).setVoided(true);
											} else {
												existingCollectionIt.remove();
											}
										}
									}

									//Let's add completely new incoming elements.
									for (Object incomingElement : incomingDiffCollection) {
                                        Object mappedToExisting = incomingToExisting.get(incomingElement);
                                        if (mappedToExisting != null) {
                                            existingCollection.add(mappedToExisting);
                                        } else {
                                            existingCollection.add(incomingElement);
                                        }
                                    }
								} else {
									for (Object incomingElement : incomingCollection) {
										Object existing = incomingToExisting.get(incomingElement);

										if (existing == null) {
											boolean incomingMissing = true;
											for (Object existingElement : existingCollection) {
												if (comparisonEngine.equal(incomingElement, existingElement, incomingToExisting)) {
													incomingMissing = false;
													break;
												}
											}

											if (incomingMissing) {
												existingCollection.add(incomingElement);
											}
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

					private Object findAndRemoveMatchingElementByUuid(List<Object> incomingDiffCollection, Object existingElement) {
						Iterator<Object> incomingDiffCollectionIt = incomingDiffCollection.iterator();
						while (incomingDiffCollectionIt.hasNext()) {
							Object incomingElement = incomingDiffCollectionIt.next();
							// if this was mapped to another element, look at that one instead
							if (incomingToExisting.get(incomingElement) != null) {
								incomingElement = incomingToExisting.get(incomingElement);
							}
							if (equalUuids(existingElement, incomingElement)) {
								incomingDiffCollectionIt.remove();
								return incomingElement;
							}
						}
						return null;
					}

					private boolean equalUuids(Object a, Object b) {
						try {
							return ((OpenmrsObject) a).getUuid().equals(((OpenmrsObject) b).getUuid());
						} catch (Exception ex) {
							return false;
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
