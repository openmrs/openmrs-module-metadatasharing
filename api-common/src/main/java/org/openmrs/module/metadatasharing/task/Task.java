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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.metadatasharing.Package;

/**
 *
 */
public abstract class Task {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private String uuid = UUID.randomUUID().toString();
	
	private TaskType type;
	
	private List<TaskLogEntry> logs = new CopyOnWriteArrayList<TaskLogEntry>();
	
	private List<TaskLogEntry> errors = new CopyOnWriteArrayList<TaskLogEntry>();
	
	private volatile boolean active;
	
	private volatile boolean completed;
	
	/**
	 * @return
	 */
	public abstract Package getPackage();
	
	/**
	 */
	public abstract void execute() throws TaskException;
	
	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}
	
	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * @return the type
	 */
	public TaskType getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(TaskType type) {
		this.type = type;
	}
	
	/**
	 * @return the logs
	 */
	public List<TaskLogEntry> getLogs() {
		return logs;
	}
	
	/**
	 * @return the errors
	 */
	public List<TaskLogEntry> getErrors() {
		return errors;
	}
	
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * @param active the active to set
	 */
	protected void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * @return the completed
	 */
	public boolean isCompleted() {
		return completed;
	}
	
	/**
	 * @param completed the completed to set
	 */
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	protected void log(String message, Exception exception) {
		log.error(message, new TaskException(message, exception));
		
		TaskLogEntry error = new TaskLogEntry(message, new TaskException(message, exception));
		logs.add(error);
		errors.add(error);
	}
	
	protected void log(String message) {
		log.info(message);
		
		logs.add(new TaskLogEntry(message));
	}
	
}
