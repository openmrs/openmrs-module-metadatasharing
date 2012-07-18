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
package org.openmrs.module.metadatasharing.wrapper.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.Package;
import org.openmrs.module.metadatasharing.SerializedPackage;
import org.openmrs.module.metadatasharing.task.Task;
import org.openmrs.module.metadatasharing.task.TaskException;
import org.openmrs.module.metadatasharing.task.impl.ExportPackageTask;
import org.openmrs.module.metadatasharing.wrapper.PackageExporter;

/**
 * Exports the given package.
 */
public class PackageExporterImpl extends PackageExporter {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private final ExportedPackage exportedPackage;
	
	public PackageExporterImpl() {
		exportedPackage = new ExportedPackage();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageExporter#getPackage()
	 */
	@Override
	public Package getPackage() {
		return exportedPackage;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageExporter#getExportedPackage()
	 */
	@Override
	public ExportedPackage getExportedPackage() {
		return exportedPackage;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageExporter#updatePackageHeader()
	 */
	@Override
	public void updatePackageHeader() throws TaskException {
		StringBuilder header = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		try {
			if (exportedPackage.getSerializedPackage() == null) {
				throw new TaskException("Package must be already exported");
			}
			
			header.append(MetadataSharing.getInstance().getMetadataSerializer().serialize(exportedPackage)).append("\n");
			
			exportedPackage.setSerializedPackage(new SerializedPackage(header.toString(), exportedPackage
			        .getSerializedPackage().getMetadata()));
			
			MetadataSharing.getService().saveExportedPackage(exportedPackage);
		}
		catch (Exception e) {
			throw new TaskException("Failed to update header", e);
		}
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageExporter#exportPackage()
	 */
	@Override
	public ExportedPackage exportPackage() throws TaskException {
		ExportPackageTask task = new ExportPackageTask(getExportedPackage(), false);
		task.execute();
		return task.getExportedPackage();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.wrapper.PackageExporter#schedulePackageExport()
	 */
	@Override
	public Task schedulePackageExport() throws TaskException {
		Task task = new ExportPackageTask(getExportedPackage(), true);
		MetadataSharing.getInstance().getTaskEngine().scheduleTask(task);
		return task;
	}
}
