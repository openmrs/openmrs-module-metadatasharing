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
package org.openmrs.module.metadatasharing.serializer;

import java.net.URI;
import java.util.Date;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.subscription.SubscriptionHeader;
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests if serializing and deserializing of subscription header works properly
 * 
 * @see SubscriptionHeader
 */
public class SubscriptionHeaderSerializationTest extends BaseModuleContextSensitiveTest {
	
	private OpenmrsSerializer serializer;
	
	private SubscriptionHeader header;
	
	/**
	 * Initializes serializer and some mock subscription header
	 * 
	 * @throws Exception
	 */
	@Before
	public void before() throws Exception {
		serializer = Context.getSerializationService().getSerializer(MetadataSerializer.class);
		header = new SubscriptionHeader();
		ExportedPackage pack = new ExportedPackage();
		pack.setDateCreated(new Date());
		pack.setDescription("My Description");
		pack.setName("My Name");
		pack.setOpenmrsVersion("1.6");
		header.setPackageHeader(pack);
		header.setContentUri(new URI("/content"));
	}
	
	/**
	 * Tests if serialization works properly
	 * 
	 * @throws Exception
	 */
	@Test
	public void serialize() throws Exception {
		String xml = serializer.serialize(header);
		XMLAssert.assertXpathExists("/subscriptionHeader/contentUri", xml);
		XMLAssert.assertXpathExists("/subscriptionHeader/packageHeader", xml);
		XMLAssert.assertXpathExists("/subscriptionHeader/packageHeader/name", xml);
	}
	
	/**
	 * Tests if serialization ignores null values
	 * 
	 * @throws Exception
	 */
	@Test
	public void serializationIgnoresNullValues() throws Exception {
		ExportedPackage pack = header.getPackageHeader();
		pack.setIncrementalVersion(null);
		String xml = serializer.serialize(header);
		XMLAssert.assertXpathExists("/subscriptionHeader/contentUri", xml);
		XMLAssert.assertXpathExists("/subscriptionHeader/packageHeader", xml);
		XMLAssert.assertXpathExists("/subscriptionHeader/packageHeader/name", xml);
		XMLAssert.assertXpathNotExists("/subscriptionHeader/packageHeader/incrementalVersion", xml);
	}
	
	/**
	 * Tests if deserialization works proprely
	 * 
	 * @throws Exception
	 */
	@Test
	public void deserialize() throws Exception {
		SubscriptionHeader deserialized = serializer.deserialize(serializer.serialize(header), SubscriptionHeader.class);
		Assert.assertNotNull(deserialized.getPackageHeader().getDateCreated());
		Assert.assertEquals(header.getPackageHeader().getDescription(), deserialized.getPackageHeader().getDescription());
		Assert.assertEquals(header.getPackageHeader().getName(), deserialized.getPackageHeader().getName());
		Assert.assertEquals(header.getPackageHeader().getOpenmrsVersion(), deserialized.getPackageHeader()
		        .getOpenmrsVersion());
		Assert.assertEquals(header.getPackageHeader().getGroupUuid(), deserialized.getPackageHeader().getGroupUuid());
		Assert.assertEquals(header.getPackageHeader().getVersion(), deserialized.getPackageHeader().getVersion());
		Assert.assertEquals(header.getContentUri(), deserialized.getContentUri());
	}
}
