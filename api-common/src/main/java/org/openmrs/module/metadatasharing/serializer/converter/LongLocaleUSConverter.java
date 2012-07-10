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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import com.thoughtworks.xstream.converters.basic.LongConverter;

public class LongLocaleUSConverter extends LongConverter {
	
	@Override
	public Object fromString(String str) {
		try {
			return super.fromString(str);
		}
		catch (Exception e) {
			NumberFormat formatter = DecimalFormat.getInstance(Locale.US);
			try {
				return formatter.parse(str).longValue();
			}
			catch (ParseException ex) {
				throw new RuntimeException(ex);
			}
		}
	}
}
