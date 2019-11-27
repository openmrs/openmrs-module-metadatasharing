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
package org.openmrs.module.metadatasharing.api.impl;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptSource;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.ExportedPackageSummary;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.api.db.MetadataSharingDAO;
import org.openmrs.module.metadatasharing.api.db.hibernate.CustomSessionFlushTask;
import org.openmrs.module.metadatasharing.task.Task;
import org.openmrs.module.metadatasharing.updater.SubscriptionUpdater;

/**
 * Default implementation of {@link MetadataSharingService}.
 * <p>
 * This class should not be used on its own. The current implementation should be fetched from the
 * Context via <code>Context.getService(MetadataSharingService.class)</code>
 * 
 * @see org.openmrs.api.context.Context
 * @see org.openmrs.module.metadatasharing.api.MetadataSharingService
 */
public class MetadataSharingServiceImpl extends BaseOpenmrsService implements MetadataSharingService {
	
	protected final Log log = LogFactory.getLog(MetadataSharingServiceImpl.class);
	
	@Resource(name = "hibernateMetadataSharingDAO")
	private MetadataSharingDAO dao;
	
	@Resource(name = MetadataSharingConsts.MODULE_ID + ".SubscriptionUpdaterImpl")
	private SubscriptionUpdater subscriptionUpdater;
	
	@Override
	public ExportedPackage getExportedPackageById(Integer packageId) throws APIException {
		return dao.getExportedPackage(packageId);
	}
	
	@Override
	public void purgeExportedPackage(ExportedPackage pack) throws APIException {
		dao.purgeExportedPackage(pack);
	}
	
	@Override
	public ExportedPackage saveExportedPackage(ExportedPackage pack) throws APIException {
		return dao.saveExportedPackage(pack);
	}
	
	@Override
	public List<ExportedPackage> getAllExportedPackages() throws APIException {
		return dao.getAllExportedPackages();
	}

	@Override
	public List<ExportedPackageSummary> getAllExportedPackageSummaries() {
		return dao.getAllExportedPackageSummaries();
	}

	@Override
	public ImportedItem getImportedItemByUuid(Class<?> type, String uuid) throws APIException {
		return dao.getImportItem(type, uuid);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.MetadataSharingService#persistImportedItem(org.openmrs.module.metadatasharing.ImportedItem)
	 */
	@Override
	public ImportedItem persistImportedItem(ImportedItem importItem) throws APIException {
		return dao.persistImportItem(importItem);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.MetadataSharingService#persistImportedItems(java.util.Collection)
	 */
	@Override
	public Collection<ImportedItem> persistImportedItems(Collection<ImportedItem> importItems) throws APIException {
		return dao.persistImportItems(importItems);
	}

	@Override
	public ExportedPackage getLatestExportedPackageByGroup(String groupUuid) throws APIException {
		return dao.getLatestExportedPackage(groupUuid);
	}
	
	@Override
	public List<ExportedPackage> getExportedPackagesByGroup(String group) throws APIException {
		return dao.getExportedPackageGroup(group);
	}
	
	@Override
	public ExportedPackage getLatestPublishedPackageByGroup(String group) throws APIException {
		return dao.getLatestPublishedPackage(group);
	}
	
	@Override
	public ExportedPackage getPublishedPackageByGroup(String group, Integer version) {
		return dao.getPublishedPackage(group, version);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.SubscriptionService#getImportedPackageByGroup(java.lang.Integer)
	 */
	@Override
	public ImportedPackage getImportedPackageById(Integer id) throws APIException {
		return dao.getImportedPackageById(id);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.SubscriptionService#saveImportedPackage(org.openmrs.module.metadatasharing.subscription.Subscription)
	 */
	@Override
	public ImportedPackage saveImportedPackage(ImportedPackage subscription) throws APIException {
		return dao.saveImportedPackage(subscription);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.SubscriptionService#deleteImportedPackage(org.openmrs.module.metadatasharing.subscription.Subscription)
	 */
	@Override
	public void deleteImportedPackage(ImportedPackage subscription) throws APIException {
		dao.deleteImportedPackage(subscription);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.SubscriptionService#getAllImportedPackages()
	 */
	@Override
	public List<ImportedPackage> getAllImportedPackages() throws APIException {
		return dao.getAllImportedPackages();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.SubscriptionService#getImportedPackageByGroup(java.lang.String)
	 */
	@Override
	public ImportedPackage getImportedPackageByGroup(String group) throws APIException {
		return dao.getImportedPackageByGroup(group);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.SubscriptionService#getSubscriptionUpdater()
	 */
	@Override
	public SubscriptionUpdater getSubscriptionUpdater() {
		return subscriptionUpdater;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.MetadataSharingService#getPreferredSourceNames()
	 */
	@Override
	public Set<String> getPreferredSourceNames() {
		String[] preferredSourceIdsArray = Context.getAdministrationService()
		        .getGlobalProperty(MetadataSharingConsts.GP_PREFERRED_CONCEPT_SOURCE_IDs, "").split(",");
		Set<String> preferredSourceNames = new HashSet<String>(preferredSourceIdsArray.length);
		
		ConceptService cs = Context.getConceptService();
		for (String sourceId : preferredSourceIdsArray) {
			sourceId = sourceId.trim();
			if (sourceId.length() > 0) {
				try {
					ConceptSource source = cs.getConceptSource(Integer.valueOf(sourceId));
					if (source != null)
						preferredSourceNames.add(source.getName());
				}
				catch (NumberFormatException e) {
					//just skip it
				}
			}
		}
		
		return preferredSourceNames;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.MetadataSharingService#executeTask(org.openmrs.module.metadatasharing.task.Task)
	 */
	@Override
	public void executeTask(final Task task) throws APIException {
		new CustomSessionFlushTask<Object>() {
			
			@Override
			protected Object task() {
				task.execute();
				
				return null;
			}
			
		}.executeInManualFlushMode();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.MetadataSharingService#purgePreviousAssessments()
	 */
	@Override
	public void purgePreviousAssessments() throws APIException {
		dao.purgePreviousAssessments();
	}
	
	@Override
	public String getMetadataUuid(Class<?> type, String uuid) throws APIException {
		ImportedItem importedItem = getImportedItemByUuid(type, uuid);
		if (importedItem != null && importedItem.getExistingUuid() != null) {
			return importedItem.getExistingUuid();
		}
		return uuid;
	}
}
