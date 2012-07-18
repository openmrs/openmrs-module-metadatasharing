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

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a log entry.
 */
public class TaskLogEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Date date;
	
	private String message;
	
	private Exception exception;
	
	public TaskLogEntry(String message, Exception exception) {
		this.date = new Date();
		this.message = message;
		this.exception = exception;
	}
	
	public TaskLogEntry(String message) {
		this(message, null);
	}
	
	public TaskLogEntry() {
		this(null, null);
	}
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}
	
	/**
	 * @param exception the exception to set
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}
	
}
