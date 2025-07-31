package org.openmrs.module.metadatasharing.handler.impl;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.SerializedPackage;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class ConceptReferenceTermHandlerTest extends BaseModuleContextSensitiveTest {
	
	private ConceptService conceptService;
	
	public static final String CONCEPT_SOURCE_UUID = UUID.randomUUID().toString();
	
	public static final String CONCEPT_REFERENCE_TERMS_HEADER = "mds-concept-reference-terms-header.xml";
	
	public static final String CONCEPT_REFERENCE_TERMS_METADATA = "mds-concept-reference-terms-metadata.xml";
	
	private String headerXml;
	
	private String contentXml;
	
	@Before
	public void before() throws IOException {
		File file1 = new File(ClassLoader.getSystemResource(CONCEPT_REFERENCE_TERMS_HEADER).getPath());
		File file2 = new File(ClassLoader.getSystemResource(CONCEPT_REFERENCE_TERMS_METADATA).getPath());
		
		headerXml = FileUtils.readFileToString(file1, "UTF-8");
		contentXml = FileUtils.readFileToString(file2, "UTF-8");
		
		conceptService = Context.getConceptService();
		
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		metadataImporter.loadSerializedPackage(new SerializedPackage(headerXml, contentXml));
		metadataImporter.importPackage();
	}
	
	@Test
	public void shouldImport() {
		Assert.assertNotNull("useless concept exists?", conceptService.getConcept("useless"));
		Assert.assertEquals("useless", conceptService.getConcept("useless").getName().getName());
		
	}
	
	@Test
	public void shouldSaveTermA(){
		Assert.assertEquals("name-1",
		    conceptService.getConceptReferenceTermByUuid("e6be7ef5-29bd-487e-b13f-6749f7e6616b").getName());
		Assert.assertEquals("CODE-1",
		    conceptService.getConceptReferenceTermByUuid("e6be7ef5-29bd-487e-b13f-6749f7e6616b").getCode());
	}
	
	@Test
	public void shouldSaveTermB() {
		Assert.assertEquals("name-2",
		    conceptService.getConceptReferenceTermByUuid("a26bea69-1102-4b38-9f7e-4aaf28a2ae44").getName());
		Assert.assertEquals("CODE-2",
		    conceptService.getConceptReferenceTermByUuid("a26bea69-1102-4b38-9f7e-4aaf28a2ae44").getCode());
	}
	
}
