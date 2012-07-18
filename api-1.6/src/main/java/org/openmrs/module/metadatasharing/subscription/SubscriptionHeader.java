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
package org.openmrs.module.metadatasharing.subscription;

import java.io.Serializable;
import java.net.URI;

import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.serializer.MetadataSerializer;
import org.openmrs.module.metadatasharing.serializer.converter.UriConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * This is class representing the header of a subscription. Generally it is used for ease of XStream
 * serialization. <br>
 * It contains package header with all information about latest version of a package in this
 * subscription and the contentUri, which is an URI where to download package from.
 * 
 * @see ExportedPackage
 * @see MetadataSerializer
 */
@XStreamAlias("subscriptionHeader")
public class SubscriptionHeader implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ExportedPackage packageHeader;
	
	@XStreamConverter(UriConverter.class)
	private URI contentUri;
	
	/**
	 * @param packageHeader the packageHeader to set
	 */
	public void setPackageHeader(ExportedPackage packageHeader) {
		this.packageHeader = packageHeader;
	}
	
	/**
	 * @return the packageHeader
	 */
	public ExportedPackage getPackageHeader() {
		return packageHeader;
	}
	
	/**
	 * @param contentUri the contentUri to set
	 */
	public void setContentUri(URI contentUri) {
		this.contentUri = contentUri;
	}
	
	/**
	 * @return the contentUri
	 */
	public URI getContentUri() {
		return contentUri;
	}
}
