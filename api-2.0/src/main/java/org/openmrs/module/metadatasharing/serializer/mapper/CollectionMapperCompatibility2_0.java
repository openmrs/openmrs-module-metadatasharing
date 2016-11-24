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
package org.openmrs.module.metadatasharing.serializer.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.internal.PersistentMap;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.internal.PersistentSortedMap;
import org.hibernate.collection.internal.PersistentSortedSet;
import org.hibernate.collection.spi.PersistentCollection;
import org.openmrs.annotation.OpenmrsProfile;

@OpenmrsProfile(openmrsPlatformVersion = "2.*")
public class CollectionMapperCompatibility2_0 implements CollectionMapperCompatibility {

	private final Class<?>[] hbClasses = { PersistentList.class, PersistentSet.class, PersistentMap.class,
	        PersistentSortedSet.class, PersistentSortedMap.class };
	
	private final Class<?>[] jdkClasses = { ArrayList.class, HashSet.class, HashMap.class, TreeSet.class, TreeMap.class };

	@Override
	public Class<?> serializedClass(Class type) {
		if (PersistentCollection.class.isAssignableFrom(type)) {
			for (int i = 0; i < hbClasses.length; i++) {
				if (type.equals(hbClasses[i])) {
					return jdkClasses[i];
				}
			}
		}
		
		return null;
	}
}
