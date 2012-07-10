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
package org.openmrs.module.metadatasharing.serializer;

import java.util.List;

import org.openmrs.Location;
import org.openmrs.OpenmrsObject;

/**
 * Shareable {@link Location} that is serialized.
 * <p>
 * The class that is serialized must have a method saveReplace that returns the underlying object
 * that can be saved with a service method.
 */
public class ShareableLocation extends Location {
	
	private static final long serialVersionUID = 1L;
	
	List<OpenmrsObject> dependencies;
	
	/**
	 * Copy constructor.
	 */
	public ShareableLocation(LocationMock loc) {
		setAddress1(loc.getAddress1());
		setAddress2(loc.getAddress2());
		setChangedBy(loc.getChangedBy());
		setChildLocations(loc.getChildLocations(true));
		setCityVillage(loc.getCityVillage());
		setCountry(loc.getCountry());
		setCountyDistrict(loc.getCountyDistrict());
		setCreator(loc.getCreator());
		setDateChanged(loc.getDateChanged());
		setDateCreated(loc.getDateCreated());
		setDateRetired(loc.getDateRetired());
		setDescription(loc.getDescription());
		setId(loc.getId());
		setLatitude(loc.getLatitude());
		setLongitude(loc.getLongitude());
		setName(loc.getName());
		setNeighborhoodCell(loc.getNeighborhoodCell());
		setParentLocation(loc.getParentLocation());
		setPostalCode(loc.getPostalCode());
		setRegion(loc.getRegion());
		setRetired(loc.getRetired());
		setRetiredBy(loc.getRetiredBy());
		setRetireReason(loc.getRetireReason());
		setStateProvince(loc.getStateProvince());
		setSubregion(loc.getSubregion());
		setTags(loc.getTags());
		setTownshipDivision(loc.getTownshipDivision());
		setUuid(loc.getUuid());
	}
	
	/**
	 * @param dependencies the dependencies to set
	 */
	public void setDependencies(List<OpenmrsObject> dependencies) {
		this.dependencies = dependencies;
	}
	
	/**
	 * @return the dependencies
	 */
	public List<OpenmrsObject> getDependencies() {
		return dependencies;
	}
	
	/**
	 * Provides with the underlying object that can be saved through a service method.
	 * 
	 * @return Location
	 */
	protected Location saveReplace() {
		Location loc = new Location();
		loc.setAddress1(getAddress1());
		loc.setAddress2(getAddress2());
		loc.setChangedBy(getChangedBy());
		loc.setChildLocations(getChildLocations(true));
		loc.setCityVillage(getCityVillage());
		loc.setCountry(getCountry());
		loc.setCountyDistrict(getCountyDistrict());
		loc.setCreator(getCreator());
		loc.setDateChanged(getDateChanged());
		loc.setDateCreated(getDateCreated());
		loc.setDateRetired(getDateRetired());
		loc.setDescription(getDescription());
		loc.setId(getId());
		loc.setLatitude(getLatitude());
		loc.setLongitude(getLongitude());
		loc.setName(getName());
		loc.setNeighborhoodCell(getNeighborhoodCell());
		loc.setParentLocation(getParentLocation());
		loc.setPostalCode(getPostalCode());
		loc.setRegion(getRegion());
		loc.setRetired(getRetired());
		loc.setRetiredBy(getRetiredBy());
		loc.setRetireReason(getRetireReason());
		loc.setStateProvince(getStateProvince());
		loc.setSubregion(getSubregion());
		loc.setTags(getTags());
		loc.setTownshipDivision(getTownshipDivision());
		loc.setUuid(getUuid());
		return loc;
	}
}
