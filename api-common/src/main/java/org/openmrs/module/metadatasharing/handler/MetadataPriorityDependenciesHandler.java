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

/**
 * Allows to list dependencies which should be persisted prior to the given object.
 */
public interface MetadataPriorityDependenciesHandler<T> extends MetadataHandler<T> {
	
	/**
	 * Returns the list of dependencies which should be persisted prior to the given object. It
	 * affects only the import order.
	 * <p>
	 * Dependencies will be saved with an appropriate {@link MetadataSaveHandler}.
	 * 
	 * @param object
	 * @return the list of dependencies
	 */
	List<Object> getPriorityDependencies(T object);
}
