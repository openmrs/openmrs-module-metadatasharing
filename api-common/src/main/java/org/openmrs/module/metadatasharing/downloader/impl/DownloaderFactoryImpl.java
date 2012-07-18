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
package org.openmrs.module.metadatasharing.downloader.impl;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.openmrs.module.metadatasharing.downloader.Downloader;
import org.openmrs.module.metadatasharing.downloader.DownloaderFactory;

/**
 * It is map-based implementation of DownloaderFactory
 * 
 * @see DownloaderFactory
 * @see Map
 */
public class DownloaderFactoryImpl implements DownloaderFactory {
	
	/**
	 * Map of downloaders, where key is a protocol it can handle and the value is a class to
	 * instance
	 */
	private Map<String, Class<? extends Downloader>> downloadersMap = null;
	
	/**
	 * Default timeout for downloading data
	 */
	private int defaultTimeout;
	
	/**
	 * @see org.openmrs.module.metadatasharing.downloader.DownloaderFactory#getDownloader(java.net.URL)
	 */
	@Override
	public Downloader getDownloader(URL url) {
		Class<? extends Downloader> clazz = downloadersMap.get(url.getProtocol());
		if (clazz == null)
			return null;
		Constructor<? extends Downloader> construct = null;
		Downloader downloader = null;
		try {
			construct = clazz.getConstructor(URL.class);
			downloader = construct.newInstance(url);
		}
		catch (Exception e) { //we are sure that there is such constructor, so there actually cannot be thrown any exception
			throw new RuntimeException("Cannot instance donwloader");
		}
		downloader.setTimeout(defaultTimeout);
		return downloader;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.downloader.DownloaderFactory#setDefaultTimeout(int)
	 */
	@Override
	public void setDefaultTimeout(int timeout) {
		this.defaultTimeout = timeout;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.downloader.DownloaderFactory#setDownloaders(java.util.Map)
	 */
	@Override
	public void setDownloaders(Map<String, Class<? extends Downloader>> downloaders) {
		this.downloadersMap = downloaders;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.downloader.DownloaderFactory#getAvailableProtocols()
	 */
	@Override
	public Set<String> getAvailableProtocols() {
		return downloadersMap.keySet();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.downloader.DownloaderFactory#getDownloader(java.lang.String)
	 */
	@Override
	public Downloader getDownloader(String url) {
		try {
			URL validUrl = new URL(url);
			return getDownloader(validUrl);
		}
		catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
}
