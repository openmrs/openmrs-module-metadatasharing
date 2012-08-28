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
package org.openmrs.module.metadatasharing.handler.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptSource;
import org.openmrs.module.metadatasharing.handler.MetadataDeserializationHandler;
import org.openmrs.module.metadatasharing.handler.MetadataDeserializer;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.openmrs.module.metadatasharing.util.Version;
import org.springframework.stereotype.Component;

@Component("metadatasharing.ConceptMapHandler")
public class ConceptMapHandler implements MetadataPriorityDependenciesHandler<ConceptMap>, MetadataDeserializationHandler<ConceptMap> {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public int getPriority() {
		return 0;
	}
	
	@Override
	public List<Object> getPriorityDependencies(ConceptMap object) {
		List<Object> result = new ArrayList<Object>();
		
		if (object.getSource() != null) {
			result.add(object.getSource());
		}
		
		return result;
	}
	
	@Override
	public ConceptMap deserialize(MetadataDeserializer deserializer) {
		if (deserializer.getFromVersion() == null || deserializer.getToVersion() == null) {
			return null;
		}
		
		if (new Version(deserializer.getToVersion()).compareTo(new Version("1.9")) >= 0) {
			return null;
		}
		
		if (new Version(deserializer.getFromVersion()).compareTo(new Version("1.9")) < 0) {
			return null;
		}
		
		//else toVersion < 1.9 and fromVersion >= 1.9
		
		ConceptMap map = new ConceptMap();
		map.setUuid(deserializer.getAttribute("uuid"));
		
		log.info("Converting concept map [" + map.getUuid() + "] from " + deserializer.getFromVersion() + " to "
		        + deserializer.getToVersion());
		
		while (deserializer.hasMoreChildren()) {
			deserializer.moveDown();
			if ("conceptMapId".equals(deserializer.getNodeName())) {
				map.setConceptMapId(deserializer.convert(map, Integer.class));
			} else if ("concept".equals(deserializer.getNodeName())) {
				map.setConcept((Concept) deserializer.convert(map, Concept.class));
			} else if ("dateCreated".equals(deserializer.getNodeName())) {
				map.setDateCreated(deserializer.convert(map, Date.class));
			} else if ("conceptReferenceTerm".equals(deserializer.getNodeName())) {
				Object reference = deserializer.getReference();
				
				ConceptReferenceTerm term;
				
				if (reference instanceof ConceptReferenceTerm) {
					term = (ConceptReferenceTerm) reference;
				} else {
					term = new ConceptReferenceTerm();

					while (deserializer.hasMoreChildren()) {
						deserializer.moveDown();
						if ("conceptSource".equals(deserializer.getNodeName())) {
							term.source = deserializer.convert(map, ConceptSource.class);
						} else if ("code".equals(deserializer.getNodeName())) {
							term.sourceCode = deserializer.getValue();
						}
						deserializer.moveUp();
					}
					
					deserializer.putReference(term);
				}
				
				map.setSource(term.source);
				map.setSourceId(term.source.getConceptSourceId());
				map.setSourceCode(term.sourceCode);
				
			} else if ("conceptMapType".equals(deserializer.getNodeName())) {
				Object reference = deserializer.getReference();
				
				String comment = "";
				if (reference instanceof String) {
					comment = (String) reference;
				} else {
					while (deserializer.hasMoreChildren()) {
						deserializer.moveDown();
						if ("name".equals(deserializer.getNodeName())) {
							comment = deserializer.getValue();
							deserializer.moveUp();
							break;
						}
						deserializer.moveUp();
					}
					
					deserializer.putReference(comment);
				}
				
				map.setComment("MAP TYPE:" + comment);
			}
			
			deserializer.moveUp();
		}
		return map;
	}
	
	private static class ConceptReferenceTerm {
		public ConceptSource source;
		public String sourceCode;
	}
}
