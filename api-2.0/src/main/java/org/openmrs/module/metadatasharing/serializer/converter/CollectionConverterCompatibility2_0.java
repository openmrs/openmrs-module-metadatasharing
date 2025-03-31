package org.openmrs.module.metadatasharing.serializer.converter;

import org.hibernate.collection.internal.*;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.*;

import org.hibernate.collection.spi.PersistentCollection; 

public class CollectionConverterCompatibility2_0 {

    /**
     * Converts Hibernate persistent collections to regular Java collections before marshaling
     */
    public void marshal(Object source, HierarchicalStreamWriter writer,
                       MarshallingContext context, ConverterLookup converterLookup) {
        
        Object convertedSource = convertHibernateCollection(source);
        
        // delegate the collection to the appropriate converter
        converterLookup.lookupConverterForType(convertedSource.getClass())
            .marshal(convertedSource, writer, context);
    }

    /**
     * Converts Hibernate persistent collections to standard Java collections
     */
    public static Object convertHibernateCollection(Object source) {
        if (source instanceof PersistentBag || source instanceof PersistentList) {
            return new ArrayList<>((Collection<?>) source);
        } else if (source instanceof PersistentMap) {
            return new HashMap<>((Map<?, ?>) source);
        } else if (source instanceof PersistentSortedMap) {
            return new TreeMap<>((SortedMap<?, ?>) source);
        } else if (source instanceof PersistentSortedSet) {
            return new TreeSet<>((SortedSet<?>) source);
        } else if (source instanceof PersistentSet) {
            return new HashSet<>((Set<?>) source);
        }
        return source;
    }

    /**
     * Checks if the object is a Hibernate persistent collection
     */
    public static boolean isHibernateCollection(Object source) {
        return source instanceof PersistentCollection ||
               source instanceof PersistentBag ||
               source instanceof PersistentList ||
               source instanceof PersistentMap ||
               source instanceof PersistentSortedMap ||
               source instanceof PersistentSortedSet ||
               source instanceof PersistentSet;
    }
}