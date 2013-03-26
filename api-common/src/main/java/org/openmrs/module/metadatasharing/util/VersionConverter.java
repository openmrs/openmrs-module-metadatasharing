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
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.serialization.SerializationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Performs XML text transformations to allow importing metadata between 1.6.x and 1.7+ openmrs
 * versions
 */
public class VersionConverter {
	
	private static final String FULLY_SPECIFIED = "FULLY_SPECIFIED";
	
	private static final String SHORT = "SHORT";
	
	//cache for mappings between found concept name tag names and their uuids for 
	//later quick access in case we are importing many concepts in one go
	Map<String, String> tagNameUuidMap = null;
	
	Map<String, Node> references = new HashMap<String, Node>();
	
	Integer referenceId = Integer.MAX_VALUE;
	
	/**
	 * Converts an incoming 1.6 xml to match the 1.7+ concept name data model or vice versa,
	 * depending on the values of the fromVersion and String toVersion arguments
	 * 
	 * @param xml
	 * @param fromVersion
	 * @param toVersion
	 * @return
	 * @throws SerializationException
	 * @should convert a message from one dot six
	 * @should convert a message down to one dot six
	 */
	public String convert(String xml, String fromVersion, String toVersion) throws SerializationException {
		if (fromVersion == null || toVersion == null)
			return xml;

        // currently the only conversions we handle are between 1.6.x and 1.7+ versions of concepts
        // return fast if we have no conversions to do
        int numThatAre16 = (fromVersion.startsWith("1.6.") ? 1 : 0) + (toVersion.startsWith("1.6.") ? 1 : 0);
        if (numThatAre16 != 1) {
            return xml;
        }
		
		Document doc = fromXML(xml);
		
		if (fromVersion.startsWith("1.6.") && !toVersion.startsWith("1.6.")) {
			try {
				NodeList nameNodes = doc.getElementsByTagName(ConceptName.class.getName());
				Map<String, Element> conceptNameTagNodesCache = new HashMap<String, Element>();
				for (int i = 0; i < nameNodes.getLength(); i++) {
					Element conceptNameNode = (Element) nameNodes.item(i);
					
					// handle ConceptNameTags
					NodeList conceptNameTagNodes = conceptNameNode.getElementsByTagName(ConceptNameTag.class.getName());
					boolean isLocalePreferred = false;
					String conceptNameType = null;
					for (int j = 0; j < conceptNameTagNodes.getLength(); j++) {
						Element conceptNameTagNode = (Element) conceptNameTagNodes.item(j);
						if (conceptNameTagNode != null) {
							if (conceptNameTagNode.hasAttribute("id")) {
								//Remember id so that we can get it later if referenced.
								conceptNameTagNodesCache.put(conceptNameTagNode.getAttribute("id"), conceptNameTagNode);
							} else {
								conceptNameTagNode = conceptNameTagNodesCache.get(conceptNameTagNode
								        .getAttribute("reference"));
							}
							
							Element tagNode = getChildElement(conceptNameTagNode, "tag");
							if (tagNode != null) {
								String conceptNameTagName = tagNode.getTextContent();
								if (conceptNameTagName != null) {
									if (conceptNameTagName.toLowerCase().startsWith("preferred_")) {
										isLocalePreferred = true;
									} else if (conceptNameTagName.equalsIgnoreCase("preferred")) {
										conceptNameType = FULLY_SPECIFIED;
										break;
									} else if (conceptNameTagName.equalsIgnoreCase(SHORT)) {
										conceptNameType = SHORT;
										break;
									}
								}
							}
						}
					}
					
					conceptNameNode.appendChild(newTextElement(doc, "localePreferred", String.valueOf(isLocalePreferred)));
					if (conceptNameType != null) {
						conceptNameNode.appendChild(newTextElement(doc, "conceptNameType", conceptNameType));
					}
					
					// just remove tags elements
					Element tagsNode = getChildElement(conceptNameNode, "tags");
					if (tagsNode != null) {
						conceptNameNode.removeChild(tagsNode);
					}
				}
			}
			catch (Exception e) {
				throw new SerializationException("Error while converting from 1.6.x:", e);
			}
			
		} else if (toVersion.startsWith("1.6.") && !fromVersion.startsWith("1.6.")) {
			tagNameUuidMap = new HashMap<String, String>();
			try {
				NodeList nameNodes = doc.getElementsByTagName(ConceptName.class.getName());
				for (int i = 0; i < nameNodes.getLength(); ++i) {
					Element name = (Element) nameNodes.item(i);
					
					// handle ConceptNameType (and remove it)
					Element nameTypeNode = getChildElement(name, "conceptNameType");
					boolean hasConceptNameType = false;
					if (nameTypeNode != null) {
						String nameType = nameTypeNode.getTextContent();
						if (nameType != null) {
							if (FULLY_SPECIFIED.equalsIgnoreCase(nameType)) {
								addNameTag(doc, name, "preferred", "preferred name in a language");
								hasConceptNameType = true;
							} else if (SHORT.equalsIgnoreCase(nameType)) {
								addNameTag(doc, name, "short", "shortened name for a concept");
								hasConceptNameType = true;
							}
						}
						
						name.removeChild(nameTypeNode);
					}
					
					if (!hasConceptNameType) {
						//anything else is a synonym
						addNameTag(doc, name, "synonym", "synonym name for a concept");
					}
					
					// just remove localePreferred
					Element localePreferredElement = getChildElement(name, "localePreferred");
					if (localePreferredElement != null)
						name.removeChild(localePreferredElement);
				}
				
			}
			catch (Exception e) {
				throw new SerializationException("Errow while converting to 1.6.x", e);
			}
			
		}
		
		xml = toXML(doc);
		
		return xml;
	}
	
	/**
	 * Adds a concept name tag to the existing <tags> node, creating it if necessary
	 * 
	 * @param doc
	 * @param nameNode
	 * @param tagName
	 * @param tagDescription
	 */
	private void addNameTag(Document doc, Element nameNode, String tagName, String tagDescription) {
		Element tags = getChildElement(nameNode, "tags");
		if (tags == null) {
			tags = doc.createElement("tags");
			tags.setAttribute("class", "set");
			nameNode.appendChild(tags);
		}
		
		String tagNameTemp = tagName.toLowerCase();
		String uuid = getConceptNameTagUuid(tagNameTemp);
		
		Node node = references.get(uuid);
		if (node != null) {
			tags.appendChild(node);
			return;
		}
		
		/* Something like this:
		 * <conceptNameTag id="31" uuid="4ACCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC">
		 *     <tag>preferred</tag>
		 *     <description>preferred name in a language</description>
		 *     <dateCreated>2012-01-06</dateCreated>
		 *     <voided>false</voided>
		 * </conceptNameTag>
		 */
		Element tag = doc.createElement("org.openmrs.ConceptNameTag");
		
		//set the uuid attribute so that the  import engine doesn't fail when checking for failed imports.
		//Otherwise it will fail for null uuids and at this point the concept name tag 
		//hasn't yet been invoked to set the existing tag that has a uuid and  matches the name
		Attr uuidAttr = doc.createAttribute("uuid");
		
		uuidAttr.setTextContent(uuid);
		tag.setAttributeNode(uuidAttr);
		tag.setAttribute("id", referenceId.toString());
		
		tag.appendChild(newTextElement(doc, "tag", tagName));
		tag.appendChild(newTextElement(doc, "description", tagDescription));
		tag.appendChild(newTextElement(doc, "dateCreated",
		    new SimpleDateFormat(MetadataSharingConsts.DATE_FORMAT).format(new Date())));
		tag.appendChild(newTextElement(doc, "voided", "false"));
		tags.appendChild(tag);
		
		Element tagReference = doc.createElement("org.openmrs.ConceptNameTag");
		tagReference.setAttribute("reference", referenceId.toString());
		referenceId--;
		references.put(uuid, tagReference);
	}
	
	/**
	 * Helper that finds the existing concept name tag's uuid if any, otherwise it returns a newly
	 * generated random uuid
	 * 
	 * @param tag the name of the concept name tag
	 * @return uuid
	 */
	private String getConceptNameTagUuid(String tag) {
		String uuid = tagNameUuidMap.get(tag);
		if (uuid != null) {
			return uuid;
		}
		
		ConceptNameTag conceptNameTag = Context.getConceptService().getConceptNameTagByName(tag);
		if (conceptNameTag != null)
			uuid = conceptNameTag.getUuid();
		else
			uuid = UUID.randomUUID().toString();
		
		tagNameUuidMap.put(tag, uuid);
		
		return uuid;
	}
	
	/**
	 * Helper method to create a text element
	 * 
	 * @param doc
	 * @param tagName
	 * @param value
	 * @return
	 */
	private Node newTextElement(Document doc, String tagName, String value) {
		Element el = doc.createElement(tagName);
		el.setTextContent(value);
		return el;
	}
	
	/**
	 * Gets the immediate child element of an XML element
	 * 
	 * @param parent
	 * @param childTagName
	 * @return
	 */
	private Element getChildElement(Node parent, String childTagName) {
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child instanceof Element) {
				if (((Element) child).getTagName().equals(childTagName))
					return (Element) child;
			}
		}
		return null;
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
