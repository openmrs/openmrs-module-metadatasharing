/**
b * The contents of this file are subject to the OpenMRS Public License
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service(MetadataSharingConsts.MODULE_ID + ".TaskEngine")
public class TaskEngine {
	
	private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
	
	/**
	 * Queued tasks.
	 */
	private Map<String, Task> queuedTasks = new LinkedHashMap<String, Task>();
	
	/**
	 * Circular queue for finished tasks.
	 */
	private Map<String, Task> finishedTasks = new LinkedHashMap<String, Task>() {
		
		private static final long serialVersionUID = 1L;
		
		protected boolean removeEldestEntry(Map.Entry<String, Task> eldest) {
			return size() > 10;
		}
	};
	
	private final Object lock = new Object();
	
	public void scheduleTask(Task task) {
		synchronized (lock) {
			queuedTasks.put(task.getUuid(), task);
		}
		
		RunnableTask executorTask = new RunnableTask(task, this);
		executor.execute(executorTask);
	}
	
	/**
	 * @return the tasks
	 */
	public List<Task> getTasks() {
		synchronized (lock) {
			List<Task> list = new ArrayList<Task>(queuedTasks.values());
			list.addAll(finishedTasks.values());
			return list;
		}
	}
	
	public Task getTask(String uuid) {
		synchronized (lock) {
			Task task = queuedTasks.get(uuid);
			if (task == null) {
				task = finishedTasks.get(uuid);
			}
			return task;
		}
	}
	
	public boolean removeTask(String uuid) {
		Task task = null;
		synchronized (lock) {
			task = queuedTasks.remove(uuid);
			if (task == null) {
				task = finishedTasks.remove(uuid);
			}
		}
		return (task != null);
	}
	
	public boolean isActive() {
		return executor.getActiveCount() > 0;
	}
	
	private static class RunnableTask implements Runnable {
		
		private final Task task;
		
		private final TaskEngine engine;
		
		private final UserContext userContext;
		
		public RunnableTask(Task task, TaskEngine engine) {
			this.task = task;
			this.userContext = Context.getUserContext();
			this.engine = engine;
		}
		
		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			task.setActive(true);
			task.setCompleted(false);
			try {
				Context.setUserContext(userContext);
				MetadataSharing.getService().executeTask(task);
			}
			catch (Exception e) {
				task.log("Task failed", new TaskException("Task failed", e));
			}
			finally {
				task.setActive(false);
				task.setCompleted(true);
				Context.clearUserContext();
				synchronized (engine.lock) {
					engine.queuedTasks.remove(task.getUuid());
					engine.finishedTasks.put(task.getUuid(), task);
				}
			}
		}
		
	}
}
