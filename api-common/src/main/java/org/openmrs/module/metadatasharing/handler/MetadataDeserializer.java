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
package org.openmrs.module.metadatasharing.handler;

import java.util.Collection;

public abstract class MetadataDeserializer {
	
	public abstract Collection<String> getAttributeNames();
	
	public abstract String getAttribute(String name);
	
	public abstract boolean hasMoreChildren();
	
	public abstract void moveDown();
	
	public abstract void moveUp();
	
	public abstract String getNodeName();
	
	public abstract String getValue();
	
	public abstract void putReference(Object value);
	
	public abstract Object getReference();
	
	public abstract <T> T convert(Object current, Class<? extends T> type);
	
	public abstract Class<?> getType();
	
	public abstract String getFromVersion();
	
	public abstract String getToVersion();
}
