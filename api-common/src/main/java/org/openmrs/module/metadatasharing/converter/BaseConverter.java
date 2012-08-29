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
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.util.Version;
import org.openmrs.serialization.SerializationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Allows to perform an xml conversion before deserialization.
 */
public abstract class BaseConverter {
	
	public static class ConverterContext {
		
		public Map<String, Node> references = new HashMap<String, Node>();
		
		public int freeReferenceId = Integer.MAX_VALUE;
	}
	
	public abstract void convert(Document doc, Version fromVersion, Version toVersion, ConverterContext context)
	    throws SerializationException;
	
	/**
	 * Stores or returns a reference to the given node.
	 * 
	 * @param node
	 * @return the referenced node
	 */
	protected Node reference(Node node, ConverterContext context) {
		String reference = ((Element) node).getAttribute("reference");
		
		Node referencedNode = null;
		if (!StringUtils.isBlank(reference)) {
			referencedNode = context.references.get(reference);
		}
		
		if (referencedNode == null) {
			String id = ((Element) node).getAttribute("id");
			
			if (!StringUtils.isBlank(id)) {
				context.references.put(id, node);
			}
			
			referencedNode = node;
		}
		
		return referencedNode;
	}
	
	protected Node newReference(Element element, ConverterContext context) {
		element.setAttribute("id", "" + context.freeReferenceId--);
		
		return reference(element, context);
	}
	
	protected void newReferenced(Element element, Node referencedNode) {
		String id = ((Element) referencedNode).getAttribute("id");
		
		element.setAttribute("reference", id);
	}
	
	protected Element newElement(Document doc, String name, Object object, ConverterContext context)
	    throws SerializationException {
		String serializedObject = MetadataSharing.getInstance().getMetadataSerializer().serialize(object);
		
		Document tmpDoc;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			tmpDoc = db.parse(new InputSource(new StringReader(serializedObject)));
		}
		catch (Exception e) {
			throw new SerializationException(e);
		}
		
		Element element = tmpDoc.getDocumentElement();
		element = (Element) tmpDoc.renameNode(element, "", name);
		
		element = (Element) newReference(element, context);
		newChildReferences(element, context);
		
		Element importedElement = (Element) doc.importNode(element, true);
		
		return importedElement;
	}
	
	private void newChildReferences(Element element, ConverterContext context) {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
	        Node child = children.item(i);
	        if (child instanceof Element) {
	        	Element childElement = (Element) child;
	        	String id = childElement.getAttribute("id");
	        	if (!StringUtils.isBlank(id)) {
	        		newReference(childElement, context);
	        		newChildReferences(childElement, context);
	        	} else {
	        		String reference = childElement.getAttribute("reference");
	        		if (!StringUtils.isBlank(reference)) {
	        			reference(childElement, context);
	        		}
	        	}
	        }
        }
	}
	
	/**
	 * Creates a text element
	 * 
	 * @param doc
	 * @param name
	 * @param value
	 * @return the text element
	 */
	protected Element newTextElement(Document doc, String name, String value) {
		Element el = doc.createElement(name);
		el.setTextContent(value);
		return el;
	}
	
	/**
	 * Gets an immediate child element of the given node
	 * 
	 * @param parent
	 * @param childName
	 * @return the element or null if not present
	 */
	protected Element getChildElement(Node parent, String childName) {
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child instanceof Element) {
				if (((Element) child).getTagName().equals(childName))
					return (Element) child;
			}
		}
		return null;
	}
	
	protected static String toString(Node doc) throws SerializationException {
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
