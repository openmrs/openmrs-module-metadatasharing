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
 * Defines import modes.
 */
public enum ImportMode {
	PARENT_AND_CHILD("metadatasharing.import.mode.parentAndChild"), PEER_TO_PEER("metadatasharing.import.mode.peerToPeer"), TEST(
	        "metadatasharing.import.mode.test"), PREVIOUS("metadatasharing.import.mode.previous"), MIRROR("metadatasharing.import.mode.mirror");
	
	private final String code;
	
	private ImportMode(String code) {
		this.code = code;
	}
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	
	public boolean isParentAndChild() {
		return PARENT_AND_CHILD.equals(this);
	}
	
	public boolean isPeerToPeer() {
		return PEER_TO_PEER.equals(this);
	}
	
	public boolean isTest() {
		return TEST.equals(this);
	}
	
	public boolean isPrevious() {
		return PREVIOUS.equals(this);
	}
	
	public boolean isMirror() {
		return MIRROR.equals(this);
	}
}
