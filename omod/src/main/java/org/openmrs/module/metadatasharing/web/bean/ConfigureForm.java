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

import org.openmrs.module.metadatasharing.web.controller.ConfigureController;

/**
 * A form backing object for {@link ConfigureController}
 */
public class ConfigureForm {
	
	private String urlPrefix;
	
	private Boolean notifyAutomatically;
	
	private Integer intervalDays;
	
	private Boolean enableOnTheFlyPackages;
	
	private String webservicesKey;
	
	/**
	 * @param urlPrefix the urlPrefix to set
	 */
	public void setUrlPrefix(String urlPrefix) {
		if (urlPrefix != null) {
			this.urlPrefix = urlPrefix.trim();
			if (this.urlPrefix == "") {
				this.urlPrefix = null;
			}
		}
	}
	
	/**
	 * @return the urlPrefix
	 */
	public String getUrlPrefix() {
		return urlPrefix;
	}
	
	/**
	 * @param automaticNotifications the automaticNotifications to set
	 */
	public void setNotifyAutomatically(Boolean automaticNotifications) {
		this.notifyAutomatically = automaticNotifications;
	}
	
	/**
	 * @return the automaticNotifications
	 */
	public Boolean getNotifyAutomatically() {
		if (notifyAutomatically == null) {
			return false;
		} else {
			return notifyAutomatically;
		}
	}
	
	/**
	 * @param intervalDays the intervalDays to set
	 */
	public void setIntervalDays(Integer intervalDays) {
		this.intervalDays = intervalDays;
	}
	
	/**
	 * @return the intervalDays
	 */
	public Integer getIntervalDays() {
		return intervalDays;
	}
	
	
	/**
	 * @return the enableOnTheFlyPackages
	 */
	public Boolean getEnableOnTheFlyPackages() {
		return enableOnTheFlyPackages;
	}
	
	/**
	 * @param enableOnTheFlyPackages the enableOnTheFlyPackages to set
	 */
	public void setEnableOnTheFlyPackages(Boolean enableOnTheFlyPackages) {
		this.enableOnTheFlyPackages = enableOnTheFlyPackages;
	}
	
	/**
	 * @return the webservicesKey
	 */
	public String getWebservicesKey() {
		return webservicesKey;
	}
	
	/**
	 * @param webservicesKey the webservicesKey to set
	 */
	public void setWebservicesKey(String webservicesKey) {
		this.webservicesKey = webservicesKey;
	}
}
