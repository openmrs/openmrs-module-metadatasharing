package org.openmrs.module.metadatasharing.serializer.converter;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.hibernate.collection.PersistentBag;
import org.hibernate.collection.PersistentList;
import org.hibernate.collection.PersistentMap;
import org.hibernate.collection.PersistentSet;
import org.hibernate.collection.PersistentSortedMap;
import org.hibernate.collection.PersistentSortedSet;
import org.hibernate.engine.SessionImplementor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;

/**
 * Tests if hibernate collections are converted properly
 *
 * @see CollectionConverterCompatibility1_9
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class CollectionConverterCompatibility19Test {
	
	@Mock
	ConverterLookup converterLookup;
	@Mock
	Converter converter;
	@Mock
	HierarchicalStreamWriter writer;
	@Mock
	MarshallingContext context;
	
	@Mock
	SessionImplementor session;
	Integer key = 3;
	Integer value = 2;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(converterLookup.lookupConverterForType(any(Class.class)))
				.thenReturn(converter);
	}
	
	@Test
	public void marshal_PersistentList() {
		PersistentList source = PowerMockito.mock(PersistentList.class);
		when(source.toArray())
				.thenReturn(new Object[]{value});
		
		new CollectionConverterCompatibility1_9().marshal(source, writer, context, converterLookup);
		
		verify(converterLookup).lookupConverterForType(ArrayList.class);
		verify(converter).marshal(argThat(hasItem(value)),
				same(writer), same(context));
	}
	
	@Test
	public void marshal_PersistentBag() {
		PersistentBag source = PowerMockito.mock(PersistentBag.class);
		when(source.toArray()).thenReturn(new Object[]{value});
		
		new CollectionConverterCompatibility1_9().marshal(source, writer, context, converterLookup);
		
		verify(converterLookup).lookupConverterForType(ArrayList.class);
		verify(converter).marshal(argThat(hasItem(value)),
				same(writer), same(context));
	}
	
	@Test
	public void marshal_PersistentSortedMap() {
		SortedMap backingMap = new TreeMap();
		backingMap.put(key, value);
		PersistentSortedMap source = new PersistentSortedMap(session, backingMap);
		
		new CollectionConverterCompatibility1_9().marshal(source, writer, context, converterLookup);
		
		verify(converterLookup).lookupConverterForType(TreeMap.class);
		verify(converter).marshal(argThat(hasEntry(key, value)),
				same(writer), same(context));
	}
	
	@Test
	public void marshal_PersistentMap() {
		Map backingMap = new HashMap();
		backingMap.put(key, value);
		PersistentMap source = new PersistentMap(session, backingMap);
		
		new CollectionConverterCompatibility1_9().marshal(source, writer, context, converterLookup);
		
		verify(converterLookup).lookupConverterForType(HashMap.class);
		verify(converter).marshal(argThat(hasEntry(key, value)),
				same(writer), same(context));
	}
	
	@Test
	public void marshal_PersistentSortedSet() {
		SortedSet backingSet = new TreeSet();
		backingSet.add(value);
		PersistentSortedSet source =
				new PersistentSortedSet(session, backingSet);
		
		new CollectionConverterCompatibility1_9().marshal(source, writer, context, converterLookup);
		
		verify(converterLookup).lookupConverterForType(TreeSet.class);
		verify(converter).marshal(argThat(hasItem(value)),
				same(writer), same(context));
	}
	
	@Test
	public void marshal_PersistentSet() {
		Set backingSet = new HashSet();
		backingSet.add(value);
		PersistentSet source = new PersistentSet(session, backingSet);
		
		new CollectionConverterCompatibility1_9().marshal(source, writer, context, converterLookup);
		
		verify(converterLookup).lookupConverterForType(HashSet.class);
		verify(converter).marshal(argThat(hasItem(value)),
				same(writer), same(context));
	}
	
	@Test
	public void marshal_nonPersistentCollection() {
		Set source = new HashSet();
		source.add(value);
		
		new CollectionConverterCompatibility1_9().marshal(source, writer, context, converterLookup);
		
		verify(converterLookup).lookupConverterForType(HashSet.class);
		verify(converter).marshal(same(source), same(writer), same(context));
	}
}
