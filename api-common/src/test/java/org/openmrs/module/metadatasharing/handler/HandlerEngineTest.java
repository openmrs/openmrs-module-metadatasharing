package org.openmrs.module.metadatasharing.handler;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.OpenmrsObject;

public class HandlerEngineTest {
	
	/**
	 * @see DefaultHandlerEngine#findBestMetadataHandler(Class,Map)
	 * @verifies return null if handler not found
	 */
	@Test
	public void findBestMetadataHandler_shouldReturnNullIfHandlerNotFound() throws Exception {
		//given
		Map<Class<?>, MetadataHandler<?>> handlers = new HashMap<Class<?>, MetadataHandler<?>>();
		MetadataHandler<OpenmrsMetadata> openmrsMetadataHandler = new MetadataHandler<OpenmrsMetadata>() {

			@Override
            public int getPriority() {
	            return 0;
            }};
		handlers.put(OpenmrsMetadata.class, openmrsMetadataHandler);
		
		HandlerEngine engine = new HandlerEngine();
		engine.setHandlers(handlers.values());
		
		//when
		MetadataHandler<?> handler = engine.findBestMetadataHandler(Concept.class, handlers);
		
		//then
		Assert.assertNull(handler);
	}
	
	/**
	 * @see DefaultHandlerEngine#findBestMetadataHandler(Class,Map)
	 * @verifies return the most specific handler
	 */
	@Test
	public void findBestMetadataHandler_shouldReturnTheMostSpecificHandler() throws Exception {
		//given
		Map<Class<?>, MetadataHandler<?>> handlers = new HashMap<Class<?>, MetadataHandler<?>>();
		MetadataHandler<OpenmrsMetadata> openmrsMetadataHandler = new MetadataHandler<OpenmrsMetadata>() {

			@Override
            public int getPriority() {
	            return 0;
            }};
		MetadataHandler<OpenmrsObject> openmrsObjectHandler = new MetadataHandler<OpenmrsObject>() {

			@Override
            public int getPriority() {
	            return 0;
            }};
		MetadataHandler<Concept> conceptHandler = new MetadataHandler<Concept>() {

			@Override
            public int getPriority() {
	            return 0;
            }};
		handlers.put(OpenmrsMetadata.class, openmrsMetadataHandler);
		handlers.put(OpenmrsObject.class, openmrsObjectHandler);
		handlers.put(Concept.class, conceptHandler);
		
		HandlerEngine engine = new HandlerEngine();
		engine.setHandlers(handlers.values());
		
		//when
		MetadataHandler<?> foundConceptHandler = engine.findBestMetadataHandler(Concept.class, handlers);
		MetadataHandler<?> foundLocationHandler = engine.findBestMetadataHandler(Location.class, handlers);
		
		//then
		Assert.assertEquals("Should return Concept handler", conceptHandler, foundConceptHandler);
		Assert.assertEquals("Should return OpenmrsMetadata handler", openmrsMetadataHandler, foundLocationHandler);
	}
}
