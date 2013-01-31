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
 * This class contains set of subscription statuses. The general rule of adding new statuses is that
 * the smaller number it is, the "better" status it is. <br>
 * This means, statuses ranged form:
 * <ul>
 * <li>10 to 19 are "good", e.g. up to date</li>
 * <li>20 to 29 are "neutral", e.g. update available</li>
 * <li>30+ are errors</li>
 * </ul>
 */
public enum SubscriptionStatus {
	
	/**
	 * This status code is used when not subscribed
	 */
	DISABLED((byte) 0),
	
	/**
	 * This status code is only used when subscription hasn't been initialized yet.
	 */
	NEVER_CHECKED((byte) 1),

	/**
	 * This status means, that the package we have locally is up to date with the repository.
	 */
	UP_TO_DATE((byte) 10),

	/**
	 * This status means, that there is a package update available to download.
	 */
	UPDATE_AVAILABLE((byte) 20),

	/**
	 * This status means, that there was an error in subscription workflow, but we cannot categorize
	 * it, or check exactly what was the problem. Basically, if there is an unknown error, use this
	 * status.
	 */
	FATAL_ERROR((byte) 30),

	/**
	 * This status means, that when we were checking for updates the remote server didn't respond
	 * within a certain time.
	 */
	TIMEOUT_LIMIT_EXCEEDED((byte) 31),

	/**
	 * This status means, that the subscription's URL we are fetching doesn't contain valid header
	 * information.
	 */
	INVALID_SUBSCRIPTION((byte) 32),

	/**
	 * This status means, that we tried to fetch the subscription's URL, but the remote server was
	 * unavailable
	 */
	SERVICE_UNAVAILABLE((byte) 33),

	/**
	 * This status means, that there is another subscription with the same group
	 */
	DUPLICATE_SUBSCRIPTION((byte) 34);
	
	/**
	 * The byte-value of subscription status
	 */
	private byte value;
	
	/**
	 * Returns the byte value of this subscription status
	 * 
	 * @return the byte value of this subscription status
	 */
	public byte getValue() {
		return value;
	}
	
	private SubscriptionStatus(byte value) {
		this.value = value;
	}
	
	public boolean isUpToDate() {
		return this.equals(UP_TO_DATE);
	}
}
