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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.springframework.stereotype.Component;

/**
 * Makes sure that priority dependencies returned by the getDependencies method (if exists) on the
 * give object are saved first.
 */
@Component("metadatasharing.ObjectHandler")
public class ObjectHandler implements MetadataPriorityDependenciesHandler<Object> {
	
	protected Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler#getPriorityDependencies(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getPriorityDependencies(Object object) {
		List<Object> dependencies = new ArrayList<Object>();
		try {
			Method method = object.getClass().getMethod("getDependencies");
			if (method != null) {
				Collection<Object> items = (Collection<Object>) method.invoke(object);
				if (items != null) {
					dependencies.addAll(items);
				}
			}	
		}
		catch (NoSuchMethodException e) {
			log.debug("No priority dependencies for " + object.toString());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return dependencies;
	}
	
}
