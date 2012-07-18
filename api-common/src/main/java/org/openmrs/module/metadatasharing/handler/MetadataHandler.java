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

/**
 * It is a marker interface which represents a handler which supports the specified type and
 * sub-types.
 * 
 * @param <T> the supported type
 * @see HandlerEngine
 */
public interface MetadataHandler<T> {
	
	/**
	 * The higher number the higher priority.
	 * 
	 * @return the priority
	 */
	int getPriority();
}
