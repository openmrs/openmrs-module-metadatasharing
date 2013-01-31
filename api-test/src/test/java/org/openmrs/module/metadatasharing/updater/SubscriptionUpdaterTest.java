package org.openmrs.module.metadatasharing.updater;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.SubscriptionStatus;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.downloader.Downloader;
import org.openmrs.module.metadatasharing.downloader.DownloaderFactory;
import org.openmrs.module.metadatasharing.downloader.exception.ServiceUnavailableException;
import org.openmrs.module.metadatasharing.downloader.exception.TimeoutException;
import org.openmrs.module.metadatasharing.downloader.impl.DownloaderFactoryImpl;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class SubscriptionUpdaterTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * Updater instanced by the Spring
	 */
	@Autowired
	private SubscriptionUpdater updater = null;
	
	/**
	 * Downloader factory instanced by the Spring
	 */
	@Autowired
	private DownloaderFactory factory = null;
	
	/**
	 * @see SubscriptionUpdater#checkForUpdates(Subscription)
	 * @verifies handle timeout exception
	 */
	@Test
	public void checkForUpdates_shouldHandleTimeoutException() throws Exception {
		updater.setDownloaderFactory(getMockDownloaderFactoryThatThrowsException(TimeoutException.class));
		ImportedPackage subscription = getMockSubscription("http://test.test");
		updater.checkForUpdates(subscription);
		Assert.assertEquals(SubscriptionStatus.TIMEOUT_LIMIT_EXCEEDED, subscription.getSubscriptionStatus());
		Assert.assertTrue(subscription.hasSubscriptionErrors());
	}
	
	/**
	 * @see SubscriptionUpdater#checkForUpdates(Subscription)
	 * @verifies handle service unavailable exception
	 */
	@Test
	@Ignore
	public void checkForUpdates_shouldHandleServiceUnavailableException() throws Exception {
		ImportedPackage subscription = getMockSubscription("http://test.test");
		updater.checkForUpdates(subscription);
		Assert.assertEquals(SubscriptionStatus.SERVICE_UNAVAILABLE, subscription.getSubscriptionStatus());
		Assert.assertTrue(subscription.hasSubscriptionErrors());
	}
	
	/**
	 * @see SubscriptionUpdater#checkForUpdates(Subscription)
	 * @verifies handle IOException
	 */
	@Test
	public void checkForUpdates_shouldHandleIOException() throws Exception {
		updater.setDownloaderFactory(getMockDownloaderFactoryThatThrowsException(IOException.class));
		ImportedPackage subscription = getMockSubscription("http://test.test");
		updater.checkForUpdates(subscription);
		Assert.assertEquals(SubscriptionStatus.FATAL_ERROR, subscription.getSubscriptionStatus());
		Assert.assertTrue(subscription.hasSubscriptionErrors());
	}
	
	/**
	 * @see SubscriptionUpdater#checkForUpdates(Subscription)
	 * @verifies handle invalid subscription
	 */
	@Test
	public void checkForUpdates_shouldHandleInvalidSubscription() throws Exception {
		updater.setDownloaderFactory(getMockDownloaderFactoryThatReturnsString("asgdfhjk sdfha "));
		ImportedPackage subscription = getMockSubscription("http://test.test");
		updater.checkForUpdates(subscription);
		Assert.assertEquals(SubscriptionStatus.INVALID_SUBSCRIPTION, subscription.getSubscriptionStatus());
		Assert.assertTrue(subscription.hasSubscriptionErrors());
		
		//some almost valid data 
		updater.setDownloaderFactory(getMockDownloaderFactoryThatReturnsString("<subscription id=\"1\"><packageHeader><dateCreated>adfd</dateCreated></packageHeader></subscription>"));
		subscription = getMockSubscription("http://test.test");
		updater.checkForUpdates(subscription);
		Assert.assertEquals(SubscriptionStatus.INVALID_SUBSCRIPTION, subscription.getSubscriptionStatus());
		Assert.assertTrue(subscription.hasSubscriptionErrors());
	}
	
	/**
	 * @see SubscriptionUpdater#refreshRelatedSubscription(ExportedPackage)
	 * @verifies refresh related subscription
	 */
	@Test
	public void refreshRelatedSubscription_shouldRefreshRelatedSubscription() throws Exception {
		MetadataSharingService service = Context.getService(MetadataSharingService.class);
		ImportedPackage subscription = getMockSubscription("URL");
		subscription.setGroupUuid("MY_GROUP");
		subscription.setVersion(null);
		subscription.setRemoteVersion(1);
		service.saveImportedPackage(subscription);
		ExportedPackage pack = new ExportedPackage();
		pack.setGroupUuid("MY_GROUP");
		pack.setVersion(7);
		pack.setName("MY_NAME");
		updater.refreshRelatedSubscription(pack);
		
		subscription = service.getImportedPackageByGroup("MY_GROUP");
		Assert.assertEquals(pack.getVersion(), subscription.getVersion());
		Assert.assertEquals(pack.getName(), subscription.getName());
		Assert.assertEquals(SubscriptionStatus.UP_TO_DATE, subscription.getSubscriptionStatus());
		
		pack = new ExportedPackage();
		pack.setGroupUuid("MY_GROUP");
		pack.setVersion(2);
		pack.setName("MY_NAME_TWO");
		updater.refreshRelatedSubscription(pack);
		
		subscription = service.getImportedPackageByGroup("MY_GROUP");
		Assert.assertEquals((Integer) 7, subscription.getVersion());
		Assert.assertEquals("MY_NAME", subscription.getName());
		Assert.assertEquals(SubscriptionStatus.UP_TO_DATE, subscription.getSubscriptionStatus());
	}
	
	/**
	 * Sets the downloader factory to default instance.
	 */
	@Before
	public void before() {
		updater.setDownloaderFactory(factory); //we set back the default factory
	}
	
	/**
	 * Creates and returns mock subscription
	 * 
	 * @param url the URL of the mock subscription we want to create
	 * @return mock subscription
	 */
	private ImportedPackage getMockSubscription(String url) {
		ImportedPackage subscription = new ImportedPackage();
		subscription.setSubscriptionUrl(url);
		return subscription;
	}
	
	/**
	 * Returns a mock DownloaderFactory which returns a downloader which returns the specified
	 * string (huh... :)
	 * 
	 * @param content the content to be returned in downloadAsString method
	 * @return DownloaderFactory instance
	 */
	private DownloaderFactory getMockDownloaderFactoryThatReturnsString(final String content) {
		return new DownloaderFactoryImpl() {
			
			@Override
			public Downloader getDownloader(URL url) {
				return new MockDownloader(
				                          url) {
					
					/**
					 * returns specified content
					 */
					@Override
					public String downloadAsString() {
						return content;
					}
				};
			}
		};
	}
	
	/**
	 * Returns a mock DownloaderFactory which returns a downloader that will throw an exception of
	 * class class in downloadAsString method
	 * 
	 * @param clazz the exception's to be thrown class
	 * @return DownloaderFactory instance
	 */
	private DownloaderFactory getMockDownloaderFactoryThatThrowsException(final Class<? extends Exception> clazz) {
		return new DownloaderFactoryImpl() {
			
			@Override
			public Downloader getDownloader(URL url) {
				return new MockDownloader(
				                          url) {
					
					/**
					 * Throws specified exception
					 */
					@Override
					public String downloadAsString() throws IOException {
						Constructor<? extends Exception> construct;
						Exception exception = null;
						try {
							construct = clazz.getConstructor();
							exception = construct.newInstance();
						}
						catch (Exception e) {}
						if (exception instanceof RuntimeException)
							throw (RuntimeException) exception;
						else
							throw (IOException) exception;
					}
				};
			}
		};
	}
	
	/**
	 * Mock implementation of Downloader. It does nothing, only implements methods.
	 */
	private class MockDownloader extends Downloader {
		
		public MockDownloader(URL url) {
			super(url);
		}
		
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
	}
}
