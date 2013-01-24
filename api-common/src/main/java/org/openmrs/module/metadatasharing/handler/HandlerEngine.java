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
package org.openmrs.module.metadatasharing.handler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.reflection.ClassUtil;
import org.springframework.stereotype.Service;

/**
 * Allows to access {@link MetadataHandler}s.
 * <p>
 * Handlers are Spring managed beans. It is enough to annotate a handler with
 * <code>@Component</code> in order for it to be picked up automatically by {@link HandlerEngine}.
 * <p>
 * There may be only one handler for a type, however you may always overwrite a handler of the
 * chosen type using the same bean name in the Spring configuration.
 * <p>
 * The most specific handler will be always returned.
 * 
 * @see MetadataPropertiesHandler
 * @see MetadataSearchHandler
 * @see MetadataSaveHandler
 * @see MetadataPriorityDependenciesHandler
 */
@Service(MetadataSharingConsts.MODULE_ID + ".HandlerEngine")
public class HandlerEngine {
	
	private Map<Class<?>, MetadataHandler<?>> typesHandlers;
	
	private Map<Class<?>, MetadataHandler<?>> propertiesHandlers;
	
	private Map<Class<?>, MetadataHandler<?>> searchHandlers;
	
	private Map<Class<?>, MetadataHandler<?>> saveHandlers;
	
	private Map<Class<?>, MetadataHandler<?>> priorityDependenciesHandlers;
	
	private Map<Class<?>, MetadataHandler<?>> mergeHandlers;
	
	private Map<Class<?>, String> classes;
	
	private Map<String, Class<?>> types;
	
	private volatile Collection<MetadataHandler<?>> handlers;
	
	private volatile boolean initialized;
	
	private Object initLock = new Object();
	
	/**
	 * Returns a map of types to be displayed to the user.
	 * 
	 * @return the set of types
	 */
	public Map<String, Class<?>> getRegisteredTypes() {
		initHandlerEngine();
		
		return types;
	}
	
	/**
	 * Returns a class for the given type.
	 * 
	 * @param type
	 * @return the type
	 */
	public Class<?> getRegisteredClass(String type) {
		initHandlerEngine();
		
		return types.get(type);
	}
	
	/**
	 * Returns a class of the given object as listed in {@link #getRegisteredTypes()}.
	 * 
	 * @param object
	 * @return the class
	 */
	public Class<?> getRegisteredClass(Object object) {
		Class<Object> clazz = ClassUtil.getDeproxiedClass(object);
		return getRegisteredClass(clazz);
	}
	
	/**
	 * Returns a class for the given clazz as listed in {@link #getRegisteredTypes()}.
	 * 
	 * @param clazz
	 * @return the class
	 */
	public Class<?> getRegisteredClass(Class<?> clazz) {
		initHandlerEngine();
		
		String type = classes.get(clazz);
		if (type != null) {
			return clazz;
		}
		
		Entry<Class<?>, String> bestEntry = findBestEntry(clazz);
		
		if (bestEntry != null) {
			return bestEntry.getKey();
		} else {
			return null;
		}
	}
	
	/**
	 * Returns a user-friendly name of the class of the given object.
	 * 
	 * @param object
	 * @return the type
	 */
	public String getRegisteredType(Object object) {
		Class<Object> clazz = ClassUtil.getDeproxiedClass(object);
		return getRegisteredType(clazz);
	}
	
	/**
	 * Returns a user-friendly name of the given clazz.
	 * 
	 * @param clazz
	 * @return the type
	 */
	public String getRegisteredType(Class<?> clazz) {
		initHandlerEngine();
		
		String type = classes.get(clazz);
		if (type != null) {
			return type;
		}
		
		Entry<Class<?>, String> bestEntry = findBestEntry(clazz);
		
		if (bestEntry != null) {
			return bestEntry.getValue();
		} else {
			return null;
		}
	}
	
	/**
	 * Tests if the given given object is to be displayed to the user.
	 * 
	 * @param object
	 * @return <code>true</code> if the given object is hidden
	 */
	public boolean isHidden(Object object) {
		return StringUtils.isBlank(getRegisteredType(object));
	}
	
	/**
	 * Initializes properties of the given object.
	 * 
	 * @param object
	 */
	public void initObject(Object object) {
		MetadataPropertiesHandler<Object> handler = getPropertiesHandler(object);
		handler.getId(object);
		handler.getUuid(object);
		handler.getName(object);
		handler.getDescription(object);
		handler.getDateChanged(object);
		handler.getProperties(object);
	}
	
	/**
	 * @see MetadataPropertiesHandler
	 */
	public <T> MetadataPropertiesHandler<T> getPropertiesHandler(T object) throws HandlerNotFoundException {
		initHandlerEngine();
		
		Class<T> type = ClassUtil.getDeproxiedClass(object);
		
		@SuppressWarnings("unchecked")
		MetadataPropertiesHandler<T> handler = (MetadataPropertiesHandler<T>) findBestMetadataHandler(type,
		    propertiesHandlers);
		
		if (handler == null) {
			throw new HandlerNotFoundException("Handler for " + type + " not found");
		}
		return handler;
	}
	
	/**
	 * @see MetadataSearchHandler
	 */
	public <T> MetadataSearchHandler<T> getSearchHandler(Class<? extends T> type) throws HandlerNotFoundException {
		initHandlerEngine();
		
		Class<?> clazz = ClassUtil.getDeproxiedClass(type);
		
		@SuppressWarnings("unchecked")
		MetadataSearchHandler<T> handler = (MetadataSearchHandler<T>) findBestMetadataHandler(clazz, searchHandlers);
		
		if (handler == null) {
			throw new HandlerNotFoundException("Handler for " + clazz + " not found");
		}
		return handler;
	}
	
	/**
	 * @see MetadataSearchHandler
	 */
	public <T> MetadataSearchHandler<T> getSearchHandler(T object) throws HandlerNotFoundException {
		Class<T> type = ClassUtil.getDeproxiedClass(object);
		return getSearchHandler(type);
	}
	
	/**
	 * @see MetadataSaveHandler
	 */
	public <T> MetadataSaveHandler<T> getSaveHandler(T object) throws HandlerNotFoundException {
		initHandlerEngine();
		
		Class<T> type = ClassUtil.getDeproxiedClass(object);
		
		@SuppressWarnings("unchecked")
		MetadataSaveHandler<T> handler = (MetadataSaveHandler<T>) findBestMetadataHandler(type, saveHandlers);
		
		if (handler == null) {
			throw new HandlerNotFoundException("Handler for " + type + " not found");
		}
		return handler;
	}
	
	/**
	 * @see MetadataPriorityDependenciesHandler
	 */
	public <T> MetadataPriorityDependenciesHandler<T> getPriorityDependenciesHandler(T object)
	    throws HandlerNotFoundException {
		initHandlerEngine();
		
		Class<T> type = ClassUtil.getDeproxiedClass(object);
		
		@SuppressWarnings("unchecked")
		MetadataPriorityDependenciesHandler<T> handler = (MetadataPriorityDependenciesHandler<T>) findBestMetadataHandler(
		    type, priorityDependenciesHandlers);
		
		if (handler == null) {
			throw new HandlerNotFoundException("Handler for " + type + " not found");
		}
		return handler;
	}
	
	public boolean hasPriorityDependenciesHandler(Object object) {
		initHandlerEngine();
		
		Class<?> type = ClassUtil.getDeproxiedClass(object);
		
		MetadataHandler<?> handler = findBestMetadataHandler(type, priorityDependenciesHandlers);
		
		return (handler != null);
	}
	
	/**
	 * @see MetadataMergeHandler
	 */
	public <T> MetadataMergeHandler<T> getMergeHandler(T object) throws HandlerNotFoundException {
		initHandlerEngine();
		
		Class<T> type = ClassUtil.getDeproxiedClass(object);
		
		@SuppressWarnings("unchecked")
		MetadataMergeHandler<T> handler = (MetadataMergeHandler<T>) findBestMetadataHandler(type, mergeHandlers);
		
		if (handler == null) {
			throw new HandlerNotFoundException("Handler for " + type + " not found");
		}
		return handler;
	}
	
	/**
	 * Finds a supported type of the given handler
	 * 
	 * @param handlerType
	 * @param handler
	 * @return the type
	 */
	Class<?> findSupportedType(Class<?> handlerType, MetadataHandler<?> handler) {
		Class<?> supportedType = null;
		
		List<Type> types = new ArrayList<Type>();
		types.addAll(Arrays.asList(handler.getClass().getGenericInterfaces()));
		if (handler.getClass().getGenericSuperclass() != null) {
			types.add(handler.getClass().getGenericSuperclass());
		}
		
		for (Type type : types) {
			if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				if (handlerType.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
					if (parameterizedType.getActualTypeArguments()[0] instanceof Class) {
						supportedType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
						break;
					}
				}
			}
		}
		
		if (supportedType == null) {
			throw new IllegalArgumentException(handler.getClass()
			        + " must implement handler interface directly or inherit from a single generic superclass");
		}
		
		return supportedType;
	}
	
	/**
	 * Finds the most specific handler for the given type.
	 * 
	 * @param type
	 * @param handlers
	 * @return the MetadataHandler
	 * @should return null if handler not found
	 * @should return the most specific handler
	 */
	MetadataHandler<?> findBestMetadataHandler(Class<?> type, Map<Class<?>, MetadataHandler<?>> handlers) {
		MetadataHandler<?> handler = handlers.get(type);
		
		if (handler != null) {
			return handler;
		}
		
		Entry<Class<?>, MetadataHandler<?>> bestHandler = null;
		for (Entry<Class<?>, MetadataHandler<?>> eachHandler : handlers.entrySet()) {
			Class<?> clazz = eachHandler.getKey();
			if (clazz.isAssignableFrom(type)) {
				//test if it is more specific than the best match so far
				if (bestHandler == null || bestHandler.getKey().isAssignableFrom(clazz)) {
					bestHandler = eachHandler;
				}
			}
		}
		
		if (bestHandler != null) {
			handlers.put(type, bestHandler.getValue());
			return bestHandler.getValue();
		} else {
			return null;
		}
	}
	
	/**
	 * Finds an entry with the most specific class.
	 * 
	 * @param clazz
	 * @return the best entry
	 */
	Entry<Class<?>, String> findBestEntry(Class<?> clazz) {
		Entry<Class<?>, String> bestEntry = null;
		for (Entry<Class<?>, String> entry : classes.entrySet()) {
			if (entry.getKey().isAssignableFrom(clazz)) {
				if (bestEntry == null || bestEntry.getKey().isAssignableFrom(entry.getKey())) {
					bestEntry = entry;
				}
			}
		}
		return bestEntry;
	}
	
	private void initHandlerEngine() {
		if (!initialized) {
			synchronized (initLock) {
				if (!initialized) {
					setupHandlers();
					initialized = true;
				}
			}
		}
	}
	
	/**
	 * @return the handlers
	 */
	private Collection<MetadataHandler<?>> getHandlers() {
		if (handlers == null) {
			handlers = new ArrayList<MetadataHandler<?>>();
			@SuppressWarnings("rawtypes")
			List<MetadataHandler> registeredComponents = Context.getRegisteredComponents(MetadataHandler.class);
			
			for (MetadataHandler<?> registeredComponent : registeredComponents) {
				handlers.add(registeredComponent);
			}
		}
		return handlers;
	}
	
	/**
	 * @param handlers the handlers to set
	 */
	void setHandlers(Collection<MetadataHandler<?>> handlers) {
		this.handlers = handlers;
	}
	
	private void setupHandlers() {
		typesHandlers = new ConcurrentHashMap<Class<?>, MetadataHandler<?>>();
		propertiesHandlers = new ConcurrentHashMap<Class<?>, MetadataHandler<?>>();
		searchHandlers = new ConcurrentHashMap<Class<?>, MetadataHandler<?>>();
		saveHandlers = new ConcurrentHashMap<Class<?>, MetadataHandler<?>>();
		priorityDependenciesHandlers = new ConcurrentHashMap<Class<?>, MetadataHandler<?>>();
		mergeHandlers = new ConcurrentHashMap<Class<?>, MetadataHandler<?>>();
		
		for (MetadataHandler<?> handler : getHandlers()) {
			if (handler instanceof MetadataTypesHandler) {
				Class<?> type = findSupportedType(MetadataTypesHandler.class, handler);
				
				MetadataHandler<?> previousHandler = typesHandlers.get(type);
				if (previousHandler == null || previousHandler.getPriority() < handler.getPriority()) {
					typesHandlers.put(type, handler);
				}
			}
			
			if (handler instanceof MetadataPropertiesHandler) {
				Class<?> type = findSupportedType(MetadataPropertiesHandler.class, handler);
				
				MetadataHandler<?> previousHandler = propertiesHandlers.get(type);
				if (previousHandler == null || previousHandler.getPriority() < handler.getPriority()) {
					propertiesHandlers.put(type, handler);
				}
			}
			
			if (handler instanceof MetadataSearchHandler) {
				Class<?> type = findSupportedType(MetadataSearchHandler.class, handler);
				
				MetadataHandler<?> previousHandler = searchHandlers.get(type);
				if (previousHandler == null || previousHandler.getPriority() < handler.getPriority()) {
					searchHandlers.put(type, handler);
				}
			}
			
			if (handler instanceof MetadataSaveHandler) {
				Class<?> type = findSupportedType(MetadataSaveHandler.class, handler);
				
				MetadataHandler<?> previousHandler = saveHandlers.get(type);
				if (previousHandler == null || previousHandler.getPriority() < handler.getPriority()) {
					saveHandlers.put(type, handler);
				}
			}
			
			if (handler instanceof MetadataPriorityDependenciesHandler) {
				Class<?> type = findSupportedType(MetadataPriorityDependenciesHandler.class, handler);
				
				MetadataHandler<?> previousHandler = priorityDependenciesHandlers.get(type);
				if (previousHandler == null || previousHandler.getPriority() < handler.getPriority()) {
					priorityDependenciesHandlers.put(type, handler);
				}
			}
			
			if (handler instanceof MetadataMergeHandler) {
				Class<?> type = findSupportedType(MetadataMergeHandler.class, handler);
				
				MetadataHandler<?> previousHandler = mergeHandlers.get(type);
				if (previousHandler == null || previousHandler.getPriority() < handler.getPriority()) {
					mergeHandlers.put(type, handler);
				}
			}
		}
		
		classes = new HashMap<Class<?>, String>();
		types = new HashMap<String, Class<?>>();
		
		for (MetadataHandler<?> handler :  typesHandlers.values()) {
			MetadataTypesHandler<?> typeHandler = (MetadataTypesHandler<?>) handler;
			
			for (Entry<?, String> entry : typeHandler.getTypes().entrySet()) {
				Class<?> clazz = (Class<?>) entry.getKey();
				String type = entry.getValue();
				
				MetadataTypesHandler<?> bestTypeHandler = (MetadataTypesHandler<?>) findBestMetadataHandler(clazz,
				    typesHandlers);
				
				if (handler.equals(bestTypeHandler)) {
					classes.put(clazz, type);
				} else {
					String bestType = bestTypeHandler.getTypes().get(clazz);
					if (bestType != null) {
						classes.put(clazz, bestType);
					} else {
						Class<?> supportedBestType = findSupportedType(MetadataTypesHandler.class, bestTypeHandler);
						bestType = bestTypeHandler.getTypes().get(supportedBestType);
						if (bestType == null) {
							bestType = supportedBestType.getSimpleName();
						}
						classes.put(supportedBestType, bestType);
					}
				}
			}
		}
		
		for (Entry<Class<?>, String> classEntry : classes.entrySet()) {
			if (StringUtils.isBlank(classEntry.getValue())) {
				continue;
			}
			
			Class<?> previousClass = types.put(classEntry.getValue(), classEntry.getKey());
			if (previousClass != null) {
				throw new IllegalStateException(classEntry.getValue() + " must not represent more than one class. Found "
				        + classEntry.getKey() + " and " + previousClass);
			}
		}
	}
	
}
