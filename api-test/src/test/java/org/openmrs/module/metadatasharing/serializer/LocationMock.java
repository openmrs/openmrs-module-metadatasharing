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

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.OpenmrsObject;

/**
 * Serializable version of {@link Location}.
 * <p>
 * To provide a custom serialization you need to deliver {@link #writeReplace()} method. The method
 * must be added to the exsiting class that you want to serialize. The return parameter must be a
 * subclass of the class that you want to export. The method can be of any access.
 */
public class LocationMock extends Location {
	
	private static final long serialVersionUID = 1L;
	
	public LocationMock(Location loc) {
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
	
	protected ShareableLocation writeReplace() {
		ShareableLocation locationClone = new ShareableLocation(this);
		List<OpenmrsObject> dependencies = new ArrayList<OpenmrsObject>();
		dependencies.addAll(getChildLocations(true));
		locationClone.setDependencies(dependencies);
		return locationClone;
	}
}
