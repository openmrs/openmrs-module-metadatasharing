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
import javax.servlet.http.HttpServletResponse;

import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.web.exception.PackageNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles MetadataSharing specific exceptions which may occur in some web related code. When some
 * known exception is found it redirects to appropriate error page
 */
@Component(MetadataSharingConsts.MODULE_ID + ".ExceptionResolver")
public class ExceptionResolver implements HandlerExceptionResolver {
	
	/**
	 * @see org.springframework.web.servlet.HandlerExceptionResolver#resolveException(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
	 */
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
	                                     Exception exception) {
		if (exception instanceof PackageNotFoundException) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return new ModelAndView("/module/metadatasharing/packageNotFound");
		}
		return null;
	}
	
}
