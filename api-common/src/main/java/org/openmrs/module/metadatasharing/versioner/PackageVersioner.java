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
package org.openmrs.module.metadatasharing.versioner;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.Item;

/**
 * The class used for creating new version of packages. Every instance is connected with the given
 * package<br>
 * It should be retrieved by calling: MetadataSharing.getInstance().getPackageVersioner(...);
 */
public abstract class PackageVersioner {
	
	protected ExportedPackage pack;
	
	public PackageVersioner(ExportedPackage pack) {
		this.pack = pack;
	}
	
	/**
	 * Returns a new {@link ExportedPackage} instance which represents a new version of the package
	 * associated with this versioner
	 * 
	 * @param scratch are we creating package from scratch?
	 * @return new version of the given package
	 * @should set appropriate version
	 * @should copy name, description and group
	 * @should set published to false
	 */
	public abstract ExportedPackage createNewPackageVersion(Boolean scratch);
	
	/**
	 * @return the package items map created by getting from the database objects associated with
	 *         package header items
	 * @should return package items that are in the database
	 */
	public abstract Map<String, Set<Item>> getPackageItems();
	
	/**
	 * @return the map of items that were not found in the database. Key is typeid and value is a
	 *         set of objects items with this typeid
	 * @should return missing items
	 */
	public abstract Map<String, Set<Item>> getMissingItems();
	
	/**
	 * @return the list of classes that weren't found during creation of package items map
	 * @should return missing classes
	 */
	public abstract List<String> getMissingClasses();
	
	/**
	 * @return set indicating which typeids are completely added to the package items
	 */
	public abstract Set<String> getCompleteTypes();
}
