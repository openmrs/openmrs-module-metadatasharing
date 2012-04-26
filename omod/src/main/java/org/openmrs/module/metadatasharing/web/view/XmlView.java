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
package org.openmrs.module.metadatasharing.web.view;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.module.metadatasharing.serializer.MetadataSerializer;
import org.openmrs.module.metadatasharing.web.controller.PublishController;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An abstract view which serializes the given object to the XML.<br>
 * Used mostly in {@link PublishController}
 */
public abstract class XmlView extends SerializeView {
	
	/**
	 * The serializer we will be using for writing output
	 */
	@Autowired
	protected MetadataSerializer serializer = null;
	
	/**
	 * @see org.springframework.web.servlet.view.AbstractView#renderMergedOutputModel(java.util.Map,
	 *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void renderMergedOutputModel(@SuppressWarnings("rawtypes") Map model, HttpServletRequest request,
	                                       HttpServletResponse response) throws Exception {
		response.setHeader("Pragma", "No-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("application/xml");
		PrintWriter writer = response.getWriter();
		writer.write(serializer.serialize(getObjectToSerialize(model, request, response)));
	}
}
