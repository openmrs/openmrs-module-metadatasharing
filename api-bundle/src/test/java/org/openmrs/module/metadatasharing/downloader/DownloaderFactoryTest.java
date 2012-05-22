package org.openmrs.module.metadatasharing.downloader;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.metadatasharing.downloader.exception.ServiceUnavailableException;
import org.openmrs.module.metadatasharing.downloader.exception.TimeoutException;

/**
 * Abstract test for DownloaderFacotory
 * 
 * @see Downloader
 * @see DownloaderFactory
 */
public abstract class DownloaderFactoryTest {
	
	/**
	 * Returns current DownloaderFactory implementation we are testing, with downloaders registered.
	 * 
	 * @param protocols the list of available protocols
	 * @return DownloaderFactory implementation
	 */
	protected abstract DownloaderFactory getDownloaderFactory(String... protocols);
	
	/**
	 * @see DownloaderFactory#getDownloader(String)
	 * @verifies return null if there is no suitable downloader
	 */
	@Test
	public void getDownloader_shouldReturnNullIfThereIsNoSuitableDownloader() throws Exception {
		DownloaderFactory factory = getDownloaderFactory("http", "ftp", "mailto");
		Assert.assertNull(factory.getDownloader(new URL("https://google.pl")));
		Assert.assertNull(factory.getDownloader(new URL("file://google.pl")));
	}
	
	/**
	 * @see DownloaderFactory#getDownloader(String)
	 * @verifies return suitable downloader
	 */
	@Test
	public void getDownloader_shouldReturnSuitableDownloader() throws Exception {
		DownloaderFactory factory = getDownloaderFactory("http", "ftp", "mailto");
		
		Assert.assertNotNull(factory.getDownloader(new URL("http://google.pl")));
		Assert.assertNotNull(factory.getDownloader(new URL("ftp://google.pl")));
		Assert.assertNotNull(factory.getDownloader(new URL("mailto://google.pl")));
	}
	
	/**
	 * @see DownloaderFactory#getAvailableProtocols()
	 * @verifies return a set of available protocols
	 */
	@Test
	public void getAvailableProtocols_shouldReturnASetOfAvailableProtocols() throws Exception {
		DownloaderFactory factory = getDownloaderFactory("http", "ftp", "mailto");
		Set<String> protocols = factory.getAvailableProtocols();
		Assert.assertTrue(protocols.contains("http"));
		Assert.assertTrue(protocols.contains("ftp"));
		Assert.assertTrue(protocols.contains("mailto"));
		Assert.assertFalse(protocols.contains("https"));
	}
	
	/**
	 * @see DownloaderFactory#getAvailableProtocols()
	 * @verifies throw IllegalArgumentException when the url is malformed
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getDownloader_shouldThrowIllegalArgumentExceptionWhenTheUrlIsMalformed() throws Exception {
		DownloaderFactory factory = getDownloaderFactory("http", "ftp", "mailto");
		factory.getDownloader("bad_url");
	}
	
	/**
	 * Mock implementation of Downloader
	 */
	protected static class MockDownloader extends Downloader {
		
		@Override
		public String downloadAsString() throws IOException, TimeoutException, ServiceUnavailableException {
			return null;
		}
		
		@Override
		public byte[] downloadAsByteArray() throws IOException, TimeoutException, ServiceUnavailableException {
			return null;
		}
		
		@Override
		public int getProgress() {
			return 0;
		}
		
		public MockDownloader(URL url) {
			super(url);
		}
	};
}
