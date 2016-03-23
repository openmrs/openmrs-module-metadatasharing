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
package org.openmrs.module.metadatasharing.converter;

import java.io.File;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.metadatasharing.converter.BaseConverter.ConverterContext;
import org.openmrs.module.metadatasharing.util.Version;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.w3c.dom.Document;

public class ConceptMapConverterTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEXT_CONCEPT_MAP_XML_FILE = "testConceptMapXmlFile.xml";
	
	private static Document document;
	
	private static String originalXml;
	
	private static final String CONCEPT_MAP_TYPE = "conceptMapType";
	
	private static final String CONCEPT_REFERENCE_TERM = "conceptReferenceTerm";
	
	@Before
	public void before() throws Exception {
		File file = new File(ClassLoader.getSystemResource(TEXT_CONCEPT_MAP_XML_FILE).getPath());
		originalXml = FileUtils.readFileToString(file, "UTF-8");
		document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		        .parse(ClassLoader.getSystemResourceAsStream(TEXT_CONCEPT_MAP_XML_FILE));
	}
	
	/**
	 * @see {@link ConceptMapConverter#convert(Document,Version,Version,ConverterContext)}
	 */
	@Test
	@Verifies(value = "should replace source if the from version is pre one nine", method = "convert(Document,Version,Version,ConverterContext)")
	public void convert_shouldReplaceSourceIfTheFromVersionIsPreOneNine() throws Exception {
		Assert.assertTrue(originalXml.indexOf(CONCEPT_MAP_TYPE) < 0);
		Assert.assertTrue(originalXml.indexOf(CONCEPT_REFERENCE_TERM) < 0);
		String convertedXml = convertXml("1.8.3", "1.10.0");
		Assert.assertTrue(convertedXml.indexOf(CONCEPT_MAP_TYPE) > 0);
		Assert.assertTrue(convertedXml.indexOf(CONCEPT_REFERENCE_TERM) > 0);
	}
	
	/**
	 * @see {@link ConceptMapConverter#convert(Document,Version,Version,ConverterContext)}
	 */
	@Test
	@Verifies(value = "should replace nothing if the to version is post one nine", method = "convert(Document,Version,Version,ConverterContext)")
	public void convert_shouldReplaceNothingIfTheToVersionIsPostOneNine() throws Exception {
		String convertedXml = convertXml("1.9.0", "1.9.1");
		Assert.assertTrue(convertedXml.indexOf(CONCEPT_MAP_TYPE) < 0);
		Assert.assertTrue(convertedXml.indexOf(CONCEPT_REFERENCE_TERM) < 0);
	}
	
	private static String convertXml(String formVersion, String toVersion) throws Exception {
		new ConceptMapConverter().convert(document, new Version(formVersion), new Version(toVersion),
		    new BaseConverter.ConverterContext());
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		StringWriter stringWriter = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
		return stringWriter.toString();
	}
}
