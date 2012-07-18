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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

/**
 * This class helps to zip/unzip text data in the declared encoding.
 * 
 * @see StringZipper#ENCODING
 */
public class StringZipper {
	
	public static final String ENCODING = "UTF-8";
	
	/**
	 * Unzips files with the given filenames from the input.
	 * 
	 * @param input
	 * @return text data by filenames
	 * @throws IOException
	 */
	public Map<String, String> unzip(InputStream input) throws IOException {
		Map<String, String> files = new LinkedHashMap<String, String>();
		ZipInputStream zip = new ZipInputStream(input);
		try {
			for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
				String file = IOUtils.toString(zip, ENCODING);
				files.put(entry.getName(), file);
			}
		}
		finally {
			if (zip != null) {
				zip.close();
			}
		}
		return files;
	}
	
	/**
	 * Unzips a file with the given filename from the input.
	 * 
	 * @param input
	 * @param filename
	 * @return text data from the given file
	 * @throws IOException
	 */
	public String unzip(InputStream input, String filename) throws IOException {
		Map<String, String> files = unzip(input);
		return files.get(filename);
	}
	
	/**
	 * Zips the given text data to files of the given filenames writing to the output.
	 * 
	 * @param output
	 * @param filenames
	 * @param strings
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public void zip(OutputStream output, String[] filenames, String[] strings) throws IOException, IllegalArgumentException {
		if (filenames.length != strings.length) {
			throw new IllegalArgumentException("The given arrays must be of equal lenghts");
		}
		ZipOutputStream zip = new ZipOutputStream(output);
		try {
			for (int i = 0; i < filenames.length; i++) {
				zip.putNextEntry(new ZipEntry(filenames[i]));
				zip.write(strings[i].getBytes(ENCODING));
				zip.closeEntry();
			}
		}
		finally {
			if (zip != null) {
				zip.close();
			}
		}
	}
}
