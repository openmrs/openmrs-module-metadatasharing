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
package org.openmrs.module.metadatasharing.web.extension.html;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.web.extension.AdministrationSectionExt;

/**
 * This class defines the links that will appear on the administration page under the
 * "metadatasharing.title" heading. This extension is enabled by defining (uncommenting) it in the
 * /metadata/config.xml file.
 */
public class AdminList extends AdministrationSectionExt {
	
	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getMediaType()
	 */
	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getTitle()
	 */
	@Override
	public String getTitle() {
		return MetadataSharingConsts.MODULE_ID + ".title";
	}
	
	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getLinks()
	 */
	@Override
	public Map<String, String> getLinks() {
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		map.put(MetadataSharingConsts.MODULE_PATH + "/export/list.form", MetadataSharingConsts.MODULE_ID
		        + ".exportMetadata");
		
		map.put(MetadataSharingConsts.MODULE_PATH + "/import/list.form", MetadataSharingConsts.MODULE_ID
		        + ".importMetadata");
		
		map.put(MetadataSharingConsts.MODULE_PATH + "/task/list.form", MetadataSharingConsts.MODULE_ID
	        + ".task.manageTasks");
		
		map.put(MetadataSharingConsts.MODULE_PATH + "/configure.form", MetadataSharingConsts.MODULE_ID + ".configure");
		
		return map;
	}
	
	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getRequiredPrivilege()
	 */
	@Override
	public String getRequiredPrivilege() {
		return MetadataSharingConsts.MODULE_PRIVILEGE;
	}
	
}
