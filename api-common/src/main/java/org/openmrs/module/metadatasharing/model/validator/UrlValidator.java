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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.downloader.DownloaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates subscription URL
 * 
 * @see Subscription
 */
@Component(MetadataSharingConsts.MODULE_ID + ".UrlValidator")
public class UrlValidator implements Validator {
	
	/**
	 * The maximum length of the URL
	 */
	private int maxLength = 512;
	
	@Autowired
	private DownloaderFactory downloaderFactory = null;
	
	/**
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean supports(Class clazz) {
		return String.class.isAssignableFrom(clazz);
	}
	
	/**
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 * @should reject value if the length is greater than maxLength
	 * @should reject non available protocols
	 * @should not reject available protocols
	 */
	@Override
	public void validate(Object target, Errors errors) {
		if (((String) target).length() > maxLength) {
			errors.reject("metadatasharing.error.subscription.url.tooLong", new Integer[] { maxLength }, null);
		}
		try {
			URL url = new URL((String) target);
			Set<String> protocols = downloaderFactory.getAvailableProtocols();
			if (!protocols.contains(url.getProtocol())) { //let's provide information about avilable protocols
				String message = "";
				Iterator<String> it = protocols.iterator();
				while (it.hasNext()) {
					message += it.next();
					if (it.hasNext())
						message += ", ";
				}
				errors.reject("metadatasharing.error.subscription.url.unsupported", new Object[] { message }, null);
			}
		}
		catch (MalformedURLException e) {
			errors.reject("metadatasharing.error.subscription.url.invalid");
		}
	}
	
	/**
	 * @param maxLength the maxLength to set
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	
	/**
	 * @return the maxLength
	 */
	public int getMaxLength() {
		return maxLength;
	}
}
