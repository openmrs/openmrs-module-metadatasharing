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
package org.openmrs.module.metadatasharing.updater;

import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.Package;
import org.openmrs.module.metadatasharing.SubscriptionStatus;
import org.openmrs.module.metadatasharing.downloader.Downloader;
import org.openmrs.module.metadatasharing.downloader.DownloaderFactory;
import org.openmrs.module.metadatasharing.serializer.MetadataSerializer;
import org.openmrs.module.metadatasharing.subscription.CheckForUpdatesTask;
import org.openmrs.module.metadatasharing.subscription.SubscriptionHeader;

/**
 * This interface is implemented by classes that will be capable for updating subscriptions
 * 
 * @see Subscription
 */
public interface SubscriptionUpdater {
	
	/**
	 * Does all the checking for updates work. I.e. connects to the subscription's URL, downloads
	 * the SubscriptionHeader, validates it, changes the status of the subscription and then returns
	 * the downloaded SubscriptionHeader. It should handle all subscribing exceptions and signal any
	 * errors in status field
	 * 
	 * @see ImportedPackage
	 * @see SubscriptionStatus
	 * @see Downloader
	 * @param subscription the subscription we want to check updates for
	 * @return the subscription header we've downloaded and performed update check
	 * @should handle timeout exception
	 * @should handle service unavailable exception
	 * @should handle IOException
	 * @should handle invalid subscription
	 */
	SubscriptionHeader checkForUpdates(ImportedPackage importedPackage);
	
	/**
	 * Check for updates for all subscriptions
	 * 
	 * @return number of subscriptions which changed status to "Update Available" or had status
	 *         "Update Available" and there was newer version available
	 */
	int checkForUpdatesForAllSubscriptions();
	
	/**
	 * This refreshes state of the related subscription after importing package. The related
	 * subscription is the subscription with the same group. Refreshing means: changing local
	 * package version if imported package has greater and changing the status.
	 * 
	 * @param pack the package that has been imported
	 * @should refresh related subscription
	 */
	void refreshRelatedSubscription(Package pack);
	
	/**
	 * Connects to the subscription's URL and downloads the header connected with it. If there are
	 * any errors, the appropriate status is set to the given subscription.
	 * 
	 * @param subscription the subscription which's header we want to download
	 * @return SubscriptionHeader connected with the given subscription
	 */
	SubscriptionHeader getSubscriptionHeader(ImportedPackage importedPackage);
	
	/**
	 * Sets the downloader factory instance this updater will have
	 * 
	 * @param downloaderFactory the downloaderFactory to set
	 */
	void setDownloaderFactory(DownloaderFactory downloaderFactory);
	
	/**
	 * Gets the downloader factory instance this updater has
	 * 
	 * @return the downloaderFactory
	 */
	DownloaderFactory getDownloaderFactory();
	
	/**
	 * Sets the serializer instance this updater will use
	 * 
	 * @param serializer the serializer to set
	 */
	void setSerializer(MetadataSerializer serializer);
	
	/**
	 * Gets the serializer instance this updater uses
	 * 
	 * @return serializer
	 */
	MetadataSerializer getSerializer();
	
	/**
	 * Registers a scheduled task which will repeat in the given interval.
	 * 
	 * @see CheckForUpdatesTask
	 * @param interval time in seconds to repeat the task
	 */
	void scheduleCheckForUpdatesTask(Long interval);
	
	/**
	 * Unschedules the check for updates task
	 * 
	 * @see CheckForUpdatesTask
	 */
	void unscheduleCheckForUpdatesTask();
}
