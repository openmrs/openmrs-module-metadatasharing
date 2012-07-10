package org.openmrs.module.metadatasharing.model.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Implemented by validators capable of producing warnings. Warnings are distinguished from errors
 * by the error code convention which is as follows.
 * <p>
 * Warning: <code>@MODULE_ID@.warning.*</code>
 * <p>
 * Error: <code>@MODULE_ID@.error.*</code>
 */
public interface ErrorsAndWarningsValidator extends Validator {
	
	/**
	 * Produces warnings.
	 */
	void validateForWarnings(Object obj, Errors errors);
	
	/**
	 * Produces warnings and errors.
	 */
	void validateForErrorsAndWarnings(Object obj, Errors errors);
}
