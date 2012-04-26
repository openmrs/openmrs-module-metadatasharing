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
package org.openmrs.module.metadatasharing.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.GlobalPropertyListener;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.serializer.converter.OpenmrsObjectConverter;

/**
 * This class is called whenever the metadata sharing properties are changed.
 */
public class MetadataSharingGlobalPropertyListener implements
		GlobalPropertyListener {
	
	public static Log log = LogFactory.getLog(MetadataSharingGlobalPropertyListener.class);
	
	private static Set<String> classNamesToPersistIds = null;
	
	private static Set<Class> classesToPersistIds = null;
	
	/**
	 * A collection of classes that should try to have their ids persisted
	 * 
	 * @see MetadataSharingConsts#GP_PERSIST_IDS_FOR_CLASSES
	 * @see OpenmrsObjectConverter
	 */
	public static Set<String> getClassNamesToPersistIds() {
		if (classNamesToPersistIds == null) {
			GlobalProperty gp = Context.getAdministrationService()
			.getGlobalPropertyObject(
					MetadataSharingConsts.GP_PERSIST_IDS_FOR_CLASSES);
			updatePersistClasses(gp);
		}
		
		return classNamesToPersistIds;
	}
	
	public static Set<Class> getClassesToPersistIds() {
		if (classesToPersistIds == null) {
			classesToPersistIds = new HashSet<Class>();
			
			// make sure we have some class names 
			getClassNamesToPersistIds();
		}
		
		return classesToPersistIds;
	}
	
	@Override
	public boolean supportsPropertyName(String propertyName) {
		return propertyName.equals(MetadataSharingConsts.GP_PERSIST_IDS_FOR_CLASSES);
	}

	@Override
	public void globalPropertyChanged(GlobalProperty newValue) {
		if (newValue.getProperty().equals(MetadataSharingConsts.GP_PERSIST_IDS_FOR_CLASSES)) {
			updatePersistClasses(newValue);
		}
	}
	
	/**
	 * Helper method so that the next time this class is called it refetches the GP values
	 */
	public static void clearCache() {
		classNamesToPersistIds = null;
		classesToPersistIds = null;
	}

	/**
	 * Helper method to update the static variable
	 * 
	 * @param newValue the GP_PERSIST_IDS_FOR_CLASSES global property
	 */
	private static void updatePersistClasses(GlobalProperty newValue) {
		// erase current values
		classNamesToPersistIds = new HashSet<String>();
		classesToPersistIds = new HashSet<Class>();
		
		// add all new values
		if (newValue != null && !StringUtils.isBlank(newValue.getPropertyValue())) {
			for (String className : newValue.getPropertyValue().split(",")) {
				className = className.trim();
				classNamesToPersistIds.add(className);
				
				try {
					Class c = Context.loadClass(className);
					classesToPersistIds.add(c);
				}
				catch(Exception e) {
					log.debug("Unable to load class " + className, e);
				}
			}
		}
		
	}
	@Override
	public void globalPropertyDeleted(String propertyName) {
		// erase current values
		classNamesToPersistIds = new HashSet<String>();
		classesToPersistIds = new HashSet<Class>();
	}

}
