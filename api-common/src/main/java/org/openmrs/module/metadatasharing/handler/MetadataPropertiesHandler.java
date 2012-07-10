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

import java.util.Date;
import java.util.Map;

/**
 * Allows to provide a specific approach for accessing objects' properties.
 * 
 * @param <T> the supported type
 */
public interface MetadataPropertiesHandler<T> extends MetadataHandler<T> {
	
	/**
	 * Returns a database id of the given object.
	 * 
	 * @param object
	 * @return the id or <code>null</code>
	 */
	Integer getId(T object);
	
	/**
	 * Sets the database id in the given object.
	 * 
	 * @param object
	 * @param id
	 */
	void setId(T object, Integer id);
	
	/**
	 * Returns a uuid of the given object.
	 * <p>
	 * The uuid must not be <code>null</code>.
	 * 
	 * @param object
	 * @return the uuid
	 */
	String getUuid(T object);
	
	/**
	 * Sets the uuid in the given object.
	 * <p>
	 * The uuid must not be <code>null</code>.
	 * 
	 * @param object
	 * @param uuid
	 */
	void setUuid(T object, String uuid);
	
	/**
	 * @param object
	 * @return <code>true</code> if retired or <code>null</code> if not supported
	 */
	Boolean getRetired(T object);
	
	/**
	 * Sets the retired field in the given object.
	 * 
	 * @param object
	 * @param retired or <code>null</code>
	 */
	void setRetired(T object, Boolean retired);
	
	/**
	 * Returns a name of the given object.
	 * 
	 * @param object
	 * @return the name or <code>null</code>
	 */
	String getName(T object);
	
	/**
	 * Returns a description of the given object.
	 * 
	 * @param object
	 * @return the description or <code>null</code>
	 */
	String getDescription(T object);
	
	/**
	 * Returns a date of change of the given object.
	 * 
	 * @param object
	 * @return the date or <code>null</code>
	 */
	Date getDateChanged(T object);
	
	/**
	 * Returns a map of properties for the given object with keys being property names and values
	 * objects to display. It may be used to display additional properties of an object for
	 * comparison purposes.
	 * <p>
	 * Values will be displayed using {@link Object#toString()} or with the help of
	 * {@link MetadataPropertiesHandler} if there is one applicable.
	 * 
	 * @return the map of properties
	 */
	Map<String, Object> getProperties(T object);
	
}
