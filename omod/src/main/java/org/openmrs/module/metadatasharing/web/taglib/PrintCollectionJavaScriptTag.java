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
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

/**
 * This class allows to print easily items in the given collection for JavaScript code in .jsp
 * pages. When the given collection is null it outputs <pre>null</pre>, otherwise it outputs:
 * <pre>[item1.toString(), item2.toString(),...]</pre>. All values are JavaScript and HTML 
 * escaped. If asString is true, the values are wrapped in quotes. 
 */
public class PrintCollectionJavaScriptTag extends SimpleTagSupport {
	
	@SuppressWarnings("rawtypes")
	private Collection var;
	
	private Boolean asString = false;
	
	/**
	 * @see javax.servlet.jsp.tagext.SimpleTag#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		StringBuilder buffer = new StringBuilder();
		if (var == null) {
			buffer.append("null");
		} else {
			buffer.append("[");
			for (Object obj : var) {
				String str = HtmlUtils.htmlEscape(JavaScriptUtils.javaScriptEscape(obj.toString()));
				
				if (asString == true)
					buffer.append("'");
				
				buffer.append(str);
				
				if (asString == true)
					buffer.append("'");
				
				buffer.append(",");
			}
			buffer.deleteCharAt(buffer.length() - 1);
			buffer.append("]");
		}
		getJspContext().getOut().print(buffer.toString());
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
	
	/**
	 * Sets the collection to be printed
	 * 
	 * @param var the var to set
	 */
	@SuppressWarnings("rawtypes")
    public void setVar(Collection var) {
		this.var = var;
	}
	
	/**
	 * Gets the collection which will be printed
	 * 
	 * @return the var
	 */
	@SuppressWarnings("rawtypes")
    public Collection getVar() {
		return var;
	}
	
}
