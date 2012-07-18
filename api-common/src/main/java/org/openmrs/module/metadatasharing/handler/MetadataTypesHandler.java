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

import java.util.Map;

/**
 * Allows to provide user-friendly names of the supported types to be displayed to the user.
 * 
 * @param <T> the supported type
 */
public interface MetadataTypesHandler<T> extends MetadataHandler<T> {
	
	/**
	 * @return the map of user-friendly names of the supported types
	 */
	Map<Class<? extends T>, String> getTypes();
}
