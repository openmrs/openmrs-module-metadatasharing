package org.openmrs.module.metadatasharing.model.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests {@link UrlValidator}
 */
public class UrlValidatorTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private UrlValidator validator;
	
	protected Errors errors;
	
	@Before
	public void before() {
		errors = new BindException(new Object(), "url");
	}
	
	/**
	 * @see UrlValidator#validate(Object,Errors)
	 * @verifies reject non available protocols
	 */
	@Test
	public void validate_shouldRejectNonAvailableProtocols() throws Exception {
		validator.validate("mailto://google.pl", errors); //I assume that we will never implement downloader for mailto protocol...
		Assert.assertTrue(ValidatorTestUtils.errorsContain(errors, "metadatasharing.error.subscription.url.unsupported"));
	}
	
	/**
	 * @see UrlValidator#validate(Object,Errors)
	 * @verifies not reject available protocols
	 */
	@Test
	public void validate_shouldNotRejectAvailableProtocols() throws Exception {
		validator.validate("http://google.pl", errors); //I assume that we will always implement downloader for http protocol...
		Assert.assertFalse(ValidatorTestUtils.errorsContain(errors, "metadatasharing.error.subscription.url.unsupported"));
	}
	
	/**
	 * @see UrlValidator#validate(Object,Errors)
	 * @verifies reject value if the length is greater than maxLength
	 */
	@Test
	public void validate_shouldRejectValueIfTheLengthIsGreaterThanMaxLength() throws Exception {
		String str = new String();
		for (int i = 0; i < validator.getMaxLength() + 1; i++)
			str += "a";
		validator.validate(str, errors);
		Assert.assertTrue(ValidatorTestUtils.errorsContain(errors, "metadatasharing.error.subscription.url.tooLong"));
	}
}
