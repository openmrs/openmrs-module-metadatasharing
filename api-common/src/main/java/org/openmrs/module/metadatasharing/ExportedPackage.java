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
import java.sql.Blob;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.api.MetadataService;
import org.openmrs.module.metadatasharing.io.MetadataZipper;

/**
 * Defines an exported metadata package in the system.
 */
public class ExportedPackage extends Package implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer exportedPackageId;
	
	private transient boolean published;
	
	private transient Blob content;
	
	public ExportedPackage() {
		published = false; //the package is not published by default 
	}
	
	/**
	 * Constructor taking in the primary key packageId value
	 * 
	 * @param exportedPackageId internal id for this package
	 */
	public ExportedPackage(Integer exportedPackageId) {
		this.exportedPackageId = exportedPackageId;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExportedPackage) {
			ExportedPackage pack = (ExportedPackage) obj;
			
			if (getExportedPackageId() != null && pack.getExportedPackageId() != null)
				return getExportedPackageId().equals(pack.getExportedPackageId());
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
		if (getExportedPackageId() == null)
			return super.hashCode();
		return getExportedPackageId().hashCode();
	}
	
	@Override
	public Integer getId() {
		return exportedPackageId;
	}
	
	@Override
	public void setId(Integer id) {
		this.exportedPackageId = id;
	}
	
	public Integer getExportedPackageId() {
		return exportedPackageId;
	}
	
	public void setExportedPackageId(Integer exportedPackageId) {
		this.exportedPackageId = exportedPackageId;
	}
	
	protected Blob getContent() {
		return content;
	}
	
	protected void setContent(Blob content) {
		this.content = content;
	}
	
	public InputStream getSerializedPackageStream() throws IOException {
		if (getContent() == null) {
			return null;
		} else {
			try {
				return getContent().getBinaryStream();
			}
			catch (SQLException e) {
				throw new IOException("Unable to retrieve content from the database", e);
			}
		}
	}
	
	public void setSerializedPackageStream(InputStream serializedPackageStream) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		IOUtils.copy(serializedPackageStream, output);
		this.content = Context.getService(MetadataService.class).createBlob(output.toByteArray());
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		super.setSerializedPackageStream(input);
	}
	
	public SerializedPackage getSerializedPackage() throws IOException {
		if (super.getSerializedPackage() == null) {
			if (getSerializedPackageStream() != null) {
				setSerializedPackageStream(getSerializedPackageStream());
			}
		}
		
		return super.getSerializedPackage();
	}
	
	public void setSerializedPackage(SerializedPackage serializedPackage) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		new MetadataZipper().zipPackage(output, serializedPackage);
		this.content = Context.getService(MetadataService.class).createBlob(output.toByteArray());
		
		super.setSerializedPackage(serializedPackage);
	}
	
	/**
	 * The Boolean stating if the package is published
	 * 
	 * @param published the published to set
	 */
	public void setPublished(boolean published) {
		this.published = published;
	}
	
	/**
	 * The Boolean stating if the package is published
	 * 
	 * @return boolean stating if the package is published
	 */
	public boolean isPublished() {
		return published;
	}
}
