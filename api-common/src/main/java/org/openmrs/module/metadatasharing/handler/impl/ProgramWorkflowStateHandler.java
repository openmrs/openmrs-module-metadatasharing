package org.openmrs.module.metadatasharing.handler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.util.DateUtil;
import org.springframework.stereotype.Component;

@Component("metadatasharing.ProgramWorkflowStateHandler")
public class ProgramWorkflowStateHandler implements MetadataPropertiesHandler<ProgramWorkflowState>, MetadataPriorityDependenciesHandler<ProgramWorkflowState> {
	
	@Override
	public int getPriority() {
	    return 0;
	}
	
	@Override
	public Integer getId(ProgramWorkflowState object) {
		return object.getId();
	}
	
	@Override
	public void setId(ProgramWorkflowState object, Integer id) {
		object.setId(id);
	}
	
	@Override
	public String getUuid(ProgramWorkflowState object) {
		return object.getUuid();
	}
	
	@Override
	public void setUuid(ProgramWorkflowState object, String uuid) {
		object.setUuid(uuid);
	}
	
	@Override
	public Boolean getRetired(ProgramWorkflowState object) {
	    return object.isRetired();
	}
	
	@Override
	public void setRetired(ProgramWorkflowState object, Boolean retired) {
		object.setRetired(retired);
	}
	
	@Override
	public String getName(ProgramWorkflowState object) {
		// a program workflow state name is, strangely, the name of the underlying concept
		// (is this the right "name" we want to return)
		if (object.getConcept() != null && object.getConcept().getName() != null) {
			return object.getConcept().getName().getName();
		}
		else {
			return null;
		}
	}
	
	@Override
	public String getDescription(ProgramWorkflowState object) {
		// a program workflow state name is, strangely, the description of the underlying concept
		// (is this the right "name" we want to return)
		if (object.getConcept() != null && object.getConcept().getDescription() != null) {
			return object.getConcept().getDescription().getDescription();
		}
		else {
			return null;
		}
	}
	
	@Override
	public Date getDateChanged(ProgramWorkflowState object) {
		return DateUtil.getLastDateChanged(object);
	}
	
	@Override
	public Map<String, Object> getProperties(ProgramWorkflowState object) {
		// TODO: do we need to add functionality here?
		return Collections.emptyMap();
	}

	@Override
    public List<Object> getPriorityDependencies(ProgramWorkflowState object) {
		List<Object> result = new ArrayList<Object>();
		
		// add the parent workflow
		result.add(object.getProgramWorkflow());
		
		return result;
    }
	
}
