package org.openmrs.module.metadatasharing.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * Provides utility methods for {@link Class}.
 */
public abstract class ClassUtil {
	
	/**
	 * Loads the class with <code>OpenmrsClassLoader</code>.
	 * 
	 * @see org.openmrs.util.OpenmrsClassLoader
	 * @return the class or null if it is not present
	 */
	public static Class<?> loadClass(String name) {
		OpenmrsClassLoader classLoader = OpenmrsClassLoader.getInstance();
		Class<?> loadedClass = null;
		try {
			loadedClass = classLoader.loadClass(name);
		}
		catch (ClassNotFoundException e) {
			LogFactory.getLog(ClassUtil.class).debug("Class is not present on the classpath: " + name);
		}
		return loadedClass;
	}
	
	/**
	 * Returns the class of the given object. It strips off proxies.
	 * 
	 * @param object
	 * @return the class
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getDeproxiedClass(T object) {
		return Hibernate.getClass(object);
	}
	
	/**
	 * Strips off proxies.
	 * 
	 * @param clazz
	 * @return the class
	 * @should handle cglib generated proxy names
	 * @should handle javassist generated proxy names
	 */
	public static Class<?> getDeproxiedClass(Class<?> clazz) {
		String name = clazz.getName();
		// first we look for javassist proxies like "org.openmrs.ConceptDatatype_$$_javassist_65"
		int proxySuffix = name.indexOf("_$");
		// next we look for cglib proxies like "org.openmrs.ConceptDatatype$$EnhancerByCGLIB$$3c710f29"
		if (proxySuffix < 0)
			proxySuffix = name.indexOf("$");
		if (proxySuffix > 0) {
			name = name.substring(0, proxySuffix);
			return loadClass(name);
		} else {
			return clazz;
		}
	}
	
	/**
	 * Gets the first parameter of the given generic type that is implemented by the given type.
	 * 
	 * @param inType
	 * @param genericType
	 * @return the type or <code>null</code>
	 */
	public static Class<?> getFirstParameterOfGenericType(Class<?> inType, Class<?> genericType) {
		Class<?> supportedType = null;
		
		List<Type> implementedTypes = new ArrayList<Type>();
		implementedTypes.addAll(Arrays.asList(inType.getGenericInterfaces()));
		if (inType.getGenericSuperclass() != null) {
			implementedTypes.add(inType.getGenericSuperclass());
		}
		
		for (Type implementedType : implementedTypes) {
			if (implementedType instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) implementedType;
				if (genericType.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
					if (parameterizedType.getActualTypeArguments()[0] instanceof Class) {
						supportedType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
						break;
					}
				}
			}
		}
		
		return supportedType;
	}
}
