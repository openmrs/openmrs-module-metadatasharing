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
package org.openmrs.module.metadatasharing.reflection;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptDatatype;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.PersonAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Component;

/**
 * Reflection utilities to search the classpath for OpenmrsMetadata classes and corresponding
 * services.
 * <p>
 * Search results are cached with a time-out set to {@link #CACHE_TIMEOUT_IN_MS}.
 */
@Component(MetadataSharingConsts.MODULE_ID + ".OpenmrsClassScanner")
public class OpenmrsClassScanner {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private static final OpenmrsClassScanner instance = new OpenmrsClassScanner();
	
	private final MetadataReaderFactory metadataReaderFactory;
	
	private final ResourcePatternResolver resourceResolver;
	
	private final ClassLoader classLoader;
	
	private Map<Class<?>, ClassMethod<OpenmrsService>> serviceSaveMethodsCache;
	
	private List<Class<OpenmrsMetadata>> metadataTypesCache;
	
	private Date metadataTypesCacheTimeout;
	
	private Date serviceSaveMethodsCacheTimeout;
	
	private long CACHE_TIMEOUT_IN_MS = 600000;
	
	private class ClassMethod<T> {
		
		public final Class<? extends T> service;
		
		public final Method method;
		
		public ClassMethod(Class<? extends T> service, Method method) {
			this.service = service;
			this.method = method;
		}
	}
	
	OpenmrsClassScanner() {
		this.classLoader = OpenmrsClassLoader.getInstance();
		this.metadataReaderFactory = new SimpleMetadataReaderFactory(classLoader);
		this.resourceResolver = new PathMatchingResourcePatternResolver(classLoader);
	}
	
	/**
	 * @return the instance
	 */
	public static OpenmrsClassScanner getInstance() {
		return instance;
	}
	
	/**
	 * Searches for classes extending or implementing the given type.
	 * 
	 * @param <T>
	 * @param type
	 * @param concrete true if only concrete classes should be returned
	 * @return the list of found classes
	 * @throws IOException
	 */
	public <T> List<Class<T>> getClasses(Class<? extends T> type, boolean concrete) throws IOException {
		Set<Class<T>> types = new LinkedHashSet<Class<T>>();
		String pattern = "classpath*:org/openmrs/**/*.class";
		Resource[] resources = resourceResolver.getResources(pattern);
		TypeFilter typeFilter = new AssignableTypeFilter(type);
		for (Resource resource : resources) {
			try {
				MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
				if (typeFilter.match(metadataReader, metadataReaderFactory)) {
					if (concrete == metadataReader.getClassMetadata().isConcrete()) {
						String classname = metadataReader.getClassMetadata().getClassName();
						try {
							@SuppressWarnings("unchecked")
							Class<T> metadata = (Class<T>) OpenmrsClassLoader.getInstance().loadClass(classname);
							types.add(metadata);
						}
						catch (ClassNotFoundException e) {
							throw new IOException("Class cannot be loaded: " + classname, e);
						}
					}
				}
			}
			catch (IOException e) {
				log.debug("Resource cannot be loaded: " + resource);
			}
		}
		return new ArrayList<Class<T>>(types);
	}
	
	/**
	 * Searches for classes implementing {@link OpenmrsMetadata}.
	 * <p>
	 * The search result is cached for {@link #CACHE_TIMEOUT_IN_MS}.
	 * 
	 * @return the list of classes
	 * @throws IOException
	 */
	public List<Class<OpenmrsMetadata>> getOpenmrsMetadataClasses() throws IOException {
		if (metadataTypesCache == null || metadataTypesCacheTimeout.before(new Date())) {
			log.debug("Refreshing OpenmrsMetadata classes cache");
			long future = new Date().getTime();
			future += CACHE_TIMEOUT_IN_MS;
			metadataTypesCacheTimeout = new Date(future);
			metadataTypesCache = getClasses(OpenmrsMetadata.class, true);
			for (Iterator<Class<OpenmrsMetadata>> i = metadataTypesCache.iterator(); i.hasNext(); ) {
				if (!canBeSaved(i.next())) {
					i.remove();
				}
			}
		}
		return new ArrayList<Class<OpenmrsMetadata>>(metadataTypesCache);
	}
	
	/**
	 * Invokes service's save method for the given item.
	 * <p>
	 * The list of service classes is cached for {@link #CACHE_TIMEOUT_IN_MS}. It is always the
	 * first matching save method that is invoked. If there is a match for the given item, the
	 * persisted item is returned, otherwise <code>null</code> is returned.
	 * 
	 * @param <T>
	 * @param item
	 * @return the persisted item or <code>null</code> if the item was not saved
	 * @throws IOException
	 */
	public <T> T serviceSaveItem(T item) throws IOException {
		initServiceSaveMethodsCache();
		
		T result = invokeServiceSaveItem(item);
		if (result != null) {
			return result;
		}
		return null;
	}
	
	public boolean hasServiceSaveMethod(Class<?> type) throws IOException {
		initServiceSaveMethodsCache();
		return (serviceSaveMethodsCache.get(type) != null);
	}
	
	protected void initServiceSaveMethodsCache() throws IOException {
		if (serviceSaveMethodsCache != null && serviceSaveMethodsCacheTimeout.after(new Date())) {
			return;
		}
		log.debug("Refreshing OpenmrsService classes cache");
		long future = new Date().getTime();
		future += CACHE_TIMEOUT_IN_MS;
		serviceSaveMethodsCacheTimeout = new Date(future);
		serviceSaveMethodsCache = new HashMap<Class<?>, ClassMethod<OpenmrsService>>();
		
		List<Class<OpenmrsService>> services = getClasses(OpenmrsService.class, false);
		for (Class<OpenmrsService> service : services) {
			Method[] methods = service.getMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("save")) {
					if (Arrays.equals(method.getParameterTypes(), new Class<?>[] { method.getReturnType() })) {
						if (canBeSaved(method.getReturnType())) {
							serviceSaveMethodsCache
								.put(method.getReturnType(), new ClassMethod<OpenmrsService>(service, method));
						}
					}
				}
			}
		}
		//META-114 HACK
		try {
			serviceSaveMethodsCache.put(
			    PersonAttributeType.class,
			    new ClassMethod(PersonService.class, PersonService.class.getMethod("savePersonAttributeType",
			        PersonAttributeType.class)));
			serviceSaveMethodsCache.put(
			    RelationshipType.class,
			    new ClassMethod(PersonService.class, PersonService.class.getMethod("saveRelationshipType",
			        RelationshipType.class)));
		}
		catch (Exception ex) {}
	}
	
	/**
     * Currently this only handles the case where starting in OpenMRS 1.9 you are no longer allowed to modify
     * a ConceptDatatype.
     * 
     * @param clazz
     * @return true for all cases except ConceptDatatype on OpenMRS 1.9+
     */
    private boolean canBeSaved(Class<?> clazz) {
    	if (clazz.equals(ConceptDatatype.class) && ModuleUtil.compareVersion(OpenmrsConstants.OPENMRS_VERSION_SHORT, "1.9") >= 0) {
    		return false;
    	} else {
    		return true;
    	}
    }

	protected <T> T invokeServiceSaveItem(T item) throws APIException {
		ClassMethod<OpenmrsService> classMethod = serviceSaveMethodsCache.get(item.getClass());
		if (classMethod == null) {
			classMethod = serviceSaveMethodsCache.get(item.getClass().getSuperclass());
		}
		
		if (classMethod != null) {
			log.debug("Getting service " + classMethod.service.getName());
			
			//try block is for META-114 -- casting to OpenmrsService fails because PersonService is not an OpenmrsService
			OpenmrsService openmrsService = null;
			try {
				openmrsService = Context.getService(classMethod.service);
			}
			catch (Exception ex) {
				if (item.getClass().equals(PersonAttributeType.class) || item.getClass().equals(RelationshipType.class)) {
					try {
						@SuppressWarnings("unchecked")
						T result = (T) classMethod.method.invoke(Context.getPersonService(), item);
						log.debug("Invoking " + classMethod.method + "() suceeded");
						return result;
					}
					catch (Exception exInner) {
						throw new APIException("Cannot invoke " + classMethod.method + "()", exInner);
					}
				}
			}
			if (openmrsService != null) {
				log.debug("Getting service " + classMethod.service.getName() + " suceeded");
				try {
					log.debug("Invoking " + classMethod.method + "() in service " + classMethod.method.getName());
					@SuppressWarnings("unchecked")
					T result = (T) classMethod.method.invoke(openmrsService, item);
					log.debug("Invoking " + classMethod.method + "() suceeded");
					return result;
				}
				catch (IllegalArgumentException e) {
					throw new APIException("Cannot invoke " + classMethod.method + "()", e);
				}
				catch (IllegalAccessException e) {
					throw new APIException("Cannot invoke " + classMethod.method + "()", e);
				}
				catch (InvocationTargetException e) {
					throw new APIException("Cannot save " + item.toString() + ". Failed with the following exception:\n\n"
					        + ExceptionUtils.getFullStackTrace(e));
				}
			} else {
				log.debug("Getting service " + classMethod.service.getName() + " not suceeded");
			}
		}
		return null;
	}
}
