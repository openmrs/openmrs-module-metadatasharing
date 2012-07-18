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

import java.util.Date;

import org.openmrs.module.metadatasharing.MetadataSharingConsts;

import com.thoughtworks.xstream.converters.basic.DateConverter;

/**
 * Converts sql specific time types to java defaults.
 */
public class DateTimeConverter extends DateConverter {
	
	public DateTimeConverter() {
		super(MetadataSharingConsts.DATE_FORMAT, new String[] { "yyyy-MM-dd HH:mm:ss.S z", "yyyy-MM-dd HH:mm:ss.S a",
		        "yyyy-MM-dd HH:mm:ssz", "yyyy-MM-dd HH:mm:ss z", // JDK 1.3 needs both versions
		        "yyyy-MM-dd HH:mm:ssa", "yyyy-MM-dd" }, // backwards compatibility
		        false);
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.basic.DateConverter#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return Date.class.isAssignableFrom(type);
	}
}
