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
package org.openmrs.module.metadatasharing.web.utils;

import org.openmrs.Concept;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.api.MetadataService;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Set of utils used in controllers, to shorten the code
 */
public class WebUtils {
	
	public static String redirect(String url) {
		return "redirect:" + url + ".form";
	}
	
	public static String redirect(String url, String attributes) {
		return "redirect:" + url + ".form?" + attributes;
	}
	
	/**
	 * Invokes ModelAndView.redirectNotExposingModelAttributes(url, null)
	 * 
	 * @see WebUtils#redirect(String, String, boolean);
	 */
	public static ModelAndView redirectNotExposingModelAttributes(String url) {
		return redirectNotExposingModelAttributes(url, null);
	}
	
	/**
	 * Invokes ModelAndView.redirect(url, args, false)
	 * 
	 * @see WebUtils#redirect(String, String, boolean);
	 */
	public static ModelAndView redirectNotExposingModelAttributes(String url, String args) {
		return redirect(url, args, false);
	}
	
	/**
	 * Redirects to the given URL.
	 * 
	 * @param url the URL to redirect
	 * @param args the URL arguments. E.g. "group=123&id=2"
	 * @param exposeModelAttributes the boolean stating if the model attributes should be exposed in
	 *            the redirect URL
	 * @return ModelAndView which redirects to the given page
	 */
	public static ModelAndView redirect(String url, String args, boolean exposeModelAttributes) {
		url = url + ".form";
		if (args != null) {
			url = url + "?" + args;
		}
		RedirectView redirect = new RedirectView(url, true);
		redirect.setExposeModelAttributes(exposeModelAttributes);
		return new ModelAndView(redirect);
	}

	public static String metadataUrl(String type, String uuid) {
		try {
			Class clazz = OpenmrsClassLoader.getInstance().loadClass(type);
			Object o = Context.getService(MetadataService.class).getItem(clazz, uuid);
			if (o instanceof OpenmrsObject) {
				Integer id = ((OpenmrsObject)o).getId();
				if (clazz == Concept.class) {
					return "dictionary/concept.htm?conceptId=" + id;
				}
				// TODO: Add additional here
			}
		}
		catch (Exception e) {
		}
		return "";
	}
	
	private WebUtils() {
	}
}
