package org.openmrs.module.metadatasharing.web.bean.validator;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.web.bean.ConfigureForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * A validator for {@link ConfigureForm} objects
 */
@Component(MetadataSharingConsts.MODULE_ID + ".ConfigureFormValidator")
public class ConfigureFormValidator implements Validator {
	
	/**
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
		return ConfigureForm.class.isAssignableFrom(clazz);
	}
	
	/**
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		ConfigureForm configureForm = (ConfigureForm) target;
		
		if (configureForm.getNotifyAutomatically().equals(true)) {
			Integer days = configureForm.getIntervalDays();
			if (days == null || days < 1 || days > 356) {
				errors.rejectValue("intervalDays", "metadatasharing.error.alert.invalidInterval");
			}
		}
		if (configureForm.getUrlPrefix() != null) {
			if (!(StringUtils.startsWith(configureForm.getUrlPrefix(), "http://") || StringUtils.startsWith(
			    configureForm.getUrlPrefix(), "https://"))) {
				errors.rejectValue("urlPrefix", "metadatasharing.error.urlPrefix.unknownProtocol");
			}
			
			if (StringUtils.endsWith(configureForm.getUrlPrefix(), "/")) { //the URL shouldn't end with slash
				errors.rejectValue("urlPrefix", "metadatasharing.error.urlPrefix.endsWithSlash");
			}
		}
	}	
}
