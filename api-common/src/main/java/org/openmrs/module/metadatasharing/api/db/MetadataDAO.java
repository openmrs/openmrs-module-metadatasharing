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
package org.openmrs.module.metadatasharing.api.db;

import java.sql.Blob;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.metadatasharing.api.MetadataService;

/**
 * Database methods for {@link MetadataService}.
 */
public interface MetadataDAO {
	
	/**
	 * @see MetadataService#getItems(Class, String, Integer, Integer)
	 */
	<T> List<T> getItems(Class<? extends T> type, boolean includeRetired, String phrase, Integer firstResult,
	                     Integer maxResults) throws DAOException;
	
	/**
	 * @see MetadataService#getItems(Class, String, Integer, Integer)
	 */
	<T> int getItemsCount(Class<? extends T> type, boolean includeRetired, String phrase) throws DAOException;
	
	/**
	 * @see MetadataService#getItem(Class, String)
	 */
	<T> T getItemByUuid(Class<? extends T> type, String uuid);
	
	/**
	 * @see MetadataService#getItemById(Class, String)
	 */
	<T> T getItemById(Class<? extends T> type, String id);
	
	/**
	 * @see MetadataService#saveItem(Object)
	 */
	<T> T saveItem(T item);
	
	/**
	 * @see MetadataService#getConcepts(boolean, String, Integer, Integer)
	 */
	List<Concept> getConcepts(boolean includeRetired, String phrase, Integer firstResult, Integer maxResults);
	
	/**
	 * @see MetadataService#getConceptsCount(boolean, String)
	 */
	int getConceptsCount(boolean includeRetired, String phrase);
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataDAO#getItemById(Class, Integer)
	 */
	<T> T getItemById(Class<? extends T> type, Integer id);
	
	Blob createBlob(byte[] bytes);
}
