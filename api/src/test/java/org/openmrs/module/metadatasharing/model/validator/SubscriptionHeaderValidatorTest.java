package org.openmrs.module.metadatasharing.model.validator;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.metadatasharing.subscription.SubscriptionHeader;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

public class SubscriptionHeaderValidatorTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private SubscriptionHeaderValidator validator;
	
	private SubscriptionHeader header;
	
	private Errors errors;
	
	@Before
	public void before() throws URISyntaxException {
		header = new SubscriptionHeader();
		header.setPackageHeader(PackageValidatorTest.getMockPackage());
		header.setContentUri(new URI("http://google.pl"));
		errors = new BindException(header, "header");
	}
	
	/**
	 * @see SubscriptionHeaderValidator#validate(Object,Errors)
	 * @verifies reject invalid package header
	 */
	@Test
	public void validate_shouldRejectInvalidPackageHeader() throws Exception {
		header.getPackageHeader().setDescription(null);
		validator.validate(header, errors);
		Assert.assertEquals(1, errors.getFieldErrorCount());
		FieldError fe = (FieldError) errors.getFieldErrors().get(0);
		Assert.assertEquals("packageHeader.description", fe.getField());
	}
	
	/**
	 * @see SubscriptionHeaderValidator#validate(Object,Errors)
	 * @verifies reject empty content URI
	 */
	@Test
	public void validate_shouldRejectEmptyContentURI() throws Exception {
		header.setContentUri(null);
		validator.validate(header, errors);
		Assert.assertNotNull(errors.getFieldError("contentUri"));
	}
}
