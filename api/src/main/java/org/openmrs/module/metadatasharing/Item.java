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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.reflection.ClassUtil;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * A short package item summary.
 */
public class Item extends BaseOpenmrsObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String classname;
	
	private Long serialVersion;
	
	private Date dateChanged;
	
	private Boolean retired;
	
	@XStreamOmitField
	private String name;
	
	@XStreamOmitField
	private String description;
	
	public Item() {
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return id;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * @return the packageItemId
	 */
	public Integer getPackageItemId() {
		return id;
	}
	
	/**
	 * @param packageItemId the packageItemId to set
	 */
	public void setPackageItemId(Integer packageItemId) {
		this.id = packageItemId;
	}
	
	/**
	 * @return the classname
	 */
	public String getClassname() {
		return classname;
	}
	
	/**
	 * @param classname the classname to set
	 */
	public void setClassname(String classname) {
		this.classname = classname;
	}
	
	/**
	 * @return the serialVersion
	 */
	public Long getSerialVersion() {
		return serialVersion;
	}
	
	/**
	 * @param serialVersion the serialVersion to set
	 */
	public void setSerialVersion(Long serialVersion) {
		this.serialVersion = serialVersion;
	}
	
	/**
	 * @return the dateChanged
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
	 * @return the retired
	 */
	public boolean isRetired() {
		return retired;
	}
	
	/**
	 * @param retired the retired to set
	 */
	public void setRetired(Boolean retired) {
		this.retired = retired;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classname == null) ? 0 : classname.hashCode());
		result += ((getUuid() == null) ? 0 : getUuid().hashCode());
		return result;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Item)) {
			return false;
		}
		Item other = (Item) obj;
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
	 * Converts the given object to {@link Item}.
	 * 
	 * @param item
	 * @return the packageItem
	 */
	public static Item valueOf(Object item) {
		if (item instanceof Item) {
			return (Item) item;
		} else {
			Item packageItem = new Item();
			packageItem.setUuid(Handler.getUuid(item));
			packageItem.setId(Handler.getId(item));
			packageItem.setClassname(ClassUtil.getDeproxiedClass(item).getName());
			packageItem.setDateChanged(Handler.getDateChanged(item));
			packageItem.setRetired(Handler.getRetired(item));
			packageItem.setName(Handler.getName(item));
			packageItem.setDescription(Handler.getDescription(item));
			return packageItem;
		}
	}
	
	public static Collection<Item> valueOf(Collection<Object> items) {
		Collection<Item> packageItems = new ArrayList<Item>(items.size());
		for (Object item : items) {
			packageItems.add(valueOf(item));
		}
		return packageItems;
	}
	
	public Class<?> getContainedClass() {
		return ClassUtil.loadClass(classname);
	}
}
