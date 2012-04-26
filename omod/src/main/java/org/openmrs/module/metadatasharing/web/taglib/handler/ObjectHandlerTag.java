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
package org.openmrs.module.metadatasharing.web.taglib.handler;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.openmrs.module.metadatasharing.handler.Handler;
import org.springframework.web.util.HtmlUtils;

/**
 * This class allows to access objects through {@link MetadataTypeHandler} in jsp pages.
 */
public class ObjectHandlerTag extends SimpleTagSupport {
	
	private Object object;
	
	private String get;
	
	public void setObject(Object object) {
		this.object = object;
	}
	
	public void setGet(String get) {
		this.get = get;
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.SimpleTag#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		String output = "";
		
		if (object != null) {
			if (get.equals("uuid")) {
				output = Handler.getUuid(object);
			} else if (get.equals("type")) {
				output = Handler.getRegisteredType(object);
			} else if (get.equals("name")) {
				output = Handler.getName(object);
			} else if (get.equals("description")) {
				output = Handler.getDescription(object);
			} else if (get.equals("dateChanged")) {
				if (Handler.getDateChanged(object) != null) {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					output = dateFormat.format(Handler.getDateChanged(object));
				} else {
					output = "";
				}
			}
		}
		output = HtmlUtils.htmlEscape(output);
		getJspContext().getOut().print(output);
	}
	
}
