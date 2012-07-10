package org.openmrs.module.metadatasharing.serializer.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentList;
import org.hibernate.collection.PersistentMap;
import org.hibernate.collection.PersistentSet;
import org.hibernate.collection.PersistentSortedMap;
import org.hibernate.collection.PersistentSortedSet;
import org.openmrs.module.metadatasharing.serializer.converter.HibernatePersistentCollectionConverter;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Replaces Hibernate 3 specific collections with java.util implementations.
 * <p>
 * This mapper only takes care of the collections naming while
 * {@link HibernatePersistentCollectionConverter} takes care of the values inside the collections.
 * <p>
 * Reference: <a
 * href="http://jira.codehaus.org/browse/XSTR-226">http://jira.codehaus.org/browse/XSTR-226</a>
 * 
 * @see org.openmrs.module.metadatasharing.serializer.converter.HibernatePersistentCollectionConverter
 * @see org.openmrs.module.metadatasharing.serializer.converter.HibernateMapConverter
 */
public class HibernatePersistentCollectionMapper extends MapperWrapper {
	
	private final Class<?>[] hbClasses = { PersistentList.class, PersistentSet.class, PersistentMap.class,
	        PersistentSortedSet.class, PersistentSortedMap.class };
	
	private final Class<?>[] jdkClasses = { ArrayList.class, HashSet.class, HashMap.class, TreeSet.class, TreeMap.class };
	
	public HibernatePersistentCollectionMapper(Mapper wrapped) {
		super(wrapped);
	}
	
	/**
	 * @see com.thoughtworks.xstream.mapper.Mapper#serializedClass(java.lang.Class)
	 */
	@Override
    public String serializedClass(@SuppressWarnings("rawtypes") Class type) {
		if (PersistentCollection.class.isAssignableFrom(type)) {
			for (int i = 0; i < hbClasses.length; i++) {
				if (type.equals(hbClasses[i])) {
					return super.serializedClass(jdkClasses[i]);
				}
			}
		}
		return super.serializedClass(type);
	}
}
