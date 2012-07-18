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
 * Defines the structure of a package in the system.
 * 
 * @since 1.0
 */
public class SerializedPackage {
	
	/**
	 * @since 1.0
	 */
	private final String header;
	
	/**
	 * @since 1.0
	 */
	private final String[] metadata;
	
	/**
	 * @param header
	 * @param metadata
	 * @since 1.0
	 */
	public SerializedPackage(String header, String... metadata) {
		this.header = header;
		this.metadata = metadata;
	}
	
	/**
	 * @return the header
	 * @since 1.0
	 */
	public String getHeader() {
		return header;
	}
	
	/**
	 * @return the metadata
	 * @since 1.0
	 */
	public String[] getMetadata() {
		return metadata;
	}
}
