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
package org.openmrs.module.metadatasharing.task;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Represents any task exception.
 */
public class TaskException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public TaskException() {
	}
	
	/**
	 * @param message
	 */
	public TaskException(String message) {
		super(message);
	}
	
	/**
	 * @param cause
	 */
	public TaskException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public TaskException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public String getFullStackTrace() {
		return ExceptionUtils.getFullStackTrace(this);
	}
	
}
