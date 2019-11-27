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
package org.openmrs.module.metadatasharing.api.db;

import java.util.Collection;
import java.util.List;

import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.ExportedPackageSummary;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;

/**
 * Database methods for {@link MetadataSharingService}
 * 
 * @see org.openmrs.api.context.Context
 * @see MetadataSharingService
 */
public interface MetadataSharingDAO {
	
	/**
	 * @see MetadataSharingService#getLatestExportedPackageByGroup(String)
	 */
	ExportedPackage getLatestExportedPackage(String group);
	
	/**
	 * @see MetadataSharingService#getExportedPackageById(Integer)
	 */
	ExportedPackage getExportedPackage(Integer packageId);
	
	/**
	 * @see MetadataSharingService#purgeExportedPackage(ExportedPackage)
	 */
	void purgeExportedPackage(ExportedPackage pack);
	
	/**
	 * @see MetadataSharingService#saveExportedPackage(ExportedPackage)
	 */
	ExportedPackage saveExportedPackage(ExportedPackage pack);
	
	/**
	 * @see MetadataSharingService#getAllExportedPackages()
	 */
	List<ExportedPackage> getAllExportedPackages();

	/**
	 * @return all export package ids
	 * @since 1.6
	 */
	List<ExportedPackageSummary> getAllExportedPackageSummaries();

	/**
	 * @see MetadataSharingService#getExportedPackagesByGroup(String)
	 */
	List<ExportedPackage> getExportedPackageGroup(String group);
	
	/**
	 * @see MetadataSharingService#getImportedItemByUuid(Class, String)
	 */
	ImportedItem getImportItem(Class<?> type, String uuid);
	
	/**
     * Auto generated method comment
     * 
     * @param importItem
     * @return
     */
    ImportedItem persistImportItem(ImportedItem importItem);
	
	/**
	 * Saves imported items.
	 * 
	 * @param importedItems
	 * @return the collection of imported items
	 */
	Collection<ImportedItem> persistImportItems(Collection<ImportedItem> importedItems);
	
	/**
	 * @see MetadataSharingService#getLatestPublishedPackageByGroup(String)
	 */
	ExportedPackage getLatestPublishedPackage(String group);
	
	/**
	 * @see MetadataSharingService#getPublishedPackageByGroup(String, Integer)
	 */
	ExportedPackage getPublishedPackage(String group, Integer version);
	
	/**
	 * @see MetadataSharingService#getImportedPackageById(Integer)
	 */
	ImportedPackage getImportedPackageById(Integer id);
	
	/**
	 * @see MetadataSharingService#getImportedPackageByGroup(String)
	 */
	ImportedPackage getImportedPackageByGroup(String group);
	
	/**
	 * @see MetadataSharingService#saveImportedPackage(ImportedPackage)
	 */
	ImportedPackage saveImportedPackage(ImportedPackage importedPackage);
	
	/**
	 * @see MetadataSharingService#getAllImportedPackages()
	 */
	List<ImportedPackage> getAllImportedPackages();
	
	/**
	 * @see MetadataSharingService#deleteImportedPackage(ImportedPackage)
	 */
	void deleteImportedPackage(ImportedPackage importedPackage);

	/**
     * @see MetadataSharingService#purgePreviousAssessments()
     */
    void purgePreviousAssessments();

	
}
