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


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Functional test
 * 
 * @see org.openmrs.module.metadatasharing.serializer.MetadataSerializer
 */
public class MetadataSerializerTest extends BaseModuleContextSensitiveTest {
	private OpenmrsSerializer serializer;
	private LocationService locationService;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void before() throws Exception {
		executeDataSet("MetadataSerializerTest.xml");
		serializer = Context.getSerializationService().getSerializer(MetadataSerializer.class);
		locationService = Context.getLocationService();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.serializer.MetadataSerializer#serialize(Object)
	 */
	@Test
	@Verifies(value = "should not serialize User", method = "serialize(Object)")
	public void serialize_shouldNotSerializeUser() throws Exception {
		//given
		Location location = locationService.getLocationByUuid("dc5c1fcc-0459-4201-bf70-0b90535ba362");
		//when
		String xml = serializer.serialize(location);
		//then
		XMLAssert.assertXpathEvaluatesTo("dc5c1fcc-0459-4201-bf70-0b90535ba362", "/org.openmrs.Location/@uuid",
		    xml);
		XMLAssert.assertXpathNotExists("/org.openmrs.Location/creator/*", xml);
	}

	/**
	 * @see {@link MetadataSerializer#serialize(Object)}
	 * 
	 */
	@Test
	@Verifies(value = "should serialize clone if possible", method = "serialize(Object)")
	public void serialize_shouldSerializeCloneIfPossible() throws Exception {
		//given
		Location location = locationService.getLocationByUuid("dc5c1fcc-0459-4201-bf70-0b90535ba362");
		LocationMock locationMock = new LocationMock(location);
		Location lastChild = null;
		for (Location child: locationMock.getChildLocations(true)) {
			child.setParentLocation(locationMock);
			lastChild = child;
		}
		
		//when
		String xml = serializer.serialize(lastChild);
		
		//then
		XMLAssert.assertXpathEvaluatesTo("dc5c1fcc-0459-4201-bf70-0b90535ba362", "/org.openmrs.Location/parentLocation/@uuid",
		    xml);
		XMLAssert.assertXpathEvaluatesTo("org.openmrs.module.metadatasharing.serializer.ShareableLocation", "/org.openmrs.Location/parentLocation/@resolves-to",
		    xml);
		lastChild = serializer.deserialize(xml, Location.class);
		Assert.assertTrue(lastChild.getParentLocation() instanceof ShareableLocation);
	}
	
	public static class NumberTest  {
    	public Integer i = 1000000;
		public Short s = 10000;
		public Long l = 1000000L;
		public Double d = 1000000.1;
		public Float f = 1000000.1f;
    }

	/**
     * @see MetadataSerializer#serialize(Object)
     * @verifies serialize numbers in us locale format
     */
    @Test
    public void serialize_shouldSerializeNumbersInLocaleIndependentFormat() throws Exception {
    	Locale defaultLocale = Locale.getDefault();
    	Locale.setDefault(Locale.US);
    	NumberTest metadata = new NumberTest();
		String xml = serializer.serialize(metadata);
		XMLAssert.assertXpathEvaluatesTo("1000000", "org.openmrs.module.metadatasharing.serializer.MetadataSerializerTest_-NumberTest/i", xml);
		XMLAssert.assertXpathEvaluatesTo("10000", "org.openmrs.module.metadatasharing.serializer.MetadataSerializerTest_-NumberTest/s", xml);
		XMLAssert.assertXpathEvaluatesTo("1000000", "org.openmrs.module.metadatasharing.serializer.MetadataSerializerTest_-NumberTest/l", xml);
		XMLAssert.assertXpathEvaluatesTo("1000000.1", "org.openmrs.module.metadatasharing.serializer.MetadataSerializerTest_-NumberTest/d", xml);
		XMLAssert.assertXpathEvaluatesTo("1000000.1", "org.openmrs.module.metadatasharing.serializer.MetadataSerializerTest_-NumberTest/f", xml);
		Locale.setDefault(defaultLocale);
    }
    
    

	/**
     * @see MetadataSerializer#deserialize(String,Class)
     * @verifies deserialize number in us locale format
     */
    @Test
    public void deserialize_shouldDeserializeNumberInUsLocaleFormat() throws Exception {
    	Locale defaultLocale = Locale.getDefault();
    	Locale.setDefault(Locale.GERMANY);
	    String xml = "<org.openmrs.module.metadatasharing.serializer.MetadataSerializerTest_-NumberTest id=\"1\">" +
	    		"<i>1,000,000</i><s>10,000</s><l>1,000,000</l><d>1,000,000.1</d><f>1,000,000.1</f>" +
	    		"</org.openmrs.module.metadatasharing.serializer.MetadataSerializerTest_-NumberTest>";
	    NumberTest numberTest = serializer.deserialize(xml, NumberTest.class);
		Assert.assertEquals(Integer.valueOf(1000000), numberTest.i);
		Assert.assertEquals(Short.valueOf((short) 10000), numberTest.s);
		Assert.assertEquals(Long.valueOf(1000000), numberTest.l);
		Assert.assertEquals(Double.valueOf(1000000.1), numberTest.d);
		Assert.assertEquals(Float.valueOf(1000000.1f), numberTest.f);
		Locale.setDefault(defaultLocale);
    }
    
    /**
     * @see MetadataSerializer#deserialize(String,Class)
     * @verifies not deserialize ENTITY
     */
    @Test
    public void deserialize_shouldNotDeserializeEntity() throws Exception {
	    String xml = "<!DOCTYPE ZSL [<!ENTITY xxe1 \"some name\" >]>"
	    		+ "<package id=\"1\" uuid=\"eecb64f8-35b0-412b-acda-3d83edf4ee63\">" +
	    		"<name>&xxe1;</name>" +
	    		"</package>";
	    
	    thrown.expectMessage("could not resolve entity named 'xxe1'");
	    ExportedPackage exportedPackage = serializer.deserialize(xml, ExportedPackage.class);
	    
	    assertThat(exportedPackage.getName(), is("&xxe1;"));
    }
    
    /**
     * @see MetadataSerializer#serialize(Object)
     * @verifies serialize numbers in us locale format
     */
    @Test
    public void serialize_shouldDeserializeNumbersInLocaleIndependentFormat() throws Exception {
    	Locale defaultLocale = Locale.getDefault();
    	Locale.setDefault(Locale.US);
	    String xml = "<org.openmrs.module.metadatasharing.serializer.MetadataSerializerTest_-NumberTest id=\"1\">" +
	    		"<i>1000000</i><s>10000</s><l>1000000</l><d>1000000.1</d><f>1000000.1</f>" +
	    		"</org.openmrs.module.metadatasharing.serializer.MetadataSerializerTest_-NumberTest>";
	    NumberTest numberTest = serializer.deserialize(xml, NumberTest.class);
		Assert.assertEquals(Integer.valueOf(1000000), numberTest.i);
		Assert.assertEquals(Short.valueOf((short) 10000), numberTest.s);
		Assert.assertEquals(Long.valueOf(1000000), numberTest.l);
		Assert.assertEquals(Double.valueOf(1000000.1), numberTest.d);
		Assert.assertEquals(Float.valueOf(1000000.1f), numberTest.f);
		Locale.setDefault(defaultLocale);
    }
}