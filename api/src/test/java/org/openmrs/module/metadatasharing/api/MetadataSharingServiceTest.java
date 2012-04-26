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
package org.openmrs.module.metadatasharing.api;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests functionality of {@link MetadataSharingService}
 */
public class MetadataSharingServiceTest extends BaseModuleContextSensitiveTest {
	
	protected MetadataSharingService service = null;
	
	@Before
	public void before() throws Exception {
		service = Context.getService(MetadataSharingService.class);
	}
	
	@After
	public void after() throws Exception {
		deleteAllData();
	}
	
	/**
	 * Creates and returns new mock subscription
	 * 
	 * @return new mock subscription instance
	 */
	protected ImportedPackage getMockSubscription(Integer id) {
		ImportedPackage subscription = new ImportedPackage();
		subscription.setId(id);
		subscription.setSubscriptionUrl("someUrl");
		return subscription;
	}
	
	/**
	 * @see SubscriptionService#deleteImportedPackage(Subscription)
	 * @verifies delete the given subscription from the database
	 */
	@Test
	public void purgeSubscription_shouldDeleteTheGivenSubscriptionFromTheDatabase() throws Exception {
		ImportedPackage subscription = service.saveImportedPackage(getMockSubscription(100));
		Assert.assertNotNull(service.getImportedPackageById(subscription.getId()));
		
		service.deleteImportedPackage(subscription);
		Assert.assertNull(service.getImportedPackageById(subscription.getId()));
	}
	
	/**
	 * @see SubscriptionService#saveImportedPackage(Subscription)
	 * @verifies return saved subscription
	 */
	@Test
	public void saveSubscription_shouldReturnSavedSubscription() throws Exception {
		Assert.assertEquals((Integer) 100, service.saveImportedPackage(getMockSubscription(100)).getId());
	}
	
	/**
	 * @see SubscriptionService#getAllImportedPackages()
	 * @verifies return all subscriptions from the database
	 */
	@Test
	public void getAllSubscriptions_shouldReturnAllSubscriptionsFromTheDatabase() throws Exception {
		int size = service.getAllImportedPackages().size();
		
		service.saveImportedPackage(getMockSubscription(null));
		Assert.assertEquals(size + 1, service.getAllImportedPackages().size());
		
		service.saveImportedPackage(getMockSubscription(null));
		Assert.assertEquals(size + 2, service.getAllImportedPackages().size());
	}
	
	/**
	 * @see SubscriptionService#getImportedPackageByGroup(Integer)
	 * @verifies return a Subscription if it exists in the database
	 */
	@Test
	public void getSubscription_shouldReturnASubscriptionIfItExistsInTheDatabase() {
		Assert.assertNull(service.getImportedPackageById(100));
		
		service.saveImportedPackage(getMockSubscription(100));
		Assert.assertEquals((Integer) 100, service.getImportedPackageById(100).getId());
	}
	
	/**
	 * @see SubscriptionService#getImportedPackageByGroup(String)
	 * @verifies return a subscription with the given group if it exists in the database
	 */
	@Test
	public void getSubscriptionByGroup_shouldReturnASubscriptionWithTheGivenGroupIfItExistsInTheDatabase() throws Exception {
		ImportedPackage subscription = getMockSubscription(null);
		subscription.setGroupUuid("MY_GROUP");
		Assert.assertNull(service.getImportedPackageByGroup(subscription.getGroupUuid()));
		
		service.saveImportedPackage(subscription);
		Assert.assertEquals(subscription.getGroupUuid(), service.getImportedPackageByGroup(subscription.getGroupUuid()).getGroupUuid());
	}
	
	/**
	 * @see SubscriptionService#getImportedPackageByGroup(Integer)
	 * @verifies return a Subscription with the given ID if it exists in the database
	 */
	@Test
	public void getSubscriptionById_shouldReturnASubscriptionWithTheGivenIDIfItExistsInTheDatabase() throws Exception {
		Assert.assertNull(service.getImportedPackageById(100));
		
		service.saveImportedPackage(getMockSubscription(100));
		Assert.assertEquals((Integer) 100, service.getImportedPackageById(100).getId());
	}
}
