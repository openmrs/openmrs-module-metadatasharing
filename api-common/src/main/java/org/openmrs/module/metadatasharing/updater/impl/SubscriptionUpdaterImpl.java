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
package org.openmrs.module.metadatasharing.updater.impl;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.Package;
import org.openmrs.module.metadatasharing.SubscriptionStatus;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.downloader.Downloader;
import org.openmrs.module.metadatasharing.downloader.DownloaderFactory;
import org.openmrs.module.metadatasharing.downloader.exception.ServiceUnavailableException;
import org.openmrs.module.metadatasharing.downloader.exception.TimeoutException;
import org.openmrs.module.metadatasharing.model.validator.SubscriptionHeaderValidator;
import org.openmrs.module.metadatasharing.serializer.MetadataSerializer;
import org.openmrs.module.metadatasharing.subscription.CheckForUpdatesTask;
import org.openmrs.module.metadatasharing.subscription.SubscriptionHeader;
import org.openmrs.module.metadatasharing.updater.SubscriptionUpdater;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.serialization.SerializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.XStreamException;

/**
 * Default implementation of the subscription updater
 * 
 * @see SubscriptionUpdater
 */
@Component(MetadataSharingConsts.MODULE_ID + ".SubscriptionUpdaterImpl")
public class SubscriptionUpdaterImpl implements SubscriptionUpdater {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private DownloaderFactory downloaderFactory;
	
	@Autowired
	private MetadataSerializer serializer;
	
	@Autowired
	private SubscriptionHeaderValidator validator;
	
	@Autowired
	private SchedulerCompatibility scheduler;
	
	/**
	 * @see org.openmrs.module.metadatasharing.updater.SubscriptionUpdater#checkForUpdates(org.openmrs.module.metadatasharing.subscription.Subscription)
	 */
	@Override
	public SubscriptionHeader checkForUpdates(ImportedPackage importedPackage) {
		importedPackage.setSubscriptionStatus(SubscriptionStatus.NEVER_CHECKED);
		SubscriptionHeader header = getSubscriptionHeader(importedPackage);
		if (importedPackage.hasSubscriptionErrors())
			return header;
		
		MetadataSharingService service = Context.getService(MetadataSharingService.class);
		if (importedPackage.getGroupUuid() == null) { //new importedPackage
			String group = header.getPackageHeader().getGroupUuid();
			if (service.getImportedPackageByGroup(group) != null) { //duplicate
				importedPackage.setSubscriptionStatus(SubscriptionStatus.DUPLICATE_SUBSCRIPTION);
				return header;
			}
		}
		
		importedPackage.setGroupUuid(header.getPackageHeader().getGroupUuid());
		
		importedPackage.setRemoteVersion(header.getPackageHeader().getVersion());
		if (importedPackage.getDateImported() == null || importedPackage.getVersion() == null
		        || importedPackage.getVersion() < importedPackage.getRemoteVersion()) {
			importedPackage.setSubscriptionStatus(SubscriptionStatus.UPDATE_AVAILABLE);
			importedPackage.setName(header.getPackageHeader().getName());
			importedPackage.setDescription(header.getPackageHeader().getDescription());
			importedPackage.setDateCreated(header.getPackageHeader().getDateCreated());
		} else {
			importedPackage.setSubscriptionStatus(SubscriptionStatus.UP_TO_DATE);
		}
		return header;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.updater.SubscriptionUpdater#checkForUpdatesForAllSubscriptions()
	 */
	@Override
	public int checkForUpdatesForAllSubscriptions() {
		MetadataSharingService service = Context.getService(MetadataSharingService.class);
		List<ImportedPackage> importedPackages = service.getAllImportedPackages();
		int newVersionCount = 0;
		for (ImportedPackage importedPackage : importedPackages) {
			Integer previousRemotePackageVersion = importedPackage.getRemoteVersion();
			checkForUpdates(importedPackage);
			if (importedPackage.getSubscriptionStatus() == SubscriptionStatus.UPDATE_AVAILABLE
			        && (previousRemotePackageVersion == null || previousRemotePackageVersion < importedPackage
			                .getRemoteVersion())) { //there is newer version available
				newVersionCount++;
			}
			service.saveImportedPackage(importedPackage);
		}
		return newVersionCount;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.updater.SubscriptionUpdater#refreshRelatedSubscription(org.openmrs.module.metadatasharing.ExportedPackage)
	 */
	@Override
	public void refreshRelatedSubscription(Package pack) {
		if (pack.getGroupUuid() != null) {
			MetadataSharingService service = Context.getService(MetadataSharingService.class);
			ImportedPackage subscription = service.getImportedPackageByGroup(pack.getGroupUuid());
			if (subscription != null) {
				if (subscription.getVersion() == null || pack.getVersion() > subscription.getVersion()) {
					subscription.setVersion(pack.getVersion());
					subscription.setName(pack.getName());
					if (pack.getVersion() >= subscription.getRemoteVersion()) {
						subscription.setSubscriptionStatus(SubscriptionStatus.UP_TO_DATE);
					}
					service.saveImportedPackage(subscription);
				}
			}
		}
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.updater.SubscriptionUpdater#getSubscriptionHeader(org.openmrs.module.metadatasharing.subscription.Subscription)
	 */
	@Override
	public SubscriptionHeader getSubscriptionHeader(ImportedPackage importedPackage) {
		Downloader downloader = downloaderFactory.getDownloader(importedPackage.getSubscriptionUrl());
		SubscriptionHeader header = null;
		try {
			String xmlData = downloader.downloadAsString();
			if (!xmlData.contains("<subscriptionHeader")) { //a heuristics for invalid header (in most cases for soft 404 pages)
				//deserialization  of regular web pages takes around one minute after exception is thrown, so we use this heuristics. 
				//It will filer 99,99% regular web pages in the world
				importedPackage.setSubscriptionStatus(SubscriptionStatus.INVALID_SUBSCRIPTION);
				return header;
			}
			header = serializer.deserialize(xmlData, SubscriptionHeader.class);
			Errors errors = new BindException(header, "header");
			validator.validate(header, errors);
			if (errors.hasErrors() || header.getPackageHeader().getGroupUuid() == null) { //we don't allow to subscribe to invalid packages and without version & group
				importedPackage.setSubscriptionStatus(SubscriptionStatus.INVALID_SUBSCRIPTION);
			}
		}
		catch (TimeoutException e) {
			importedPackage.setSubscriptionStatus(SubscriptionStatus.TIMEOUT_LIMIT_EXCEEDED);
		}
		catch (ServiceUnavailableException e) {
			importedPackage.setSubscriptionStatus(SubscriptionStatus.SERVICE_UNAVAILABLE);
		}
		catch (IOException e) {
			importedPackage.setSubscriptionStatus(SubscriptionStatus.FATAL_ERROR);
		}
		catch (XStreamException e) {
			importedPackage.setSubscriptionStatus(SubscriptionStatus.INVALID_SUBSCRIPTION);
		}
		catch (SerializationException e) {
			importedPackage.setSubscriptionStatus(SubscriptionStatus.INVALID_SUBSCRIPTION);
		}
		return header;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.updater.SubscriptionUpdater#setDownloaderFactory(org.openmrs.module.metadatasharing.downloader.DownloaderFactory)
	 */
	@Override
	public void setDownloaderFactory(DownloaderFactory downloaderFactory) {
		this.downloaderFactory = downloaderFactory;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.updater.SubscriptionUpdater#getDownloaderFactory()
	 */
	@Override
	public DownloaderFactory getDownloaderFactory() {
		return downloaderFactory;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.updater.SubscriptionUpdater#setSerializer(org.openmrs.module.metadatasharing.serializer.MetadataSerializer)
	 */
	@Override
	public void setSerializer(MetadataSerializer serializer) {
		this.serializer = serializer;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.updater.SubscriptionUpdater#getSerializer()
	 */
	@Override
	public MetadataSerializer getSerializer() {
		return serializer;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.updater.SubscriptionUpdater#setCheckForUpdatesTaskInterval(java.lang.Long,
	 *      java.lang.String)
	 */
	@Override
	public void scheduleCheckForUpdatesTask(Long interval) {
		try {
			Context.addProxyPrivilege(MetadataSharingConsts.MANAGE_SCHEDULER_PRIVILEGE);
			
			TaskDefinition taskDefinition = Context.getSchedulerService().getTaskByName(
			    MetadataSharingConsts.CHECK_FOR_UPDATES_TASK_NAME);
			if (taskDefinition == null) {
				taskDefinition = new TaskDefinition();
				taskDefinition.setTaskClass(CheckForUpdatesTask.class.getCanonicalName());
				taskDefinition.setStartOnStartup(true);
				taskDefinition.setStarted(true);
				taskDefinition.setName(MetadataSharingConsts.CHECK_FOR_UPDATES_TASK_NAME);
				taskDefinition.setDescription(Context.getMessageSourceService().getMessage(
				    "metadatasharing.alert.description"));
				taskDefinition.setRepeatInterval(interval);
				scheduler.saveTask(Context.getSchedulerService(), taskDefinition);
				Context.getSchedulerService().scheduleTask(taskDefinition);
			} else {
				taskDefinition.setRepeatInterval(interval);
				Context.getSchedulerService().rescheduleTask(taskDefinition);
			}
			
		}
		catch (SchedulerException ex) {
			log.warn("Unable to register task '" + MetadataSharingConsts.CHECK_FOR_UPDATES_TASK_NAME + "' with scheduler",
			    ex);
		}
		finally {
			Context.removeProxyPrivilege(MetadataSharingConsts.MANAGE_SCHEDULER_PRIVILEGE);
		}
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.updater.SubscriptionUpdater#unscheduleCheckForUpdatesTask()
	 */
	@Override
	public void unscheduleCheckForUpdatesTask() {
		TaskDefinition taskDef = Context.getSchedulerService().getTaskByName(
		    MetadataSharingConsts.CHECK_FOR_UPDATES_TASK_NAME);
		if (taskDef != null)
			Context.getSchedulerService().deleteTask(taskDef.getId());
	}
}
