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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.Item;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.serializer.converter.DateTimeConverter;
import org.openmrs.module.metadatasharing.serializer.converter.DisabledDynamicProxyConverter;
import org.openmrs.module.metadatasharing.serializer.converter.DoubleLocaleUSConverter;
import org.openmrs.module.metadatasharing.serializer.converter.FloatLocaleUSConverter;
import org.openmrs.module.metadatasharing.serializer.converter.HibernatePersistentCollectionConverter;
import org.openmrs.module.metadatasharing.serializer.converter.IntLocaleUSConverter;
import org.openmrs.module.metadatasharing.serializer.converter.LongLocaleUSConverter;
import org.openmrs.module.metadatasharing.serializer.converter.OpenmrsObjectConverter;
import org.openmrs.module.metadatasharing.serializer.converter.ShortLocaleUSConverter;
import org.openmrs.module.metadatasharing.serializer.converter.UserConverter;
import org.openmrs.module.metadatasharing.serializer.mapper.CollectionMapperCompatibility;
import org.openmrs.module.metadatasharing.serializer.mapper.HibernatePersistentCollectionMapper;
import org.openmrs.module.metadatasharing.serializer.mapper.HibernateProxyMapper;
import org.openmrs.module.metadatasharing.serializer.mapper.NonExistigFieldMapper;
import org.openmrs.module.metadatasharing.subscription.SubscriptionHeader;
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * MetadataSerializer serves for serializing and deserializing OpenmrsMetadata.
 * <p>
 * XStream is used for serialization and deserialization of xml files.
 * <p>
 * Usage example:
 * <p>
 * <code>
 * Location location = Context.getLocationService().getLocation(1);</code>
 * <p>
 * <code>
 * String xml = Context.getSerializationService().serialize(location, MetadataSerializer.class);
 * </code>
 * 
 * @see org.openmrs.serialization.OpenmrsSerializer
 * @see org.openmrs.OpenmrsMetadata
 */
@Component(MetadataSharingConsts.MODULE_ID + ".MetadataSerializer")
public class MetadataSerializer implements OpenmrsSerializer {
	
	public static final String FROM_VERSION_CONTEXT = "fromVersion";
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private volatile XStream xstream;
	
	private final Object lock = new Object();
	
	@Autowired
	private CollectionMapperCompatibility mapperCompatibility;
	
	/**
	 * @see org.openmrs.serialization.OpenmrsSerializer#deserialize(java.lang.String,
	 *      java.lang.Class)
	 * @should deserialize number in us locale format
	 * @should not deserialize ENTITY
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(String xml, Class<? extends T> type) throws SerializationException {
		return (T) getXStream().fromXML(xml);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T deserialize(String xml, Class<? extends T> type, String openmrsVersion) throws SerializationException {
		xml = MetadataSharing.getInstance().getConverterEngine()
		        .convert(xml, openmrsVersion, OpenmrsConstants.OPENMRS_VERSION);
		
		return (T) getXStream().fromXML(xml);
	}
	
	/**
	 * @see org.openmrs.serialization.OpenmrsSerializer#serialize(java.lang.Object)
	 * @should not serialize User
	 * @should serialize clone if possible
	 * @should serialize numbers in us locale format
	 */
	@Override
	public String serialize(Object obj) throws SerializationException {
		try {
			return getXStream().toXML(obj);
		}
		catch (Exception e) {
			//TODO: Temporary work-around for META-127
			log.error("Will retry serializing...", e);
			return getXStream().toXML(obj);
		}
	}
	
	/**
	 * @return the xstream
	 */
	public XStream getXStream() {
		if (xstream == null) {
			synchronized (lock) {
				if (xstream == null) {
					setupXstream();
				}
			}
		}
		return xstream;
	}
	
	private void setupXstream() {
		xstream = new XStream(
		                      null, new XppDriver(), OpenmrsClassLoader.getInstance()) {
			
			@Override
			protected MapperWrapper wrapMapper(MapperWrapper next) {
				next = new NonExistigFieldMapper(next);
				next = new HibernateProxyMapper(next);
				next = new HibernatePersistentCollectionMapper(next);
				((HibernatePersistentCollectionMapper)next).setCollectionMapperCompatibility(mapperCompatibility);
				return next;
			}
		};

		xstream.addImmutableType(User.class);
		xstream.registerConverter(new DisabledDynamicProxyConverter(xstream.getMapper()), XStream.PRIORITY_VERY_HIGH);
		xstream.registerConverter(new UserConverter(), XStream.PRIORITY_VERY_HIGH);
		//The proxy converter must not be used, because it breaks displaying subclass entities. It's enough to use a mapper.
		//The important thing is that proxies need to be initialized before serialization. It's done with ObjectVisitor.
		//This way the 'resolves-to' attribute will be used for proxies instead of 'class'.
		//See also META-329.
		//xstream.registerConverter(new HibernateProxyConverter(), XStream.PRIORITY_NORMAL);
		
		HibernatePersistentCollectionConverter collectionConverter = Context.getRegisteredComponent("persistentCollectionConverter", HibernatePersistentCollectionConverter.class);
		collectionConverter.setConverterLookup(xstream.getConverterLookup());

		xstream.registerConverter(collectionConverter, XStream.PRIORITY_NORMAL);
		xstream.registerConverter(new OpenmrsObjectConverter(xstream.getMapper(), xstream.getReflectionProvider()),
		    XStream.PRIORITY_LOW);
		
		xstream.registerConverter(new ShortLocaleUSConverter());
		xstream.registerConverter(new IntLocaleUSConverter());
		xstream.registerConverter(new LongLocaleUSConverter());
		xstream.registerConverter(new FloatLocaleUSConverter());
		xstream.registerConverter(new DoubleLocaleUSConverter());
		
		// we do not need to be so accurate with date classes
		xstream.registerConverter(new DateTimeConverter());
		xstream.alias("date", java.sql.Timestamp.class);
		xstream.alias("date", java.sql.Time.class);
		xstream.alias("date", java.sql.Date.class);
		
		xstream.useAttributeFor(BaseOpenmrsObject.class, "uuid");
		xstream.alias("package", ExportedPackage.class);
		xstream.setMode(XStream.ID_REFERENCES);
		xstream.processAnnotations(new Class[] { SubscriptionHeader.class, Item.class });
		
	}
	
}
