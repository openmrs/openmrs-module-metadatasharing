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
package org.openmrs.module.metadatasharing.publish;

import java.net.URI;
import java.net.URISyntaxException;

import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.util.StringUtils;

/**
 * The set of utils used when during publishing process
 */
public class PublishUtils {
	
	/**
	 * Extracts the group from the given path. We are using this approach instead of PathVariable
	 * annotation, because spring 3.0 introduces it (we are working on 2.5)
	 * 
	 * @param path the path obtained from: request.getPathInfo()
	 * @param knownPrefix the character sequence that is placed before group. E.g.
	 *            "/ws/rest/metadatasharing/package/"
	 * @return the group extracted from the path
	 */
	public static String extractGroupFromPath(String path, String knownPrefix) {
		path = path.substring(path.indexOf(knownPrefix));
		String group = path.substring(knownPrefix.length() + 1);
		int index = group.indexOf("/");
		if (index != -1) {
			group = group.substring(0, index);
		}
		return removeExtension(group);
	}
	
	/**
	 * Extracts version from the path.
	 * 
	 * @see PublishUtils#extractGroupFromPath(String, String)
	 * @param path the path obtained from: request.getPathInfo()
	 * @param knownPrefix the character sequence that is placed before group. E.g.
	 *            "/ws/rest/metadatasharing/package/"
	 * @return the version extracted from the path
	 */
	public static Integer extractVersionFromPath(String path, String knownPrefix) {
		path = path.substring(path.indexOf(knownPrefix));
		String version = path.substring(knownPrefix.length() + 1);
		version = version.substring(version.indexOf("/") + 1);
		int index = version.indexOf("/");
		if (index != -1) {
			version = version.substring(0, index);
		}
		return Integer.valueOf(removeExtension(version));
	}
	
	/**
	 * Creates a relative path to the given resource depending on which OpenMRS version we are
	 * running.<br>
	 * 1.8.1+ versions don't need ".form" extension in /ws/rest/* URLs
	 * 
	 * @param resource the resource. E.g. "{group}/download"
	 * @return relative publish path suitable to current openmrs version
	 */
	public static String createRelativeResourcePath(String resource) {
		if (PublishUtils.pathRequiresSuffix()) {
			return resource + ".form";
		} else {
			return resource;
		}
	}
	
	/**
	 * @see PublishUtils#createRelativePublishPath(String);
	 */
	public static boolean pathRequiresSuffix() {
		if (OpenmrsConstants.OPENMRS_VERSION == null) {
			//it might happen that this constant is null, so we return true because we do not know if we are 
			// running or openmrs version that supports webservices
			return true;
		} else {
			try {
				String[] systemVersion = OpenmrsConstants.OPENMRS_VERSION.split("\\.");
				int major = Integer.parseInt(systemVersion[0]);
				int minor = Integer.parseInt(systemVersion[1]);
				int maintenance = Integer.parseInt(systemVersion[2].split(" ")[0].split("-")[0]);
				if (major > 1 || (major == 1 && minor > 8) || (major == 1 && minor == 8 && maintenance > 0)) {
					//1.8.1 is the first version supporting webservices
					return false;
				} else {
					return true;
				}
			}
			catch (Exception e) {
				return true; //there was an error, probably caused by invalid version format. We are careful
								// so, let's say that the path requires suffix (because it works in all versions) 
			}
		}
	}
	
	/**
	 * Creates the relative publish URI and wraps the {@link URISyntaxException}
	 * 
	 * @see PublishUtils#createRelativeResourcePath(String)
	 */
	public static URI createRelativeURI(String resource) {
		URI uri;
		try {
			uri = new URI(createRelativeResourcePath(resource));
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return uri;
	}
	
	/**
	 * Creates an absolute publish URL for getting package updates
	 *
	 * @param pack the package to which's updates the URL will point
	 * @return the String representation of the URL
	 */
	public static String createAbsolutePublishURL(ExportedPackage pack) {
		return createAbsolutePublishURL(pack.getGroupUuid());
	}

	/**
	 * Creates an absolute publish URL for getting package updates
	 *
	 * @param groupUuid the groupUuid of the package to which's updates the URL will point
	 * @return the String representation of the URL
	 */
	public static String createAbsolutePublishURL(String groupUuid) {
		String prefix = Context.getAdministrationService().getGlobalProperty(MetadataSharingConsts.GP_URL_PREFIX);
		if (prefix == null) {
			return null;
		} else {
			return prefix + createRelativeResourcePath("/ws/rest/metadatasharing/package/" + groupUuid + "/latest");
		}
	}
	
	/**
	 * Removes extension from the given path. If present.
	 */
	private static String removeExtension(String path) {
		if (StringUtils.getFilenameExtension(path) != null) {
			path = path.substring(0, path.length() - StringUtils.getFilenameExtension(path).length() - 1);
		}
		return path;
	}
	
	private PublishUtils() {
	}
}
