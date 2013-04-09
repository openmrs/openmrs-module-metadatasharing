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
package org.openmrs.module.metadatasharing.visitor;

/**
 * Allows to traverse objects.
 * <p>
 * It is possible to visit each field of an object using a call-back method by implementing
 * {@link FieldVisitor}.
 * 
 * @see ObjectVisitorFactory
 * @see FieldVisitor
 */
public interface ObjectVisitor {
	
	/**
	 * Allows to traverse fields of an object.
	 */
	interface FieldVisitor {
		
		/**
		 * Call-back for each visit.
		 * 
		 * @param fieldName the field name
		 * @param type the class of the field
		 * @param definedIn the class where the field is defined
		 * @param value the value of the field
		 */
		void visit(String fieldName, Class<?> type, Class<?> definedIn, Object value);
	}
	
	/**
	 * Visits fields of the given object.
	 * 
	 * @param object the object to be visited
	 * @param writeReplace whether a writeReplace method of the object should be called before
	 *            visiting object's fields
	 * @param deproxy strips off proxies in visited fields
	 * @param visitor the field visitor
	 */
	void visitFields(Object object, boolean writeReplace, FieldVisitor visitor);
	
	/**
	 * Writes the given value into the specified field.
	 * 
	 * @param object the object
	 * @param fieldName the field name
	 * @param value the new value
	 * @param definedIn the class where the field is defined
	 */
	void writeField(Object object, String fieldName, Object value, Class<?> definedIn);
	
	/**
	 * Copies fields different than Collection and Map.
	 * 
	 * @param source
	 * @param target
	 */
	void copyFields(Object source, Object target);

	public abstract Object readField(Object object, String fieldName, Class<?> definedIn);
}
