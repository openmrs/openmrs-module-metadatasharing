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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.openmrs.module.metadatasharing.downloader.Downloader;
import org.openmrs.module.metadatasharing.downloader.exception.ServiceUnavailableException;
import org.openmrs.module.metadatasharing.downloader.exception.TimeoutException;

/**
 * This class is available to download data form http and https URLs
 * 
 * @see Downloader
 */
public class HTTPDownloader extends Downloader {
	
	/**
	 * The dafault buffer size while downloading data. It can be re-computed during downloading
	 * process
	 */
	private int bufferSize = 0x10000; //64 kB
	
	/**
	 * @see HttpURLConnection
	 */
	private HttpURLConnection connection = null;
	
	/**
	 * The connection's charset. Default is UTF-8
	 */
	private String charset = "UTF-8";
	
	/**
	 * The number of bytes we have read so far. It is used for determining the progress of
	 * downloading
	 */
	private int bytesRead = 0;
	
	/**
	 * @see Downloader#Downloader(URL)
	 */
	public HTTPDownloader(URL url) {
		super(url);
		if (!url.getProtocol().equals("http") && !url.getProtocol().equals("https"))
			throw new IllegalArgumentException("This downloader is available process http and https URLs only");
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.downloader.Downloader#downloadAsString()
	 */
	@Override
	public String downloadAsString() throws IOException, TimeoutException, ServiceUnavailableException {
		byte byteArray[] = downloadAsByteArray();
		return new String(byteArray, charset);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.downloader.Downloader#downloadAsByteArray()
	 */
	@Override
	public byte[] downloadAsByteArray() throws IOException, TimeoutException, ServiceUnavailableException {
		BufferedInputStream reader = null;
		if (connection != null) {
			throw new IllegalStateException("Downloader instance cannot download twice");
		}
		try {
			initialzeConnection();
			if (connection.getResponseCode() != 200 /* HttpServletResponse.SC_OK */) {
				throw new ServiceUnavailableException("Error code:" + connection.getResponseCode());
			}
			reader = new BufferedInputStream(connection.getInputStream(), bufferSize);
			int read;
			byte[] buffer = new byte[bufferSize];
			ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
			do {
				read = reader.read(buffer, 0, bufferSize);
				if (read > 0) {
					byteArrayOutput.write(buffer, 0, read);
					bytesRead += read;
				}
			} while (read > 0);
			return byteArrayOutput.toByteArray();
		}
		catch (SocketTimeoutException e) {
			throw new TimeoutException(e);
		}
		catch (IOException e) {
			throw new ServiceUnavailableException(e);
		}
		finally {
			if (reader != null)
				reader.close();
			connection.disconnect();
		}
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.downloader.Downloader#getProgress()
	 */
	@Override
	public int getProgress() {
		if (connection.getContentLength() == -1)
			return -1;
		else
			return (bytesRead * 100) / connection.getContentLength();
	}
	
	/**
	 * Initializes connection to the remote server, sets the charset and bufferSize
	 * 
	 * @throws IOException
	 */
	private void initialzeConnection() throws IOException {
		connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(this.timeout * 1000);
		connection.setReadTimeout(this.timeout * 1000);
		connection.setRequestProperty("User-Agent", "OpenMRS MetadataSharing");
		connection.setRequestProperty("Accept", "text/xml");
		connection.setRequestProperty("Accept-Charset", "utf-8");
		connection.setDoInput(true);
		connection.setDoOutput(false);
		connection.setRequestMethod("GET");
		connection.connect();
		if (connection.getContentEncoding() != null)
			charset = connection.getContentEncoding();
		
		if (connection.getContentLength() != -1) {//buffer initialization
			if (connection.getContentLength() > bufferSize) {
				bufferSize = connection.getContentLength() / 20;
				//let's divide it to 20 parts to have some progress information
			}
		}
	}
}
