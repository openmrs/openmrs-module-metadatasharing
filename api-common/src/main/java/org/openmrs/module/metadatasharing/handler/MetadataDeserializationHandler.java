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

/**
 * Performs custom deserialization.
 * 
 * @param <T>
 * 
 * @since 1.1
 */
public interface MetadataDeserializationHandler<T> extends MetadataHandler<T> {
	
	/**
	 * Deserializes an object from the given stream.
	 * <p>
	 * No moveUp and moveDown operations must be performed if null is returned.
	 * 
	 * @param deserializer
	 * @return deserialized object or null if no custom deserialization required
	 */
	T deserialize(MetadataDeserializer deserializer);
}
