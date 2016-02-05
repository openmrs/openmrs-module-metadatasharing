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
package org.openmrs.module.metadatasharing.task.impl;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openmrs.OpenmrsObject;
import org.openmrs.aop.RequiredDataAdvice;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.handler.SaveHandler;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.Item;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.Package;
import org.openmrs.module.metadatasharing.api.ValidationException;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.handler.MetadataMergeHandler;
import org.openmrs.module.metadatasharing.merger.ConvertUtil;
import org.openmrs.module.metadatasharing.model.validator.ValidateCustomUtil;
import org.openmrs.module.metadatasharing.task.Task;
import org.openmrs.module.metadatasharing.task.TaskException;
import org.openmrs.module.metadatasharing.task.TaskType;
import org.openmrs.module.metadatasharing.util.ImportUtil;
import org.openmrs.module.metadatasharing.wrapper.ObjectWrapper;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 *
 */
public class ImportPackageTask extends Task {

	private final PackageImporter packageImporter;

	public ImportPackageTask(PackageImporter packageImporter) {
		this.packageImporter = packageImporter;
		setType(TaskType.IMPORT);
	}

	/**
	 * @see org.openmrs.module.metadatasharing.task.Task#getPackage()
	 */
	@Override
	public Package getPackage() {
		return packageImporter.getPackage();
	}

	/**
	 * @see org.openmrs.module.metadatasharing.task.Task#execute()
	 */
	@Override
	public void execute() throws TaskException {
		try {
			log("Saving import state");
			packageImporter.saveState();
			packageImporter.clearState();

			int partsCount = packageImporter.getPartsCount();

			for (int i = 0; i < partsCount; i++) {
				log("Importing subpackage " + (i + 1) + " of " + partsCount);

				log("Resolving related items");
				importItems(packageImporter.getImportedItems(i));
			}

			log("Updating mappings");
			Date dateImported = new Date();
			for (Item item : packageImporter.getImportedPackage().getItems()) {
				updateItemMapping(item, dateImported);
			}
			for (Item item : packageImporter.getImportedPackage().getRelatedItems()) {
				updateItemMapping(item, dateImported);
			}
			packageImporter.getImportedPackage().setDateImported(dateImported);
			MetadataSharing.getService().saveImportedPackage(packageImporter.getImportedPackage());

			//Clean-up session after import. Otherwise we would have a lot of objects in session,
			//which slows Hibernate down.
			Context.flushSession();
			Context.clearSession();

			log("Import completed");
		}
		catch (Exception e) {
			String msg = "Import failed";
			throw new TaskException(msg, e);
		}
	}

	private void updateItemMapping(Item item, Date dateImported) {
		if (item.getContainedClass() == null) {
			return;
		}

	    ImportedItem importedItem = MetadataSharing.getService().getImportedItemByUuid(item.getContainedClass(),
	        item.getUuid());
	    if (importedItem != null) {
	    	if (importedItem.getExistingUuid() == null) {
	    		Object existing = Handler.getItemByUuid(item.getContainedClass(), item.getUuid());
	    		importedItem.setExisting(existing);
	    	}
	    	importedItem.setAssessed(false);
	    	importedItem.setDateImported(dateImported);
	    	MetadataSharing.getService().persistImportedItem(importedItem);
	    }
    }

	public void importItems(Collection<ImportedItem> importedItems) throws APIException, ValidationException {
		log("Adjusting import settings");
		Set<ImportedItem> visited = new HashSet<ImportedItem>();
		for (ImportedItem importedItem : importedItems) {
			if (!Handler.isHidden(importedItem.getIncoming())) {
				setImportTypeForHiddenObjects(importedItem, visited);
			}
		}

		for (ImportedItem importedItem : importedItems) {
			if (importedItem.getImportType().isCreate()) {
				importedItem.setExisting(null);
			}
		}

		log("Loading existing items");
		ImportUtil.reloadExistingItems(importedItems);

		log("Converting items");
		ConvertUtil.convert(importedItems);

		log("Setting audit fields");
		for (ImportedItem importedItem : importedItems) {
			importedItem.setDateChanged(Handler.getDateChanged(importedItem.getIncoming()));
		}
		for (ImportedItem importedItem : importedItems) {
			if (importedItem.getIncoming() instanceof OpenmrsObject) {
				OpenmrsObject incoming = (OpenmrsObject) importedItem.getIncoming();
				RequiredDataAdvice.recursivelyHandle(SaveHandler.class, incoming, "Persisted through metadatasharing");
			}
		}

		log("Merging items");
		Map<Object, Object> incomingToExisting = new LinkedHashMap<Object, Object>();
		for (ImportedItem importedItem : importedItems) {
			if (importedItem.getExisting() != null) {
				incomingToExisting.put(importedItem.getIncoming(), importedItem.getExisting());
			}
		}

		for (ImportedItem importedItem : importedItems) {
			//Replacing in hidden items first
			if (Handler.isHidden(importedItem.getIncoming())) {
				MetadataMergeHandler<Object> merger = MetadataSharing.getInstance().getHandlerEngine().getMergeHandler(importedItem.getIncoming());
				merger.merge(importedItem.getExisting(), importedItem.getIncoming(), importedItem.getImportType(), incomingToExisting);
			}
		}

		for (ImportedItem importedItem : importedItems) {
			if (!Handler.isHidden(importedItem.getIncoming())) {
				MetadataMergeHandler<Object> merger = MetadataSharing.getInstance().getHandlerEngine().getMergeHandler(importedItem.getIncoming());
				merger.merge(importedItem.getExisting(), importedItem.getIncoming(), importedItem.getImportType(), incomingToExisting);
			}
		}

		log("Preparing items to save");
		prepareItemsToSave(importedItems, incomingToExisting);

		log("Validating items");
		Errors errors = new BindException(importedItems, "items");
		for (ImportedItem importedItem : importedItems) {
			Object item = null;
			if (importedItem.getExisting() != null) {
				item = importedItem.getExisting();
			} else {
				item = importedItem.getIncoming();
			}

			try {
				ValidateCustomUtil.validate(item);
			}
			catch (Exception e) {
				Integer itemId = Handler.getId(item);
				String id = (itemId != null) ? itemId.toString() : "";
				log(Handler.getRegisteredType(item) + " [id:" + id + " uuid:" + Handler.getUuid(item) + "] failed validation", e);
				errors.reject("", Handler.getRegisteredType(item) + " [" + Handler.getUuid(item) + "] " + e.getMessage());
			}
		}
		if (errors.hasErrors()) {
			throw new ValidationException(errors);
		}

		Set<ObjectWrapper<Object>> savedItems = new HashSet<ObjectWrapper<Object>>();

		log("Saving items");
		for (ImportedItem importedItem : importedItems) {
			if (!importedItem.getImportType().isOmit()) {
				saveItem(importedItem, savedItems);
			}
		}

		//Clean up to save memory
		Context.flushSession();
		Context.clearSession();
	}

	/**
	 * @param importedItem
	 */
	private void setImportTypeForHiddenObjects(ImportedItem importedItem, Set<ImportedItem> visited) {
		if (!visited.add(importedItem)) {
			return;
		}
		for (ImportedItem relatedItem : importedItem.getRelatedItems()) {
			if (Handler.isHidden(relatedItem.getIncoming())) {
				if (importedItem.getImportType().isCreate()) {
					if (relatedItem.getExisting() != null) {
						relatedItem.setImportType(ImportType.PREFER_MINE);
					}
				} else {
					if (relatedItem.getExisting() != null) {
						relatedItem.setImportType(importedItem.getImportType());
					}
				}
				setImportTypeForHiddenObjects(relatedItem, visited);
			}
		}
	}

	public void prepareItemsToSave(Collection<ImportedItem> importedItems, Map<Object, Object> mappings) {
		Map<OpenmrsObject, OpenmrsObject> openmrsObjectMappings = new LinkedHashMap<OpenmrsObject, OpenmrsObject>();
		for (Entry<Object, Object> mapping : mappings.entrySet()) {
	        if (mapping.getKey() instanceof OpenmrsObject && mapping.getValue() instanceof OpenmrsObject) {
	        	openmrsObjectMappings.put((OpenmrsObject) mapping.getKey(), (OpenmrsObject) mapping.getValue());
	        }
        }

		for (ImportedItem importedItem : importedItems) {
			importedItem.initIncomingToSave(openmrsObjectMappings);
		}
	}

	public Map<OpenmrsObject, OpenmrsObject> evaluateMappings(Collection<ImportedItem> importedItems) {
		Map<OpenmrsObject, OpenmrsObject> mappings = new HashMap<OpenmrsObject, OpenmrsObject>();
		EnumSet<ImportType> map = EnumSet.of(ImportType.PREFER_MINE, ImportType.PREFER_THEIRS, ImportType.OMIT);

		for (ImportedItem importedItem : importedItems) {
			if (importedItem.getIncoming() instanceof OpenmrsObject) {
				if (map.contains(importedItem.getImportType()) && importedItem.getExisting() != null) {
					mappings.put((OpenmrsObject) importedItem.getIncoming(), (OpenmrsObject) importedItem.getExisting());
				}
			}
		}

		return mappings;
	}

	private void saveItem(ImportedItem importedItem, Set<ObjectWrapper<Object>> savedItems) throws APIException {
		Object item = (importedItem.getExisting() != null) ? importedItem.getExisting() : importedItem.getIncoming();

		if (!savedItems.add(new ObjectWrapper<Object>(item))) {
			return;
		}

		List<Object> dependencies = Handler.getPriorityDependencies(item);
		for (Object dependency : dependencies) {
			saveItem(new ImportedItem(dependency), savedItems);
		}

		log.debug("Saving " + item.getClass().getName() + " [" + Handler.getUuid(item) + "]");

		Object savedItem = Handler.saveItem(item);

		if (savedItem == null) {
			log.debug(item.getClass().getName() + " [" + Handler.getUuid(item) + "] will be saved with a parent");
		} else {
			log.debug(item.getClass().getName() + "[" + Handler.getUuid(item) + "] saved");
		}

	}

}
