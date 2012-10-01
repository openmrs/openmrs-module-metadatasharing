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
package org.openmrs.module.metadatasharing.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.openmrs.module.metadatasharing.SerializedPackage;

/**
 * This class zips or unzips a package.
 */
public class MetadataZipper extends StringZipper {
	
	public static final String HEADER_FILE = "header.xml";
	
	public static final String METADATA_FILE = "metadata.xml";
	
	/**
	 * Unzips the given input and returns an instance of {@link SerializedPackage}.
	 * 
	 * @param input
	 * @return package content
	 * @throws IOException
	 */
	public SerializedPackage unzipPackage(InputStream input) throws IOException {
		Map<String, String> content = unzip(input);
		String header = content.get(HEADER_FILE);
		content.remove(HEADER_FILE);
		return new SerializedPackage(header, content.values().toArray(new String[0]));
	}
	
	public String unzipHeader(InputStream in) throws IOException {
		String header = unzip(in, HEADER_FILE);
		return header;
	}
	
	/**
	 * Zips the given package content, writing to the given output.
	 * 
	 * @param output
	 * @param serializedPackage
	 * @throws IOException
	 */
	public void zipPackage(OutputStream output, SerializedPackage serializedPackage) throws IOException {
		String[] files = new String[serializedPackage.getMetadata().length + 1];
		String[] filenames = new String[files.length];
		
		files[0] = serializedPackage.getHeader();
		filenames[0] = HEADER_FILE;
		
		if (files.length > 1) {
			files[1] = serializedPackage.getMetadata()[0];
			filenames[1] = METADATA_FILE;
			
			for (int i = 2; i < files.length; i++) {
				files[i] = serializedPackage.getMetadata()[i - 1];
		        filenames[i] = METADATA_FILE + "." + i;
	        }
		}
		
		zip(output, filenames, files);
	}
}
