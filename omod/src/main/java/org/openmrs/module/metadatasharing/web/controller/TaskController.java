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
package org.openmrs.module.metadatasharing.web.controller;

import javax.servlet.http.HttpSession;

import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.task.Task;
import org.openmrs.module.metadatasharing.task.TaskEngine;
import org.openmrs.module.metadatasharing.web.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 */
@Controller(MetadataSharingConsts.MODULE_ID + ".TaskController")
public class TaskController {
	
	public static final String TASKS = "tasks";
	
	public static final String TASK = "task";
	
	public static final String TASK_PATH = MetadataSharingConsts.MODULE_PATH + "/task";
	
	public static final String LIST_PATH = TASK_PATH + "/list";
	
	public static final String REMOVE_PATH = TASK_PATH + "/remove";
	
	public static final String DETAILS_PATH = TASK_PATH + "/details";
	
	@Autowired
	private TaskEngine taskEngine;
	
	@RequestMapping(value = LIST_PATH)
	public void list(Model model) {
		model.addAttribute(TASKS, taskEngine.getTasks());
	}
	
	@RequestMapping(value = REMOVE_PATH)
	public String remove(String uuid, HttpSession session) {
		taskEngine.removeTask(uuid);
		return WebUtils.redirect(LIST_PATH);
	}
	
	@RequestMapping(value = DETAILS_PATH)
	public void details(String uuid, Model model) {
		Task task = taskEngine.getTask(uuid);
		model.addAttribute(TASK, task);
	}
}
