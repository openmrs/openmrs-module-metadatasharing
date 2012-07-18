package org.openmrs.module.metadatasharing.handler.impl;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.ConceptMap;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.springframework.stereotype.Component;

@Component("metadatasharing.ConceptMapHandler")
public class ConceptMapHandler implements MetadataPriorityDependenciesHandler<ConceptMap> {

	@Override
    public int getPriority() {
	    return 0;
    }

	@Override
    public List<Object> getPriorityDependencies(ConceptMap object) {
		List<Object> result = new ArrayList<Object>();
		
		if (object.getSource() != null) {
			result.add(object.getSource());
		}
		
		return result;
    }
	
}
