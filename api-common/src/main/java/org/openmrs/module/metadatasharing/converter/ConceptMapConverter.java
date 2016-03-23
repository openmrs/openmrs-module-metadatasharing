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

import org.apache.commons.lang.StringUtils;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.util.Version;
import org.openmrs.serialization.SerializationException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Converts ConcetpMap from pre-1.9 to post-1.9.
 */
@Component(MetadataSharingConsts.MODULE_ID + ".ConceptMapConverter")
public class ConceptMapConverter extends BaseConverter {

	/**
	 * @see org.openmrs.module.metadatasharing.converter.BaseConverter#convert(org.w3c.dom.Document, org.openmrs.module.metadatasharing.util.Version, org.openmrs.module.metadatasharing.util.Version, org.openmrs.module.metadatasharing.converter.BaseConverter.ConverterContext)
	 * @should replace source if the from version is pre one nine
	 * @should replace nothing if the to version is post one nine
	 */
	@Override
	public void convert(Document doc, Version fromVersion, Version toVersion, ConverterContext context)
			throws SerializationException {
		if (fromVersion.compareTo(new Version("1.9")) >= 0 || toVersion.compareTo(new Version("1.9")) < 0) {
			return;
		}

		Map<String, Element> mapTypeElements = new HashMap<String, Element>();

		NodeList maps = doc.getElementsByTagName("org.openmrs.ConceptMap");
		for (int i = 0; i < maps.getLength(); i++) {
			Node map = maps.item(i);

			Element commentElement = getChildElement(map, "comment");
			if (commentElement == null) {
				continue;
			}

			map.removeChild(commentElement);

			String mapTypeName = getMapTypeName(commentElement);

			if (mapTypeName == null) {
				continue;
			}

			Element mapTypeElement = mapTypeElements.get(mapTypeName);

			if (mapTypeElement == null) {
				ConceptMapType mapType = Context.getConceptService().getConceptMapTypeByName(mapTypeName);

				if (mapType == null) {
					continue;
				}

				mapTypeElement = newElement(doc, "conceptMapType", mapType, context);
				mapTypeElements.put(mapTypeName, mapTypeElement);

				map.appendChild(mapTypeElement);
			} else {
				Element reference = doc.createElement("conceptMapTye");
				newReferenced(reference, mapTypeElement);
				map.appendChild(reference);
			}
		}

		Map<String, Element> referenceTermElements = new HashMap<String, Element>();

		for (int i = 0; i < maps.getLength(); i++) {
			Node map = maps.item(i);
			Element source = getChildElement(map, "source");
			map.removeChild(source);
			Element referencedSource = (Element) reference(source, context);

			Element sourceCode = getChildElement(map, "sourceCode");
			map.removeChild(sourceCode);

			//ConceptReferenceTerms are the same if source and sourceCode are the same
			String key = referencedSource.getAttribute("uuid") + sourceCode.getTextContent();

			Element referenceTermElement = referenceTermElements.get(key);

			if (referenceTermElement == null) {
				ConceptReferenceTerm referenceTerm = new ConceptReferenceTerm();
				String uuid = UUID.nameUUIDFromBytes(((Element) map).getAttribute("uuid").getBytes()).toString();
				referenceTerm.setUuid(uuid);
				referenceTerm.setCode(sourceCode.getTextContent());
				referenceTermElement = newElement(doc, "conceptReferenceTerm", referenceTerm, context);
				source = (Element) source.cloneNode(true);
				source = (Element) doc.renameNode(source, "", "conceptSource");
				referenceTermElement.appendChild(source);

				referenceTermElements.put(key, referenceTermElement);
				map.appendChild(referenceTermElement);
			} else {
				Element reference = doc.createElement("conceptReferenceTerm");
				newReferenced(reference, referenceTermElement);
				map.appendChild(reference);
			}
		}
	}

	private String getMapTypeName(Element commentElement) {
		if (commentElement == null) {
			return null;
		}

		String comment = commentElement.getTextContent();

		if (StringUtils.isBlank(comment)) {
			return null;
		}

		comment = comment.toUpperCase();

		if (!comment.startsWith("MAP TYPE:")) {
			return null;
		}

		comment = comment.substring(9).trim();

		if (comment.equals("SAME-AS FROM RXNORM")) {
			comment = "SAME-AS";
		}

		return comment;
	}
	
}