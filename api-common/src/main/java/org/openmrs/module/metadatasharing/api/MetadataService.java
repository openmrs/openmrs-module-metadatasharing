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
package org.openmrs.module.metadatasharing.api;

import java.sql.Blob;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.handler.impl.ConceptHandler;
import org.openmrs.module.metadatasharing.handler.impl.OpenmrsObjectHandler;
import org.openmrs.module.metadatasharing.handler.impl.SerializedObjectHandler;
import org.springframework.transaction.annotation.Transactional;

/**
 * <b>It is for internal use only.</b> Contains methods for {@link OpenmrsObjectHandler}.
 * <p>
 * Usage example:<br>
 * <code>
 * Context.getService(MetadataService.class).getItems(...);
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
public interface MetadataService extends OpenmrsService {
	
	/**
	 * Access method for {@link OpenmrsObjectHandler#getItemByUuid(Class, String)}.
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	<T> T getItem(Class<? extends T> type, String uuid) throws APIException;
	
	/**
	 * Access method for {@link OpenmrsObjectHandler#getItemById(Class, String)}.
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	<T> T getItemById(Class<? extends T> type, String id) throws APIException;
	
	/**
	 * Access method for {@link OpenmrsObjectHandler#getItems(Class, String, Integer, Integer)}
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	<T> List<T> getItems(Class<? extends T> type, boolean includeRetired, String phrase, Integer firstResult,
	                     Integer maxResults) throws APIException;
	
	/**
	 * Access method for {@link OpenmrsObjectHandler#getItemsCount(Class, String)}.
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	<T> int getItemsCount(Class<? extends T> type, boolean includeRetired, String phrase) throws APIException;
	
	/**
	 * Access method for {@link SerializedObjectHandler}.
	 * 
	 * @param item
	 * @return the saved item
	 * @throws APIException
	 */
	<T> T saveItem(T item) throws APIException;
	
	/**
	 * Access method for {@link ConceptHandler#getItems(Class, boolean, String, Integer, Integer)}.
	 * 
	 * @param includeRetired
	 * @param phrase
	 * @param firsResult
	 * @param maxResults
	 * @return list of matching Concepts
	 * @throws APIException
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Concept> getConcepts(boolean includeRetired, String phrase, Integer firstResult, Integer maxResults)
	    throws APIException;
	
	/**
	 * Access method for {@link ConceptHandler#getItemsCount(Class, boolean, String)}.
	 * 
	 * @param includeRetired
	 * @param phrase
	 * @return number of matching Concepts
	 * @throws APIException
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	int getConceptsCount(boolean includeRetired, String phrase) throws APIException;
	
	/**
	 * Gets an item by id from the database.
	 * <p>
	 * Item is retrieved bypassing any OpenmrsService methods and going straight to Hibernate.
	 * 
	 * @param type <code>Class</code> implementing <code>OpenmrsObject</code>
	 * @param id the unique id of the item to retrieve
	 * @return Item matching the passed in type and id
	 * @throws APIException
	 * @should return the item matching the passed in type and id
	 */
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	<T> T getItemById(Class<? extends T> type, Integer id) throws APIException;
	
	@Authorized(MetadataSharingConsts.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Blob createBlob(byte[] bytes);
}
