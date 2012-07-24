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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.reflection.ClassUtil;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;

/**
 * Base class for testing exporting & importing different types.
 * 
 * @param <T>
 */
public abstract class BaseShareScenarioTest<T> extends BaseShareTest {
	
	private List<T> packageItems = new ArrayList<T>();
	
	private List<ImportedItem> importedItems;
	
	@SuppressWarnings("unchecked")
	public Class<T> getTestedType() {
		return (Class<T>) ClassUtil.getFirstParameterOfGenericType(getClass(), BaseShareScenarioTest.class);
	}
	
	public abstract List<T> prepareItemsToExport();
	
	public abstract void prepareItemsOnImportServer();
	
	public abstract void assertImportedCorrectly();
	
	@Test
	public final void shouldImportPackageToEmptyServer() throws Exception {
		runShareTest(new ShareTestHelper() {
			
			@Override
			public List<?> prepareExportServer() throws Exception {
				preparePackageItems();
				return packageItems;
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#runOnExportServerAfterExport(org.openmrs.module.metadatasharing.ExportedPackage)
			 */
			@Override
			public void runOnExportServerAfterExport(ExportedPackage exportedPackage) throws Exception {
				assertItemsExportedCorrectly(exportedPackage);
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#runOnImportServerBeforeImport(org.openmrs.module.metadatasharing.wrapper.PackageImporter)
			 */
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
				prepareImportedItems(importer);
				
				for (ImportedItem importedItem : importedItems) {
					if (getTestedType().isAssignableFrom(importedItem.getIncoming().getClass())) {
						assertEquals("Should set import type", ImportType.CREATE, importedItem.getImportType());
					}
				}
			}
			
			@Override
			public void runOnImportServerAfterImport() throws Exception {
				assertImported();
			}
		});
	}
	
	private void assertImported() {
		for (ImportedItem importedItem : importedItems) {
			Object incoming = importedItem.getIncoming();
			Object existing = null;
			
			if (importedItem.getExisting() == null) {
				existing = Handler.getItemByUuid(incoming.getClass(), Handler.getUuid(incoming));
			} else {
				existing = Handler.getItemByUuid(incoming.getClass(), importedItem.getExistingUuid());
			}
			
			if (!importedItem.getImportType().isOmit()) {
				assertNotNull(incoming.getClass().getSimpleName() + " with uuid " + Handler.getUuid(incoming)
				        + " should have been created", existing);
			}
		}
	}
	
	/**
	 * Prepares {@link #packageItems}
	 */
	private void preparePackageItems() {
		packageItems = prepareItemsToExport();
	}
	
	/**
	 * Prepares {@link #importedItems}.
	 * 
	 * @param importer
	 */
	private void prepareImportedItems(PackageImporter importer) {
		importedItems = new ArrayList<ImportedItem>();
		for (int part = 0; part < importer.getPartsCount(); part++) {
			importedItems.addAll(importer.getImportedItems(part));
		}
	}
	
	/**
	 * @param exportedPackage
	 */
	private void assertItemsExportedCorrectly(ExportedPackage exportedPackage) {
		assertEquals("Should include all items", packageItems.size(), exportedPackage.getItems().size());
	}
	
	@Override
	public boolean insertInitialDataSet() {
	    return false;
	}
	
}
