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
package org.openmrs.module.metadatasharing.wrapper.impl;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.openmrs.OpenmrsObject;
import org.openmrs.User;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.ImportedItemsStats;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.Package;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.task.Task;
import org.openmrs.module.metadatasharing.task.TaskException;
import org.openmrs.module.metadatasharing.task.impl.ImportPackageTask;
import org.openmrs.module.metadatasharing.util.VersionConverter;
import org.openmrs.module.metadatasharing.visitor.ObjectVisitor;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.OpenmrsConstants;

/**
 *
 */
public class PackageImporterImpl extends PackageImporter {
	
	private final ImportedPackage importedPackage;
	
	private final Map<Integer, List<ImportedItem>> importedItems = new ConcurrentHashMap<Integer, List<ImportedItem>>();
	
	private final Map<Integer, WeakReference<List<ImportedItem>>> importedItemsCache = new ConcurrentHashMap<Integer, WeakReference<List<ImportedItem>>>();
	
	public PackageImporterImpl() {
		importedPackage = new ImportedPackage();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageImporter#getImportConfig()
	 */
	@Override
	public ImportConfig getImportConfig() {
		return importedPackage.getImportConfig();
	}
	
	/**
	 * @param importConfig the importConfig to set
	 */
	@Override
	public void setImportConfig(ImportConfig importConfig) {
		if (!importConfig.isPrevious()) {
			MetadataSharing.getService().purgePreviousAssessments();
		}
		importedPackage.setImportConfig(importConfig);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageContainer#getPackage()
	 */
	@Override
	public Package getPackage() {
		return importedPackage;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageImporter#getImportedPackage()
	 */
	@Override
	public ImportedPackage getImportedPackage() {
		return importedPackage;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageImporter#getImportedItems(int)
	 */
	@Override
	public Collection<ImportedItem> getImportedItems(int part) {
		List<ImportedItem> list = importedItems.get(part);
		if (list != null) {
			return list;
		}
		
		//Try luck with the cache
		WeakReference<List<ImportedItem>> listRef = importedItemsCache.get(part);
		if (listRef != null) {
			if (listRef.get() != null) {
				//Move the requested part back from the cache to prevent garbage collecting
				importedItems.put(part, listRef.get());
				importedItemsCache.remove(part);
				return listRef.get();
			}
		}
		
		try {
			list = createImportedItems(part);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		importedItems.put(part, list);
		
		return list;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageImporter#getImportedItemsStats(int)
	 */
	@Override
	public ImportedItemsStats getImportedItemsStats(int part) {
		return new ImportedItemsStats(getImportedItems(part), part);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageImporter#getPartsCount()
	 */
	@Override
	public int getPartsCount() {
		try {
			return importedPackage.getSerializedPackage().getMetadata().length;
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageImporter#saveState()
	 */
	@Override
	public void saveState() {
		//Save and move all parts to cache
		for (Entry<Integer, List<ImportedItem>> list : importedItems.entrySet()) {
			MetadataSharing.getService().persistImportedItems(list.getValue());
			importedItemsCache.put(list.getKey(), new WeakReference<List<ImportedItem>>(list.getValue()));
		}
		importedItems.clear();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageImporter#clearState()
	 */
	@Override
	public void clearState() {
		importedItems.clear();
		importedItemsCache.clear();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageImporter#importPackage()
	 */
	@Override
	public void importPackage() throws TaskException {
		Task task = new ImportPackageTask(this);
		MetadataSharing.getService().executeTask(task);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageImporter#schedulePackageImport()
	 */
	@Override
	public Task schedulePackageImport() throws TaskException {
		Task task = new ImportPackageTask(this);
		MetadataSharing.getInstance().getTaskEngine().scheduleTask(task);
		return task;
	}
	
	/**
	 * Auto generated method comment
	 * 
	 * @param part
	 * @return
	 * @throws IOException
	 * @throws SerializationException
	 */
	private List<ImportedItem> createImportedItems(int part) throws IOException, SerializationException {
		String xml = importedPackage.getSerializedPackage().getMetadata()[part];
		
		Map<ImportedItem, ImportedItem> tmpImportItems = new LinkedHashMap<ImportedItem, ImportedItem>();
		
		@SuppressWarnings("unchecked")
		List<Object> importedMetadata = MetadataSharing.getInstance().getMetadataSerializer()
		        .deserialize(xml, List.class, importedPackage.getOpenmrsVersion());
		for (Object each : importedMetadata) {
			ImportedItem importedItem = getExistingOrNewImportedItem(each);
			tmpImportItems.put(importedItem, importedItem);
		}
		
		// We need to copy importItems to a new list as we'll be adding to importItems while iterating
		List<ImportedItem> unresolvedItems = new ArrayList<ImportedItem>(tmpImportItems.keySet());
		for (ImportedItem unresolvedItem : unresolvedItems) {
			resolveRelatedImportItems(unresolvedItem, tmpImportItems);
		}
		
		MetadataSharing.getInstance().getResolverEngine()
		        .resolve(tmpImportItems.keySet(), importedPackage.getImportConfig());
		
		List<ImportedItem> resolvedItems = new ArrayList<ImportedItem>(tmpImportItems.keySet());
		
		MetadataSharing.getService().persistImportedItems(resolvedItems);
		
		return resolvedItems;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageContainer#loadPackage(org.openmrs.module.metadatasharing.Package)
	 */
	@Override
	public void loadPackage(Package other) throws IOException {
		super.loadPackage(other);
		
		convertMetadata();
	}
	
	/**
	 * Converts metadata to the current OpenMRS version.
	 * 
	 * @throws IOException
	 */
	private void convertMetadata() throws IOException {
		VersionConverter converter = new VersionConverter();
		String[] metadata = getPackage().getSerializedPackage().getMetadata();
		for (int i = 0; i < metadata.length; i++) {
			if (getPackage().getOpenmrsVersion() != null) {
				try {
					metadata[i] = converter.convert(metadata[i], getPackage().getOpenmrsVersion(),
					    OpenmrsConstants.OPENMRS_VERSION);
				}
				catch (SerializationException e) {
					throw new IOException(e);
				}
			}
		}
	}
	
	private void resolveRelatedImportItems(final ImportedItem importedItem,
	                                       final Map<ImportedItem, ImportedItem> visitedItems) {
		List<Object> priorityDependencies = Handler.getPriorityDependencies(importedItem.getIncoming());
		for (Object priorityDependency : priorityDependencies) {
			addRelatedItemToImportedItem(importedItem, priorityDependency, visitedItems);
		}
		
		MetadataSharing.getInstance().getObjectVisitor()
		        .visitFields(importedItem.getIncoming(), false, new ObjectVisitor.FieldVisitor() {
			        
			        @Override
			        public void visit(String name, Class<?> type, Class<?> definedIn, Object value) {
				        visitMetadata(value);
				        
				        if (value instanceof Collection) {
					        for (Object object : (Collection<?>) value) {
						        visitMetadata(object);
					        }
				        }
			        }
			        
			        private void visitMetadata(Object object) {
				        addRelatedItemToImportedItem(importedItem, object, visitedItems);
			        }
		        });
	}
	
	private void addRelatedItemToImportedItem(final ImportedItem importedItem, Object relatedItem,
	                                          final Map<ImportedItem, ImportedItem> visitedItems) {
		if (relatedItem instanceof OpenmrsObject && !(relatedItem instanceof User)) {
			ImportedItem item = getExistingOrNewImportedItem(relatedItem);
			
			ImportedItem visitedItem = visitedItems.put(item, item);
			if (visitedItem == null) {
				importedItem.addRelatedItem(item);
				
				resolveRelatedImportItems(item, visitedItems);
			} else {
				importedItem.addRelatedItem(visitedItem);
			}
		}
	}
	
	private ImportedItem getExistingOrNewImportedItem(Object item) {
		ImportedItem importItem = MetadataSharing.getService().getImportedItemByUuid(item.getClass(), Handler.getUuid(item));
		
		if (importItem == null) {
			importItem = new ImportedItem(item);
		} else {
			importItem.setIncoming(item);
			
			Object existing = null;
			
			if (importItem.getExistingUuid() != null) {
				existing = Handler.getItemByUuid(item.getClass(), importItem.getExistingUuid());
			} else {
				existing = Handler.getItemByUuid(item.getClass(), Handler.getUuid(item));
			}
			
			if (existing != null) {
				importItem.setExisting(existing);
			} else {
				importItem.setExistingUuid(null);
				importItem.setImportType(ImportType.CREATE);
			}
		}
		return importItem;
	}
	
}
