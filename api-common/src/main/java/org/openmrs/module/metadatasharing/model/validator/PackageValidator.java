package org.openmrs.module.metadatasharing.model.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.Package;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validates the {@link Package}
 */
@Component(MetadataSharingConsts.MODULE_ID + ".PackageValidator")
public class PackageValidator implements ErrorsAndWarningsValidator {
	
	public static int MAX_NAME_LENGTH = 64;
	
	public static int MAX_DESCRIPTION_LENGTH = 256;
	
	/**
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
		return Package.class.isAssignableFrom(clazz);
	}
	
	/**
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 * @should reject empty name
	 * @should reject empty description
	 * @should reject empty date created
	 * @should reject empty non-empty group and empty version
	 * @should reject too long name
	 * @should reject too long description
	 * @should reject package version if not subsequent incremental version
	 */
	@Override
	public void validate(Object obj, Errors errors) {
		Package pack = (Package) obj;
		
		ValidationUtils.rejectIfEmpty(errors, "name", "metadatasharing.error.package.field.empty");
		if (pack.getName() != null && pack.getName().length() > MAX_NAME_LENGTH) {
			errors.rejectValue("name", "metadatasharing.error.package.name.tooLong", new Integer[] { MAX_NAME_LENGTH }, null);
		}
		
		ValidationUtils.rejectIfEmpty(errors, "description", "metadatasharing.error.package.field.empty");
		if (pack.getDescription() != null && pack.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
			errors.rejectValue("description", "metadatasharing.error.package.description.tooLong",
			    new Integer[] { MAX_DESCRIPTION_LENGTH }, null);
		}
		
		ValidationUtils.rejectIfEmpty(errors, "dateCreated", "metadatasharing.error.package.field.empty");
		
		if (pack.getGroupUuid() != null) {
			ValidationUtils.rejectIfEmpty(errors, "version", "metadatasharing.error.package.field.empty");
		}
		
		if (pack instanceof ImportedPackage && pack.isIncrementalVersion()) {
			MetadataSharingService packageService = Context.getService(MetadataSharingService.class);
			Package existingPackage = packageService.getImportedPackageByGroup(pack.getGroupUuid());
			int existingVersion = 0;
			
			if(existingPackage != null)
				existingVersion = existingPackage.getVersion();
			
			int importedVersion = pack.getVersion();
			
			if (existingVersion + 1 != importedVersion) {
				errors.rejectValue("version", "metadatasharing.error.package.invalidVersion", new Integer[] {
				        importedVersion, existingVersion }, null);
			}
		}
		
		Map<String, String> missingRequiredModules = getMissingRequiredModules(pack);
		for (Entry<String, String> missingRequiredModule : missingRequiredModules.entrySet()) {
			errors.rejectValue("requiredModules", "metadatasharing.error.package.modules.missingRequiredModule",
			    new String[] { missingRequiredModule.getKey(), missingRequiredModule.getValue() }, null);
		}
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.model.validator.ErrorsAndWarningsValidator#validateForErrorsAndWarnings(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 */
	@Override
	public void validateForErrorsAndWarnings(Object obj, Errors e) {
		validate(obj, e);
		validateForWarnings(obj, e);
	}
	
	@Override
	public void validateForWarnings(Object obj, Errors e) {
		Package pack = (Package) obj;
		// OpenmrsConstants.OPENMRS_VERSION might happen to be null when run on jetty for some reason
		if (pack.getOpenmrsVersion() != null && OpenmrsConstants.OPENMRS_VERSION != null) {
			String[] systemVersion = OpenmrsConstants.OPENMRS_VERSION.split("\\.");
			String[] packageVersion = pack.getOpenmrsVersion().split("\\.");
			
			if (systemVersion.length >= 2 && packageVersion.length >= 2) {
				if (!(systemVersion[0].equals(packageVersion[0]) && systemVersion[1].equals(packageVersion[1]))) {
					e.rejectValue("openmrsVersion", "metadatasharing.warning.package.openmrsVersion.incompatible");
				}
			} else {
				e.rejectValue("openmrsVersion", "metadatasharing.package.openmrsVersion.wrongForma");
			}
		}
		
		if (pack.getGroupUuid() != null) {
			MetadataSharingService packageService = Context.getService(MetadataSharingService.class);
			ImportedPackage importedPackage = packageService.getImportedPackageByGroup(pack.getGroupUuid());
			if (importedPackage != null && importedPackage.isImported()) {
				if (importedPackage.getVersion() >= pack.getVersion()) {
					e.rejectValue("version", "metadatasharing.warning.package.version.old");
				}
			}
		}
		
		// if the package was exported from a system running a newer version of any module that
		// we're running locally, warn about that
		if (pack.getModules() != null) {
			Map<String, Module> localModules = ModuleFactory.getStartedModulesMap();
			for (Entry<String, String> module : pack.getModules().entrySet()) {
				String moduleId = module.getKey();
				String version = module.getValue();
				String localVersion = null;
				
				if (localModules.get(moduleId) != null)
					localVersion = localModules.get(moduleId).getVersion();
				
				if (localVersion != null) {
					if (ModuleUtil.compareVersion(version, localVersion) > 0) {
						// their version is higher: warn about this
						e.rejectValue("modules", "metadatasharing.warning.lower.module.version", new Object[] { moduleId,
						        localVersion, version }, null);
					} else if (ModuleUtil.compareVersion(version, localVersion) < 0) {
						// my version is higher: this should generally be okay
					} else {
						// versions are the same: this is definitely okay
					}
				}
			}
		}
	}
	
	/**
	 * Returns missing required modules.
	 * 
	 * @param pack
	 * @return the missingModules[moduleId, version]
	 */
	public Map<String, String> getMissingRequiredModules(Package pack) {
		Map<String, String> missingRequiredModules = new HashMap<String, String>();
		
		Map<String, String> requiredModules = pack.getRequiredModules();
		Map<String, Module> startedModules = ModuleFactory.getStartedModulesMap();
		for (Entry<String, String> requiredModule : requiredModules.entrySet()) {
			if (!startedModules.containsKey(requiredModule.getKey())) {
				missingRequiredModules.put(requiredModule.getKey(), requiredModule.getValue());
			}
		}
		
		return missingRequiredModules;
	}
}
