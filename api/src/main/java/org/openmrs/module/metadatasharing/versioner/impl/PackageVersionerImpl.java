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
package org.openmrs.module.metadatasharing.versioner.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.Item;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.reflection.ClassUtil;
import org.openmrs.module.metadatasharing.versioner.PackageVersioner;

/**
 * The default implementation of {@link PackageVersioner}
 */
public class PackageVersionerImpl extends PackageVersioner {
	
	/**
	 * The package items retrieved from the database
	 */
	private Map<String, Set<Item>> packageItems;
	
	/**
	 * List of classes that were not found
	 */
	private List<String> missingClasses;
	
	/**
	 * Map of items that were not found in the database
	 */
	private Map<String, Set<Item>> missingItems;
	
	/**
	 * List of types of which all types are added;
	 */
	private Set<String> completeTypes;
	
	/**
	 * Initializes the PackageVersioner by the given ExportedPackage. This means it adds the items
	 * from the package into packageItems, missingClasses or missingItems collections
	 * 
	 * @param pack the package to initialize versioner
	 */
	public PackageVersionerImpl(ExportedPackage pack) {
		super(pack);
		packageItems = new HashMap<String, Set<Item>>();
		missingClasses = new LinkedList<String>();
		missingItems = new HashMap<String, Set<Item>>();
		completeTypes = new HashSet<String>();
		
		if (pack.getItems() != null) {
			for (Item item : pack.getItems()) {
				Class<?> clazz = ClassUtil.loadClass(item.getClassname());
				if (clazz == null) {
					missingClasses.add(item.getClassname());
				} else {
					processItem(item, clazz);
				}
				
			}
			
			for (Entry<String, Set<Item>> entry : packageItems.entrySet()) {
				int allItems = Handler.getItemsCount(Handler.getRegisteredClass(entry.getKey()), true, null);
				if (allItems == entry.getValue().size()) {
					completeTypes.add(entry.getKey());
				}
			}
		}
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.versioner.PackageVersioner#createNewPackageVersion(java.lang.Boolean)
	 */
	@Override
	public ExportedPackage createNewPackageVersion(Boolean fromScratch) {
		ExportedPackage toReturn = new ExportedPackage();
		MetadataSharingService service = Context.getService(MetadataSharingService.class);
		ExportedPackage latest = service.getLatestExportedPackageByGroup(pack.getGroupUuid());
		
		toReturn.setName(pack.getName());
		toReturn.setDescription(pack.getDescription());
		toReturn.setGroupUuid(pack.getGroupUuid());
		toReturn.setVersion(latest.getVersion() + 1);
		toReturn.setPublished(pack.isPublished());
		return toReturn;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.versioner.PackageVersioner#getPackageItems()
	 */
	@Override
	public Map<String, Set<Item>> getPackageItems() {
		return packageItems;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.versioner.PackageVersioner#getMissingItems()
	 */
	@Override
	public Map<String, Set<Item>> getMissingItems() {
		return missingItems;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.versioner.PackageVersioner#getMissingClasses()
	 */
	@Override
	public List<String> getMissingClasses() {
		return missingClasses;
	}
	
	/**
	 * Processes the given item which represents object of class clazz.<br>
	 * This means, that it gets the associated object from the database and adds it to packageItems
	 * map
	 * 
	 * @param item the item to porcess
	 * @param clazz the class of the object represented by the item
	 */
	private void processItem(Item item, Class<?> clazz) {
		Object obj = Handler.getItemByUuid(Handler.getRegisteredClass(clazz), item.getUuid());
		if (obj == null) {
			String type = Handler.getRegisteredType(clazz);
			if (!missingItems.containsKey(clazz)) {
				missingItems.put(type, new HashSet<Item>());
			}
			missingItems.get(type).add(item);
		} else {
			String type = Handler.getRegisteredType(obj);
			if (!packageItems.containsKey(type)) {
				packageItems.put(type, new HashSet<Item>());
			}
			packageItems.get(type).add(Item.valueOf(obj));
		}
		
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.versioner.PackageVersioner#getCompleteTypes()
	 */
	@Override
	public Set<String> getCompleteTypes() {
		return completeTypes;
	}
}
