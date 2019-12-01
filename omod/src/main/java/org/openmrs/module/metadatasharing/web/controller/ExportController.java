/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.metadatasharing.web.controller;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.beanutils.BeanComparator;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.ExportedPackageSummary;
import org.openmrs.module.metadatasharing.Item;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.SerializedPackage;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.model.validator.PackageContainerValidator;
import org.openmrs.module.metadatasharing.publish.PublishUtils;
import org.openmrs.module.metadatasharing.task.Task;
import org.openmrs.module.metadatasharing.versioner.PackageVersioner;
import org.openmrs.module.metadatasharing.web.bean.DatatablesRequest;
import org.openmrs.module.metadatasharing.web.bean.DatatablesResponse;
import org.openmrs.module.metadatasharing.web.bean.PackageItems;
import org.openmrs.module.metadatasharing.web.utils.WebUtils;
import org.openmrs.module.metadatasharing.web.view.DownloadPackageView;
import org.openmrs.module.metadatasharing.web.view.JsonObjectView;
import org.openmrs.module.metadatasharing.wrapper.PackageExporter;
import org.openmrs.serialization.SerializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for package export pages.
 */
@Controller(MetadataSharingConsts.MODULE_ID + ".ExportController")
@SessionAttributes({ ExportController.PACKAGE_ITEMS, ExportController.EXPORTER })
public class ExportController {
	
	public static final String EXPORTER = "exporter";
	
	public static final String PACKAGE_ITEMS = "packageItems";
	
	public static final String EXPORT_PATH = MetadataSharingConsts.MODULE_PATH + "/export";
	
	public static final String LIST_PATH = EXPORT_PATH + "/list";
	
	public static final String DETAILS_PATH = EXPORT_PATH + "/details";
	
	public static final String DOWNLOAD_PATH = EXPORT_PATH + "/download";
	
	public static final String DELETE_PATH = EXPORT_PATH + "/delete";
	
	public static final String TOGGLE_PUBLISHED_PATH = EXPORT_PATH + "/togglePublished";
	
	public static final String CREATE_PATH = EXPORT_PATH + "/create";
	
	public static final String UPGRADE_PATH = EXPORT_PATH + "/upgrade";
	
	public static final String EDIT_PATH = EXPORT_PATH + "/edit";
	
	public static final String ADD_ALL_ITEMS_PATH = EXPORT_PATH + "/addAllItems";
	
	public static final String REMOVE_ALL_ITEMS_PATH = EXPORT_PATH + "/removeAllItems";
	
	public static final String SELECT_ITEMS_PATH = EXPORT_PATH + "/selectItems";
	
	public static final String JSON_ITEMS_PATH = EXPORT_PATH + "/json/items";
	
	public static final String COMPLETE_PATH = EXPORT_PATH + "/complete";
	
	@Autowired
	private PackageContainerValidator validator;
	
	@Autowired
	private DownloadPackageView downloadPackageView;
	
	@Autowired
	private JsonObjectView jsonView;
	
	@RequestMapping(LIST_PATH)
	public void list(Model model) {
		List<ExportedPackageSummary> packages = getService().getAllExportedPackageSummaries();
		Map<String, ExportedPackageSummary> latestVersion = new HashMap<String, ExportedPackageSummary>();
		// key is group
		
		Map<String, List<Integer>> publishedVersions = new HashMap<String, List<Integer>>();
		// key is group
		
		Map<String, Integer> order = new HashMap<String, Integer>();
		// the packages will be ordered by creation date of the first version
		// by default
		
		int groupsCount = 0;
		for (ExportedPackageSummary pack : packages) {
			ExportedPackageSummary latest = latestVersion.get(pack.getGroupUuid());
			if (latest == null) { // the package is added first time, so let's
				                  // provide ordering information
				order.put(pack.getGroupUuid(), groupsCount++);
				latestVersion.put(pack.getGroupUuid(), pack); // update the latest version
			} else if (latest.getVersion() < pack.getVersion()) {
				latestVersion.put(pack.getGroupUuid(), pack); // update the latest version
			}
			if (pack.isPublished() == true) {
				List<Integer> versions = publishedVersions.get(pack.getGroupUuid());
				if (versions == null) {
					versions = new LinkedList<Integer>();
					publishedVersions.put(pack.getGroupUuid(), versions);
				}
				versions.add(pack.getVersion());
			}
		}
		model.addAttribute("packages", latestVersion.values());
		model.addAttribute("publishedVersions", publishedVersions);
		model.addAttribute("order", order);
		model.addAttribute("publishConfigured", MetadataSharing.getInstance().isPublishConfigured());
	}
	
	/**
	 * Gets all packages with the given group and passes to the model (they will be rendered in .jsp
	 * page)
	 */
	@RequestMapping(DETAILS_PATH)
	public String details(String group, Model model) {
		List<ExportedPackageSummary> packages = new ArrayList<ExportedPackageSummary>();
		for (ExportedPackageSummary summary : getService().getAllExportedPackageSummaries()) {
			if (summary.getGroupUuid().equalsIgnoreCase(group)) {
				packages.add(summary);
			}
		}
		Collections.sort(packages, new BeanComparator("version"));
		Collections.reverse(packages);
		if (packages.isEmpty()) {
			return WebUtils.redirect(LIST_PATH);
		}
		else {
			model.addAttribute("packages", packages);
			model.addAttribute("publishUrl", PublishUtils.createAbsolutePublishURL(packages.get(0).getGroupUuid()));
			model.addAttribute("publishConfigured", MetadataSharing.getInstance().isPublishConfigured());
			return DETAILS_PATH;
		}
	}
	
	@RequestMapping(DELETE_PATH)
	public String delete(Integer id, Model model) {
		ExportedPackage pack = MetadataSharing.getService().getExportedPackageById(id);
		if (pack != null) {
			String group = pack.getGroupUuid();
			MetadataSharing.getService().purgeExportedPackage(pack);
			if (MetadataSharing.getService().getLatestExportedPackageByGroup(group) != null) {
				model.addAttribute("group", pack.getGroupUuid());
				return WebUtils.redirect(DETAILS_PATH);
			}
		}
		return WebUtils.redirect(LIST_PATH);
	}
	
	/**
	 * Toggles the "published" field of the package with the given ID and redirects to this
	 * package's summary page
	 */
	@RequestMapping(TOGGLE_PUBLISHED_PATH)
	public String togglePublished(Integer id, Model model) {
		ExportedPackage pack = MetadataSharing.getService().getExportedPackageById(id);
		
		if (MetadataSharing.getInstance().isPublishConfigured()) {
			pack.setPublished(!pack.isPublished());
			MetadataSharing.getService().saveExportedPackage(pack);
			model.addAttribute("group", pack.getGroupUuid());
			return WebUtils.redirect(DETAILS_PATH);
		} else {
			return WebUtils.redirect(LIST_PATH);
		}
	}
	
	@RequestMapping(DOWNLOAD_PATH)
	public ModelAndView download(Integer id, Model model) {
		ExportedPackage pack = MetadataSharing.getService().getExportedPackageById(id);
		model.addAttribute("package", pack);
		return new ModelAndView(downloadPackageView);
	}
	
	@RequestMapping(value = CREATE_PATH, method = RequestMethod.GET)
	public void create(Model model) {
		if (!model.containsAttribute(EXPORTER)) {
			model.addAttribute(EXPORTER, MetadataSharing.getInstance().newPackageExporter());
		}
		model.addAttribute("publishConfigured", MetadataSharing.getInstance().isPublishConfigured());
	}
	
	@RequestMapping(value = UPGRADE_PATH)
	public String upgrade(Integer id, Boolean empty, Model model) throws SerializationException, IOException {
		ExportedPackage exportedPackage = MetadataSharing.getService().getExportedPackageById(id);
		PackageVersioner versioner = null;
		
		if (empty) {
			versioner = MetadataSharing.getInstance().getPackageVersioner(exportedPackage);
		} else {
			SerializedPackage content = exportedPackage.getSerializedPackage();
			ExportedPackage packTmp = MetadataSharing.getInstance().getMetadataSerializer()
			        .deserialize(content.getHeader(), ExportedPackage.class);
			packTmp.setId(exportedPackage.getId());
			versioner = MetadataSharing.getInstance().getPackageVersioner(packTmp);
		}
		
		exportedPackage = versioner.createNewPackageVersion(empty);
		
		PackageExporter exporter = MetadataSharing.getInstance().newPackageExporter();
		exporter.loadPackage(exportedPackage);
		
		model.addAttribute(EXPORTER, exporter);
		
		PackageItems packageItems = new PackageItems();
		packageItems.setCompleteTypes(versioner.getCompleteTypes());
		packageItems.setItems(versioner.getPackageItems());
		
		model.addAttribute(packageItems);
		if (!versioner.getMissingClasses().isEmpty() || !versioner.getMissingItems().isEmpty()) { // there were                                                                    // errors
			model.addAttribute("hasErrors", true);
			model.addAttribute("missingClasses", versioner.getMissingClasses());
			model.addAttribute("missingItems", versioner.getMissingItems());
		}
		
		return WebUtils.redirect(CREATE_PATH);
	}
	
	@RequestMapping(value = CREATE_PATH, method = RequestMethod.POST)
	public String create(@ModelAttribute(EXPORTER)
	PackageExporter exporter, Errors errors, Model model) {
		model.addAttribute("publishConfigured", MetadataSharing.getInstance().isPublishConfigured());
		
		if (exporter.getExportedPackage().isPublished()) {
			exporter.getExportedPackage().setSubscriptionUrl(
			    PublishUtils.createAbsolutePublishURL(exporter.getExportedPackage()));
		}
		
		validator.validate(exporter, errors);
		
		if (errors.hasErrors()) {
			return null;
		} else {
			if (!model.containsAttribute(PACKAGE_ITEMS)) {
				model.addAttribute(PACKAGE_ITEMS, new PackageItems());
			}
			return WebUtils.redirect(EDIT_PATH);
		}
	}
	
	@RequestMapping(value = EDIT_PATH)
	public void edit() {
	}
	
	@RequestMapping(value = ADD_ALL_ITEMS_PATH)
	public String addAllItems(@ModelAttribute(PACKAGE_ITEMS)
	PackageItems packageItems, String type, Boolean includeRetired) {
		if (includeRetired) {
			packageItems.getCompleteTypes().add(type);
		}
		
		Set<Item> items = packageItems.getItems().get(type);
		if (items == null) {
			items = new LinkedHashSet<Item>();
			packageItems.getItems().put(type, items);
		}
		
		List<Object> metadata = Handler.getItems(Handler.getRegisteredClass(type), includeRetired, null, null, null);
		items.addAll(Item.valueOf(metadata));
		
		return WebUtils.redirect(EDIT_PATH);
	}
	
	@RequestMapping(value = REMOVE_ALL_ITEMS_PATH)
	public String removeAllItems(@ModelAttribute(PACKAGE_ITEMS)
	PackageItems packageItems, String type) {
		packageItems.getCompleteTypes().remove(type);
		packageItems.getItems().remove(type);
		return WebUtils.redirect(EDIT_PATH);
	}
	
	@RequestMapping(value = SELECT_ITEMS_PATH, method = RequestMethod.GET)
	public void selectItems(@RequestParam(required = false)
	String type, Model model) {
		if (type == null) {
			Set<String> types = new TreeSet<String>(Handler.getRegisteredTypes().keySet());
			model.addAttribute("types", types);
		} else {
			model.addAttribute("type", type);
		}
		// Items will be fetched with JSON.
	}
	
	@RequestMapping(value = SELECT_ITEMS_PATH, method = RequestMethod.POST)
	public String selectItems(@ModelAttribute(PACKAGE_ITEMS)
	PackageItems packageItems, String type, HttpServletRequest request) {
		String[] addUuids = request.getParameterValues("addUuids");
		String[] removeUuids = request.getParameterValues("removeUuids");
		
		if (addUuids == null) {
			addUuids = new String[0];
		}
		if (removeUuids == null) {
			removeUuids = new String[0];
		}
		
		Set<Item> items = packageItems.getItems().get(type);
		
		for (String addUuid : addUuids) {
			Object metadata = Handler.getItemByUuid(Handler.getRegisteredClass(type), addUuid);
			if (metadata != null) {
				if (items == null) {
					items = new HashSet<Item>();
					packageItems.getItems().put(type, items);
				}
				items.add(Item.valueOf(metadata));
			}
		}
		
		if (items != null) {
			for (String removeUuid : removeUuids) {
				Object metadata = Handler.getItemByUuid(Handler.getRegisteredClass(type), removeUuid);
				if (metadata != null) {
					items.remove(Item.valueOf(metadata));
				}
			}
			
			if (items.size() == 0) {
				packageItems.getItems().remove(type);
				packageItems.getCompleteTypes().remove(type);
			} else {
				int allItems = Handler.getItemsCount(Handler.getRegisteredClass(type), true, null);
				if (items.size() == allItems) {
					packageItems.getCompleteTypes().add(type);
				} else {
					packageItems.getCompleteTypes().remove(type);
				}
			}
		}
		
		return WebUtils.redirect(EDIT_PATH);
	}
	
	@RequestMapping(JSON_ITEMS_PATH)
	public ModelAndView jsonItems(@ModelAttribute(PACKAGE_ITEMS)
	PackageItems packageItems, String type, HttpServletRequest httpRequest, Model model) throws IOException {
		DatatablesRequest request = DatatablesRequest.parseRequest(httpRequest);
		
		Class<?> clazz = Handler.getRegisteredClass(type);
		
		int totalRecords = Handler.getItemsCount(clazz, true, request.getsSearch());
		
		List<Object> items = Handler.getItems(clazz, true, request.getsSearch(), request.getiDisplayStart(),
		    request.getiDisplayLength());
		
		DatatablesResponse response = DatatablesResponse.newResponse(request);
		response.setsColumns("uuid", "id", "retired", "dateChanged", "name", "description", "selected");
		response.setiTotalRecords(totalRecords);
		response.setiTotalDisplayRecords(totalRecords);
		
		DateFormat dateFormat = new SimpleDateFormat(MetadataSharingConsts.DATE_FORMAT);
		Set<Item> selectedItems = packageItems.getItems().get(type);
		if (selectedItems == null) {
			selectedItems = Collections.emptySet();
		}
		
		for (Object item : items) {
			Map<String, String> row = new HashMap<String, String>();
			row.put("uuid", Handler.getUuid(item));
			if (Handler.getId(item) != null) {
				row.put("id", Handler.getId(item).toString());
			}
			if (Handler.getRetired(item) != null) {
				row.put("retired", Handler.getRetired(item).toString());
			}
			if (Handler.getDateChanged(item) != null) {
				row.put("dateChanged", dateFormat.format(Handler.getDateChanged(item)));
			}
			row.put("name", Handler.getName(item));
			row.put("description", Handler.getDescription(item));
			if (selectedItems.contains(Item.valueOf(item))) {
				row.put("selected", "true");
			}
			
			response.addRow(row);
		}
		
		model.addAttribute("object", response);
		
		return new ModelAndView(jsonView);
	}
	
	@RequestMapping(value = COMPLETE_PATH, method = RequestMethod.POST)
	public String complete(@ModelAttribute(EXPORTER)
	PackageExporter exporter, Errors errors, @ModelAttribute(PACKAGE_ITEMS)
	PackageItems packageItems, Model model, SessionStatus session) {
		if (packageItems.getItems().get("Concept") != null) {
			if (Context.getService(MetadataMappingService.class).isAddLocalMappingToConceptOnExport()
			        && !Context.getService(MetadataMappingService.class).isLocalConceptSourceConfigured()) {
				errors.rejectValue("package", "metadatasharing.error.conceptSource.notConfigured");
				return EDIT_PATH;
			}
		}
		
		for (Set<Item> items : packageItems.getItems().values()) {
			for (Item item : items) {
				exporter.addItem(item);
			}
		}
		
		Task task = exporter.schedulePackageExport();
		
		session.setComplete();
		
		model.addAttribute("uuid", task.getUuid());
		return WebUtils.redirect(TaskController.DETAILS_PATH);
	}

	private MetadataSharingService getService() {
		return Context.getService(MetadataSharingService.class);
	}
}
