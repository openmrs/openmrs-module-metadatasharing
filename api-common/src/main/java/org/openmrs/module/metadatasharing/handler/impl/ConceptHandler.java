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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSet;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.metadatasharing.api.MetadataService;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSearchHandler;
import org.openmrs.module.metadatasharing.handler.MetadataTypesHandler;
import org.openmrs.module.metadatasharing.util.DateUtil;
import org.springframework.stereotype.Component;

@Component("metadatasharing.ConceptHandler")
public class ConceptHandler implements MetadataSearchHandler<Concept>, MetadataTypesHandler<Concept>, MetadataPropertiesHandler<Concept>, MetadataPriorityDependenciesHandler<Concept> {
	
	private final Map<Class<? extends Concept>, String> types;
	
	public ConceptHandler() {
		Map<Class<? extends Concept>, String> tmpTypes = new HashMap<Class<? extends Concept>, String>();
		tmpTypes.put(Concept.class, "Concept");
		types = Collections.unmodifiableMap(tmpTypes);
	}
	
	@Override
	public int getPriority() {
	    return 0;
	}
	
	@Override
	public Map<Class<? extends Concept>, String> getTypes() {
		return types;
	}
	
	@Override
	public Integer getId(Concept object) {
		return object.getId();
	}
	
	@Override
	public void setId(Concept object, Integer id) {
		object.setId(id);
	}
	
	@Override
	public void setUuid(Concept object, String uuid) {
		object.setUuid(uuid);
	}
	
	@Override
	public String getUuid(Concept object) {
		return (object.getUuid() != null) ? object.getUuid() : "";
	}
	
	@Override
	public Boolean getRetired(Concept object) {
		return object.isRetired();
	}
	
	@Override
	public void setRetired(Concept object, Boolean retired) {
		object.setRetired(retired);
	}
	
	@Override
	public String getName(Concept object) {
		if (object.getName() != null) {
			return (object.getName().getName() != null) ? object.getName().getName() : "";
		} else {
			return "";
		}
	}
	
	@Override
	public String getDescription(Concept object) {
		if (object.getDescription() != null) {
			return (object.getDescription().getDescription() != null) ? object.getDescription().getDescription() : "";
		} else {
			return "";
		}
	}
	
	@Override
	public Date getDateChanged(Concept object) {
		return DateUtil.getLastDateChanged(object);
	}
	
	@Override
	public Map<String, Object> getProperties(Concept object) {
		Map<String, Object> properties = new HashMap<String, Object>();
		if (object.getDatatype() != null) {
			properties.put("datatype", object.getDatatype().getName());
		}
		if (object.getConceptClass() != null && object.getConceptClass().getName() != null) {
			properties.put("class", object.getConceptClass().getName());
		}
		return properties;
	}
	
	@Override
	public List<Object> getPriorityDependencies(Concept object) {
		List<Object> result = new ArrayList<Object>();
		
		Collection<ConceptAnswer> answers = object.getAnswers(true);
		if (answers != null) {
			result.addAll(answers);
		}
		
		Collection<ConceptSet> conceptSets = object.getConceptSets();
		if (conceptSets != null) {
			result.addAll(conceptSets);
		}
		
		Collection<ConceptMap> conceptMappings = object.getConceptMappings();
		if (conceptMappings != null) {
			result.addAll(conceptMappings);
		}
		
		if (object.getId() != null) {
			for (ConceptName name : object.getNames(true)) {
				if (name.getId() == null) {
					result.add(name);
				}
			}
		}
		
		if (object.getConceptClass() != null) {
			result.add(object.getConceptClass());
		}
		
		if (object.getDatatype() != null) {
			result.add(object.getDatatype());
		}
		return result;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataSearchHandler#getItemsCount(java.lang.Class,
	 *      boolean, java.lang.String)
	 */
	@Override
	public int getItemsCount(Class<? extends Concept> type, boolean includeRetired, String phrase) throws DAOException {
		return Context.getService(MetadataService.class).getConceptsCount(includeRetired, phrase);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataSearchHandler#getItems(java.lang.Class,
	 *      boolean, java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<Concept> getItems(Class<? extends Concept> type, boolean includeRetired, String phrase, Integer firstResult,
	                              Integer maxResults) throws DAOException {
		return Context.getService(MetadataService.class).getConcepts(includeRetired, phrase, firstResult, maxResults);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataSearchHandler#getItemByUuid(java.lang.Class,
	 *      java.lang.String)
	 */
	@Override
	public Concept getItemByUuid(Class<? extends Concept> type, String uuid) throws DAOException {
		return Context.getConceptService().getConceptByUuid(uuid);
	}
	
	@Override
	public Concept getItemById(Class<? extends Concept> type, Integer id) throws DAOException {
		return Context.getConceptService().getConcept(id);
	}
}
