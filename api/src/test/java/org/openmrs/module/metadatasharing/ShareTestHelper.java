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

import java.util.List;

import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;

/**
 * This class is meant to be implemented inline by MDS tests. The
 * {@link BaseShareTest#runShareTest(MDSTestHelper)} method calls the methods here to test
 * exporting metadata from a server to another server
 */
public abstract class ShareTestHelper {
	
	/**
	 * This method should do the initial work of exporting data to a package
	 * 
	 * @return the list of objects to include in the package
	 * @throws Exception
	 */
	public abstract List<?> prepareExportServer() throws Exception;
	
	/**
	 * Allows to inspect exported package.
	 * 
	 * @param exportedPackage
	 * @throws Exception
	 */
	public void runOnExportServerAfterExport(ExportedPackage exportedPackage) throws Exception {
	}
	
	/**
	 * Allows to setup import server before import
	 * 
	 * @throws Exception
	 */
	public void prepareImportServer() throws Exception {
	}
	
	/**
	 * Allows to modify default import config. Defaults to {@link ImportMode#PARENT_AND_CHILD}.
	 * 
	 * @return the ImportConfig
	 */
	public ImportConfig getImportConfig() {
		return ImportConfig.valueOf(ImportMode.PARENT_AND_CHILD);
	}
	
	/**
	 * This method is called after compiling the import package. The end test is allowed to modify
	 * this as needed (e.g. conflict resolution)
	 * 
	 * @param importer the importer that is used
	 * @throws Exception
	 */
	public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
	}
	
	/**
	 * This method runs assertions to make sure the second server got all the changes
	 * 
	 * @throws Exception
	 */
	public abstract void runOnImportServerAfterImport() throws Exception;
	
}
