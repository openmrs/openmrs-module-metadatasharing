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
package org.openmrs.module.metadatasharing.api;

import org.openmrs.api.APIException;
import org.springframework.validation.Errors;

/**
 * Allows to preserve {@link Errors} with the Exception.
 */
public class ValidationException extends APIException {
	
	private static final long serialVersionUID = 1L;
	
	private final Errors errors;
	
	public ValidationException(Errors errors) {
		this.errors = errors;
	}
	
	public ValidationException(String message, Errors errors) {
		super(message);
		this.errors = errors;
	}
	
	public ValidationException(Errors errors, Throwable cause) {
		super(cause);
		this.errors = errors;
	}
	
	public ValidationException(String message, Errors errors, Throwable cause) {
		super(message, cause);
		this.errors = errors;
	}
	
	/**
	 * @return the errors
	 */
	public Errors getErrors() {
		return errors;
	}
	
	@Override
	public String getMessage() {
	    return errors.toString();
	}
	
}
