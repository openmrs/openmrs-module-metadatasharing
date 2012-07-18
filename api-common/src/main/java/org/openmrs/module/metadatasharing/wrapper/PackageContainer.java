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
package org.openmrs.module.metadatasharing.wrapper;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.Package;
import org.openmrs.module.metadatasharing.SerializedPackage;
import org.openmrs.module.metadatasharing.io.MetadataZipper;
import org.openmrs.serialization.SerializationException;

/**
 * Indicates that the object can contain a package.
 */
public abstract class PackageContainer {
	
	/**
	 * @return the package
	 */
	public abstract Package getPackage();
	
	public void loadPackage(Package other) throws IOException {
		getPackage().loadPackage(other);
	}
	
	public void loadSerializedPackage(SerializedPackage serializedPackage) throws IOException {
		try {
			Package other = MetadataSharing.getInstance().getMetadataSerializer()
			        .deserialize(serializedPackage.getHeader(), Package.class);
			other.setSerializedPackage(serializedPackage);
			
			loadPackage(other);
		}
		catch (SerializationException e) {
			throw new IOException(e);
		}
	}
	
	public void loadSerializedPackageStream(InputStream serializedPackageStream) throws IOException {
		SerializedPackage serializedPackage = new MetadataZipper().unzipPackage(serializedPackageStream);
		
		loadSerializedPackage(serializedPackage);
	}
	
}
