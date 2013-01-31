package org.openmrs.module.metadatasharing.model.validator;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.Item;
import org.openmrs.module.metadatasharing.Package;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests the {@link PackageValidator}
 */
public class PackageValidatorTest extends BaseModuleContextSensitiveTest {
	
	private PackageValidator packageValidator = new PackageValidator();
	
	private Package pack;
	
	private ImportedPackage importedPack;
	
	private BindException errors;
	
	@Before
	public void before() {
		pack = PackageValidatorTest.getMockPackage();
		importedPack = PackageValidatorTest.getMockImportedPackage();
		errors = new BindException(pack, "pack");
	}
	
	/**
	 * @see PackageValidator#validate(Object,Errors)
	 * @verifies reject empty name
	 */
	@Test
	public void validate_shouldRejectEmptyName() throws Exception {
		pack.setName(null);
		packageValidator.validate(pack, errors);
		Assert.assertNotNull(errors.getFieldError("name"));
	}
	
	/**
	 * @see PackageValidator#validate(Object,Errors)
	 * @verifies reject empty description
	 */
	@Test
	public void validate_shouldRejectEmptyDescription() throws Exception {
		pack.setDescription(null);
		packageValidator.validate(pack, errors);
		Assert.assertNotNull(errors.getFieldError("description"));
	}
	
	/**
	 * @see PackageValidator#validate(Object,Errors)
	 * @verifies reject empty date created
	 */
	@Test
	public void validate_shouldRejectEmptyDateCreated() throws Exception {
		pack.setDateCreated(null);
		packageValidator.validate(pack, errors);
		Assert.assertNotNull(errors.getFieldError("dateCreated"));
	}
	
	/**
	 * @see PackageValidator#validate(Object,Errors)
	 * @verifies reject empty non-empty group and empty version
	 */
	@Test
	public void validate_shouldRejectEmptyNonemptyGroupAndEmptyVersion() throws Exception {
		pack.setVersion(null);
		packageValidator.validate(pack, errors);
		Assert.assertNotNull(errors.getFieldError("version"));
	}
	
	/**
	 * @see PackageValidator#validate(Object,Errors)
	 * @verifies reject too long name
	 */
	@Test
	public void validate_shouldRejectTooLongName() throws Exception {
		char[] buf = new char[PackageValidator.MAX_NAME_LENGTH + 1];
		Arrays.fill(buf, 'x');
		pack.setName(new String(buf));
		packageValidator.validate(pack, errors);
		Assert.assertNotNull(errors.getFieldError("name"));
		Assert.assertEquals("metadatasharing.error.package.name.tooLong", errors.getFieldError("name").getCode());
	}
	
	/**
	 * @see PackageValidator#validate(Object,Errors)
	 * @verifies reject too long description
	 */
	@Test
	public void validate_shouldRejectTooLongDescription() throws Exception {
		char[] buf = new char[PackageValidator.MAX_DESCRIPTION_LENGTH + 1];
		Arrays.fill(buf, 'x');
		pack.setDescription(new String(buf));
		packageValidator.validate(pack, errors);
		Assert.assertNotNull(errors.getFieldError("description"));
		Assert.assertEquals("metadatasharing.error.package.description.tooLong", errors.getFieldError("description")
		        .getCode());
		
	}
	
    /**
     * @see PackageValidator#validate(Object,Errors)
     * @verifies reject unacceptable version ID
     */
    @Test
    public void validate_shouldRejectInvalidVersionNumber() throws Exception {
     
     MetadataSharingService packageService = Context.getService(MetadataSharingService.class);
     importedPack.setIncrementalVersion(true);
     packageService.saveImportedPackage(importedPack);
     Package existingPackage = packageService.getImportedPackageByGroup(pack.getGroupUuid());
     
     Assert.assertEquals(importedPack.getVersion(),new Integer(0));
     Assert.assertEquals(pack.getVersion(),new Integer(1));
     
     packageValidator.validate(existingPackage, errors);
  
     Assert.assertNotNull(errors.getFieldError("version"));
     Assert.assertEquals("metadatasharing.error.package.invalidVersion", errors.getFieldError("version")
          .getCode());
    }
	
	public static ExportedPackage getMockPackage() {
		ExportedPackage pack = new ExportedPackage();
		pack.setName("SomeName");
		pack.setDateCreated(new Date());
		pack.setGroupUuid("SomeGroup");
		pack.setVersion(1);
		pack.setDescription("SomeDesc");
		pack.setOpenmrsVersion("1.6");
		return pack;
	}
	
	 public static ImportedPackage getMockImportedPackage() {
		  ImportedPackage pack = new ImportedPackage();
		  pack.setName("SomeName");
		  pack.setDateCreated(new Date());
		  pack.setGroupUuid("SomeGroup");
		  pack.setVersion(0);
		  pack.setDescription("SomeDesc");
		  pack.setOpenmrsVersion("1.6");
		  return pack;
		 }

	/**
     * @see PackageValidator#getMissingRequiredModules(Package)
     * @verifies get modules ids and class names based on conventions
     */
    @Test
    public void getMissingModules_shouldGetModulesIdsAndClassNamesBasedOnConventions() throws Exception {
	    Package pack = getMockPackageWithModuleItems();
	    Map<String, String> moduleToItems = packageValidator.getMissingRequiredModules(pack);
	    Assert.assertEquals(2, moduleToItems.keySet().size());
	    Assert.assertTrue(moduleToItems.containsKey("foo"));
	    Assert.assertTrue(moduleToItems.containsKey("bar"));
    }

    private Package getMockPackageWithModuleItems() {
    	ExportedPackage pack = new ExportedPackage();
    	pack.getItems().add(buildItemSummary("org.openmrs.Location", "uuid-1", "A Location"));
    	pack.getItems().add(buildItemSummary("org.openmrs.module.foo.model.FooParent", "uuid-2", "Big Foo"));
    	pack.getRelatedItems().add(buildItemSummary("org.openmrs.module.foo.model.FooChild", "uuid-3", "Little Foo"));
    	pack.getItems().add(buildItemSummary("org.openmrs.module.bar.Bar", "uuid-4", "Cheers"));

	    return pack;
    }

    private Item buildItemSummary(String classname, String uuid, String name) {
    	Item ret = new Item();
	    ret.setClassname(classname);
	    ret.setUuid(uuid);
	    ret.setName(name);
	    return ret;
    }
	
}
