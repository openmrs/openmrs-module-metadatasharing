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
package org.openmrs.module.metadatasharing.api.db.hibernate;

import java.lang.reflect.Method;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptSearchResult;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.metadatasharing.api.db.HibernateCompatibility;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(openmrsPlatformVersion = "1.11")
public class HibernateCompatibility1_11 implements HibernateCompatibility {
	@Autowired
	private DbSessionFactory sessionFactory;

	@Override
	public List<Concept> getConcepts(boolean includeRetired, String filter, Integer firstResult, Integer maxResults) {
		List<Concept> concepts  = new ArrayList<Concept>();

		List<ConceptSearchResult> results = Context.getConceptService().getConcepts(filter, null, includeRetired, null, null, null, null, null, firstResult, maxResults);
		for (ConceptSearchResult result : results) {
			concepts.add(result.getConcept());
		}

		return concepts;
	}

	@Override
	public Integer getConceptsCount(boolean includeRetired, String filter) {
		return Context.getConceptService().getCountOfConcepts(filter, null, includeRetired, null, null, null, null, null);
	}

	@Override
	public Blob createBlob(byte[] bytes) {
		return Hibernate.getLobCreator(getCurrentSession()).createBlob(bytes);
	}
	
	/**
	 * Gets the current hibernate session while taking care of the hibernate 3 and 4 differences.
	 * 
	 * @return the current hibernate session.
	 */
	private org.hibernate.Session getCurrentSession() {
		SessionFactory hibernateSessionFactory = sessionFactory.getHibernateSessionFactory();
		try {
			return hibernateSessionFactory.getCurrentSession();
		}
		catch (NoSuchMethodError ex) {
			try {
				Method method = hibernateSessionFactory.getClass().getMethod("getCurrentSession", null);
				return (org.hibernate.Session)method.invoke(hibernateSessionFactory, null);
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to get the current hibernate session", e);
			}
		}
	}
}