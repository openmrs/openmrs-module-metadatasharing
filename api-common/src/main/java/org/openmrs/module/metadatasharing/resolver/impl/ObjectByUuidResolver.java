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

import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.resolver.Resolver;
import org.springframework.stereotype.Component;

/**
 * Identifies conflict when the metadata object has same uuid as the item in the database.
 */
@Component(MetadataSharingConsts.MODULE_ID + ".ObjectByUuidResolver")
public class ObjectByUuidResolver extends Resolver<Object> {
	
	/**
	 * @see org.openmrs.module.metadatasharing.resolver.Resolver#getPriority()
	 */
	@Override
	public int getPriority() {
	    return 100;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.resolver.Resolver#getExactMatch(java.lang.Object)
	 */
	@Override
	public Object getExactMatch(Object incoming) {
		return Handler.getItemByUuid(incoming.getClass(), Handler.getUuid(incoming));
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.resolver.Resolver#getPossibleMatch(java.lang.Object)
	 */
	@Override
	public Object getPossibleMatch(Object incoming) {
		return null;
	}
	
}
