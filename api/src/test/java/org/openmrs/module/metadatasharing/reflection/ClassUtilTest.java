package org.openmrs.module.metadatasharing.reflection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openmrs.module.metadatasharing.resolver.Resolver;
import org.openmrs.module.metadatasharing.resolver.impl.ObjectByUuidResolver;

public class ClassUtilTest {
	
	/**
	 * @see ClassUtil#getFirstGenericParameter(Class)
	 * @verifies return the first parameter
	 */
	@Test
	public void getFirstGenericParameter_shouldReturnTheFirstParameter() throws Exception {
		Class<?> firstGenericParameter = ClassUtil
		        .getFirstParameterOfGenericType(ObjectByUuidResolver.class, Resolver.class);
		assertEquals(Object.class, firstGenericParameter);
	}
}
