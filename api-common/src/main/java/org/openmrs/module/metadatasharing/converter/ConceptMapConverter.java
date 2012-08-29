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

import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.util.Version;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Converts ConcetpMap from post-1.9 to pre-1.9.
 */
@Component(MetadataSharingConsts.MODULE_ID + ".ConceptMapConverter")
public class ConceptMapConverter extends BaseConverter {

	@Override
    public void convert(Document doc, Version fromVersion, Version toVersion, ConverterContext context) {
		if (fromVersion.compareTo(new Version("1.9")) < 0 && toVersion.compareTo(new Version("1.9")) >= 0) {
			return;
		}
		
		NodeList mapTypes = doc.getElementsByTagName("conceptMapType");
		while(mapTypes.getLength() > 0) {
			Node mapType = mapTypes.item(0);
			
			Node map = mapType.getParentNode();
			map.removeChild(mapType);
			
			mapType = reference(mapType, context);
			
	        Element name = getChildElement(mapType, "name");
	        map.appendChild(newTextElement(doc, "comment", name.getTextContent()));
        }
		
		NodeList referenceTerms = doc.getElementsByTagName("conceptReferenceTerm");
		while (referenceTerms.getLength() > 0) {
	        Node referenceTerm = referenceTerms.item(0);
	        
	        Node map = referenceTerm.getParentNode();
	        map.removeChild(referenceTerm);
	        
	        referenceTerm = reference(referenceTerm, context);
	        
	        Element conceptSource = getChildElement(referenceTerm, "conceptSource");
	        Node source = conceptSource.cloneNode(true);
	        source = doc.renameNode(source, "", "source");
	        map.appendChild(source);
	        
	        Element code = getChildElement(referenceTerm, "code");
	        map.appendChild(newTextElement(doc, "code", code.getTextContent()));
        }
    }
	
}
