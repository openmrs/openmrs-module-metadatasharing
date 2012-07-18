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

import org.openmrs.module.metadatasharing.SubscriptionStatus;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * Converts a {@link SubscriptionStatus} to a string.
 */
public class SubscriptionStatusConverter implements SingleValueConverter {
	
	/**
	 * @see com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter#canConvert(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean canConvert(Class type) {
		return type.equals(SubscriptionStatus.class);
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter#fromString(java.lang.String)
	 */
	@Override
	public Object fromString(String str) {
		//this is very inefficient implementation, because we newer convert a String to SubscriptionStatus
		//when we start to do so, we can re-implement this method to something efficient 
		Byte statusValue = Byte.valueOf(str);
		for (SubscriptionStatus status : SubscriptionStatus.values()) {
			if (statusValue == status.getValue())
				return status;
		}
		throw new ConversionException("Wrong subscription status: " + str);
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.SingleValueConverter#toString(java.lang.Object)
	 */
	@Override
	public String toString(Object obj) {
		SubscriptionStatus status = (SubscriptionStatus) obj;
		return Byte.toString(status.getValue());
	}
}
