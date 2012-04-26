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
package org.openmrs.module.metadatasharing.web.bean.validator;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.model.validator.UrlValidator;
import org.openmrs.module.metadatasharing.web.bean.UploadForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates {@link UploadForm} objects<br>
 * It rejects the file property if it is empty
 */
@Component(MetadataSharingConsts.MODULE_ID + ".UploadFormValidator")
public class UploadFormValidator implements Validator {
	
	@Autowired
	private UrlValidator urlValidator;
	
	/**
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean supports(Class clazz) {
		return UploadForm.class.isAssignableFrom(clazz);
	}
	
	/**
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object obj, Errors errors) {
		UploadForm uploadPackage = (UploadForm) obj;
		if (StringUtils.isBlank(uploadPackage.getUrl())) {
			if (uploadPackage.getFile() == null || uploadPackage.getFile().isEmpty()) {
				errors.rejectValue("file", "metadatasharing.error.package.file.empty");
			}
		} else {
			if (uploadPackage.getFile() != null && !uploadPackage.getFile().isEmpty()) {
				errors.rejectValue("url", "metadatasharing.error.package.url.notEmpty");
			}
			
			errors.pushNestedPath("url");
			urlValidator.validate(uploadPackage.getUrl(), errors);
			errors.popNestedPath();
		}
	}
}
