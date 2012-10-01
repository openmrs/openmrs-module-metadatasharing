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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.metadatasharing.io.MetadataZipper;
import org.openmrs.util.OpenmrsConstants;

/**
 * Defines a metadata package in the system.
 * 
 * @since 1.0
 */
public abstract class Package extends BaseOpenmrsObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @since 1.0
	 */
	private Date dateCreated;
	
	/**
	 * @since 1.0
	 */
	private String name;
	
	/**
	 * @since 1.0
	 */
	private String description;
	
	/**
	 * @since 1.0
	 */
	private String openmrsVersion;
	
	/**
	 * @since 1.0
	 */
	private Integer version;
	
	/**
	 * @since 1.0
	 */
	private String groupUuid;
	
	/**
	 * @since 1.0
	 */
	private String subscriptionUrl;
	
	/**
	 * @since 1.0
	 */
	private SerializedPackage serializedPackage;
	
	/**
	 * @since 1.0
	 */
	private Map<String, String> modules;
	
	/**
	 * @since 1.0
	 */
	private Set<Item> items;
	
	/**
	 * @since 1.0
	 */
	private Set<Item> relatedItems;
	
	/**
	 * @since 1.1
	 */
	private Boolean incrementalVersion;
	
	public Package() {
		setUuid(UUID.randomUUID().toString());
		dateCreated = new Date();
		name = "";
		description = "";
		version = 1;
		groupUuid = UUID.randomUUID().toString();
		openmrsVersion = OpenmrsConstants.OPENMRS_VERSION;
		modules = new HashMap<String, String>();
		for (Module module : ModuleFactory.getStartedModules()) {
			modules.put(module.getModuleId(), module.getVersion());
		}
		items = new LinkedHashSet<Item>();
		relatedItems = new LinkedHashSet<Item>();
		incrementalVersion = null;
	}
	
	/**
	 * @return the dateCreated
	 * @since 1.0
	 */
	public Date getDateCreated() {
		return dateCreated;
	}
	
	/**
	 * @param dateCreated the dateCreated to set
	 * @since 1.0
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	/**
	 * @return the name
	 * @since 1.0
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 * @since 1.0
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the description
	 * @since 1.0
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 * @since 1.0
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the openmrsVersion
	 * @since 1.0
	 */
	public String getOpenmrsVersion() {
		return openmrsVersion;
	}
	
	/**
	 * @param openmrsVersion the openmrsVersion to set
	 * @since 1.0
	 */
	public void setOpenmrsVersion(String openmrsVersion) {
		this.openmrsVersion = openmrsVersion;
	}
	
	/**
	 * @param version the version to set
	 * @since 1.0
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	/**
	 * @return the version
	 * @since 1.0
	 */
	public Integer getVersion() {
		return version;
	}
	
	/**
	 * @param groupUuid the groupUuid to set
	 * @since 1.0
	 */
	public void setGroupUuid(String groupUuid) {
		this.groupUuid = groupUuid;
	}
	
	/**
	 * @return the groupUuid
	 * @since 1.0
	 */
	public String getGroupUuid() {
		return groupUuid;
	}
	
	/**
	 * The URL which points to the future package updates
	 * 
	 * @param subscriptionUrl the subscriptionUrl to set
	 * @since 1.0
	 */
	public void setSubscriptionUrl(String subscriptionUrl) {
		this.subscriptionUrl = subscriptionUrl;
	}
	
	/**
	 * The URL which points to the future package updates
	 * 
	 * @return the subscriptionUrl
	 * @since 1.0
	 */
	public String getSubscriptionUrl() {
		return subscriptionUrl;
	}
	
	/**
	 * @return the serializedPackage
	 * @since 1.0
	 */
	public SerializedPackage getSerializedPackage() throws IOException {
		return serializedPackage;
	}
	
	/**
	 * @param serializedPackage the serializedPackage to set
	 * @since 1.0
	 */
	public void setSerializedPackage(SerializedPackage serializedPackage) throws IOException {
		this.serializedPackage = serializedPackage;
	}
	
	/**
	 * @param serializedPackageStream the serializedPackageStream to set
	 * @since 1.0
	 */
	public void setSerializedPackageStream(InputStream serializedPackageStream) throws IOException {
		serializedPackage = new MetadataZipper().unzipPackage(serializedPackageStream);
	}
	
	/**
	 * @return the serializedPackageStream
	 * @since 1.0
	 */
	public InputStream getSerializedPackageStream() throws IOException {
		if (serializedPackage == null) {
			return null;
		} else {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			new MetadataZipper().zipPackage(output, serializedPackage);
			ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
			return input;
		}
	}
	
	/**
	 * @return the items
	 * @since 1.0
	 */
	public Set<Item> getItems() {
		return items;
	}
	
	/**
	 * @return the relatedItems
	 * @since 1.0
	 */
	public Set<Item> getRelatedItems() {
		return relatedItems;
	}
	
	/**
	 * @return the modules[moduleId, version]
	 * @since 1.0
	 */
	public Map<String, String> getModules() {
		return modules;
	}
	
	/**
	 * @since 1.1
	 */
	public boolean isIncrementalVersion() {
		return (incrementalVersion != null) ? incrementalVersion : false;
	}

	/**
	 * @since 1.1
	 */
	public void setIncrementalVersion(Boolean incrementalVersion) {
		this.incrementalVersion = incrementalVersion;
	}
	
	/**
	 * Imperfect check to see what modules are required for the items in this package (based on
	 * looking for java package names like org.openmrs.module.XYZ).
	 * 
	 * @param pack
	 * @return the requiredModules[moduleId, version]
	 * @should get modules ids and versions
	 * @since 1.0
	 */
	public Map<String, String> getRequiredModules() {
		Set<String> classNames = new HashSet<String>();
		
		Transformer getClassnameTransform = new Transformer() {
			
			@Override
			public Object transform(Object item) {
				try {
					return ((Item) item).getClassname();
				}
				catch (Exception ex) {
					throw new RuntimeException("Programming Error: expected ItemSummary or ImportedItem", ex);
				}
			}
		};
		
		CollectionUtils.collect(items, getClassnameTransform, classNames);
		CollectionUtils.collect(relatedItems, getClassnameTransform, classNames);
		
		// org.openmrs.module.(group 1: moduleId)[.more.packages].(group 2: SimpleClassName)
		Pattern regex = Pattern.compile("org\\.openmrs\\.module\\.(\\w+)[\\.\\w+]*\\.(\\w+)");
		
		Map<String, String> requiredModules = new HashMap<String, String>();
		for (String cn : classNames) {
			Matcher m = regex.matcher(cn);
			if (m.matches()) {
				String moduleId = m.group(1);
				requiredModules.put(moduleId, modules.get(moduleId));
			}
		}
		
		return requiredModules;
	}
	
	/**
	 * Loads the given package in the current instance.
	 * 
	 * @param other
	 * @throws IOException
	 * @since 1.0
	 */
	public void loadPackage(Package other) throws IOException {
		setUuid(other.getUuid());
		dateCreated = other.getDateCreated();
		name = other.getName();
		description = other.getDescription();
		version = other.getVersion();
		groupUuid = other.getGroupUuid();
		subscriptionUrl = other.getSubscriptionUrl();
		openmrsVersion = other.getOpenmrsVersion();
		modules = other.getModules();
		items = other.getItems();
		relatedItems = other.getRelatedItems();
		serializedPackage = other.getSerializedPackage();
		incrementalVersion = other.isIncrementalVersion();
	}
}
