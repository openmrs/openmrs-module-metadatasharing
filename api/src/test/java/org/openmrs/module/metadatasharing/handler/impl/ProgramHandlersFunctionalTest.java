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

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.handler.BaseHandlersTest;


public class ProgramHandlersFunctionalTest extends BaseHandlersTest<Program> {

	@Override
	public Program getNewItem(){
		Program program = new Program();
		program.setName("name");
		program.setDescription("description");
		program.setConcept(Context.getConceptService().getAllConcepts().get(0));
		return program;
	}
	
	@Override
	public Program getExistingItem(){
		return Context.getProgramWorkflowService().getProgram(1);
	}
	
	@Override
	public Program getUpdatedItem(){
		Program program = getExistingItem();
		program.setRetired(true);
		program.setRetireReason("reason");
		return program;
	}
	
	@Override
	public void assertExistingSameAsUpdated(Program item){
		Program program = (Program)item;
		Assert.assertTrue(program.isRetired());
		Assert.assertEquals("reason", program.getRetireReason());
	}
	
	@Override
	public List<Program> getAllItems(){
		return Context.getProgramWorkflowService().getAllPrograms();
	}
	
	@Override
	public int getNewItemPriorityDependenciesSize() {
		return 1;
	}
}