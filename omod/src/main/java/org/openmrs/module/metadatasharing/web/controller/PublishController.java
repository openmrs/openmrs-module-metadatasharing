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

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.PrivilegeCompatibility;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.io.MetadataZipper;
import org.openmrs.module.metadatasharing.publish.PublishUtils;
import org.openmrs.module.metadatasharing.subscription.SubscriptionHeader;
import org.openmrs.module.metadatasharing.web.exception.PackageNotFoundException;
import org.openmrs.module.metadatasharing.web.view.DownloadPackageView;
import org.openmrs.module.metadatasharing.web.view.GetSubscriptionHeaderView;
import org.openmrs.module.metadatasharing.wrapper.PackageExporter;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * The controller for publish related pages.
 */
@Controller(MetadataSharingConsts.MODULE_ID + ".PublishController")
public class PublishController {
	
	/**
	 * The view which returns XML serialized "header" variable from the model
	 */
	@Autowired
	private GetSubscriptionHeaderView subscriptionHeaderView;
	
	/**
	 * The view which returns zipped package content to the client
	 */
	@Autowired
	private DownloadPackageView packageContentView;
	
	@Autowired
	private PrivilegeCompatibility privilegeCompatibility;
	
	/**
	 * The prefix of each URL this controller is mapping to. It is used to extract the group form
	 * the request URL.
	 * 
	 * @see PublishUtils#extractGroupFromPath(String, String)
	 */
	public static final String PREFIX = MetadataSharingConsts.MODULE_ID + "/package";
	
	/**
	 * In OpenMRS versions earlier than 1.8.1 we should map to this path
	 */
	public static final String OLD_PACKAGE_PATH = "/ws/rest/" + PREFIX;
	
	/**
	 * In OpenMRS versions 1.8.1+ we should map to this path
	 */
	public static final String NEW_PACKAGE_PATH = "/rest/" + PREFIX;
	
	/**
	 * Serializes to the client a subscription header of the latest package version with the given
	 * group (group is extracted from the request URL)
	 */
	@RequestMapping(value = OLD_PACKAGE_PATH + "/*/latest", method = RequestMethod.GET)
	public ModelAndView getSubscriptionHeader(Model model, HttpServletRequest request, HttpServletResponse response) {
		try {
			Context.addProxyPrivilege(MetadataSharingConsts.MODULE_PRIVILEGE);
			String req = request.getRequestURI();
			String group = PublishUtils.extractGroupFromPath(req, PREFIX);
			MetadataSharingService service = Context.getService(MetadataSharingService.class);
			ExportedPackage pack = service.getLatestPublishedPackageByGroup(group);
			if (pack != null) {
				SubscriptionHeader header = new SubscriptionHeader();
				pack.setOpenmrsVersion(OpenmrsConstants.OPENMRS_VERSION);
				header.setPackageHeader(pack);
				header.setContentUri(PublishUtils.createRelativeURI("../" + pack.getGroupUuid() + "/" + pack.getVersion()
				        + "/download"));
				model.addAttribute("header", header);
				return new ModelAndView(subscriptionHeaderView, model.asMap());
			} else {
				throw new PackageNotFoundException();
			}
		}
		finally {
			Context.removeProxyPrivilege(MetadataSharingConsts.MODULE_PRIVILEGE);
		}
	}
	
	/**
	 * In 1.8.1+ OpenMRS versions we should map to /rest/* <br>
	 * This method/mapping solves compatibility issues between 1.8.1+ versions and previous
	 */
	@RequestMapping(value = NEW_PACKAGE_PATH + "/*/latest", method = RequestMethod.GET)
	public ModelAndView getSubscriptionHeaderCompatibility(Model model, HttpServletRequest request,
	                                                       HttpServletResponse response) {
		return getSubscriptionHeader(model, request, response);
	}
	
	/**
	 * Gets the package from the database and passes it to the view<br>
	 * The path variables are: group and version (respectively)
	 */
	@RequestMapping(value = OLD_PACKAGE_PATH + "/*/*/download", method = RequestMethod.GET)
	public ModelAndView getPackageContent(Model model, HttpServletRequest request, HttpServletResponse response) {
		try {
			Context.addProxyPrivilege(MetadataSharingConsts.MODULE_PRIVILEGE);
			MetadataSharingService service = Context.getService(MetadataSharingService.class);
			String group = PublishUtils.extractGroupFromPath(request.getRequestURI(), PREFIX);
			Integer version = PublishUtils.extractVersionFromPath(request.getRequestURI(), PREFIX);
			ExportedPackage pack = service.getPublishedPackageByGroup(group, version);
			if (pack != null) {
				model.addAttribute("package", pack);
				return new ModelAndView(packageContentView, model.asMap());
			} else {
				throw new PackageNotFoundException();
			}
		}
		finally {
			Context.removeProxyPrivilege(MetadataSharingConsts.MODULE_PRIVILEGE);
		}
	}
	
	/**
	 * In 1.8.1+ OpenMRS versions we should map to /rest/* <br>
	 * This method/mapping solves compatibility issues between 1.8.1+ versions and previous
	 */
	@RequestMapping(value = NEW_PACKAGE_PATH + "/*/*/download", method = RequestMethod.GET)
	public ModelAndView getPackageContentCompatibility(Model model, HttpServletRequest request, HttpServletResponse response) {
		return getPackageContent(model, request, response);
	}
	
	/**
	 * Mapped to URL "ws/rest/metadatasharing/package/new.form". XML can be requested with either
	 * GET or POST parameters. Parameters this takes are:
	 * <ul>
	 * <li>key: This must be set to 5b635b8d02812d2e1c97691cd71fc05a</li>
	 * <li>type: Required. This is the fully-qualified class name of the type of object you wish to
	 * export</li>
	 * <li>uuids: This is a comma-separated list of uuids to export (you may specify either uuids,
	 * ids, or both)</li>
	 * <li>ids: This is a comma-separated list of ids to export (you may specify either uuids, ids,
	 * or both)</li>
	 * <li>modifiedSince: When specified, items to be returned should have had created or
	 * modifications after the specified date</li>
	 * </ul>
	 * <br/>
	 * Example:
	 * 
	 * <pre>
	 *   ws/rest/metadatasharing/package/new.form?key=5b635b8d02812d2e1c97691cd71fc05a&type=org.openmrs.Location&ids=1,2,3&compress=true
	 * </pre>
	 */
	@RequestMapping(value = OLD_PACKAGE_PATH + "/new")
	public void getNewPackage(@RequestParam(required = false)
	String key, Class<?> type, @RequestParam(required = false)
	String ids, @RequestParam(required = false)
	String uuids, @RequestParam(required = false)
	Date modifiedSince, HttpServletResponse response) throws IOException, SerializationException {
		if (Boolean.valueOf(Context.getAdministrationService()
		        .getGlobalProperty(MetadataSharingConsts.GP_ENABLE_ON_THE_FLY_PACKAGES, "false").trim())) {
			String accessKey = Context.getAdministrationService()
			        .getGlobalProperty(MetadataSharingConsts.GP_WEBSERVICES_KEY);
			if (accessKey != null)
				accessKey = accessKey.trim();
			
			if (StringUtils.isBlank(accessKey) || OpenmrsUtil.nullSafeEquals(accessKey, key)) {
				
				try {
					Context.addProxyPrivilege(MetadataSharingConsts.MODULE_PRIVILEGE);
					//The extra privilege is needed because the Concept handler uses Context.getConceptService().
					Context.addProxyPrivilege(privilegeCompatibility.GET_CONCEPTS());
					
					PackageExporter exporter = MetadataSharing.getInstance().newPackageExporter();
					if (OpenmrsUtil.compareWithNullAsEarliest(new Date(), modifiedSince) > 0) {
						if (uuids != null) {
							String splitUuids[] = uuids.split(",");
							for (String uuid : splitUuids) {
								Object item = Handler.getItemByUuid(type, uuid.trim());
								if (item != null) {
									if (modifiedSince == null
									        || OpenmrsUtil.compareWithNullAsEarliest(Handler.getDateChanged(item),
									            modifiedSince) > 0) {
										exporter.addItem(item);
									}
								}
							}
						}
						
						if (ids != null) {
							String splitIds[] = ids.split(",");
							for (String id : splitIds) {
								Object item = Handler.getItemById(type, Integer.valueOf(id.trim()));
								if (item != null) {
									if (modifiedSince == null
									        || OpenmrsUtil.compareWithNullAsEarliest(Handler.getDateChanged(item),
									            modifiedSince) > 0) {
										exporter.addItem(item);
									}
								}
							}
						}
					}
					exporter.getPackage().setName("Package");
					exporter.getPackage().setDescription(
					    "Contains " + exporter.getPackage().getItems().size() + " items of type " + type.getSimpleName());
					
					exporter.exportPackage();
					
					response.setContentType("application/zip");
					response.setHeader("Content-Disposition", "attachment; filename=\"metadata.zip\"");
					MetadataZipper zipper = new MetadataZipper();
					zipper.zipPackage(response.getOutputStream(), exporter.getExportedPackage().getSerializedPackage());
				}
				finally {
					Context.removeProxyPrivilege(MetadataSharingConsts.MODULE_PRIVILEGE);
					Context.removeProxyPrivilege(privilegeCompatibility.GET_CONCEPTS());
				}
				
			} else {
				//TODO limit failed number of attempts by IP address
				sendErrorResponseAfterDelay(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid key");
			}
		} else {
			sendErrorResponseAfterDelay(response, HttpServletResponse.SC_FORBIDDEN,
			    "The remote system doesn't provide the requested service");
		}
	}
	
	/**
	 * In 1.8.1+ OpenMRS versions we should map to /rest/* <br>
	 * This method/mapping solves compatibility issues between 1.8.1+ versions and previous
	 */
	@RequestMapping(value = NEW_PACKAGE_PATH + "/new", method = RequestMethod.GET)
	public void getNewPackageCompatibility(@RequestParam(required = false)
	String key, Class<?> type, @RequestParam(required = false)
	String ids, @RequestParam(required = false)
	String uuids, @RequestParam(required = false)
	Date modifiedSince, HttpServletResponse response) throws IOException, SerializationException {
		getNewPackage(key, type, ids, uuids, modifiedSince, response);
	}
	
	private static void sendErrorResponseAfterDelay(HttpServletResponse response, int httpCode, String errorMessage)
	    throws IOException {
		
		try {
			//delay in case of a password brute force
			Thread.sleep(5000);
		}
		catch (InterruptedException ie) {}
		
		response.sendError(httpCode, errorMessage);
	}
}
