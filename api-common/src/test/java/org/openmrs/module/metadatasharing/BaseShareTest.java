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

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.util.MetadataSharingGlobalPropertyListener;
import org.openmrs.module.metadatasharing.wrapper.PackageExporter;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.util.StringUtils;

public abstract class BaseShareTest extends BaseModuleContextSensitiveTest {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	protected final static String omrsMinorVersion;
	
	static {
		Properties properties = new Properties();
		try {
	        properties.load(ClassLoader.getSystemResourceAsStream("mds-test-config.properties"));
        }
        catch (IOException e) {
	        throw new RuntimeException(e);
        }
		omrsMinorVersion = properties.getProperty("omrsMinorVersion");
	}
	
	public boolean insertInitialDataSet() {
		return false;
	}
	
	/**
	 * @return class pathname to dbunit xml file
	 */
	public String getInitialDataset() {
		return null;
	}
	
	/**
	 * The dataset to run after {@link ShareTestHelper#prepareExportServer()} and
	 * {@link #repopulateDB(String)} are called but before
	 * {@link metadatasharingTestHelper#runOnImportServer()}
	 * 
	 * @return string or null if no dataset should execute
	 */
	public String getImportServerDataset() {
		return null;
	}
	
	/**
	 * Executes the metadatasharing test workflow: <br/>
	 * 1. prepopulate DB <br/>
	 * 2. Execute set of instructions simulating metadatasharing parent<br/>
	 * 3. Fetch metadatasharing package, re-initialize DB for importing server and then apply the
	 * mds package<br/>
	 * 
	 * @param testMethods helper object holding methods for export and import server execution
	 * @throws Exception
	 */
	public void runShareTest(ShareTestHelper testMethods) throws Exception {
		
		beforeRunOnExportingServer();
		
		List<?> objectsToExport = runOnExportServer(testMethods);
		
		// create the package
		PackageExporter metadataExporter = MetadataSharing.getInstance().newPackageExporter();
		for (Object o : objectsToExport) {
			metadataExporter.addItem(o);
		}
		metadataExporter.getPackage().setName("Package");
		metadataExporter.getPackage().setDescription("Package");
		metadataExporter.exportPackage();
		
		runOnExportServerAfterExport(metadataExporter.getExportedPackage(), testMethods);
		
		// clear the db and set up the importing server's db
		repopulateDB(getImportServerDataset());
		
		runOnImportServerBeforeImport(testMethods);
		
		//Clear session so that objects are loaded with Hibernate proxies, test for META-331
		Context.flushSession();
		Context.clearSession();
		
		// import the previously exported package to our "fresh" server
		importPackage(metadataExporter.getExportedPackage(), testMethods);
		
		Context.flushSession();
		Context.clearSession();
		
		runOnImportServerAfterImport(testMethods);
	}
	
	/**
	 * Allows to inspect generated package.
	 * 
	 * @param testMethods
	 */
	protected void runOnExportServerAfterExport(ExportedPackage exportedPackage, ShareTestHelper testMethods)
	    throws Exception {
		testMethods.runOnExportServerAfterExport(exportedPackage);
	}
	
	/**
	 * Sets up initial data set before set of instructions simulating child changes is executed.
	 * 
	 * @see #runOnExportServer(ShareTestHelper)
	 * @see #runOnImportServer(ShareTestHelper)
	 * @throws Exception
	 */
	protected void beforeRunOnExportingServer() throws Exception {
		Context.openSession();
		deleteAllData();
		String version = omrsMinorVersion;
		if ("1.7".equals(omrsMinorVersion)) {
			version = "1.6";
		}
		
		if (insertInitialDataSet()) {
			executeDataSet("MDSCreateTest-" + version + ".xml");
		} else {
			baseSetupWithStandardDataAndAuthentication();
		}
		executeDataSet("MDSImportServer.xml");
		
		String initialDataset = getInitialDataset();
		if (StringUtils.hasText(initialDataset))
			executeDataSet(initialDataset);
		
		MetadataSharingGlobalPropertyListener.clearCache();
		
		authenticate();
	}
	
	protected void runOnImportServerBeforeImport(ShareTestHelper testMethods) throws Exception {
		authenticate();
		log.info("\n************************************* Running On Import Server Before Import *************************************");
		testMethods.prepareImportServer();
	}
	
	protected void runOnImportServerAfterImport(ShareTestHelper testMethods) throws Exception {
		authenticate();
		log.info("\n************************************* Running On Import Server After Import *************************************");
		testMethods.runOnImportServerAfterImport();
	}
	
	protected void repopulateDB(String xmlFileToExecute) throws Exception {
		
		Context.clearSession();
		
		//reload db from scratch
		log.info("\n************************************* Reload Database *************************************");
		deleteAllData();
		String version = omrsMinorVersion;
		if ("1.7".equals(omrsMinorVersion)) {
			version = "1.6";
		}
		
		if (insertInitialDataSet()) {
			executeDataSet("MDSCreateTest-" + version + ".xml");
		} else {
			baseSetupWithStandardDataAndAuthentication();
		}
		
		GlobalProperty allowedLocales = Context.getAdministrationService().getGlobalPropertyObject(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST);
		allowedLocales.setPropertyValue(allowedLocales.getPropertyValue() + ",en_US");
		Context.getAdministrationService().saveGlobalProperty(allowedLocales);
		
		if (xmlFileToExecute == null)
			xmlFileToExecute = "MDSImportServer.xml";
		executeDataSet(xmlFileToExecute);
		
		MetadataSharingGlobalPropertyListener.clearCache();
		
		return;
	}
	
	protected List<?> runOnExportServer(ShareTestHelper testMethods) throws Exception {
		authenticate();
		log.info("\n************************************* Running On Export Server *************************************");
		return testMethods.prepareExportServer();
	}
	
	protected void importPackage(ExportedPackage mdsPackage, ShareTestHelper testMethods) throws Exception {
		// run this package
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		metadataImporter.setImportConfig(testMethods.getImportConfig());
		metadataImporter.loadSerializedPackageStream(mdsPackage.getSerializedPackageStream());
		
		testMethods.runOnImportServerBeforeImport(metadataImporter);
		
		for (int i = 0; i < metadataImporter.getPartsCount(); i++) {
			for (ImportedItem importedItem : metadataImporter.getImportedItems(i)) {
				importedItem.setAssessed(true);
			}
		}
		
		metadataImporter.saveState();
		
		metadataImporter.importPackage();
		
		Context.flushSession();
	}
	
}
