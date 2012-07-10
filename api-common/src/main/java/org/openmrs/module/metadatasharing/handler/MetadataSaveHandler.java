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

import org.openmrs.api.db.DAOException;

/**
 * Allows to provide a specific save approach for objects.
 * 
 * @param <T> the supported type
 */
public interface MetadataSaveHandler<T> extends MetadataHandler<T> {
	
	/**
	 * Saves or updates the given item.
	 * 
	 * @param object
	 * @return the persisted item or <code>null</code> if it is not persisted
	 */
	T saveItem(T item) throws DAOException;
	
}
