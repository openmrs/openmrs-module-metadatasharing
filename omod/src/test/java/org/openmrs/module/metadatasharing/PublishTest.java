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
package org.openmrs.module.metadatasharing;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import junit.framework.Assert;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.publish.PublishUtils;
import org.openmrs.module.metadatasharing.web.controller.PublishController;
import org.openmrs.module.metadatasharing.wrapper.PackageExporter;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.xml.sax.SAXException;

/**
 * Test which verifies all publishing related things
 */
public class PublishTest extends BaseModuleWebContextSensitiveTest {
	
	/**
	 * @verifies that the published field is not serialized
	 */
	@Test
	public void publishedPackageSerializationTest() throws SerializationException, XpathException, IOException, SAXException {
		PackageExporter exporter = MetadataSharing.getInstance().newPackageExporter();
		exporter.getPackage().setName("Package");
		exporter.getPackage().setDescription("Test package");
		exporter.exportPackage();
		ExportedPackage pack = exporter.getExportedPackage();
		XMLAssert.assertXpathNotExists("package/published", pack.getSerializedPackage().getHeader());
	}
	
	/**
	 * @verifies that the subscription URL serializes properly
	 */
	@Test
	public void subscriptionUrlserializationTest() throws Exception {
		ExportedPackage pack = new ExportedPackage();
		pack.setSubscriptionUrl("http://google.pl");
		pack = ImportExportTestUtils.exportPackage(pack);
		XMLAssert.assertXpathExists("package/subscriptionUrl", pack.getSerializedPackage().getHeader());
	}
	
	/**
	 * @verifies that the subscription URL deserializes properly
	 */
	@Test
	public void subscriptionUrldeserializationTest() throws Exception {
		ImportedPackage pack = ImportExportTestUtils.importPackage(1, "ABC");
		Assert.assertEquals("http://google.pl", pack.getSubscriptionUrl());
	}
	
	/**
	 * @verifies {@link PublishUtils#extractGroupFromPath(String, String)}
	 */
	@Test
	public void PublishUtils_extractGroupFromPath() {
		String prefix = PublishController.PREFIX;
		Assert.assertEquals("GGGG",
		    PublishUtils.extractGroupFromPath("/openmrs" + PublishController.OLD_PACKAGE_PATH + "/GGGG.form", prefix));
		Assert.assertEquals("GGGG",
		    PublishUtils.extractGroupFromPath(PublishController.OLD_PACKAGE_PATH + "/GGGG.form", prefix));
		Assert.assertEquals("GGGG", PublishUtils.extractGroupFromPath(PublishController.OLD_PACKAGE_PATH + "/GGGG", prefix));
		Assert.assertEquals("GGGG",
		    PublishUtils.extractGroupFromPath(PublishController.OLD_PACKAGE_PATH + "/GGGG/download.form", prefix));
		Assert.assertEquals("GGGG",
		    PublishUtils.extractGroupFromPath(PublishController.OLD_PACKAGE_PATH + "/GGGG/download", prefix));
		Assert.assertEquals("GGGG",
		    PublishUtils.extractGroupFromPath(PublishController.OLD_PACKAGE_PATH + "/GGGG/1/download.form", prefix));
		Assert.assertEquals("GGGG",
		    PublishUtils.extractGroupFromPath(PublishController.OLD_PACKAGE_PATH + "/GGGG/1/download", prefix));
	}
	
	/**
	 * @verifies {@link PublishUtils#extractVersionFromPath(String, String);
	 */
	@Test
	public void s() {
		String prefix = PublishController.PREFIX;
		Assert.assertEquals((Integer) 1,
		    PublishUtils.extractVersionFromPath("/openmrs" + PublishController.OLD_PACKAGE_PATH + "/GR/1.form", prefix));
		Assert.assertEquals((Integer) 1,
		    PublishUtils.extractVersionFromPath("/openmrs" + PublishController.OLD_PACKAGE_PATH + "/GR/1", prefix));
		Assert.assertEquals((Integer) 1,
		    PublishUtils.extractVersionFromPath("/openmrs" + PublishController.OLD_PACKAGE_PATH + "/GR/1.form", prefix));
		Assert.assertEquals((Integer) 1, PublishUtils.extractVersionFromPath("/openmrs" + PublishController.OLD_PACKAGE_PATH
		        + "/GR/1/download.form", prefix));
		Assert.assertEquals((Integer) 1,
		    PublishUtils.extractVersionFromPath("/openmrs" + PublishController.OLD_PACKAGE_PATH + "/GR/1/download", prefix));
		
	}
	
	/**
	 * @verifies {@link PublishUtils#createRelativeResourcePath(String)}
	 */
	@Test
	public void PublishUtils_createRelativePublishPath() throws Exception {
		Field f = OpenmrsConstants.class.getDeclaredField("OPENMRS_VERSION");
		setFinalStatic(f, "1.6.3 Build 1234");
		Assert.assertEquals("group/download.form", PublishUtils.createRelativeResourcePath("group/download"));
		
		setFinalStatic(f, "1.8.0 SNAPSHOT 1234");
		Assert.assertEquals("group/download.form", PublishUtils.createRelativeResourcePath("group/download"));
		
		setFinalStatic(f, "1.9.0-SNAPSHOT");
		Assert.assertEquals("group/download", PublishUtils.createRelativeResourcePath("group/download"));
		
		setFinalStatic(f, "1.9"); //invalid value
		Assert.assertEquals("group/download.form", PublishUtils.createRelativeResourcePath("group/download"));
		
		setFinalStatic(f, "qwerty"); //invalid value
		Assert.assertEquals("group/download.form", PublishUtils.createRelativeResourcePath("group/download"));
		
		setFinalStatic(f, "1.8.1");
		Assert.assertEquals("group/download", PublishUtils.createRelativeResourcePath("group/download"));
		
		setFinalStatic(f, null);
		Assert.assertEquals("group/download.form", PublishUtils.createRelativeResourcePath("group/download"));
	}
	
	/**
	 * @verifies {@link PublishUtils#createAbsolutePublishURL(ExportedPackage)}
	 */
	@Test
	public void PublishUtils_createAbsolutePublishURL() throws Exception {
		Context.getAdministrationService().saveGlobalProperty(
		    new GlobalProperty(MetadataSharingConsts.GP_URL_PREFIX, "http://google.pl"));
		ExportedPackage pack = new ExportedPackage();
		pack.setGroupUuid("GROUP");
		Assert.assertEquals("http://google.pl/ws/rest/metadatasharing/package/GROUP/latest.form",
		    PublishUtils.createAbsolutePublishURL(pack));
	}
	
	/**
	 * Changes the value of the given final field. Used to change OPENMRS_VERSION constant
	 */
	private static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);
		
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		
		field.set(null, newValue);
	}
}
