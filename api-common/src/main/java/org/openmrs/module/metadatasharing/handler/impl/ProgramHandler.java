package org.openmrs.module.metadatasharing.handler.impl;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.springframework.stereotype.Component;

@Component("metadatasharing.ProgramHandler")
public class ProgramHandler implements MetadataPriorityDependenciesHandler<Program> {

	@Override
	public int getPriority() {
	    return 0;
	}
	
	@Override
    public List<Object> getPriorityDependencies(Program object) {
		List<Object> result = new ArrayList<Object>();
		
		// add the associated concept
		if (object.getConcept() != null) {
			result.add(object.getConcept());
		}
		
		// now add the all the concepts of the workflows and workflow states
		if (object.getAllWorkflows() != null) {
			for (ProgramWorkflow workflow : object.getAllWorkflows()) {
				// add the workflow concept
				if (workflow.getConcept() != null) {
					result.add(workflow.getConcept());
				}
				
				if (workflow.getStates(true) != null) {
					for (ProgramWorkflowState state : workflow.getStates(true)) {
						// add the state concept
						if (state.getConcept() != null) {
							result.add(state.getConcept());
						}
					}	
				}
			}
		}
			
		return result;
    }
	
}
