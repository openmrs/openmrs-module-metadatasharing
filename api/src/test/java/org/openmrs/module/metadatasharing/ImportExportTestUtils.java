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

import java.util.Collection;
import java.util.UUID;

import org.openmrs.ConceptSource;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.wrapper.PackageExporter;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;

/**
 * Set of methods for importing/exporting mock packages into the system. <br>
 * Requires Spring Framework to be initialized
 */
public class ImportExportTestUtils {
	
	/**
	 * This is a contentXml for importing mock packages
	 */
	protected static String contentXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><list id=\"1\"/>";
	
	/**
	 * @see ImportExportTestUtils#exportPackage(ExportedPackage, Collection)
	 */
	public static ExportedPackage exportPackage(Integer version, String group) throws Exception {
		return exportPackage(version, group, false);
	}
	
	/**
	 * @see ImportExportTestUtils#exportPackage(ExportedPackage, Collection)
	 */
	public static ExportedPackage exportPackage(Integer version, String group, Boolean published) throws Exception {
		ExportedPackage pack = new ExportedPackage();
		pack.setName("Package");
		pack.setDescription("Test package");
		pack.setVersion(version);
		pack.setGroupUuid(group);
		pack.setPublished(published);
		return exportPackage(pack);
	}
	
	/**
	 * @see ImportExportTestUtils#exportPackage(ExportedPackage, Collection)
	 */
	public static ExportedPackage exportPackage(ExportedPackage pack) throws Exception {
		return exportPackage(pack, null);
	}
	
	/**
	 * @see ImportExportTestUtils#exportPackage(ExportedPackage, Collection)
	 */
	public static ExportedPackage exportPackage() throws Exception {
		return exportPackage(1, UUID.randomUUID().toString());
	}
	
	/**
	 * Exports to the system a mock package with given items
	 * 
	 * @param pack the package to export. If null the default package will be created
	 * @param items the items to be added to package. Null if none
	 */
	public static ExportedPackage exportPackage(ExportedPackage pack, Collection<?> items)
	    throws Exception {
		PackageExporter exporter = MetadataSharing.getInstance().newPackageExporter();
		if (pack != null) {
			exporter.loadPackage(pack);
		}
		exporter.getPackage().setName("Package");
		exporter.getPackage().setDescription("Test package");
		
		ConceptService conceptService = Context.getConceptService();
		AdministrationService administrationService = Context.getAdministrationService();
		
		if (administrationService.getGlobalProperty(MetadataSharingConsts.GP_SYSTEM_CONCEPT_SOURCE) == null) {
			String conceptSourceUuid = UUID.randomUUID().toString();
			
			ConceptSource conceptSource = new ConceptSource();
			conceptSource.setUuid(conceptSourceUuid);
			conceptSource.setName(conceptSourceUuid);
			conceptService.saveConceptSource(conceptSource);
			
			GlobalProperty gp = new GlobalProperty(MetadataSharingConsts.GP_SYSTEM_CONCEPT_SOURCE, conceptSourceUuid);
			administrationService.saveGlobalProperty(gp);
		}
		
		if (items != null) {
			for (Object obj : items) {
				exporter.addItem(obj);
			}
		}
		exporter.exportPackage();
		MetadataSharingService service = Context.getService(MetadataSharingService.class);
		return service.saveExportedPackage(exporter.getExportedPackage());
	}
	
	/**
	 * Imports a mock packages with given version, group and uuid
	 * 
	 * @return a package which has been imported
	 */
	public static ImportedPackage importPackage(Integer version, String group, String uuid) throws Exception {
		PackageExporter exporter = MetadataSharing.getInstance().newPackageExporter();
		ExportedPackage pack = exporter.getExportedPackage();
		pack.setName("Package");
		pack.setDescription("Test package");
		pack.setVersion(version);
		pack.setGroupUuid(group);
		pack.setUuid(uuid);
		pack.setSubscriptionUrl("http://google.pl");
		exporter.exportPackage();
		
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		metadataImporter.loadSerializedPackage(new SerializedPackage(pack.getSerializedPackage().getHeader(), contentXml));
		metadataImporter.importPackage();
		return metadataImporter.getImportedPackage();
	}
	
	/**
	 * A wrapper method for importing packages with random uuid
	 */
	public static ImportedPackage importPackage(Integer version, String group) throws Exception {
		return importPackage(version, group, UUID.randomUUID().toString());
	}
}
