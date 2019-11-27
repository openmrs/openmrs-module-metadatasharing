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
package org.openmrs.module.metadatasharing.api;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.openmrs.annotation.Authorized;
import org.openmrs.annotation.Logging;
import org.openmrs.aop.RequiredDataAdvice;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.ExportedPackageSummary;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.task.Task;
import org.openmrs.module.metadatasharing.updater.SubscriptionUpdater;
import org.springframework.transaction.annotation.Transactional;

/**
 * Contains methods exposing the core functionality. It is an API that can be used outside of the
 * module.
 * <p>
 * Usage example:<br>
 * <code>
 * Context.getService(MetadataSharingService.class).getAllExportedPackages();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
public interface MetadataSharingService extends OpenmrsService {
	
	/**
	 * Saves the given exported package to the database.
	 * 
	 * @param pack the package to be created or updated
	 * @return package that was created or updated
	 * @throws APIException
	 * @since 1.0
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional
	ExportedPackage saveExportedPackage(ExportedPackage pack) throws APIException;
	
	/**
	 * Gets an exported package by internal identifier from the database.
	 * 
	 * @param packageId the internal package identifier
	 * @return package with the given internal identifier
	 * @throws APIException
	 * @since 1.0
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	ExportedPackage getExportedPackageById(Integer packageId) throws APIException;
	
	/**
	 * Gets the latest package version among all exported packages with given group
	 * 
	 * @param groupUuid the package's group
	 * @return the package with the latest version
	 * @throws APIException
	 * @since 1.0
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	ExportedPackage getLatestExportedPackageByGroup(String groupUuid) throws APIException;
	
	/**
	 * Deletes the given exported package from the database.
	 * 
	 * @param pack the package to be deleted
	 * @throws APIException
	 * @since 1.0
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional
	void purgeExportedPackage(ExportedPackage pack) throws APIException;
	
	/**
	 * Returns all packages from the database.
	 * 
	 * @return the list of all packages
	 * @throws APIException
	 * @since 1.0
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<ExportedPackage> getAllExportedPackages() throws APIException;


	/**
	 * @return the list of all Export Package Summaries
	 * @since 1.6
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<ExportedPackageSummary> getAllExportedPackageSummaries();
	
	/**
	 * Gets all packages with the given group
	 * 
	 * @param groupUuid the packages' group
	 * @return the list of packages with the same group
	 * @throws APIException
	 * @since 1.0
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<ExportedPackage> getExportedPackagesByGroup(String groupUuid) throws APIException;
	
	/**
	 * Returns {@link ImportedItem} by the given uuid.
	 * 
	 * @param type
	 * @param uuid
	 * @return {@link ImportedItem} or <code>null</code>
	 * @throws APIException
	 * @since 1.0
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	ImportedItem getImportedItemByUuid(Class<? extends Object> type, String uuid) throws APIException;
	
	/**
	 * Saves the given imported item to the database.
	 * <p>
	 * It is not named saveImportedItem to prevent {@link RequiredDataAdvice} from handling this
	 * method which would slow it down.
	 * 
	 * @param importedItem
	 * @return the importedItem
	 * @throws APIException
	 * @since 1.0
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Logging(ignoreAllArgumentValues=true)
	@Transactional
	ImportedItem persistImportedItem(ImportedItem importedItem) throws APIException;
	
	/**
	 * Saves the given imported items to the database.
	 * <p>
	 * It is not named saveImportedItems to prevent {@link RequiredDataAdvice} from handling this
	 * method which would slow it down.
	 * 
	 * @param importedItems
	 * @return the importedItems
	 * @throws APIException
	 * @since 1.0
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Logging(ignoreAllArgumentValues=true)
	@Transactional
	Collection<ImportedItem> persistImportedItems(Collection<ImportedItem> importedItems) throws APIException;
	
	/**
	 * Gets the latest version of the package which is published. If there is no such package,
	 * returns null.
	 * 
	 * @param groupUuid the group of the package to get
	 * @return latest version of the published package with the given group, null otherwise
	 * @throws APIException
	 * @since 1.0
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	ExportedPackage getLatestPublishedPackageByGroup(String groupUuid) throws APIException;
	
	/**
	 * Gets the ExportedPackage which is published and has the given group and version.
	 * 
	 * @param groupUuid the package's group
	 * @param version the package's version
	 * @return ExportedPackage or null if none matching instance existis in the database
	 * @since 1.0
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	ExportedPackage getPublishedPackageByGroup(String groupUuid, Integer version);
	
	/**
	 * Gets from the database imported package with the given ID
	 * 
	 * @param id the imported package's id
	 * @return imported package if it exists in database, otherwise null
	 * @throws APIException
	 * @should return a Subscription with the given ID if it exists in the database
	 * @since 1.0
	 */
	@Authorized({ MetadataSharingConsts.MODULE_PRIVILEGE })
	@Transactional(readOnly = true)
	ImportedPackage getImportedPackageById(Integer id) throws APIException;
	
	/**
	 * Gets from the database imported package with the given group
	 * 
	 * @param groupUuid the imported package's group
	 * @return imported package if it exists in database, otherwise null
	 * @throws APIException
	 * @should return a imported package with the given group if it exists in the database
	 * @since 1.0
	 */
	@Authorized({ MetadataSharingConsts.MODULE_PRIVILEGE })
	@Transactional(readOnly = true)
	ImportedPackage getImportedPackageByGroup(String groupUuid) throws APIException;
	
	/**
	 * Saves the given imported package to the database
	 * 
	 * @param imported package the imported package to be saved or updated
	 * @return imported package that was saved to the database
	 * @throws APIException
	 * @should return saved imported package
	 * @since 1.0
	 */
	@Authorized({ MetadataSharingConsts.MODULE_PRIVILEGE })
	@Transactional
	ImportedPackage saveImportedPackage(ImportedPackage importedPackage) throws APIException;
	
	/**
	 * Deletes the given imported package from the database
	 * 
	 * @param imported package the imported package to delete
	 * @throws APIException
	 * @should delete the given imported package from the database
	 * @since 1.0
	 */
	@Authorized({ MetadataSharingConsts.MODULE_PRIVILEGE })
	@Transactional
	void deleteImportedPackage(ImportedPackage importedPackage) throws APIException;
	
	/**
	 * Gets all imported packages from the database
	 * 
	 * @return list of all imported packages from the database
	 * @throws APIException
	 * @should return all imported packages from the database
	 * @since 1.0
	 */
	@Authorized({ MetadataSharingConsts.MODULE_PRIVILEGE })
	@Transactional(readOnly = true)
	List<ImportedPackage> getAllImportedPackages() throws APIException;
	
	/**
	 * @return the {@link SubscriptionUpdater}
	 * @since 1.0
	 */
	@Authorized({ MetadataSharingConsts.MODULE_PRIVILEGE })
	SubscriptionUpdater getSubscriptionUpdater();
	
	/**
	 * Gets a set of all names of the preferred concept sources defined as the value of the global
	 * property {@link MetadataSharingConsts#GP_PREFERRED_CONCEPT_SOURCE_IDs}
	 * 
	 * @return the set of preferred source names
	 * @since 1.0
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Set<String> getPreferredSourceNames();
	
	/**
	 * <b>Internal use only.</b> Allows to execute the task in a transaction.
	 * 
	 * @param task
	 * @throws APIException
	 * @since 1.0
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional
	void executeTask(Task task) throws APIException;
	
	/**
	 * Purges all previous assessments.
	 * 
	 * @throws APIException
	 * @since 1.0
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional
	void purgePreviousAssessments() throws APIException;
	
	/**
	 * Returns UUID of a metadata object in the system for the given UUID.
	 * <p>
	 * The returned UUID may be different from the given UUID if an object has been mapped during import
	 * or the same if it has never been mapped or imported.  
	 * 
	 * @param type
	 * @param uuid
	 * @return UUID
	 * @throws APIException
	 * @since 1.0.8
	 */
	@Transactional
	String getMetadataUuid(Class<?> type, String uuid) throws APIException;
}
