package org.openmrs.module.metadatasharing.serializer.mapper;

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
	
	private CollectionMapperCompatibility mapperCompatibility;
	
	public HibernatePersistentCollectionMapper(Mapper wrapped) {
		super(wrapped);
	}
	
	public void setCollectionMapperCompatibility(CollectionMapperCompatibility mapperCompatibility) {
		this.mapperCompatibility = mapperCompatibility;
	}
	
	/**
	 * @see com.thoughtworks.xstream.mapper.Mapper#serializedClass(java.lang.Class)
	 */
	@Override
    public String serializedClass(@SuppressWarnings("rawtypes") Class type) {
		Class<?> cls = mapperCompatibility.serializedClass(type);
		if (cls != null) {
			return super.serializedClass(cls);
		}
		return super.serializedClass(type);
	}
}
