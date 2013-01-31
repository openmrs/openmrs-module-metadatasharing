package org.openmrs.module.metadatasharing.downloader.impl;

import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.metadatasharing.downloader.Downloader;
import org.openmrs.module.metadatasharing.downloader.exception.ServiceUnavailableException;

public class HTTPDownloaderTest {
	
	/**
	 * @see HTTPDownloader#downloadAsString(URL)
	 */
	@Test(expected = ServiceUnavailableException.class)
	@Ignore
	public void unknownHostTest() throws Exception {
		new HTTPDownloader(new URL("http://test.test.test")).downloadAsString();
	}
	
	/**
	 * Warning: this test requires Internet connection
	 * 
	 * @see HTTPDownloader#downloadAsString(URL)
	 */
	@Test(expected = IllegalStateException.class)
	public void goodTest() throws Exception {
		Downloader downloader = new HTTPDownloader(new URL("http://google.pl"));
		downloader.downloadAsString();
		downloader.downloadAsString();
	}
}
