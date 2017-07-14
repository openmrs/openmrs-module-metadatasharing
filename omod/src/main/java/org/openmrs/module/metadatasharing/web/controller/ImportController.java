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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.ImportedItemsStats;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.SubscriptionStatus;
import org.openmrs.module.metadatasharing.downloader.Downloader;
import org.openmrs.module.metadatasharing.downloader.DownloaderFactory;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.model.validator.PackageContainerValidator;
import org.openmrs.module.metadatasharing.model.validator.ValidateCustomUtil;
import org.openmrs.module.metadatasharing.subscription.SubscriptionHeader;
import org.openmrs.module.metadatasharing.task.Task;
import org.openmrs.module.metadatasharing.updater.SubscriptionUpdater;
import org.openmrs.module.metadatasharing.web.bean.AssessItemForm;
import org.openmrs.module.metadatasharing.web.bean.DatatablesRequest;
import org.openmrs.module.metadatasharing.web.bean.DatatablesResponse;
import org.openmrs.module.metadatasharing.web.bean.UploadForm;
import org.openmrs.module.metadatasharing.web.bean.validator.UploadFormValidator;
import org.openmrs.module.metadatasharing.web.utils.WebUtils;
import org.openmrs.module.metadatasharing.web.view.JsonObjectView;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.serialization.SerializationException;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Controller for package import pages.
 */
@Controller(MetadataSharingConsts.MODULE_ID + ".ImportController")
@SessionAttributes({ ImportController.IMPORTER, ImportController.IMPORTED_PACKAGE, ImportController.DOWNLOADER,
        ImportController.ITEMS, ImportController.ITEMS_STATS, ImportController.PARTS })
public class ImportController {
	
	public static final String IMPORTER = "importer";
	
	public static final String ITEMS = "items";
	
	public static final String ITEMS_STATS = "itemsStats";
	
	public static final String ITEM = "item";
	
	public static final String PARTS = "parts";
	
	public static final String IMPORTED_PACKAGE = "importedPackage";
	
	public static final String DOWNLOADER = "downloader";
	
	public static final String IMPORT_PATH = MetadataSharingConsts.MODULE_PATH + "/import";
	
	public static final String LIST_PATH = IMPORT_PATH + "/list";
	
	public static final String TOGGLE_SUBSCRIBE_PATH = IMPORT_PATH + "/toggleSubscribe";
	
	public static final String DELETE_SUBSCRIBE_PATH = IMPORT_PATH + "/deleteSubscribe";
	
	public static final String JSON_CHECK_UPDATES_PATH = IMPORT_PATH + "/json/checkUpdates";
	
	public static final String DOWNLOAD_PATH = IMPORT_PATH + "/download";
	
	public static final String JSON_INITIALIZE_DOWNLOAD_PATH = IMPORT_PATH + "/json/initializeDownload";
	
	public static final String JSON_PERFORM_DOWNLOAD_PATH = IMPORT_PATH + "/json/performDownload";
	
	public static final String INVALID_SUBSCRIPTION_PATH = IMPORT_PATH + "/invalidSubscription";
	
	public static final String UPLOAD_PATH = IMPORT_PATH + "/upload";
	
	public static final String VALIDATE_PATH = IMPORT_PATH + "/validate";
	
	public static final String MODE_PATH = IMPORT_PATH + "/mode";
	
	public static final String CONFIG_PATH = IMPORT_PATH + "/config";
	
	public static final String LOAD_PATH = IMPORT_PATH + "/load";
	
	public static final String VIEW_PATH = IMPORT_PATH + "/view";
	
	public static final String JSON_IMPORT_ITEMS_PATH = IMPORT_PATH + "/json/importItems";
	
	public static final String JSON_ITEMS_PATH = IMPORT_PATH + "/json/items";
	
	public static final String ITEM_PATH = IMPORT_PATH + "/assessItem";
	
	public static final String ASSESS_NEXT_ITEM_PATH = IMPORT_PATH + "/assessNextItem";
	
	public static final String COMPLETE_PATH = IMPORT_PATH + "/complete";
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private PackageContainerValidator packageContainerValidator;
	
	@Autowired
	private UploadFormValidator uploadFormValidator;
	
	@Autowired
	private JsonObjectView jsonObjectView;
	
	@Autowired
	private DownloaderFactory downloaderFactory;
	
	/**
	 * The updater we will be using for checking for updates while adding brand-new subscription
	 */
	@Autowired
	private SubscriptionUpdater updater;
	
	@RequestMapping(LIST_PATH)
	public void list(Model model) {
		model.addAttribute("subscriptions", MetadataSharing.getService().getAllImportedPackages());
	}
	
	@RequestMapping(value = TOGGLE_SUBSCRIBE_PATH, method = RequestMethod.GET)
	public String toggleSubscribe(Integer id) {
		ImportedPackage importedPackage = MetadataSharing.getService().getImportedPackageById(id);
		if (importedPackage != null) {
			if (importedPackage.isSubscribed()) {
				importedPackage.setSubscriptionStatus(SubscriptionStatus.DISABLED);
			} else {
				importedPackage.setSubscriptionStatus(SubscriptionStatus.NEVER_CHECKED);
			}
			MetadataSharing.getService().saveImportedPackage(importedPackage);
		}
		return WebUtils.redirect(LIST_PATH);
	}
	
	@RequestMapping(value = DELETE_SUBSCRIBE_PATH, method = RequestMethod.GET)
	public String purgeSubscribe(Integer id) {
		ImportedPackage importedPackage = MetadataSharing.getService().getImportedPackageById(id);
		if (importedPackage != null) {
			MetadataSharing.getService().deleteImportedPackage(importedPackage);
		}
		return WebUtils.redirect(LIST_PATH);
	}
	
	
	@RequestMapping(value = JSON_CHECK_UPDATES_PATH, method = RequestMethod.GET)
	public ModelAndView jsonCheckUpdates(Integer id, ModelMap model) {
		ImportedPackage importedPackage = MetadataSharing.getService().getImportedPackageById(id);
		if (importedPackage != null && importedPackage.isSubscribed()) {
			updater.checkForUpdates(importedPackage);
			MetadataSharing.getService().saveImportedPackage(importedPackage);
			model.addAttribute("object", importedPackage);
			return new ModelAndView(jsonObjectView);
		}
		return null;
	}
	
	@RequestMapping(value = DOWNLOAD_PATH, method = RequestMethod.GET, params = "id")
	public void download(Integer id, Model model) {
		ImportedPackage importedPackage = MetadataSharing.getService().getImportedPackageById(id);
		model.addAttribute(IMPORTED_PACKAGE, importedPackage);
	}
	
	/**
	 * Download if {@link ImportedPackage} set.
	 */
	@RequestMapping(value = DOWNLOAD_PATH, method = RequestMethod.GET)
	public void download() {
	}
	
	/**
	 * A page where we ask user if he wants to add a subscription the initial checkout of which
	 * produced errors
	 */
	@RequestMapping(value = INVALID_SUBSCRIPTION_PATH, method = RequestMethod.GET)
	public String invalidSubscription(@ModelAttribute(IMPORTED_PACKAGE)
	ImportedPackage importedPackage, @RequestParam(value = "add", required = false)
	Boolean add, HttpSession session, Model model) {
		if (add != null && add.equals(true)) {
			MetadataSharing.getService().saveImportedPackage(importedPackage);
			session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "metadatasharing.subscription.added");
			return WebUtils.redirect(LIST_PATH);
		}
		return null;
	}
	
	/**
	 * An AJAX request where we initialize the package download process.
	 */
	@RequestMapping(value = JSON_INITIALIZE_DOWNLOAD_PATH, method = RequestMethod.GET)
	public ModelAndView jsonInitializeDownload(@ModelAttribute(IMPORTED_PACKAGE)
	ImportedPackage importedPackage, Model model) throws MalformedURLException {
		SubscriptionHeader header = updater.checkForUpdates(importedPackage);
		if (!importedPackage.hasSubscriptionErrors()) {
			URL subscriptionUrl = new URL(importedPackage.getSubscriptionUrl());
			URL packageContentUrl = new URL(subscriptionUrl, header.getContentUri().toString());
			Downloader downloader = downloaderFactory.getDownloader(packageContentUrl);
			model.addAttribute(DOWNLOADER, downloader);
		}
		model.addAttribute("object", importedPackage.getSubscriptionStatus().getValue());
		return new ModelAndView(jsonObjectView);
	}
	
	/**
	 * An AJAX request where we perform the actual package download process.
	 */
	@RequestMapping(value = JSON_PERFORM_DOWNLOAD_PATH, method = RequestMethod.GET)
	public ModelAndView jsonPerformDownload(@ModelAttribute(DOWNLOADER)
	Downloader downloader, HttpSession session, Model model) throws IOException {
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		ByteArrayInputStream in = null;
		try {
			in = new ByteArrayInputStream(downloader.downloadAsByteArray());
		}
		catch (Exception e) {
			model.addAttribute("object", SubscriptionStatus.FATAL_ERROR);
			return new ModelAndView(jsonObjectView);
		}
		metadataImporter.loadSerializedPackageStream(in);
		session.setAttribute(ImportController.IMPORTER, metadataImporter);
		
		model.asMap().remove(DOWNLOADER);
		model.addAttribute("object", SubscriptionStatus.UPDATE_AVAILABLE);
		return new ModelAndView(jsonObjectView);
	}
	
	@RequestMapping(value = UPLOAD_PATH, method = RequestMethod.GET)
	public void upload(SessionStatus status) {
		status.setComplete();
	}
	
	@RequestMapping(value = UPLOAD_PATH, method = RequestMethod.POST)
	public String uploadPOST(UploadForm uploadForm, Errors errors, Model model) throws IOException {
		uploadFormValidator.validate(uploadForm, errors);
		if (errors.hasErrors()) {
			return UPLOAD_PATH;
		}
		
		if (StringUtils.isBlank(uploadForm.getUrl())) {
			PackageImporter importer = MetadataSharing.getInstance().newPackageImporter();
			InputStream in = null;
			try {
				in = uploadForm.getFile().getInputStream();
				importer.loadSerializedPackageStream(in);
				in.close();
			}
			finally {
				IOUtils.closeQuietly(in);
			}
			
			model.addAttribute(IMPORTER, importer);
			return WebUtils.redirect(VALIDATE_PATH);
		} else {
			ImportedPackage importedPackage = new ImportedPackage();
			importedPackage.setGroupUuid(null);
			importedPackage.setSubscriptionUrl(uploadForm.getUrl());
			updater.checkForUpdates(importedPackage);
			
			if (importedPackage.hasSubscriptionErrors()) {
				if (importedPackage.getSubscriptionStatus().equals(SubscriptionStatus.DUPLICATE_SUBSCRIPTION)) {
					errors.rejectValue("url", "metadatasharing.error.subscription.duplicate");
					return UPLOAD_PATH;
				} else {
					model.addAttribute(IMPORTED_PACKAGE, importedPackage);
					return WebUtils.redirect(INVALID_SUBSCRIPTION_PATH);
				}
			}
			
			model.addAttribute(IMPORTED_PACKAGE, importedPackage);
			return WebUtils.redirect(DOWNLOAD_PATH);
		}
	}
	
	@RequestMapping(value = VALIDATE_PATH, method = RequestMethod.GET)
	public void validate(@ModelAttribute(IMPORTER)
	PackageImporter importer, Errors errors) throws SerializationException {
		packageContainerValidator.validateForErrorsAndWarnings(importer, errors);
	}
	
	@RequestMapping(value = VALIDATE_PATH, method = RequestMethod.POST)
	public String validatePOST(@ModelAttribute(IMPORTER)
	PackageImporter importer, Errors errors) {
		packageContainerValidator.validate(importer, errors);
		if (errors.hasErrors()) {
			packageContainerValidator.validateForWarnings(importer, errors);
			return VALIDATE_PATH;
		} else {
			return WebUtils.redirect(MODE_PATH);
		}
	}
	
	@RequestMapping(value = MODE_PATH, method = RequestMethod.GET)
	public void mode() {
	}
	
	@RequestMapping(value = MODE_PATH, method = RequestMethod.POST)
	public String modePOST(@ModelAttribute(IMPORTER)
	PackageImporter importer, String importMode) {
		ImportMode mode = ImportMode.valueOf(importMode);
		if (mode.isPrevious()) {
			ImportedPackage previous = MetadataSharing.getService().getImportedPackageByGroup(
			    importer.getImportedPackage().getGroupUuid());
			if (previous != null) {
				previous.getImportConfig().setPrevious(true);
				importer.setImportConfig(previous.getImportConfig());
			}
		} else {
			importer.setImportConfig(ImportConfig.valueOf(mode));
		}
		return WebUtils.redirect(CONFIG_PATH);
	}
	
	@RequestMapping(value = CONFIG_PATH, method = RequestMethod.GET)
	public void config(@ModelAttribute(IMPORTER)
	PackageImporter importer) {
	}
	
	@RequestMapping(value = CONFIG_PATH, method = RequestMethod.POST)
	public String configPOST(@ModelAttribute(IMPORTER)
	PackageImporter importer, Model model) throws IOException {
		ImportedPackage importedPackage = MetadataSharing.getService().getImportedPackageByGroup(
		    importer.getImportedPackage().getGroupUuid());
		if (importedPackage != null) {
			importedPackage.loadPackage(importer.getImportedPackage());
		} else {
			importedPackage = importer.getImportedPackage();
		}
		MetadataSharing.getService().saveImportedPackage(importedPackage);
		
		model.addAttribute("part", 0);
		Map<Integer, Boolean> parts = new TreeMap<Integer, Boolean>();
		for (int i = 0; i < importer.getPartsCount(); i++) {
			parts.put(i, false);
		}
		model.addAttribute(PARTS, parts);
		return WebUtils.redirect(LOAD_PATH);
	}
	
	@RequestMapping(value = LOAD_PATH, params = "part")
	public String load(@ModelAttribute(IMPORTER)
	PackageImporter importer, @ModelAttribute(PARTS)
	Map<Integer, Boolean> parts, Integer part, Model model) {
		importer.clearState();
		
		List<ImportedItem> importItems = new ArrayList<ImportedItem>();
		for (ImportedItem importItem : importer.getImportedItems(part)) {
			if (!Handler.isHidden(importItem.getIncoming())) {
				importItems.add(importItem);
			}
		}
		
		ImportedItemsStats itemsStats = importer.getImportedItemsStats(part);
		parts.put(itemsStats.getPart(), (itemsStats.getNotAssessedTotalCount() == 0));
		for (Entry<Integer, Boolean> p : parts.entrySet()) {
			if (!p.getValue()) {
				model.addAttribute("loadNext", p.getKey());
			}
		}
		
		model.addAttribute(ITEMS, importItems);
		model.addAttribute(ITEMS_STATS, itemsStats);

		if (importer.getImportConfig().isSkipAssessing()) {
			return WebUtils.redirect(COMPLETE_PATH);
		}
		else {
			return VIEW_PATH;
		}
	}
	
	@RequestMapping(LOAD_PATH)
	public String load(@ModelAttribute(IMPORTER)
	PackageImporter importer, @ModelAttribute(ITEMS_STATS)
	ImportedItemsStats stats, @ModelAttribute(PARTS)
	Map<Integer, Boolean> parts, Model model) {
		ImportedItemsStats itemsStats = importer.getImportedItemsStats(stats.getPart());
		parts.put(itemsStats.getPart(), (itemsStats.getNotAssessedTotalCount() == 0));
		for (Entry<Integer, Boolean> p : parts.entrySet()) {
			if (!p.getValue()) {
				model.addAttribute("loadNext", p.getKey());
			}
		}
		
		model.addAttribute(ITEMS_STATS, itemsStats);
		return VIEW_PATH;
	}
	
	@RequestMapping(VIEW_PATH)
	public void view(@ModelAttribute(IMPORTER)
	PackageImporter importer, @ModelAttribute(ITEMS_STATS)
	ImportedItemsStats stats, @ModelAttribute(PARTS)
	Map<Integer, Boolean> parts, Model model) {
		ImportedItemsStats itemsStats = importer.getImportedItemsStats(stats.getPart());
		parts.put(itemsStats.getPart(), (itemsStats.getNotAssessedTotalCount() == 0));
		for (Entry<Integer, Boolean> p : parts.entrySet()) {
			if (!p.getValue()) {
				model.addAttribute("loadNext", p.getKey());
			}
		}
		model.addAttribute(ITEMS_STATS, itemsStats);
	}
	
	@RequestMapping(value = COMPLETE_PATH)
	public String complete(@ModelAttribute(IMPORTER)
	PackageImporter importer, Errors errors, Model model, SessionStatus session) {
		Task task = importer.schedulePackageImport();
		
		session.setComplete();

		return WebUtils.redirect(TaskController.DETAILS_PATH, "uuid=" + task.getUuid());
	}
	
	@RequestMapping(value = JSON_IMPORT_ITEMS_PATH, method = RequestMethod.GET)
	public ModelAndView jsonImportItems(@ModelAttribute(ITEMS)
	List<ImportedItem> importItems, HttpServletRequest httpRequest, Model model) {
		DatatablesRequest request = DatatablesRequest.parseRequest(httpRequest);
		
		int totalRecords = importItems.size();
		int iDisplayEnd = request.getiDisplayStart() + request.getiDisplayLength();
		iDisplayEnd = (iDisplayEnd > totalRecords) ? totalRecords : iDisplayEnd;
		
		DatatablesResponse response = DatatablesResponse.newResponse(request);
		response.setsColumns("index", "type", "incoming", "existing", "importType", "assessed");
		response.setiTotalRecords(totalRecords);
		response.setiTotalDisplayRecords(totalRecords);
		
		List<ImportedItem> items = importItems.subList(request.getiDisplayStart(), iDisplayEnd);
		int i = 0;
		for (ImportedItem item : items) {
			Integer index = request.getiDisplayStart() + i;
			String type = Handler.getRegisteredType(item.getIncoming());
			String incoming = Handler.getName(item.getIncoming());
			String existing = "";
			if (item.getExisting() != null) {
				existing = Handler.getName(item.getExisting());
			}
			
			String importType;
			switch (item.getImportType()) {
				case OMIT:
					importType = "Skip";
					break;
				case PREFER_THEIRS:
					importType = "Merge (Prefer Theirs)";
					break;
				case PREFER_MINE:
					importType = "Merge (Prefer Mine)";
					break;
				case CREATE:
					importType = "Create New";
					break;
				default:
					importType = item.getImportType().toString();
			}
			
			String accepted = new Boolean(item.isAssessed()).toString();
			
			response.addRow(index.toString(), type, incoming, existing, importType, accepted);
			
			i++;
		}
		model.addAttribute("object", response);
		return new ModelAndView(jsonObjectView);
	}
	
	@RequestMapping(JSON_ITEMS_PATH)
	public ModelAndView jsonItems(String type, HttpServletRequest httpRequest, Model model) throws IOException {
		DatatablesRequest request = DatatablesRequest.parseRequest(httpRequest);
		
		Class<?> clazz = Handler.getRegisteredClass(type);
		
		int totalRecords = Handler.getItemsCount(clazz, true, request.getsSearch());
		
		List<Object> items = Handler.getItems(clazz, true, request.getsSearch(), request.getiDisplayStart(),
		    request.getiDisplayLength());
		
		DatatablesResponse response = DatatablesResponse.newResponse(request);
		response.setsColumns("uuid", "id", "retired", "name", "description");
		response.setiTotalRecords(totalRecords);
		response.setiTotalDisplayRecords(totalRecords);
		
		for (Object item : items) {
			Map<String, String> row = new HashMap<String, String>();
			row.put("uuid", Handler.getUuid(item));
			if (Handler.getId(item) != null) {
				row.put("id", Handler.getId(item).toString());
			}
			if (Handler.getRetired(item) != null) {
				row.put("retired", Handler.getRetired(item).toString());
			}
			row.put("name", Handler.getName(item));
			row.put("description", Handler.getDescription(item));
			
			response.addRow(row);
		}
		
		model.addAttribute("object", response);
		
		return new ModelAndView(jsonObjectView);
	}
	
	@RequestMapping(value = ITEM_PATH, method = RequestMethod.GET)
	public void assessItem(@ModelAttribute(ITEMS)
	List<ImportedItem> items, Integer index, Model model) {
		ImportedItem importedItem = items.get(index);
		importedItem.loadExisting();
		
		try {
			ValidateCustomUtil.validate(importedItem.getIncoming());
		}
		catch (Exception e) {
			log.error(e);
			String message = e.getMessage();
			if (message == null) {
				message = e.toString();
			}
			
			model.addAttribute("incomingInvalid", message);
			if (importedItem.getImportType().isCreate()) {
				importedItem.setImportType(ImportType.OMIT);
			}
		}
		
		model.addAttribute("type", Handler.getRegisteredType(importedItem.getIncoming()));
		model.addAttribute(ITEM, importedItem);
		AssessItemForm assessItemForm = new AssessItemForm();
		assessItemForm.setIndex(index);
		assessItemForm.setImportType(importedItem.getImportType());
		model.addAttribute(assessItemForm);
	}
	
	@RequestMapping(value = ITEM_PATH, method = RequestMethod.GET, params = "uuid")
	public void assessItem(@ModelAttribute(ITEMS)
	List<ImportedItem> items, Integer index, String uuid, Model model) {
		ImportedItem importedItem = items.get(index);
		
		if (importedItem.isExistingReplaceable()) {
			Object existing = Handler.getItemByUuid(Handler.getRegisteredClass(importedItem.getIncoming()), uuid);
			if (existing != null) {
				importedItem.setExisting(existing);
				importedItem.setImportType(ImportType.PREFER_MINE);
			}
		}
		
		assessItem(items, index, model);
	}
	
	@RequestMapping(value = ITEM_PATH, method = RequestMethod.POST)
	public String assessItemPOST(@ModelAttribute(ITEMS)
	List<ImportedItem> items, AssessItemForm assessItemForm, Model model) {
		ImportedItem importedItem = items.get(assessItemForm.getIndex());
		importedItem.setImportType(assessItemForm.getImportType());
		importedItem.setAssessed(true);
		MetadataSharing.getService().persistImportedItem(importedItem);
		model.addAttribute("index", assessItemForm.getIndex());
		return WebUtils.redirect(ASSESS_NEXT_ITEM_PATH);
	}
	
	@RequestMapping(ASSESS_NEXT_ITEM_PATH)
	public String asssessNextItem(@ModelAttribute(ITEMS)
	List<ImportedItem> items, @RequestParam(required = false)
	Integer index, Model model) {
		if (index == null) {
			index = 0;
		}
		
		ImportedItem next = null;
		int nextIndex = index;
		
		for (int i = index + 1; i <= items.size(); i++) {
			if (items.size() == i) {
				i = 0;
			}
			
			if (!items.get(i).isAssessed()) {
				next = items.get(i);
				nextIndex = i;
				break;
			}
			
			if (i == index) {
				break;
			}
		}
		
		if (next != null) {
			model.addAttribute("index", nextIndex);
			return WebUtils.redirect(ITEM_PATH);
		} else {
			return WebUtils.redirect(VIEW_PATH);
		}
	}
}
