package org.openmrs.module.metadatasharing.smoke;

import java.io.InputStream;

import org.junit.Test;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

public class ConceptSmokeTest extends BaseModuleContextSensitiveTest {
	
	@Test
	@SkipBaseSetup
	public void importConceptsMVP191Small1() throws Exception {
		executeDataSet(INITIAL_XML_DATASET_PACKAGE_PATH);
		executeDataSet("conceptDatatypes.xml");
		authenticate();
		
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("packages/Concepts_MVP_1.9.1_small-1.zip");
		
		metadataImporter.loadSerializedPackageStream(inputStream);
		metadataImporter.importPackage();
	}
}
