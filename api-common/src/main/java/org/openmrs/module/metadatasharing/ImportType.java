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
package org.openmrs.module.metadatasharing;

/**
 * Defines import types.
 */
public enum ImportType {
	CREATE("metadatasharing.import.type.create"), PREFER_MINE("metadatasharing.import.type.prefermine"), PREFER_THEIRS(
	        "metadatasharing.import.type.prefertheirs"), OMIT("metadatasharing.import.type.omit"), OVERWRITE_MINE("metadatasharing.import.type.overwritemine");
	
	private final String code;
	
	private ImportType(String code) {
		this.code = code;
	}
	
    /**
     * @return the code
     */
    public String getCode() {
    	return code;
    }
    
    public boolean isCreate() {
    	return CREATE.equals(this);
    }
    
    public boolean isPreferMine() {
    	return PREFER_MINE.equals(this);
    }
    
    public boolean isPreferTheirs() {
    	return PREFER_THEIRS.equals(this);
    }
    
    public boolean isOmit() {
    	return OMIT.equals(this);
    }
    
    public boolean isOverwriteMine() {
    	return OVERWRITE_MINE.equals(this);
    }

}
