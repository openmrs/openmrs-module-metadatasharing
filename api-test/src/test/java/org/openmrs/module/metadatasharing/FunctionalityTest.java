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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptSource;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.wrapper.PackageExporter;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.serialization.SerializationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * This class holds tests that show the high-level user-facing functionality of this module. Each
 * week we'll add additional deliverables here, to help define method signatures
 */
public class FunctionalityTest extends BaseModuleContextSensitiveTest {
	
	private MetadataSharingService service;
	
	private AdministrationService administrationService;
	
	private ConceptService conceptService;
	
	public static final String CONCEPT_SOURCE_UUID = UUID.randomUUID().toString();
	
	private String headerXml;
	
	private String contentXml;
	
	@Before
	public void before() throws Exception {
		//executeDataSet("FunctionalityTest.xml");
		service = Context.getService(MetadataSharingService.class);
		conceptService = Context.getConceptService();
		administrationService = Context.getAdministrationService();
		
		ConceptSource conceptSource = new ConceptSource();
		conceptSource.setUuid(CONCEPT_SOURCE_UUID);
		conceptSource.setName(CONCEPT_SOURCE_UUID);
		conceptService.saveConceptSource(conceptSource);
		
		GlobalProperty gp = new GlobalProperty(MetadataSharingConsts.GP_SYSTEM_CONCEPT_SOURCE, CONCEPT_SOURCE_UUID);
		administrationService.saveGlobalProperty(gp);
		
		Location usa = new Location();
		usa.setName("USA");
		usa.setDescription("USA");
		usa.setUuid("USA");
		Context.getLocationService().saveLocation(usa);
		Location wa = new Location();
		wa.setName("Washington State");
		wa.setDescription("Washington State");
		wa.setUuid("Washington State");
		Context.getLocationService().saveLocation(wa);
		Location seattle = new Location();
		seattle.setName("Seattle");
		seattle.setDescription("Seattle");
		seattle.setUuid("Seattle");
		Context.getLocationService().saveLocation(seattle);
		usa.addChildLocation(wa);
		wa.addChildLocation(seattle);
		
		PackageExporter exporter = MetadataSharing.getInstance().newPackageExporter();
		exporter.getPackage().setName("Package");
		exporter.getPackage().setDescription("Test package");
		exporter.addItem(usa);
		exporter.exportPackage();
		headerXml = exporter.getExportedPackage().getSerializedPackage().getHeader();
		contentXml = exporter.getExportedPackage().getSerializedPackage().getMetadata()[0];
	}
	
	/**
	 * Our initial test-case is going to be locations
	 */
	@Test
	public void listAvailableTypesToExport() throws Exception {
		Map<String, Class<?>> types = Handler.getRegisteredTypes();
		Assert.assertTrue(types.containsKey("Location"));
	}
	
	/**
	 * Getting available items of a particular type of metadata should delegate to an underlying
	 * OpenMRS service method.
	 */
	@Test
	public void listAvailableItemsShouldDelegateCorrectly() throws Exception {
		List<Object> items = Handler.getItems(Location.class, true, null, null, null);
		List<Location> locations = Context.getLocationService().getAllLocations();
		Assert.assertTrue(items.containsAll(locations));
		Assert.assertEquals(locations.size(), items.size());
	}
	
	/**
	 * Returned item of a particular type and uuid of metadata should be the same as the one
	 * returned by underlying OpenMRS service method.
	 */
	@Test
	public void getItemByUuid() throws Exception {
		List<Location> locations = Context.getLocationService().getAllLocations();
		Object item = Handler.getItemByUuid(Location.class, locations.get(0).getUuid());
		Assert.assertEquals(locations.get(0), item);
	}
	
	/**
	 * Should create a package containing some items and create xml.
	 */
	@Test
	public void createPackageAndGenerateXml() throws Exception {
		PackageExporter exporter = MetadataSharing.getInstance().newPackageExporter();
		
		exporter.addItem(Context.getLocationService().getLocationByUuid("USA"));
		exporter.addItem(Context.getLocationService().getLocationByUuid("Washington State"));
		exporter.getPackage().setName("Package");
		exporter.getPackage().setDescription("Test package");
		exporter.exportPackage();
		
		Collection<Item> implicitItems = exporter.getExportedPackage().getRelatedItems();
		Assert.assertTrue(implicitItems.size() == 1);
		
		ExportedPackage pack = service.saveExportedPackage(exporter.getExportedPackage());
		
		String headerXml = pack.getSerializedPackage().getHeader();
		XMLAssert.assertXpathExists("/package/@uuid", headerXml);
		XMLAssert.assertXpathExists("/package/name", headerXml);
		XMLAssert.assertXpathExists("/package/description", headerXml);
		XMLAssert.assertXpathExists("/package/dateCreated", headerXml);
		String contentXml = pack.getSerializedPackage().getMetadata()[0];
		XMLAssert.assertXpathExists("/list/org.openmrs.Location[@uuid='USA']", contentXml);
		XMLAssert.assertXpathExists("/list/org.openmrs.Location[@uuid='USA']"
		        + "/childLocations/org.openmrs.Location[@uuid='Washington State']"
		        + "/childLocations/org.openmrs.Location[@uuid='Seattle']", contentXml);
		XMLAssert.assertXpathNotExists("/list/org.openmrs.Location/creator/*", contentXml);
		XMLAssert.assertXpathNotExists("/list/org.openmrs.Location/retiredBy/*", contentXml);
		XMLAssert.assertXpathNotExists("/list/org.openmrs.Location/changedBy/*", contentXml);
		XMLAssert.assertXpathNotExists("/list/org.openmrs.Location/locationId/*", contentXml);
	}
	
	/**
	 * Test importing the package that was generated in the last test
	 */
	@Test
	public void importPackage() throws Exception {
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		metadataImporter.loadSerializedPackage(new SerializedPackage(headerXml, contentXml));
		metadataImporter.importPackage();
		
		LocationService ls = Context.getLocationService();
		Assert.assertNotNull("USA should exist", ls.getLocation("USA"));
		Assert.assertSame(ls.getLocation("Washington State").getParentLocation(), ls.getLocation("USA"));
		Assert.assertNotNull("Washington State should exist", ls.getLocation("Washington State"));
		Assert.assertSame(ls.getLocation("Seattle").getParentLocation(), ls.getLocation("Washington State"));
		Assert.assertNotNull("Seattle should exist", ls.getLocation("Seattle"));
	}
	
	@Test
	public void importPackageTwice() throws Exception {
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		metadataImporter.loadSerializedPackage(new SerializedPackage(headerXml, contentXml));
		metadataImporter.importPackage();
		
		metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		metadataImporter.loadSerializedPackage(new SerializedPackage(headerXml, contentXml));
		metadataImporter.importPackage();
		
		LocationService ls = Context.getLocationService();
		Assert.assertNotNull("USA should exist", ls.getLocation("USA"));
		Assert.assertNotNull("Washington State should exist", ls.getLocation("Washington State"));
		Assert.assertSame(ls.getLocation("Washington State").getParentLocation(), ls.getLocation("USA"));
		Assert.assertNotNull("Seattle should exist", ls.getLocation("Seattle"));
		Assert.assertSame(ls.getLocation("Seattle").getParentLocation(), ls.getLocation("Washington State"));
	}
	
	/**
	 * Getting items of the type ConceptAdapter should return ConceptAdapters with underlying
	 * Concept objects.
	 */
	@Test
	public void listAvailableConcepts() throws Exception {
		List<Object> items = Handler.getItems(Concept.class, true, null, null, null);
		List<Concept> concepts = Context.getConceptService().getAllConcepts();
		
		Assert.assertEquals(concepts.size(), items.size());
	}
	
	/**
	 * Getting item by UUID of the type ConceptAdapter should return ConceptAdapter with the same
	 * underlying Concept as the one returned by ConceptService.
	 */
	@Test
	public void getConceptByUuid() throws Exception {
		List<Concept> concepts = Context.getConceptService().getAllConcepts();
		Object item = Handler.getItemByUuid(Concept.class, concepts.get(0).getUuid());
		Assert.assertEquals(concepts.get(0), item);
	}
	
	@Test
	public void importPackageWithConcept() throws IOException {
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		metadataImporter.loadSerializedPackage(new SerializedPackage(headerXml, contentXml));
		metadataImporter.importPackage();
	}
	
	@Test
	public void importPackageWithConceptTwice() throws IOException {
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		metadataImporter.loadSerializedPackage(new SerializedPackage(headerXml, contentXml));
		metadataImporter.importPackage();
		
		metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		metadataImporter.loadSerializedPackage(new SerializedPackage(headerXml, contentXml));
		metadataImporter.importPackage();
	}
	
	/**
	 * org.openmrs.module.metadatasharing.reflection.OpenmrsClassScanner.invokeServiceSaveItem(
	 * OpenmrsClassScanner.java:227) ... 119 more Caused by: org.hibernate.PropertyValueException:
	 * not-null property references a null or transient value:
	 * org.openmrs.ConceptAnswer.answerConcept
	 * 
	 * @throws IOException
	 * @throws SerializationException
	 */
	@Test
	@Ignore
	public void importPackageWithConcepts() throws IOException, SerializationException {
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		
		URL url = ClassLoader.getSystemResource("concepts.zip");
		File file = new File(url.getFile());
		
		metadataImporter.loadSerializedPackageStream(new FileInputStream(file));
		metadataImporter.importPackage();
	}
	
	/**
	 * org.openmrs.module.metadatasharing.reflection.OpenmrsClassScanner.invokeServiceSaveItem(
	 * OpenmrsClassScanner.java:227) ... 119 more Caused by: org.hibernate.PropertyValueException:
	 * not-null property references a null or transient value: org.openmrs.ConceptSet.concept
	 * 
	 * @throws IOException
	 * @throws SerializationException
	 */
	@Test
	@Ignore
	public void importPackageWithConcepts2() throws IOException, SerializationException {
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		
		URL url = ClassLoader.getSystemResource("concepts2.zip");
		File file = new File(url.getFile());
		
		metadataImporter.loadSerializedPackageStream(new FileInputStream(file));
		metadataImporter.importPackage();
	}
}
