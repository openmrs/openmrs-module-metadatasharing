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
 * Defines import configuration.
 */
public class ImportConfig {
	
	private boolean previous = false;
	
	private ImportType exactMatch = ImportType.PREFER_MINE;
	
	private ImportType possibleMatch = ImportType.PREFER_MINE;
	
	private ImportType noMatch = ImportType.CREATE;
	
	private boolean confirmExactMatch = false;
	
	private boolean confirmPossibleMatch = false;
	
	private boolean confirmNoMatch = false;

	private boolean skipAssessing = false;
	
	public ImportConfig() {
	}
	
	/**
	 * @return the previous
	 */
	public boolean isPrevious() {
		return previous;
	}
	
	/**
	 * @param previous the previous to set
	 */
	public void setPrevious(boolean previous) {
		this.previous = previous;
	}
	
	/**
	 * @return the exactMatch
	 */
	public ImportType getExactMatch() {
		return exactMatch;
	}
	
	/**
	 * @param exactMatch the exactMatch to set
	 */
	public void setExactMatch(ImportType exactMatch) {
		this.exactMatch = exactMatch;
	}
	
	/**
	 * @return the possibleMatch
	 */
	public ImportType getPossibleMatch() {
		return possibleMatch;
	}
	
	/**
	 * @param possibleMatch the possibleMatch to set
	 */
	public void setPossibleMatch(ImportType possibleMatch) {
		this.possibleMatch = possibleMatch;
	}
	
	/**
	 * @return the noMatch
	 */
	public ImportType getNoMatch() {
		return noMatch;
	}
	
	/**
	 * @param noMatch the noMatch to set
	 */
	public void setNoMatch(ImportType noMatch) {
		this.noMatch = noMatch;
	}
	
	/**
	 * @return the confirmExactMatch
	 */
	public boolean isConfirmExactMatch() {
		return confirmExactMatch;
	}
	
	/**
	 * @param confirmExactMatch the confirmExactMatch to set
	 */
	public void setConfirmExactMatch(boolean confirmExactMatch) {
		this.confirmExactMatch = confirmExactMatch;
	}
	
	/**
	 * @return the confirmPossibleMatch
	 */
	public boolean isConfirmPossibleMatch() {
		return confirmPossibleMatch;
	}
	
	/**
	 * @param confirmPossibleMatch the confirmPossibleMatch to set
	 */
	public void setConfirmPossibleMatch(boolean confirmPossibleMatch) {
		this.confirmPossibleMatch = confirmPossibleMatch;
	}
	
	/**
	 * @return the confirmNoMatch
	 */
	public boolean isConfirmNoMatch() {
		return confirmNoMatch;
	}
	
	/**
	 * @param confirmNoMatch the confirmNoMatch to set
	 */
	public void setConfirmNoMatch(boolean confirmNoMatch) {
		this.confirmNoMatch = confirmNoMatch;
	}

	public boolean isSkipAssessing() {
		return skipAssessing;
	}

	public void setSkipAssessing(boolean skipAssessing) {
		this.skipAssessing = skipAssessing;
	}

	public static ImportConfig valueOf(ImportMode importMode) {
		ImportConfig config = new ImportConfig();
		switch (importMode) {
			case PARENT_AND_CHILD:
				config.exactMatch = ImportType.PREFER_THEIRS;
				config.possibleMatch = ImportType.PREFER_THEIRS;
				config.noMatch = ImportType.CREATE;
				config.confirmExactMatch = false;
				config.confirmPossibleMatch = true;
				config.confirmNoMatch = false;
				return config;
			case PEER_TO_PEER:
				config.exactMatch = ImportType.PREFER_MINE;
				config.possibleMatch = ImportType.PREFER_MINE;
				config.noMatch = ImportType.CREATE;
				config.confirmExactMatch = true;
				config.confirmPossibleMatch = true;
				config.confirmNoMatch = false;
				return config;
			case TEST:
				config.exactMatch = ImportType.PREFER_THEIRS;
				config.possibleMatch = ImportType.CREATE;
				config.noMatch = ImportType.CREATE;
				config.confirmExactMatch = false;
				config.confirmPossibleMatch = false;
				config.confirmNoMatch = false;
				return config;
			case MIRROR:
				config.exactMatch = ImportType.OVERWRITE_MINE;
				config.possibleMatch = ImportType.OVERWRITE_MINE;
				config.noMatch = ImportType.CREATE;
				config.confirmExactMatch = false;
				config.confirmPossibleMatch = false;
				config.confirmNoMatch = false;
				return config;
			default:
				return config;
		}
	}
}
