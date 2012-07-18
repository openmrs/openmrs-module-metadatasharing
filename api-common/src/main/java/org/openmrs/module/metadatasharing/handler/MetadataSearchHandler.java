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
package org.openmrs.module.metadatasharing.handler;

import java.util.List;

import org.openmrs.api.db.DAOException;

/**
 * Allows to provide a specific search approach for objects.
 * 
 * @param <T> the supported type
 */
public interface MetadataSearchHandler<T> extends MetadataHandler<T> {
	
	/**
	 * Returns the count of items filtered by the given phrase.
	 * <p>
	 * If the phrase is <code>null</code>, it should return the count of all items.
	 * 
	 * @param type
	 * @param includeRetired
	 * @param phrase the phrase to filter items or <code>null</code>
	 * @return the count of items
	 */
	int getItemsCount(Class<? extends T> type, boolean includeRetired, String phrase) throws DAOException;
	
	/**
	 * Returns items filtered by the given phrase in the given range.
	 * <p>
	 * The phrase, firsResult and maxResults parameters are optional and <code>null</code> may be
	 * given.
	 * 
	 * @param type
	 * @param includeRetired
	 * @param phrase the phrase to filter items or <code>null</code>. If <code>null</code> is given
	 *            no filtering should be applied.
	 * @param firstResult the position from where to start listing items or <code>null</code>. The
	 *            first position is denoted by 0 and is a default value when <code>null</code> is
	 *            given.
	 * @param maxResults the maximum number of item to return or <code>null</code>. If
	 *            <code>null</code> is given it is set to infinity.
	 * @return the filtered items
	 */
	List<T> getItems(Class<? extends T> type, boolean includeRetired, String phrase, Integer firstResult, Integer maxResults) throws DAOException;
	
	/**
	 * Returns an item with the given uuid.
	 * 
	 * @param type
	 * @param uuid
	 * @return the item or <code>null</code> if not found
	 */
	T getItemByUuid(Class<? extends T> type, String uuid) throws DAOException;
	
	/**
	 * Returns an item with the given primary key id.
	 * 
	 * @param type
	 * @param id
	 * @return the item or <code>null</code> if not found
	 */
	T getItemById(Class<? extends T> type, Integer id) throws DAOException;
}
