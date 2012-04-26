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
package org.openmrs.module.metadatasharing.web.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openmrs.module.metadatasharing.Item;
import org.openmrs.module.metadatasharing.web.controller.ExportController;

/**
 * A session object for {@link ExportController}.
 */
public class PackageItems {
	
	private Map<String, Set<Item>> items = new HashMap<String, Set<Item>>();
	
	private Set<String> completeTypes = new HashSet<String>();
	
	/**
	 * @return the items
	 */
	public Map<String, Set<Item>> getItems() {
		return items;
	}
	
	/**
	 * @param items the items to set
	 */
	public void setItems(Map<String, Set<Item>> items) {
		this.items = items;
	}
	
	/**
	 * @return the completeTypes
	 */
	public Set<String> getCompleteTypes() {
		return completeTypes;
	}
	
	public Map<String, Boolean> getCompleteTypesMap() {
		Map<String,Boolean> map = new HashMap<String, Boolean>();
		for (String type : completeTypes) {
	        map.put(type, true);
        }
		return map;
	}
	
	/**
	 * @param completeTypes the completeTypes to set
	 */
	public void setCompleteTypes(Set<String> completeTypes) {
		this.completeTypes = completeTypes;
	}
	
}
