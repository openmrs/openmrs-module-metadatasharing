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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.converter.BaseConverter.ConverterContext;
import org.openmrs.module.metadatasharing.util.Version;
import org.openmrs.serialization.SerializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Converts xml using {@link BaseConverter}s.
 */
@Component(MetadataSharingConsts.MODULE_ID + ".ConverterEngine")
public class ConverterEngine {
	
	@Autowired(required = false)
	private List<BaseConverter> converters;
	
	public String convert(String xml, String fromVersion, String toVersion) throws SerializationException {
		if (fromVersion == null || toVersion == null || converters == null) {
			return xml;
		}
		
		Version from = new Version(fromVersion);
		Version to = new Version(toVersion);
		
		Document document = fromXML(xml);
		
		ConverterContext context = new BaseConverter.ConverterContext();
		
		for (BaseConverter converter : converters) {
			if (converter != null) {
				converter.convert(document, from, to, context);
			}
		}
		
		xml = toXML(document);
		return xml;
	}
	
	private Document fromXML(String xml) throws SerializationException {
		Document doc;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(new InputSource(new StringReader(xml)));
		}
		catch (Exception e) {
			throw new SerializationException(e);
		}
		return doc;
	}
	
	private static String toXML(Node doc) throws SerializationException {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(new DOMSource(doc), result);
			return result.getWriter().toString();
		}
		catch (Exception e) {
			throw new SerializationException(e);
		}
	}
}
