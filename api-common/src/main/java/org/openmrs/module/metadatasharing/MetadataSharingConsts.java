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
package org.openmrs.module.metadatasharing;

import org.openmrs.module.metadatasharing.util.MetadataSharingGlobalPropertyListener;

/**
 * Module constants
 */
public class MetadataSharingConsts {
	
	public static final String MODULE_ID = "metadatasharing";
	
	public static final String MODULE_SERVICE = "metadataSharingService";
	
	public static final String MODULE_PRIVILEGE = "Share Metadata";
	
	public static final String MODULE_PATH = "/module/" + MODULE_ID;
	
	public static final String GP_SYSTEM_CONCEPT_SOURCE = MODULE_ID + ".systemConceptSource";
	
	public static final String GP_URL_PREFIX = MODULE_ID + ".urlPrefix";
	
	/**
	 * Global property name, which says how often we should automatically check for updates
	 */
	public static final String GP_INTERVAL_DAYS = MODULE_ID + ".intervalDays";
	
	/**
	 * Global property name, which says if we should automatically notify users about packages
	 * updates
	 */
	public static final String GP_NOTIFY = MODULE_ID + ".notify";
	
	/**
	 * Global property name, which says if user has properly configured the module
	 */
	public static final String GP_CONFIGURED = MODULE_ID + ".configured";
	
	/**
	 * Global Property name, which lists classes to try and persist ids for
	 * 
	 * @see MetadataSharingGlobalPropertyListener
	 */
	public static final String GP_PERSIST_IDS_FOR_CLASSES = MODULE_ID + ".persistIdsForClasses";
	
	/**
	 * Global property name, Comma-separated list of concept source Ids for preferred sources, in
	 * case an incoming concept has duplicate mappings to any of these sources, no confirmation will
	 * be required unless its datatype or concept class differs from that of the existing concept
	 */
	public static final String GP_PREFERRED_CONCEPT_SOURCE_IDs = MODULE_ID + ".preferredConceptSourceIds";
	
	/**
	 * Global property name, specifies whether metadata packages can be exported on-the-fly
	 */
	public static final String GP_ENABLE_ON_THE_FLY_PACKAGES = MODULE_ID + ".enableOnTheFlyPackages";
	
	/**
	 * Global property name, key to grant access to remote systems to consume module webservices
	 * RESTfully
	 */
	public static final String GP_WEBSERVICES_KEY = MODULE_ID + ".webservicesKey";
	
	/**
	 * The task name for automatic update checks
	 */
	public static final String CHECK_FOR_UPDATES_TASK_NAME = "Check for Metadata Sharing Module packages updates";
	
	/**
	 * Privilege required for managing scheduler
	 */
	public static final String MANAGE_SCHEDULER_PRIVILEGE = "Manage Scheduler";
	
	/**
	 * Default simple date format for date
	 */
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
