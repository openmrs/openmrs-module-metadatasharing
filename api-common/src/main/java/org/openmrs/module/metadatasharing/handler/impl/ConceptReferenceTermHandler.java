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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.ConceptReferenceTerm;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSearchHandler;
import org.openmrs.module.metadatasharing.util.DateUtil;
import org.springframework.stereotype.Component;

@Component("metadatasharing.ConceptReferenceTermHandler")
public class ConceptReferenceTermHandler implements MetadataPriorityDependenciesHandler<ConceptReferenceTerm>, MetadataPropertiesHandler<ConceptReferenceTerm>, MetadataSearchHandler<ConceptReferenceTerm>  {

	@Override
    public int getPriority() {
	    return 0;
    }

	@Override
	public List<Object> getPriorityDependencies(ConceptReferenceTerm object) {
		List<Object> result = new ArrayList<Object>();
		if (object.getConceptSource() != null) {
			result.add(object.getConceptSource());
		}
		return result;
	}

	@Override
    public Date getDateChanged(ConceptReferenceTerm o) {
	    return DateUtil.getLastDateChanged(o);
    }

	@Override
    public String getDescription(ConceptReferenceTerm o) {
	    return (o.getDescription() != null) ? o.getDescription() : "";
    }

	@Override
    public Integer getId(ConceptReferenceTerm o) {
	    return o.getId();
    }

	@Override
    public String getName(ConceptReferenceTerm o) {
		String name = "";
		if (o.getConceptSource() != null) {
			name += o.getConceptSource().getName() + ":";
		}
	    if (o.getCode() != null) {
	    	name += o.getCode();
	    }
	    return name;
    }

	@Override
    public Map<String, Object> getProperties(ConceptReferenceTerm o) {
	    return Collections.emptyMap();
    }

	@Override
    public Boolean getRetired(ConceptReferenceTerm o) {
	    return o.getRetired();
    }

	@Override
    public String getUuid(ConceptReferenceTerm o) {
	    return o.getUuid();
    }

	@Override
    public void setId(ConceptReferenceTerm o, Integer id) {
	    o.setId(id);
    }

	@Override
    public void setRetired(ConceptReferenceTerm o, Boolean retired) {
	    o.setRetired(retired);
    }

	@Override
    public void setUuid(ConceptReferenceTerm o, String uuid) {
	    o.setUuid(uuid);
    }

	@Override
    public int getItemsCount(Class<? extends ConceptReferenceTerm> type, boolean includeRetired, String phrase)
        throws DAOException {
	    return Context.getConceptService().getConceptReferenceTerms(phrase, null, null, null, includeRetired).size();
    }

	@Override
    public List<ConceptReferenceTerm> getItems(Class<? extends ConceptReferenceTerm> type, boolean includeRetired,
                                               String phrase, Integer firstResult, Integer maxResults) throws DAOException {
	    return Context.getConceptService().getConceptReferenceTerms(phrase, null, null, null, includeRetired);
    }

	@Override
    public ConceptReferenceTerm getItemByUuid(Class<? extends ConceptReferenceTerm> type, String uuid) throws DAOException {
	    return Context.getConceptService().getConceptReferenceTermByUuid(uuid);
    }

	@Override
    public ConceptReferenceTerm getItemById(Class<? extends ConceptReferenceTerm> type, Integer id) throws DAOException {
	    return Context.getConceptService().getConceptReferenceTerm(id);
    }



}
