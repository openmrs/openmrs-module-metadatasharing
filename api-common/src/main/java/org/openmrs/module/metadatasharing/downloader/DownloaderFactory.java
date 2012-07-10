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
package org.openmrs.module.metadatasharing.downloader;

import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * Provides downloaders which are available to handle specified URL
 * 
 * @see Downloader
 */
public interface DownloaderFactory {
	
	/**
	 * Sets the downloaders classes from which we will instance new objects
	 * 
	 * @param downloaders - map of [protocol -> downloader class]
	 */
	void setDownloaders(Map<String, Class<? extends Downloader>> downloaders);
	
	/**
	 * Returns downloader instance which is available to handle the specified URL
	 * 
	 * @param url the URL we want do download from
	 * @return Downloader instance available to handle specified URL
	 * @should return null if there is no suitable downloader
	 * @should return suitable downloader
	 */
	Downloader getDownloader(URL url);
	
	/**
	 * Converts String url value to the URL instance and calls
	 * {@link DownloaderFactory#getDownloader(URL)}
	 * 
	 * @param url the URL we want do download from
	 * @throws IllegalArgumentException when the url is malformed
	 * @return Downloader instance available to handle specified URL
	 * @should throw IllegalArgumentException when the url is malformed
	 */
	Downloader getDownloader(String url);
	
	/**
	 * Sets the default timeout that will be set to all downloaders.
	 * 
	 * @see Downloader
	 * @param timeout the timeout to set
	 */
	void setDefaultTimeout(int timeout);
	
	/**
	 * Gets a set of protocols we are available to handle
	 * 
	 * @return set of protocols
	 * @should return a set of available protocols
	 */
	Set<String> getAvailableProtocols();
}
