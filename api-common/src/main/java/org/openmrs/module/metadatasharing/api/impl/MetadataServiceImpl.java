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
package org.openmrs.module.metadatasharing.api.impl;

import java.sql.Blob;
import java.util.List;

import javax.annotation.Resource;

import org.openmrs.Concept;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.metadatasharing.api.MetadataService;
import org.openmrs.module.metadatasharing.api.db.MetadataDAO;

/**
 * Default implementation of {@link MetadataService}.
 * <p>
 * This class should not be used on its own. The current implementation should be fetched from the
 * Context via <code>Context.getService(MetadataService.class)</code>
 * 
 * @see org.openmrs.api.context.Context
 */
public class MetadataServiceImpl extends BaseOpenmrsService implements MetadataService {
	
	@Resource(name = "hibernateMetadataDAO")
	private MetadataDAO dao;
	
	@Override
	public <T> List<T> getItems(Class<? extends T> type, boolean includeRetired, String phrase, Integer firstResult,
	                            Integer maxResults) throws APIException {
		return dao.getItems(type, includeRetired, phrase, firstResult, maxResults);
	}
	
	@Override
	public <T> int getItemsCount(Class<? extends T> type, boolean includeRetired, String phrase) throws APIException {
		return dao.getItemsCount(type, includeRetired, phrase);
	}
	
	@Override
	public <T> T getItem(Class<? extends T> type, String uuid) throws APIException {
		return dao.getItemByUuid(type, uuid);
	}
	
	@Override
	public <T> T getItemById(Class<? extends T> type, String id) throws APIException {
		return dao.getItemById(type, id);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.MetadataService#saveItem(java.lang.Object)
	 */
	@Override
	public <T> T saveItem(T item) throws APIException {
	    return dao.saveItem(item);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.MetadataService#getConcepts(boolean,
	 *      java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<Concept> getConcepts(boolean includeRetired, String phrase, Integer firstResult, Integer maxResults)
	    throws APIException {
		return dao.getConcepts(includeRetired, phrase, firstResult, maxResults);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.MetadataService#getConceptsCount(boolean,
	 *      java.lang.String)
	 */
	@Override
	public int getConceptsCount(boolean includeRetired, String phrase) throws APIException {
		return dao.getConceptsCount(includeRetired, phrase);
	}
	
	/**
	 * @see MetadataService#getItemById(Class, Integer)
	 */
	@Override
	public <T> T getItemById(Class<? extends T> type, Integer id) throws APIException {
		return dao.getItemById(type, id);
	}

	@Override
	public Blob createBlob(byte[] bytes) {
		return dao.createBlob(bytes);
	}
}
