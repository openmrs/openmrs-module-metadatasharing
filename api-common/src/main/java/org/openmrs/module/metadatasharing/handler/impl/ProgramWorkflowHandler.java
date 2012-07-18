package org.openmrs.module.metadatasharing.handler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.ProgramWorkflow;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.util.DateUtil;
import org.springframework.stereotype.Component;

@Component("metadatasharing.ProgramWorkflowHandler")
public class ProgramWorkflowHandler implements MetadataPropertiesHandler<ProgramWorkflow>, MetadataPriorityDependenciesHandler<ProgramWorkflow> {
	
	@Override
	public int getPriority() {
	    return 0;
	}
	
	@Override
	public Integer getId(ProgramWorkflow object) {
		return object.getId();
	}
	
	@Override
	public void setId(ProgramWorkflow object, Integer id) {
		object.setId(id);
	}
	
	@Override
	public String getUuid(ProgramWorkflow object) {
		return object.getUuid();
	}
	
	@Override
	public void setUuid(ProgramWorkflow object, String uuid) {
		object.setUuid(uuid);
	}
	
	@Override
	public Boolean getRetired(ProgramWorkflow object) {
	    return object.isRetired();
	}
	
	@Override
	public void setRetired(ProgramWorkflow object, Boolean retired) {
		object.setRetired(retired);
	}
	
	@Override
	public String getName(ProgramWorkflow object) {
		// a program workflow name is, strangely, the name of the underlying concept
		// (is this the right "name" we want to return)
		if (object.getConcept() != null && object.getConcept().getName() != null) {
			return object.getConcept().getName().getName();
		}
		else {
			return null;
		}
	}
	
	@Override
	public String getDescription(ProgramWorkflow object) {
		// a program workflow name is, strangely, the description of the underlying concept
		// (is this the right "name" we want to return)
		if (object.getConcept() != null && object.getConcept().getDescription() != null) {
			return object.getConcept().getDescription().getDescription();
		}
		else {
			return null;
		}
	}
	
	@Override
	public Date getDateChanged(ProgramWorkflow object) {
		return DateUtil.getLastDateChanged(object);
	}
	
	@Override
	public Map<String, Object> getProperties(ProgramWorkflow object) {
		// TODO: do we need to add functionality here?
		return Collections.emptyMap();
	}

	@Override
    public List<Object> getPriorityDependencies(ProgramWorkflow object) {
		List<Object> result = new ArrayList<Object>();
		
		// add the parent program
		result.add(object.getProgram());
		
		return result;
    }
}
