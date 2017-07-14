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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.serializer.MetadataSerializer;

import java.io.Serializable;
import java.util.Date;


/**
 * Defines an imported metadata package in the system.
 */
public class ImportedPackage extends Package implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer importedPackageId;
	
	private Date dateImported;
	
	private Integer remoteVersion;
	
	private ImportConfig importConfig = new ImportConfig();
	
	private SubscriptionStatus subscriptionStatus = SubscriptionStatus.DISABLED;

	@XStreamOmitField
	private XStream xstream;

	public ImportedPackage() {
	}
	
	public ImportedPackage(ExportedPackage pack) {
		setName(pack.getName());
		setDescription(pack.getDescription());
		setUuid(pack.getUuid());
		setDateCreated(pack.getDateCreated());
		setOpenmrsVersion(pack.getOpenmrsVersion());
		setSubscriptionUrl(pack.getSubscriptionUrl());
		
		getModules().putAll(pack.getModules());
		
		setGroupUuid(pack.getGroupUuid());
		setVersion(pack.getVersion());
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return importedPackageId;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		importedPackageId = id;
	}
	
	/**
	 * @return the packageImportId
	 */
	public Integer getImportedPackageId() {
		return importedPackageId;
	}
	
	/**
	 * @param importedPackageId the packageImportId to set
	 */
	public void setImportedPackageId(Integer importedPackageId) {
		this.importedPackageId = importedPackageId;
	}
	
	public Date getDateImported() {
		return dateImported;
	}
	
	public void setDateImported(Date dateImported) {
		this.dateImported = dateImported;
	}
	
	public boolean isImported() {
		return getDateImported() != null;
	}
	
	public boolean isSubscribed() {
		return !SubscriptionStatus.DISABLED.equals(subscriptionStatus);
	}
	
	/**
	 * @return the remoteVersion
	 */
	public Integer getRemoteVersion() {
		return remoteVersion;
	}
	
	/**
	 * @param remoteVersion the remoteVersion to set
	 */
	public void setRemoteVersion(Integer remoteVersion) {
		this.remoteVersion = remoteVersion;
	}
	
	/**
	 * @return the importConfig
	 */
	public ImportConfig getImportConfig() {
		return importConfig;
	}
	
	/**
	 * @param importConfig the importConfig to set
	 */
	public void setImportConfig(ImportConfig importConfig) {
		this.importConfig = importConfig;
	}
	
	/**
	 * @return the subscriptionStatus
	 */
	public SubscriptionStatus getSubscriptionStatus() {
		return subscriptionStatus;
	}
	
	/**
	 * @param subscriptionStatus the subscriptionStatus to set
	 */
	public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
	}

	public String getImportConfigXml(){
		return getXStream().toXML(importConfig);
	}

	public void setImportConfigXml(String importConfigXml){
		importConfig = (ImportConfig) getXStream().fromXML(importConfigXml);
	}

	private XStream getXStream() {
		if (xstream == null) {
			MetadataSerializer serialier = Context.getRegisteredComponent(MetadataSharingConsts.MODULE_ID + ".MetadataSerializer", MetadataSerializer.class);
			xstream = serialier.getXStream();
		}

		return xstream;
	}
	
	/**
	 * Tests if the current subscription's status indicates that there were errors.
	 * 
	 * @see {@link SubscriptionStatus}
	 * @return true if there were errors, false otherwise
	 */
	public boolean hasSubscriptionErrors() {
		if (subscriptionStatus.getValue() >= 30)
			return true;
		else
			return false;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ImportedPackage) {
			ImportedPackage packageImport = (ImportedPackage) obj;
			
			if (getImportedPackageId() != null && packageImport.getImportedPackageId() != null)
				return getImportedPackageId().equals(packageImport.getImportedPackageId());
		}
		
		// if packageId is null for either object, for equality the
		// two objects must be the same
		return this == obj;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (getImportedPackageId() == null)
			return super.hashCode();
		return getImportedPackageId().hashCode();
	}
	
}
