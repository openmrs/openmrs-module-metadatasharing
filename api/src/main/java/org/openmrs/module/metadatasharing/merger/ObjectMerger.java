package org.openmrs.module.metadatasharing.merger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openmrs.OpenmrsObject;
import org.openmrs.User;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.visitor.ObjectVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(MetadataSharingConsts.MODULE_ID + ".ObjectMerger")
public class ObjectMerger {
	
	@Autowired
	private ObjectVisitor visitor;
	
	@Autowired
	private ComparisonEngine comparisonEngine;
	
	public void merge(final ImportedItem importedItem, final Map<OpenmrsObject, OpenmrsObject> incomingToExisting) {
		final Object incoming = importedItem.getIncoming();
		if (importedItem.getExisting() == null) {
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
			if (importedItem.getImportType().isPreferTheirs() || importedItem.getImportType().isPreferMine()) {
				//Copy properties from the incoming object to the existing object
				final Object existing = importedItem.getExisting();
				
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
						} else if (!importedItem.getImportType().isPreferMine()) {
							if (incomingField instanceof OpenmrsObject) {
								OpenmrsObject existingField = incomingToExisting.get(incomingField);
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
	
	public Map<OpenmrsObject, OpenmrsObject> getIncomingToExisting(Collection<ImportedItem> importedItems) {
		Map<OpenmrsObject, OpenmrsObject> mappings = new HashMap<OpenmrsObject, OpenmrsObject>();
		
		for (ImportedItem importedItem : importedItems) {
			if (importedItem.getIncoming() instanceof OpenmrsObject) {
				if (importedItem.getExisting() != null) {
					mappings.put((OpenmrsObject) importedItem.getIncoming(), (OpenmrsObject) importedItem.getExisting());
				}
			}
		}
		
		return mappings;
	}
}
