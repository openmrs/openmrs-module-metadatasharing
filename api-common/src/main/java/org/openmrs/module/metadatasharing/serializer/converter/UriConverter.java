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
package org.openmrs.module.metadatasharing.serializer.converter;

import java.net.URI;
import java.net.URISyntaxException;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/**
 * Converts string to a {@link java.net.URI}
 */
public class UriConverter extends AbstractSingleValueConverter {
	
	/**
	 * @see com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter#canConvert(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean canConvert(Class type) {
		return type.equals(URI.class);
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter#fromString(java.lang.String)
	 */
	@Override
	public Object fromString(String str) {
		try {
			return new URI(str);
		}
		catch (URISyntaxException e) {
			throw new ConversionException(e);
		}
	}
	
}
