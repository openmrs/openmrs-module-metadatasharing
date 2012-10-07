package org.openmrs.module.metadatasharing.wrapper;

import java.util.Collections;
import java.util.Locale;

import junit.framework.Assert;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
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
		Concept c = new Concept();
		c.addName(new ConceptName("c", Locale.ENGLISH));
		c.setUuid("c");
		c.setDatatype(Context.getConceptService().getConceptDatatypeByName("N/A"));
		Context.getConceptService().saveConcept(c);
		
		ExportedPackage pack = ImportExportTestUtils.exportPackage(null,
		    Collections.singletonList(Handler.getItemByUuid(Concept.class, "c")));
		
		String headerXml = pack.getSerializedPackage().getHeader();
		
		XMLAssert.assertXpathExists("/package/items", headerXml);
	}
	
	/**
	 * @see {@link MetadataExporter#exportPackage()}
	 */
	@Test
	@Verifies(value = "should create mappings to local source by default", method = "exportPackage()")
	public void exportPackage_shouldCreateMappingsToLocalSourceByDefault() throws Exception {
		Concept c = new Concept();
		c.addName(new ConceptName("c", Locale.ENGLISH));
		c.setUuid("c");
		c.setDatatype(Context.getConceptService().getConceptDatatypeByName("N/A"));
		Context.getConceptService().saveConcept(c);
		
		final String conceptUUId = "c";
		int mapCount = Context.getConceptService().getConceptByUuid(conceptUUId).getConceptMappings().size();
		
		ImportExportTestUtils.exportPackage(null,
		    Collections.singletonList(Handler.getItemByUuid(Concept.class, conceptUUId)));
		mapCount = Context.getConceptService().getConceptByUuid(conceptUUId).getConceptMappings().size();
		//the system mapping should have been created
		Assert.assertEquals(mapCount++, Context.getConceptService().getConceptByUuid(conceptUUId).getConceptMappings()
		        .size());
	}
}
