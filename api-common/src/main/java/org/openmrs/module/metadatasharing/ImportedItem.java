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

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.reflection.ClassUtil;
import org.openmrs.module.metadatasharing.reflection.ReplaceMethodInovker;

/**
 * Defines an imported metadata item in the system. It can represent both a previously imported item
 * and an item that has not yet been imported.
 */
public class ImportedItem extends BaseOpenmrsObject implements Serializable, Comparable<ImportedItem> {
	
	private static final long serialVersionUID = 1L;
	
	private Integer importedItemId;
	
	private Object incoming;
	
	private String classname;
	
	private Object existing;
	
	private String existingUuid;
	
	private ImportType importType;
	
	private Date dateImported;
	
	private Date dateChanged;
	
	private boolean assessed;
	
	private Set<ImportedItem> relatedItems = new HashSet<ImportedItem>();
	
	public ImportedItem() {
		importType = ImportType.CREATE;
	}
	
	public ImportedItem(Object incoming) {
		setUuid(Handler.getUuid(incoming));
		this.incoming = incoming;
		
		classname = ClassUtil.getDeproxiedClass(incoming).getName();
		importType = ImportType.CREATE;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return importedItemId;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		this.importedItemId = id;
	}
	
	public Integer getImportedItemId() {
		return importedItemId;
	}
	
	public void setImportedItemId(Integer importedItemId) {
		this.importedItemId = importedItemId;
	}
	
	/**
	 * @return the classname
	 */
	public String getClassname() {
		return classname;
	}
	
	public Class<?> getContainedClass() {
		return ClassUtil.loadClass(classname);
	}
	
	/**
	 * @param classname the classname to set
	 */
	public void setClassname(String classname) {
		this.classname = classname;
	}
	
	/**
	 * @return the incoming
	 */
	public Object getIncoming() {
		return incoming;
	}
	
	/**
	 * @since 1.1.2
	 * 
	 * @return the simple class name of existing or null
	 */
	public String getExistingClassSimpleName() {
		if (existing != null) {
			return ClassUtil.getDeproxiedClass(existing).getSimpleName();
		} else {
			return null;
		}
	}
	
	/**
	 * @since 1.1.2 
	 * 
	 * @return the simple class name of incoming
	 */
	public String getIncomingClassSimpleName() {
		return ClassUtil.getDeproxiedClass(incoming).getSimpleName();
	}
	
	/**
	 * @param incoming the incoming to set
	 */
	public void setIncoming(Object incoming) {
		Validate.notNull(incoming);
		Validate.isTrue(classname.equals(ClassUtil.getDeproxiedClass(incoming).getName()));
		Validate.isTrue(getUuid().equals(Handler.getUuid(incoming)));
		this.incoming = incoming;
	}
	
	public void initIncomingToSave(Map<OpenmrsObject, OpenmrsObject> mappings) {
		ReplaceMethodInovker invoker = MetadataSharing.getInstance().getReplaceMethodInvoker();
		invoker.callOnSave(incoming, mappings);
	}
	
	/**
	 * @return the existingUuid
	 */
	public String getExistingUuid() {
		return existingUuid;
	}
	
	/**
	 * @param existingUuid the existingUuid to set
	 */
	public void setExistingUuid(String existingUuid) {
		this.existingUuid = existingUuid;
	}
	
	/**
	 * @return the existing
	 */
	public Object getExisting() {
		return existing;
	}
	
	/**
	 * @param existing the existing to set
	 */
	public void setExisting(Object existing) {
		this.existing = existing;
		
		if (existing != null) {
			existingUuid = Handler.getUuid(existing);
		} else {
			existingUuid = null;
		}
	}
	
	public boolean isExistingReplaceable() {
		return !Handler.getUuid(incoming).equals(existingUuid);
	}
	
	public void loadExisting() {
		if (existingUuid != null) {
			existing = Handler.getItemByUuid(getContainedClass(), existingUuid);
		}
	}
	
	/**
	 * @return the importType
	 */
	public ImportType getImportType() {
		return importType;
	}
	
	/**
	 * @param importType the importType to set
	 */
	public void setImportType(ImportType importType) {
		this.importType = importType;
	}
	
	/**
	 * @return the dateImported
	 */
	public Date getDateImported() {
		return dateImported;
	}
	
	/**
	 * @param dateImported the dateImported to set
	 */
	public void setDateImported(Date dateImported) {
		this.dateImported = dateImported;
	}
	
	public boolean isImported() {
		return dateImported != null;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classname == null) ? 0 : classname.hashCode());
		result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
		return result;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ImportedItem))
			return false;
		ImportedItem other = (ImportedItem) obj;
		if (classname == null) {
			if (other.classname != null)
				return false;
		} else if (!classname.equals(other.classname))
			return false;
		if (getUuid() == null) {
			if (other.getUuid() != null)
				return false;
		} else if (!getUuid().equals(other.getUuid()))
			return false;
		return true;
	}
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ImportedItem o) {
		return toString().compareTo(o.toString());
	}
	
	public String getKey() {
		return classname + "[" + getUuid() + "]";
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getKey();
	}
	
	/**
	 * @return the assess
	 */
	public boolean isAssessed() {
		return assessed;
	}
	
	/**
	 * @param assessed the assessed to set
	 */
	public void setAssessed(boolean assessed) {
		this.assessed = assessed;
	}
	
	/**
	 * Get the date that the item we imported was last modified (before it was exported from its
	 * home server). We can use this to tell if we're importing an item that has not changed since
	 * we last imported it. This should be calculated from dateCreated, dateModified, dateRetired,
	 * etc from the incoming object.
	 * 
	 * @return the dateChanged (null if unavailable)
	 */
	public Date getDateChanged() {
		return dateChanged;
	}
	
	/**
	 * @param dateChanged the dateChanged to set
	 */
	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}
	
	/**
	 * @return the relatedItems
	 */
	public Set<ImportedItem> getRelatedItems() {
		return relatedItems;
	}
	
	public boolean addRelatedItem(ImportedItem importedItem) {
		return relatedItems.add(importedItem);
	}
	
}
