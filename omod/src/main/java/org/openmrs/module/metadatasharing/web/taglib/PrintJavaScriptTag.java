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
package org.openmrs.module.metadatasharing.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

/**
 * This class allows to print easily values for JavaScript code in jsp pages. It outputs null values
 * as 'null'. Other values are JavaScript and HTML escaped;
 */
public class PrintJavaScriptTag extends SimpleTagSupport {
	
	private String text;
	
	private Boolean asString = false;
	
	/**
	 * @see javax.servlet.jsp.tagext.SimpleTag#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		String output;
		if (text == null || (text == "" && asString == false)) {
			output = "null";
		} else {
			output = HtmlUtils.htmlEscape(JavaScriptUtils.javaScriptEscape(text));
			if (asString == true) {
				output = "'" + output + "'";
			}
		}
		getJspContext().getOut().print(output);
	}
	
	/**
	 * The text which will be printed
	 * 
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * The text which will be printed
	 * 
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * This value determines whether we should print the code as JavaScript string. <br>
	 * E.g. if it is true, then for code
	 * 
	 * <pre>
	 * Some_Text
	 * </pre>
	 * 
	 * we will print
	 * 
	 * <pre>
	 * 'Some_Text'
	 * </pre>
	 * 
	 * @param asString the asString to set
	 */
	public void setAsString(Boolean asString) {
		this.asString = asString;
	}
	
	/**
	 * This value determines whether we should print the code as JavaScript string. <br>
	 * E.g. if it is true, then for code
	 * 
	 * <pre>
	 * Some_Text
	 * </pre>
	 * 
	 * we will print
	 * 
	 * <pre>
	 * 'Some_Text'
	 * </pre>
	 * 
	 * @return the asString
	 */
	public Boolean getAsString() {
		return asString;
	}
	
}
