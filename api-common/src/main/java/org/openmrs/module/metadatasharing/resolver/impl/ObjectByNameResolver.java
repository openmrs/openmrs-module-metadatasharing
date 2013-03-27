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
package org.openmrs.module.metadatasharing.resolver.impl;

import java.util.Collection;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.resolver.Resolver;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component(MetadataSharingConsts.MODULE_ID + ".ObjectByNameResolver")
public class ObjectByNameResolver extends Resolver<Object> {
	
	/**
	 * @see org.openmrs.module.metadatasharing.resolver.Resolver#getPriority()
	 */
	@Override
	public int getPriority() {
	    return 0;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.resolver.Resolver#getExactMatch(java.lang.Object)
	 */
	@Override
	public Object getExactMatch(Object incoming) {
		if (incoming instanceof Role || incoming instanceof Privilege) {
			//Roles and privileges are considered the same if they have the same names.
			List<Object> objects = Handler.getItems(incoming.getClass(), true,
			    Handler.getName(incoming), null, null);
			
			for (Object object : objects) {
				if (Handler.getName(incoming).equalsIgnoreCase(Handler.getName(object))) {
					return object;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.resolver.Resolver#getPossibleMatch(java.lang.Object)
	 */
	@Override
	public Object getPossibleMatch(Object incoming) {
		if (!Handler.isHidden(incoming) && !Handler.getName(incoming).isEmpty()) {
			List<Object> objects = Handler.getItems(incoming.getClass(), true,
			    Handler.getName(incoming), null, null);
			
			for (Object object : objects) {
				if (Handler.getName(incoming).equalsIgnoreCase(Handler.getName(object))) {
					return object;
				} else if (object instanceof Concept) {
					Concept concept = (Concept) object;
					Collection<ConceptName> names = concept.getNames(false);
					for (ConceptName name : names) {
						if (Handler.getName(incoming).equalsIgnoreCase(name.getName())) {
							return object;
						}
					}
				}
			}
		}
		
		return null;
	}
}
