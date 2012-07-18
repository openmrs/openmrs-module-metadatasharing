package org.openmrs.module.metadatasharing.util;

import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.util.OpenmrsUtil;

public class DateUtil {
	
	// properties we should consider when trying to find an item's last-modified-date 
	public static String[] possibleModifiedDateProperties = new String[] { "dateCreated", "dateChanged", "dateRetired", "dateVoided",
	        "dateModified" };
	
	/**
	 * If possible, gets the last date that the given item was modified
	 * 
	 * @param item the item to analyze
	 * @return the last date that the item was modified (or null if it can't be determined)
	 */
	public static Date getLastDateChanged(Object item) {
		Date ret = null;
		for (String propertyName : possibleModifiedDateProperties) {
			try {
				Date candidate = (Date) PropertyUtils.getProperty(item, propertyName);
				ret = latest(ret, candidate);
			}
			catch (Exception ex) {}
		}
		return ret;
	}
	
	/**
	 * Returns the later of two dates, treating null as the earliest possible date.
	 * 
	 * @param a a date
	 * @param b another date
	 * @return whichever of a or b is further in the future (or null if both a and b are null)
	 */
	public static Date latest(Date a, Date b) {
		return OpenmrsUtil.compareWithNullAsEarliest(a, b) > 0 ? a : b;
	}
}
