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
package org.openmrs.module.metadatasharing.web.exception;

/**
 * An exception which is thrown when we tried to get the package from the database, but there were'n
 * one (and thus we cannot proceed)
 */
public class PackageNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see RuntimeException#RuntimeException()
	 */
	public PackageNotFoundException() {
	}
	
	/**
	 * @see RuntimeException#RuntimeException(String)
	 */
	public PackageNotFoundException(String message) {
		super(message);
	}
	
	/**
	 * @see RuntimeException#RuntimeException(Throwable)
	 */
	public PackageNotFoundException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * @see RuntimeException#RuntimeException(String, Throwable)
	 */
	public PackageNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
