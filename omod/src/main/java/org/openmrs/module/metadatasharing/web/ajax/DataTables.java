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
package org.openmrs.module.metadatasharing.web.ajax;


/**
 *
 */
public class DataTables extends Ajaxable {
	
	private final String sEcho;
	
	private final int iTotalRecords;
	
	private final int iTotalDisplayRecords;
	
	private final String sColumns;
	
	private final String[][] aaData;
	
	/**
	 * @param echo
	 * @param totalRecords
	 * @param totalDisplayRecords
	 * @param rows
	 * @param columns
	 */
	public DataTables(String echo, int totalRecords, int totalDisplayRecords, String[][] rows, String columns) {
		sEcho = echo;
		iTotalRecords = totalRecords;
		iTotalDisplayRecords = totalDisplayRecords;
		aaData = rows;
		sColumns = columns;
	}
	
	public DataTables(String echo, int totalRecords, int totalDisplayRecords, String[][] rows, String[] columns) {
		sEcho = echo;
		iTotalRecords = totalRecords;
		iTotalDisplayRecords = totalDisplayRecords;
		aaData = rows;
		String tmpColumns = "";
		for (String column : columns) {
			tmpColumns += column + ",";
		}
		sColumns = tmpColumns.substring(0, tmpColumns.length() - 1);
	}
	
	/**
	 * @param echo
	 * @param totalRecords
	 * @param totalDisplayRecords
	 * @param rows
	 */
	public DataTables(String echo, int totalRecords, int totalDisplayRecords, String[][] rows) {
		sEcho = echo;
		iTotalRecords = totalRecords;
		iTotalDisplayRecords = totalDisplayRecords;
		aaData = rows;
		sColumns = null;
	}
	
	/**
	 * @return the echo
	 */
	public String getEcho() {
		return sEcho;
	}
	
	/**
	 * @return the totalRecords
	 */
	public int getTotalRecords() {
		return iTotalRecords;
	}
	
	/**
	 * @return the totalDisplayRecords
	 */
	public int getTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}
	
	/**
	 * @return the columns
	 */
	public String getColumns() {
		return sColumns;
	}
	
	/**
	 * @return the rows
	 */
	public String[][] getRows() {
		return aaData;
	}
}
