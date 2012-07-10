package org.openmrs.module.metadatasharing.model.validator;

import org.openmrs.module.metadatasharing.wrapper.PackageContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class PackageContainerValidator implements ErrorsAndWarningsValidator {
	
	@Autowired
	private PackageValidator packageValidator;
	
	@Override
	public boolean supports(@SuppressWarnings("rawtypes")
	Class type) {
		return PackageContainer.class.isAssignableFrom(type);
	}
	
	@Override
	public void validate(Object obj, Errors errors) {
		PackageContainer packageContainer = (PackageContainer) obj;
		
		errors.pushNestedPath("package");
		packageValidator.validate(packageContainer.getPackage(), errors);
		errors.popNestedPath();
	}
	
	@Override
	public void validateForErrorsAndWarnings(Object obj, Errors errors) {
		validate(obj, errors);
		validateForWarnings(obj, errors);
	}
	
	@Override
	public void validateForWarnings(Object obj, Errors errors) {
		PackageContainer packageContainer = (PackageContainer) obj;
		
		errors.pushNestedPath("package");
		packageValidator.validateForWarnings(packageContainer.getPackage(), errors);
		errors.popNestedPath();
	}
	
}
