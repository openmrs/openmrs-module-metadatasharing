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

import java.util.Collection;

import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.ImportedItemsStats;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.task.Task;
import org.openmrs.module.metadatasharing.task.TaskException;

/**
 *
 */
public abstract class PackageImporter extends PackageContainer {
	
	public abstract ImportConfig getImportConfig();
	
	public abstract ImportedPackage getImportedPackage();
	
	public abstract Collection<ImportedItem> getImportedItems(int part);
	
	public abstract ImportedItemsStats getImportedItemsStats(int part);
	
	public abstract int getPartsCount();
	
	public abstract void saveState();
	
	public abstract void clearState();
	
	public abstract void setImportConfig(ImportConfig importConfig);
	
	/**
	 * Imports the package.
	 * 
	 * @throws TaskException
	 */
	public abstract void importPackage() throws TaskException;
	
	/**
	 * Schedules package import.
	 * <p>
	 * The import will be executed in a separate thread.
	 * 
	 * @throws TaskException
	 */
	public abstract Task schedulePackageImport() throws TaskException;
}
