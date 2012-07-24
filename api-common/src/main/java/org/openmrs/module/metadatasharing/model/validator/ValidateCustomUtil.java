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
package org.openmrs.module.metadatasharing.model.validator;

import java.util.HashSet;
import java.util.Set;

import org.openmrs.api.APIException;
import org.openmrs.validator.ValidateUtil;

/**
 * Ignores errors about unsaved transient instances.
 */
public class ValidateCustomUtil {
	
	private final static Set<String> ignoredErrors;
	
	static {
		ignoredErrors = new HashSet<String>();
		ignoredErrors.add("object references an unsaved transient instance - save the transient instance before flushing");
		ignoredErrors.add("Only existing concept reference sources can be used");
		ignoredErrors.add("Only existing concept map types can be used");
		ignoredErrors.add("Only existing concept reference terms can be mapped");
	}
	
	public static void validate(Object obj) throws APIException {
		try {
			ValidateUtil.validate(obj);
		}
		catch (Exception e) {
			boolean ignore = false;
			
			for (String error : ignoredErrors) {
				if (!e.getMessage().contains(error)) {
					ignore = true;
				}
			}
			
			if (!ignore) {
				throw new APIException(e.getMessage(), e);
			}
		}
	}
}
