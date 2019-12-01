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

import java.io.Serializable;
import java.util.Date;

/**
 * Simple bean that represents the primary properties for an ExportedPackage,
 * not including the package content
 */
public class ExportedPackageSummary implements Serializable {

	private Integer exportedPackageId;
	private String uuid;
	private String groupUuid;
	private String name;
	private String description;
	private Integer version;
	private boolean published;
	private String subscriptionUrl;
	private Date dateCreated;

	public ExportedPackageSummary() {}

	public ExportedPackageSummary(ExportedPackage exportedPackage) {
		this.exportedPackageId = exportedPackage.getExportedPackageId();
		this.uuid = exportedPackage.getUuid();
		this.groupUuid = exportedPackage.getGroupUuid();
		this.name = exportedPackage.getName();
		this.description = exportedPackage.getDescription();
		this.version = exportedPackage.getVersion();
		this.published = exportedPackage.isPublished();
		this.subscriptionUrl = exportedPackage.getSubscriptionUrl();
		this.dateCreated = exportedPackage.getDateCreated();
	}

	public Integer getExportedPackageId() {
		return exportedPackageId;
	}

	public void setExportedPackageId(Integer exportedPackageId) {
		this.exportedPackageId = exportedPackageId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getGroupUuid() {
		return groupUuid;
	}

	public void setGroupUuid(String groupUuid) {
		this.groupUuid = groupUuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public String getSubscriptionUrl() {
		return subscriptionUrl;
	}

	public void setSubscriptionUrl(String subscriptionUrl) {
		this.subscriptionUrl = subscriptionUrl;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
}
