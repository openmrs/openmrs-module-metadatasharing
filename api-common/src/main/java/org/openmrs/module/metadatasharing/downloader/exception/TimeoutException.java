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
package org.openmrs.module.metadatasharing.downloader.exception;

import org.openmrs.module.metadatasharing.downloader.Downloader;

/**
 * This exception is thrown when downloading process exceeeded timeout limit
 * 
 * @see Downloader
 */
public class TimeoutException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see RuntimeException#RuntimeException()
	 */
	public TimeoutException() {
	}
	
	/**
	 * @see RuntimeException#RuntimeException(String)
	 */
	public TimeoutException(String message) {
		super(message);
	}
	
	/**
	 * @see RuntimeException#RuntimeException(Throwable)
	 */
	public TimeoutException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * @see RuntimeException#RuntimeException(String, Throwable)
	 */
	public TimeoutException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
