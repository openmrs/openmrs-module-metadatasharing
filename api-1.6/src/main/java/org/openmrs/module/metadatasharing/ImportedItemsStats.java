package org.openmrs.module.metadatasharing;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.metadatasharing.handler.Handler;

public class ImportedItemsStats {
	
	private Map<String, Integer> createCounts;
	
	private Map<String, Integer> omitCounts;
	
	private Map<String, Integer> preferMineCounts;
	
	private Map<String, Integer> perferTheirsCounts;
	
	private Map<String, Integer> notAssessedCounts;
	
	private Integer totalCount;
	
	private Integer hiddenTotalCount;
	
	private Integer notAssessedTotalCount;
	
	private Integer part;
	
	public ImportedItemsStats(Collection<ImportedItem> importedItems, Integer part) {
		this.part = part;
		createCounts = getItemsCount(importedItems, EnumSet.of(ImportType.CREATE), true);
		omitCounts = getItemsCount(importedItems, EnumSet.of(ImportType.OMIT), true);
		preferMineCounts = getItemsCount(importedItems, EnumSet.of(ImportType.PREFER_MINE), true);
		perferTheirsCounts = getItemsCount(importedItems, EnumSet.of(ImportType.PREFER_THEIRS), true);
		notAssessedCounts = getItemsCount(importedItems, EnumSet.allOf(ImportType.class), false);
		
		totalCount = 0;
		hiddenTotalCount = 0;
		notAssessedTotalCount = 0;
		for (ImportedItem importedItem : importedItems) {
			if (!Handler.isHidden(importedItem.getIncoming())) {
				if (!importedItem.isAssessed()) {
					notAssessedTotalCount++;
				}
				totalCount++;
			} else {
				hiddenTotalCount++;
			}
		}
		
	}
	
	public Map<String, Integer> getItemsCount(Collection<ImportedItem> importedItems, EnumSet<ImportType> importTypes,
	                                          boolean assessed) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		for (ImportedItem importedItem : importedItems) {
			Object incoming = importedItem.getIncoming();
			
			boolean assessedFilter = (assessed == importedItem.isAssessed());
			
			if (assessedFilter && importTypes.contains(importedItem.getImportType()) && !Handler.isHidden(incoming)) {
				String key = Handler.getRegisteredType(incoming);
				Integer count = map.get(key);
				if (count == null) {
					map.put(key, 1);
				} else {
					count = count + 1;
					map.put(key, count);
				}
			}
		}
		
		return map;
	}
	
	/**
	 * @return the createCounts
	 */
	public Map<String, Integer> getCreateCounts() {
		return createCounts;
	}
	
	/**
	 * @return the omitCounts
	 */
	public Map<String, Integer> getOmitCounts() {
		return omitCounts;
	}
	
	/**
	 * @return the preferMineCounts
	 */
	public Map<String, Integer> getPreferMineCounts() {
		return preferMineCounts;
	}
	
	/**
	 * @return the perferTheirsCounts
	 */
	public Map<String, Integer> getPerferTheirsCounts() {
		return perferTheirsCounts;
	}
	
	/**
	 * @return the notAssessedCounts
	 */
	public Map<String, Integer> getNotAssessedCounts() {
		return notAssessedCounts;
	}
	
	/**
	 * @return the totalCount
	 */
	public Integer getTotalCount() {
		return totalCount;
	}
	
	/**
	 * @return the hiddenTotalCount
	 */
	public Integer getHiddenTotalCount() {
		return hiddenTotalCount;
	}
	
	/**
	 * @return the notAssessedTotalCount
	 */
	public Integer getNotAssessedTotalCount() {
		return notAssessedTotalCount;
	}
	
	/**
	 * @return the part
	 */
	public Integer getPart() {
		return part;
	}
	
}
