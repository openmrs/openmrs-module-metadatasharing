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
package org.openmrs.module.metadatasharing.handler.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.FormField;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.User;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataTypesHandler;
import org.openmrs.module.metadatasharing.reflection.OpenmrsClassScanner;
import org.openmrs.module.metadatasharing.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("metadatasharing.OpenmrsMetadataHandler")
public class OpenmrsMetadataHandler implements MetadataTypesHandler<OpenmrsMetadata>, MetadataPropertiesHandler<OpenmrsMetadata> {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private final Map<Class<? extends OpenmrsMetadata>, String> types;
	
	/**
	 * @param scanner
	 * @throws IOException
	 * @should support classes with same simple name
	 */
	@Autowired
	public OpenmrsMetadataHandler(OpenmrsClassScanner scanner) throws IOException {
		Map<String, Class<? extends OpenmrsMetadata>> typesToClasses = new HashMap<String, Class<? extends OpenmrsMetadata>>();
		Map<Class<? extends OpenmrsMetadata>, String> classesToTypes = new HashMap<Class<? extends OpenmrsMetadata>, String>();
		
		Set<Class<? extends OpenmrsMetadata>> ignoredTypes = new HashSet<Class<? extends OpenmrsMetadata>>();
		ignoredTypes.add(User.class);
		ignoredTypes.add(FormField.class);
		
		for (Class<OpenmrsMetadata> clazz : scanner.getOpenmrsMetadataClasses()) {
			if (ignoredTypes.contains(clazz)) {
				continue;
			}
			
			Class<? extends OpenmrsMetadata> otherClazz = typesToClasses.get(clazz.getSimpleName());
			
			if (otherClazz != null) {
				log.info(clazz.getName() + " and " + otherClazz.getName()
				        + " have the same simple name. Trying to resolve the confict...");
				
				String otherName = extractSimpleNameForModule(otherClazz); //try with a module name
				if (otherName != null) {
					Class<? extends OpenmrsMetadata> unresolvable = typesToClasses.get(otherName);
					if (unresolvable != null) {
						otherName = otherClazz.getName(); //fall back to full name
					}
					
					log.info(otherClazz.getName() + " added as " + otherName);
					
					typesToClasses.put(otherName, otherClazz);
					classesToTypes.put(otherClazz, otherName);
					
					typesToClasses.put(clazz.getSimpleName(), clazz);
					classesToTypes.put(clazz, clazz.getSimpleName());
				} else {
					String name = extractSimpleNameForModule(clazz); //try with a module name
					Class<? extends OpenmrsMetadata> unresolvable = typesToClasses.get(name);
					if (unresolvable != null) {
						name = clazz.getName(); //fall back to full name
					}
					
					log.info(clazz.getName() + " added as " + name);
					
					typesToClasses.put(name, clazz);
					classesToTypes.put(clazz, name);
					
					typesToClasses.put(otherClazz.getSimpleName(), otherClazz);
					classesToTypes.put(otherClazz, otherClazz.getSimpleName());
				}
			} else {
				typesToClasses.put(clazz.getSimpleName(), clazz);
				classesToTypes.put(clazz, clazz.getSimpleName());
			}
		}
		
		types = Collections.unmodifiableMap(classesToTypes);
	}
	
	private String extractSimpleNameForModule(Class<? extends OpenmrsMetadata> clazz) {
		String moduleNameRegex = "org\\.openmrs\\.module\\.([^\\.]*).*";
		Pattern moduleNamePattern = Pattern.compile(moduleNameRegex);
		Matcher moduleNameMatcher = moduleNamePattern.matcher(clazz.getName());
		if (moduleNameMatcher.matches()) {
			return clazz.getSimpleName() + "." + moduleNameMatcher.group(1);
		} else {
			return null;
		}
	}
	
	@Override
	public int getPriority() {
		return 0;
	}
	
	@Override
	public Map<Class<? extends OpenmrsMetadata>, String> getTypes() {
		return types;
	}
	
	@Override
	public Integer getId(OpenmrsMetadata object) {
		return object.getId();
	}
	
	@Override
	public void setId(OpenmrsMetadata object, Integer id) {
		object.setId(id);
	}
	
	@Override
	public void setUuid(OpenmrsMetadata object, String uuid) {
		object.setUuid(uuid);
	}
	
	@Override
	public String getUuid(OpenmrsMetadata object) {
		return (object.getUuid() != null) ? object.getUuid() : "";
	}
	
	@Override
	public Boolean getRetired(OpenmrsMetadata object) {
		return object.isRetired();
	}
	
	@Override
	public void setRetired(OpenmrsMetadata object, Boolean retired) {
		object.setRetired(retired);
	}
	
	@Override
	public String getName(OpenmrsMetadata object) {
		return (object.getName() != null) ? object.getName() : "";
	}
	
	@Override
	public String getDescription(OpenmrsMetadata object) {
		return (object.getDescription() != null) ? object.getDescription() : "";
	}
	
	@Override
	public Date getDateChanged(OpenmrsMetadata object) {
		return DateUtil.getLastDateChanged(object);
	}
	
	@Override
	public Map<String, Object> getProperties(OpenmrsMetadata object) {
		return Collections.emptyMap();
	}
}
