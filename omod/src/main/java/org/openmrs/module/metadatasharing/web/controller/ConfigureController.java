package org.openmrs.module.metadatasharing.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.ConceptSource;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.api.ConceptServiceCompatibility;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.web.bean.ConfigureForm;
import org.openmrs.module.metadatasharing.web.bean.validator.ConfigureFormValidator;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller(MetadataSharingConsts.MODULE_ID + ".ConfigureController")
public class ConfigureController {
	
	/**
	 * Model and request attribute name for boolean stating if we should automatically notify the
	 * user about packages updates
	 */
	public static final String NOTIFY_AUTOMATICALLY = "notifyAutomatically";
	
	/**
	 * Model and request attribute name for notification interval days
	 */
	public static final String INTERVAL_DAYS = "intervalDays";
	
	/**
	 * The model attribute name for url prefix
	 */
	public static final String URL_PREFIX = "urlPrefix";
	
	public static final String CONFIGURE_PATH = MetadataSharingConsts.MODULE_PATH + "/configure";
	
	@Autowired
	private ConfigureFormValidator validator;
	
	@Autowired
	private ConceptServiceCompatibility conceptService;
	
	@ModelAttribute("conceptSources")
	public List<ConceptSource> getConceptSources() {
		return conceptService.getAllConceptSources();
	}
	
	@ModelAttribute("sourceIdPreferredMap")
	public Map<Integer, Boolean> getPreferredConceptSources() {
		Set<String> preferredSourceNames = Context.getService(MetadataSharingService.class).getPreferredSourceNames();
		Map<Integer, Boolean> sourceIdPreferredMap = new HashMap<Integer, Boolean>();
		for (ConceptSource conceptSource : getConceptSources()) {
			sourceIdPreferredMap.put(conceptSource.getConceptSourceId(),
			    preferredSourceNames.contains(conceptSource.getName()));
		}
		
		return sourceIdPreferredMap;
	}
	
	@RequestMapping(value = CONFIGURE_PATH, method = RequestMethod.GET)
	public void configureGet(Model model) {
		AdministrationService adminService = Context.getAdministrationService();
		ConfigureForm configureForm = new ConfigureForm();
		
		configureForm.setUrlPrefix(adminService.getGlobalProperty(MetadataSharingConsts.GP_URL_PREFIX));
		
		String notify = Context.getAdministrationService().getGlobalProperty(MetadataSharingConsts.GP_NOTIFY, "false");
		configureForm.setNotifyAutomatically(Boolean.valueOf(notify));
		
		String days = Context.getAdministrationService().getGlobalProperty(MetadataSharingConsts.GP_INTERVAL_DAYS, "1");
		configureForm.setIntervalDays(Integer.valueOf(days));
		
		configureForm.setWebservicesKey(Context.getAdministrationService().getGlobalProperty(
		    MetadataSharingConsts.GP_WEBSERVICES_KEY, ""));
		
		configureForm.setEnableOnTheFlyPackages(Boolean.valueOf(Context.getAdministrationService()
		        .getGlobalProperty(MetadataSharingConsts.GP_ENABLE_ON_THE_FLY_PACKAGES, "false").trim()));
		
		model.addAttribute(configureForm);
	}
	
	@RequestMapping(value = CONFIGURE_PATH, method = RequestMethod.POST)
	public String configurePost(ConfigureForm configureForm, Errors errors, Model model, HttpSession session,
	                            HttpServletRequest request) {
		validator.validate(configureForm, errors);
		if (!errors.hasErrors()) {
			MetadataSharingService subscriptionService = Context.getService(MetadataSharingService.class);
			
			saveGlobalProperty(MetadataSharingConsts.GP_URL_PREFIX, configureForm.getUrlPrefix());
			saveGlobalProperty(MetadataSharingConsts.GP_NOTIFY, configureForm.getNotifyAutomatically().toString());
			
			if (configureForm.getNotifyAutomatically().equals(false)) {
				subscriptionService.getSubscriptionUpdater().unscheduleCheckForUpdatesTask();
			} else if (configureForm.getIntervalDays() != null) {
				saveGlobalProperty(MetadataSharingConsts.GP_INTERVAL_DAYS, configureForm.getIntervalDays().toString());
				subscriptionService.getSubscriptionUpdater().scheduleCheckForUpdatesTask(
				    (long) (configureForm.getIntervalDays() * 3600 * 24));
			}
			
			String[] sourceIds = ServletRequestUtils.getStringParameters(request, "preferredSourceIds");
			
			String sourceIdsValue = "";
			boolean isFirst = true;
			for (String id : sourceIds) {
				id = id.trim();
				if (isFirst) {
					sourceIdsValue = id;
					isFirst = false;
				} else {
					sourceIdsValue += "," + id;
				}
			}
			
			saveGlobalProperty(MetadataSharingConsts.GP_PREFERRED_CONCEPT_SOURCE_IDs, sourceIdsValue);
			
			saveGlobalProperty(MetadataSharingConsts.GP_ENABLE_ON_THE_FLY_PACKAGES, configureForm
			        .getEnableOnTheFlyPackages().toString());
			
			saveGlobalProperty(MetadataSharingConsts.GP_WEBSERVICES_KEY, configureForm.getWebservicesKey());
			
			session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, MetadataSharingConsts.MODULE_ID + ".configure.saved");
			
			if (configureForm.getIntervalDays() == null) {
				String days = Context.getAdministrationService().getGlobalProperty(MetadataSharingConsts.GP_INTERVAL_DAYS,
				    "1");
				configureForm.setIntervalDays(Integer.valueOf(days));
			}
			
			return "redirect:" + CONFIGURE_PATH + ".form";
		}
		
		return CONFIGURE_PATH;
	}
	
	/**
	 * Saves a global property with the given name to the database.
	 */
	private void saveGlobalProperty(String name, String value) {
		AdministrationService administrationService = Context.getAdministrationService();
		GlobalProperty property = administrationService.getGlobalPropertyObject(name);
		if (property == null) {
			property = new GlobalProperty(name, value);
		} else {
			property.setPropertyValue(value);
		}
		administrationService.saveGlobalProperty(property);
	}
	
}
