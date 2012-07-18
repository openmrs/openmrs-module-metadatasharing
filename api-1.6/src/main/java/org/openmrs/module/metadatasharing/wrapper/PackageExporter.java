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
package org.openmrs.module.metadatasharing.wrapper;

import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.Item;
import org.openmrs.module.metadatasharing.task.Task;
import org.openmrs.module.metadatasharing.task.TaskException;

/**
 * Exports a package.
 */
public abstract class PackageExporter extends PackageContainer {
	
	/**
	 * @return the exportedPackage
	 */
	public abstract ExportedPackage getExportedPackage();
	
	/**
	 * Updates the header.
	 * 
	 * @throws TaskException
	 */
	public abstract void updatePackageHeader() throws TaskException;
	
	/**
	 * Exports the package. The package will not be persisted in the database.
	 * <p>
	 * Use {@link #schedulePackageExport()} to export a package in a separate thread and persist it
	 * in the database.
	 */
	public abstract ExportedPackage exportPackage() throws TaskException;
	
	/**
	 * Schedules package export.
	 * <p>
	 * The export will be executed in a separate thread.
	 * 
	 * @throws TaskException
	 */
	public abstract Task schedulePackageExport() throws TaskException;
	
	/**
	 * Converts the given item to {@link Item} and adds it to the underlying package.
	 * 
	 * @param item
	 * @return <code>true</code> if the item was added
	 */
	public boolean addItem(Object item) {
		return addItem(Item.valueOf(item));
	}
	
	/**
	 * Removes the given item from the underlying package.
	 * 
	 * @param item
	 * @return <code>true</code> if the item was added
	 */
	public boolean removeItem(Object item) {
		return removeItem(Item.valueOf(item));
	}
	
	/**
	 * Adds the given item to the underlying package.
	 * 
	 * @param item
	 * @return <code>true</code> if the item was added
	 */
	public boolean addItem(Item item) {
		return getExportedPackage().getItems().add(item);
	}
	
	/**
	 * Removes the given item from the underlying package.
	 * 
	 * @param item
	 * @return <code>true</code> if the item was added
	 */
	public boolean removeItem(Item item) {
		return getExportedPackage().getItems().remove(item);
	}
}
