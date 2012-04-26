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
package org.openmrs.module.metadatasharing.handler.impl;

import java.util.List;

import junit.framework.Assert;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.handler.BaseHandlersTest;


public class ConceptHandlersFunctionalTest extends BaseHandlersTest<Concept> {

	@Override
	public Concept getNewItem(){
		return new Concept();
	}
	
	@Override
	public Concept getExistingItem(){
		return Context.getConceptService().getConcept(3);
	}
	
	@Override
	public Concept getUpdatedItem(){
		Concept concept = getExistingItem();
		concept.setRetired(true);
		concept.setRetireReason("reason");
		return concept;
	}
	
	@Override
	public void assertExistingSameAsUpdated(Concept item){
		Concept concept = (Concept)item;
		Assert.assertTrue(concept.isRetired());
		Assert.assertEquals("reason", concept.getRetireReason());
	}
	
	@Override
	public List<Concept> getAllItems(){
		return Context.getConceptService().getAllConcepts();
	}
}
