package org.openmrs.module.metadatasharing.wrapper;

import java.util.Collections;

import junit.framework.Assert;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.ImportExportTestUtils;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class MetadataExporterTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see MetadataExporter#exportPackage()
	 * @verifies add explicit items and included dependencies to the header.xml
	 */
	@Test
	public void exportPackage_shouldAddExplicitItemsAndIncludedDependenciesToTheHeaderxml() throws Exception {
		executeDataSet("MetadataExporterTest.xml");
		ExportedPackage pack = ImportExportTestUtils.exportPackage(null,
		    Collections.singletonList(Handler.getItemByUuid(Concept.class, "ee3247fb-7ed0-4395-934e-78087ce3086e")));
		
		String headerXml = pack.getSerializedPackage().getHeader();
		
		XMLAssert.assertXpathExists("/package/items", headerXml);
	}
	
	/**
	 * @see {@link MetadataExporter#exportPackage()}
	 */
	@Test
	@Verifies(value = "should not create mappings to local source if the related global property is set to false", method = "exportPackage()")
	public void exportPackage_shouldNotCreateMappingsToLocalSourceIfTheRelatedGlobalPropertyIsSetToFalse() throws Exception {
		executeDataSet("MetadataExporterTest.xml");
		final String conceptUUId = "ee3247fb-7ed0-4395-934e-78087ce3086e";
		int initialMapCount = Context.getConceptService().getConceptByUuid(conceptUUId).getConceptMappings().size();
		
		//disable exporting concept mappings
		AdministrationService administrationService = Context.getAdministrationService();
		GlobalProperty property = administrationService.getGlobalPropertyObject(MetadataSharingConsts.GP_ADD_LOCAL_MAPPINGS);
		String value = "false";
		if (property == null) {
			property = new GlobalProperty(MetadataSharingConsts.GP_ADD_LOCAL_MAPPINGS, value);
		} else {
			property.setPropertyValue(value);
		}
		administrationService.saveGlobalProperty(property);
		ImportExportTestUtils.exportPackage(null,
		    Collections.singletonList(Handler.getItemByUuid(Concept.class, conceptUUId)));
		//The system mapping shouldn't have been created
		Assert.assertEquals(initialMapCount, Context.getConceptService().getConceptByUuid(conceptUUId).getConceptMappings()
		        .size());
	}
	
	/**
	 * @see {@link MetadataExporter#exportPackage()}
	 */
	@Test
	@Verifies(value = "should create mappings to local source by default", method = "exportPackage()")
	public void exportPackage_shouldCreateMappingsToLocalSourceByDefault() throws Exception {
		executeDataSet("MetadataExporterTest.xml");
		final String conceptUUId = "ee3247fb-7ed0-4395-934e-78087ce3086e";
		int mapCount = Context.getConceptService().getConceptByUuid(conceptUUId).getConceptMappings().size();
		
		ImportExportTestUtils.exportPackage(null,
		    Collections.singletonList(Handler.getItemByUuid(Concept.class, conceptUUId)));
		mapCount = Context.getConceptService().getConceptByUuid(conceptUUId).getConceptMappings().size();
		//the system mapping should have been created
		Assert.assertEquals(mapCount++, Context.getConceptService().getConceptByUuid(conceptUUId).getConceptMappings()
		        .size());
	}
}
