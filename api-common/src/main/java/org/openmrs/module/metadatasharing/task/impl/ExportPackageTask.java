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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.Item;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.Package;
import org.openmrs.module.metadatasharing.SerializedPackage;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.task.Task;
import org.openmrs.module.metadatasharing.task.TaskException;
import org.openmrs.module.metadatasharing.task.TaskType;
import org.openmrs.module.metadatasharing.visitor.ObjectVisitor;
import org.openmrs.serialization.SerializationException;
import org.openmrs.validator.ValidateUtil;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Exports package.
 */
public class ExportPackageTask extends Task {

	private final static int SUBPACKAGE_SIZE = 1000;

	private final ExportedPackage exportedPackage;

	private final boolean persist;

	public ExportPackageTask(ExportedPackage exportedPackage, boolean persist) {
		this.exportedPackage = exportedPackage;
		this.persist = persist;
		setType(TaskType.EXPORT);
	}

	/**
	 * @return the exportedPackage
	 */
	public ExportedPackage getExportedPackage() {
		return exportedPackage;
	}

	/**
	 * @see org.openmrs.module.metadatasharing.task.Task#getPackage()
	 */
	@Override
	public Package getPackage() {
		return exportedPackage;
	}

	/**
	 * @see org.openmrs.module.metadatasharing.task.Task#execute()
	 */
	@Override
	public void execute() throws TaskException {
		try {
			log("Export started");

			Errors errors = new BindException(exportedPackage, "package");
			MetadataSharing.getInstance().getPackageValidator().validate(exportedPackage, errors);
			if (errors.hasErrors()) {
				throw new TaskException("Failed to validate with reason: "
				        + errors.getAllErrors().iterator().next().toString());
			}

			exportedPackage.getRelatedItems().clear();

			List<String> metadata = new ArrayList<String>();

			List<Item> packageItems = new ArrayList<Item>(exportedPackage.getItems());
			for (int from = 0; from < packageItems.size(); from += SUBPACKAGE_SIZE) {
				int to = from + SUBPACKAGE_SIZE;
				if (to > packageItems.size()) {
					to = packageItems.size();
				}

				log("Exporting subpackage [items from " + from + " to " + to + " of " + packageItems.size() + "]");
				metadata.add(exportSubpackage(packageItems.subList(from, to)));
			}

			log("Serialzing header");
			StringBuilder header = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			header.append(MetadataSharing.getInstance().getMetadataSerializer().serialize(exportedPackage)).append("\n");

			getExportedPackage().setSerializedPackage(
			    new SerializedPackage(header.toString(), metadata.toArray(new String[0])));

			if (persist) {
				log("Saving package");
				MetadataSharing.getService().saveExportedPackage(exportedPackage);
			}

			log("Export completed. See the <a href=\"../export/details.form?group=" + exportedPackage.getGroupUuid()
			        + "\">package</a>");
		}
		catch (TaskException e) {
			throw e;
		}
		catch (Exception e) {
			String msg = "Export failed";
			throw new TaskException(msg, e);
		}
	}

	private String exportSubpackage(List<Item> packageItems) throws SerializationException {
		List<Object> explicitItems = new ArrayList<Object>();

		log("Validating added items");
		for (Item packageItem : packageItems) {
			Object item = Handler.getItemByUuid(packageItem.getContainedClass(), packageItem.getUuid());

			if (validateItem(item)) {
				addLocalMappingIfConcept(item);
			}
			explicitItems.add(item);
		}

		log("Resolving and validating related items");
		for (Object explicitItem : explicitItems) {
			resolveRelatedItems(explicitItem);
		}

		if (!getErrors().isEmpty()) {
			throw new APIException("Items failed validation");
		}

		Context.flushSession();

		log("Serializing items");
		StringBuilder subpackage = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		subpackage.append(MetadataSharing.getInstance().getMetadataSerializer().serialize(explicitItems)).append("\n");

		// Clean up
		Context.flushSession();
		Context.clearSession();

		return subpackage.toString();
	}

	private boolean validateItem(Object item) {
		try {
			ValidateUtil.validate(item);
		}
		catch (Exception e) {
			Integer itemId = Handler.getId(item);
			String id = (itemId != null) ? itemId.toString() : "";
			log(Handler.getRegisteredType(item) + " [id:" + id + " uuid:" + Handler.getUuid(item) + "] failed validation", e);
			return false;
		}

		return true;
	}

	/**
	 * Adds local mapping to concept here if the admin so desires. <br/>
	 * Only an item which is Concept will get mapping <br/>
	 *
	 * @see MetadataSharing#isAddLocalMappings()
	 * @param object
	 * @should add local mapping to concept if admin desires
	 * @should not add local mapping to concept if admin desires
	 */
	protected void addLocalMappingIfConcept(Object object) {
		if (object instanceof Concept) {
			if (Context.getService(MetadataMappingService.class).isAddLocalMappingToConceptOnExport()) {
				Context.getService(MetadataMappingService.class).addLocalMappingToConcept((Concept) object);
			}
		}
	}

	private void resolveRelatedItems(final Object item) {
		Context.flushSession(); //Let's save memory and make it easier to find broken data.

		List<Object> priorityDependencies = Handler.getPriorityDependencies(item);

		List<Object> itemsToVisit = new ArrayList<Object>();
		itemsToVisit.add(item);
		itemsToVisit.addAll(priorityDependencies);

		for (Object itemToVisit : itemsToVisit) {
			MetadataSharing.getInstance().getObjectVisitor().visitFields(itemToVisit, true, new ObjectVisitor.FieldVisitor() {

				@Override
				public void visit(String name, Class<?> type, Class<?> definedIn, Object value) {
					visitMetadata(value);
				}
			});
        }
	}

	private void visitMetadata(Object object) {
		if (object instanceof OpenmrsObject) {
			Item packageItem = Item.valueOf(object);
			if (!exportedPackage.getItems().contains(packageItem)
			        && exportedPackage.getRelatedItems().add(packageItem)) {
				if (validateItem(object)) {
					addLocalMappingIfConcept(object);
				}

				resolveRelatedItems(object);
			}
		} else if (object instanceof Collection) {
			for (Object element : (Collection<?>) object) {
				visitMetadata(element);
			}
		}
	}

}
