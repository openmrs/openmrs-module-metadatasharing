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
package org.openmrs.module.metadatasharing.resolver;


/**
 * It can be implemented to find a match for the given item
 * 
 * @see ResolverEngine
 */
public abstract class Resolver<T> {
	
	public int getPriority() {
		return 50;
	}
	
	public abstract T getExactMatch(T incoming);
	
	public abstract T getPossibleMatch(T incoming);
}
