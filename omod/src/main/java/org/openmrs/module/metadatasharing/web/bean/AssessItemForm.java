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

import org.openmrs.module.metadatasharing.ImportType;

/**
 *
 */
public class AssessItemForm {
	
	private Integer index;
	
	private ImportType importType;
	
	/**
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}
	
	/**
	 * @param index the index to set
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}
	
	/**
	 * @return the importType
	 */
	public ImportType getImportType() {
		return importType;
	}
	
	/**
	 * @param importType the importType to set
	 */
	public void setImportType(ImportType importType) {
		this.importType = importType;
	}
	
}
