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

import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.subscription.SubscriptionHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates the {@link SubscriptionHeader}
 */
@Component(MetadataSharingConsts.MODULE_ID + ".SubscriptionHeaderValidator")
public class SubscriptionHeaderValidator implements Validator {
	
	@Autowired
	private PackageValidator packageValidator;
	
	/**
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
		return SubscriptionHeader.class.isAssignableFrom(clazz);
	}
	
	/**
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 * @should reject invalid package header
	 * @should reject empty content URI
	 */
	@Override
	public void validate(Object target, Errors errors) {
		SubscriptionHeader header = (SubscriptionHeader) target;
		errors.pushNestedPath("packageHeader");
		ValidationUtils.invokeValidator(packageValidator, header.getPackageHeader(), errors);
		errors.popNestedPath();
		
		ValidationUtils.rejectIfEmpty(errors, "contentUri", "metadatasharing.error.subscriptionHeader.contentUri.empty",
		    "The contentUri cannot be empty");
	}
}
