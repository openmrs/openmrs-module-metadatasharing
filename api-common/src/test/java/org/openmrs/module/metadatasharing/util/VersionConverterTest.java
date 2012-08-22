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
package org.openmrs.module.metadatasharing.util;

import java.io.File;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class VersionConverterTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link VersionConverter#convertOutgoingMessage(String,String)}
	 */
	@Test
	@Verifies(value = "should convert a message down to one dot six", method = "convert(String,String,String)")
	public void convert_shouldConvertAMessageDownToOneDotSix() throws Exception {
		String filePath = ClassLoader.getSystemResource("VersionConverterTest-metadata-to-16.xml").getPath();
		String xml = FileUtils.readFileToString(new File(filePath), "UTF-8");
		Assert.assertTrue(xml.indexOf("conceptNameType") > 0);
		Assert.assertTrue(xml.indexOf("localePreferred") > 0);
		Assert.assertTrue(xml.indexOf("FULLY_SPECIFIED") > 0);
		Assert.assertTrue(xml.indexOf("SHORT") > 0);
		Assert.assertTrue(xml.indexOf("preferred name in a language") < 0);
		
		String output = new VersionConverter().convert(xml, "", "1.6.3");
		
		Assert.assertTrue(output.indexOf("conceptNameType") < 0);
		Assert.assertTrue(output.indexOf("localePreferred") < 0);
		Assert.assertTrue(output.indexOf("FULLY_SPECIFIED") < 0);
		Assert.assertTrue(output.indexOf("SHORT") < 0);
		Assert.assertTrue(output.indexOf("preferred name in a language") > 0);
	}
	
	/**
	 * @see {@link VersionConverter#convertIncomingMessage(String,String)}
	 */
	@Test
	@Verifies(value = "should convert a message from one dot six", method = "convert(String,String,String)")
	public void convert_shouldConvertAMessageFromOneDotSix() throws Exception {
		String filePath = ClassLoader.getSystemResource("VersionConverterTest-metadata-from-16.xml").getPath();
		String xml = FileUtils.readFileToString(new File(filePath), "UTF-8");
		Assert.assertTrue(xml.indexOf("conceptNameType") < 0);
		Assert.assertTrue(xml.indexOf("localePreferred") < 0);
		Assert.assertTrue(xml.indexOf("FULLY_SPECIFIED") < 0);
		Assert.assertTrue(xml.indexOf("SHORT") < 0);
		Assert.assertTrue(xml.indexOf("preferred name in a language") > 0);
		
		String output = new VersionConverter().convert(xml, "1.6.3", "");
		
		Assert.assertTrue(output.indexOf("conceptNameType") > 0);
		Assert.assertTrue(output.indexOf("localePreferred") > 0);
		Assert.assertTrue(output.indexOf("FULLY_SPECIFIED") > 0);
		Assert.assertTrue(output.indexOf("SHORT") > 0);
		Assert.assertTrue(output.indexOf("preferred name in a language") < 0);
	}
	
	@Test
	public void convertFrom16To18() throws Exception {
		org.openmrs16.Concept concept = new org.openmrs16.Concept();
		concept.setUuid(UUID.randomUUID().toString());
		concept.addName(new org.openmrs16.ConceptName("Name", Locale.US));
		concept.addName(new org.openmrs16.ConceptName("Synonym", Locale.US));
		String xml = MetadataSharing.getInstance().getMetadataSerializer().serialize(concept);
		
		xml = xml.replace("org.openmrs16", "org.openmrs");
		
		String convertedXml = new VersionConverter().convert(xml, "1.6.3", "1.8.3");
		
		//Using Concept from 1.8
		convertedXml = convertedXml.replace("org.openmrs", "org.openmrs18");
		
		org.openmrs18.Concept concept18 = MetadataSharing.getInstance().getMetadataSerializer()
		        .deserialize(convertedXml, org.openmrs18.Concept.class);
		
		Assert.assertNotNull(concept18);
	}
}
