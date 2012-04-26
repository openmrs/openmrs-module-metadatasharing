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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

/**
 * Class dedicated for DataTables. It is a request that can be extracted from
 * {@link HttpServletRequest} with {@link #parseRequest(HttpServletRequest)}.
 * 
 * @see http://datatables.net/
 */
public class DatatablesRequest {
	
	private Integer iDisplayStart;
	
	private Integer iDisplayLength;
	
	private Integer iColumns;
	
	private String sSearch;
	
	private Boolean bRegex;
	
	private Integer sEcho;
	
	private Integer iSortingCols;
	
	private Boolean[] bSearchableCol;
	
	private String[] sSearchCol;
	
	private Boolean[] bRegexCol;
	
	private Boolean[] bSortableCol;
	
	private Integer[] iSortCol;
	
	private String[] sSortDirCol;
	
	private String[] mDataPropCol;
	
	private DatatablesRequest() {
		
	}
	
	/**
	 * Display start point in the current data set.
	 * 
	 * @return the iDisplayStart
	 */
	public Integer getiDisplayStart() {
		return iDisplayStart;
	}
	
	/**
	 * Number of records that the table can display in the current draw. It is expected that the
	 * number of records returned will be equal to this number, unless the server has fewer records
	 * to return.
	 * 
	 * @return the iDisplayLength
	 */
	public Integer getiDisplayLength() {
		return iDisplayLength;
	}
	
	/**
	 * Number of columns being displayed (useful for getting individual column search info).
	 * 
	 * @return the iColumns
	 */
	public Integer getiColumns() {
		return iColumns;
	}
	
	/**
	 * Global search field.
	 * 
	 * @return the sSearch
	 */
	public String getsSearch() {
		return sSearch;
	}
	
	/**
	 * True if the global filter should be treated as a regular expression for advanced filtering,
	 * false if not.
	 * 
	 * @return the bRegex
	 */
	public Boolean getbRegex() {
		return bRegex;
	}
	
	/**
	 * Indicator for if a column is flagged as searchable or not on the client-side.
	 * 
	 * @return the bSearchableCol
	 */
	public Boolean[] getbSearchableCol() {
		return bSearchableCol;
	}
	
	/**
	 * Individual column filter.
	 * 
	 * @return the sSearchCol
	 */
	public String[] getsSearchCol() {
		return sSearchCol;
	}
	
	/**
	 * True if the individual column filter should be treated as a regular expression for advanced
	 * filtering, false if not.
	 * 
	 * @return the bRegexCol
	 */
	public Boolean[] getbRegexCol() {
		return bRegexCol;
	}
	
	/**
	 * Indicator for if a column is flagged as sortable or not on the client-side.
	 * 
	 * @return the bSortableCol
	 */
	public Boolean[] getbSortableCol() {
		return bSortableCol;
	}
	
	/**
	 * Number of columns to sort on.
	 * 
	 * @return the iSortingCols
	 */
	public Integer getiSortingCols() {
		return iSortingCols;
	}
	
	/**
	 * Column being sorted on (you will need to decode this number for your database).
	 * 
	 * @return the iSortCol
	 */
	public Integer[] getiSortCol() {
		return iSortCol;
	}
	
	/**
	 * Direction to be sorted - "desc" or "asc".
	 * 
	 * @return the sSortDirCol
	 */
	public String[] getsSortDirCol() {
		return sSortDirCol;
	}
	
	/**
	 * The value specified by mDataProp for each column. This can be useful for ensuring that the
	 * processing of data is independent from the order of the columns.
	 * 
	 * @return the mDataPropCol
	 */
	public String[] getmDataPropCol() {
		return mDataPropCol;
	}
	
	/**
	 * Information for DataTables to use for rendering.
	 * 
	 * @return the sEcho
	 */
	public Integer getsEcho() {
		return sEcho;
	}
	
	/**
	 * Creates {@link DatatablesRequest} from parameters found in the given request.
	 * 
	 * @param request
	 * @return {@link DatatablesRequest}
	 */
	public static DatatablesRequest parseRequest(HttpServletRequest request) {
		DatatablesRequest d = new DatatablesRequest();
		
		d.iDisplayStart = Integer.valueOf(request.getParameter("iDisplayStart"));
		d.iDisplayLength = Integer.valueOf(request.getParameter("iDisplayLength"));
		d.iColumns = Integer.valueOf(request.getParameter("iColumns"));
		d.sSearch = request.getParameter("sSearch");
		d.bRegex = Boolean.valueOf(request.getParameter("bRegex"));
		d.sEcho = Integer.valueOf(request.getParameter("sEcho"));
		
		String iSortingCols = request.getParameter("iSortingCols");
		if (iSortingCols != null) {
			d.iSortingCols = Integer.valueOf(request.getParameter("iSortingCols"));
		}
		
		String[] values = parseColumns("bSearchable", request);
		d.bSearchableCol = convertToBoolean(values);
		
		values = parseColumns("sSearch", request);
		d.sSearchCol = values;
		
		values = parseColumns("bRegex", request);
		d.bRegexCol = convertToBoolean(values);
		
		values = parseColumns("bSortable", request);
		d.bSortableCol = convertToBoolean(values);
		
		values = parseColumns("iSortCol", request);
		d.iSortCol = convertToInteger(values);
		
		values = parseColumns("sSortDir", request);
		d.sSortDirCol = values;
		
		values = parseColumns("mDataProp", request);
		d.mDataPropCol =values;
		
		return d;
	}
	
	private static Boolean[] convertToBoolean(String[] values) {
		Boolean[] result = new Boolean[values.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Boolean.valueOf(values[i]);
		}
		return result;
	}
	
	private static Integer[] convertToInteger(String[] values) {
		Integer[] result = new Integer[values.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.valueOf(values[i]);
		}
		return result;
	}
	
	private static String[] parseColumns(String type, HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		Map<String, String[]> parameterMap = request.getParameterMap();
		
		Map<Integer, String> parameters = new HashMap<Integer, String>();
		type = type + "_";
		
		for (Entry<String, String[]> parameter : parameterMap.entrySet()) {
			if (parameter.getKey().startsWith(type)) {
				Integer index = Integer.valueOf(parameter.getKey().replace(type, ""));
				String value = parameter.getValue()[0];
				parameters.put(index, value);
			}
		}
		
		String[] values = new String[parameters.size()];
		for (Entry<Integer, String> parameter : parameters.entrySet()) {
			values[parameter.getKey()] = parameter.getValue();
		}
		return values;
	}
	
}
