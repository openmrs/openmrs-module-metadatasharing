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
package org.openmrs.module.metadatasharing.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.springframework.stereotype.Component;


/**
 *
 */
@Component(MetadataSharingConsts.MODULE_ID + ".JsonObjectView")
public class JsonObjectView extends JsonView {

	/**
     * @see org.openmrs.module.metadatasharing.web.view.SerializeView#getObjectToSerialize(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public Object getObjectToSerialize(@SuppressWarnings("rawtypes") Map model, HttpServletRequest request, HttpServletResponse response) {
	    return model.get("object");
    }
	
}
