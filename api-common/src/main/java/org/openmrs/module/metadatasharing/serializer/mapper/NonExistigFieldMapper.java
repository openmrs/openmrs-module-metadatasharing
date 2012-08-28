package org.openmrs.module.metadatasharing.serializer.mapper;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Omits fields which are not defined in a class.
 */
public class NonExistigFieldMapper extends MapperWrapper {
	
	public NonExistigFieldMapper(Mapper wrapped) {
		super(wrapped);
	}
	
	@Override
	public boolean shouldSerializeMember(@SuppressWarnings("rawtypes") Class definedIn, String fieldName) {
		if (Object.class.equals(definedIn)) {
			return false;
		} else {
			return super.shouldSerializeMember(definedIn, fieldName);
		}
	}
}
