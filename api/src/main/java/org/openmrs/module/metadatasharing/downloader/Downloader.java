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

import java.io.IOException;
import java.net.URL;

import org.openmrs.module.metadatasharing.downloader.exception.ServiceUnavailableException;
import org.openmrs.module.metadatasharing.downloader.exception.TimeoutException;

/**
 * This is class for downloading data form the given URL. Superclasses should be registered to the
 * DownloaderFactory in applicationContext.xml file
 * 
 * @see DownloaderFactory
 */
public abstract class Downloader {
	
	/**
	 * Timeout in seconds for finishing downloading data. 0 means no timeout
	 */
	protected int timeout;
	
	/**
	 * The URL we will be downloading from
	 */
	protected URL url;
	
	/**
	 * Creates new downloader with the specified URL
	 * 
	 * @param url the URL we will be downloading form
	 */
	public Downloader(URL url) {
		this.url = url;
	}
	
	/**
	 * This method connects to the downloader's URL and returns the data it contains. Downloader
	 * instance can download data only one time.
	 * 
	 * @return data as string
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws ServiceUnavailableException
	 */
	public abstract String downloadAsString() throws IOException, TimeoutException, ServiceUnavailableException;
	
	/**
	 * This method connects to the downloader's URL and returns the data it contains. Downloader
	 * instance can download data only one time.
	 * 
	 * @return data as byte array
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws ServiceUnavailableException
	 */
	public abstract byte[] downloadAsByteArray() throws IOException, TimeoutException, ServiceUnavailableException;
	
	/**
	 * Gets the download progress in percents. Only when the downloading process is finished it
	 * should return 100. <br>
	 * -1 means that we cannot specify the progress
	 * 
	 * @return download progress in percents
	 */
	public abstract int getProgress();
	
	/**
	 * @param timeout the timeout (in seconds) to set. 0 means no timeout.
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * @return the timeout (in seconds). 0 means no timeout.
	 */
	public int getTimeout() {
		return timeout;
	}
}
