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

import java.sql.Blob;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.openmrs.Concept;
import org.openmrs.ConceptWord;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.metadatasharing.api.db.HibernateCompatibility;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(openmrsPlatformVersion = "1.9.9 - 1.10.*")
public class HibernateCompatibility1_9 implements HibernateCompatibility {

	@Autowired
	private DbSessionFactory sessionFactory;
	
	@Override
	public List<Concept> getConcepts(boolean includeRetired, String filter, Integer firstResult, Integer maxResults) {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConceptWord.class, "conceptWord");
		
		filterConceptWords(criteria, includeRetired, filter);
		
		criteria.addOrder(Order.asc("concept"));
		criteria.setProjection(Projections.distinct(Projections.property("concept")));
		
		if (firstResult != null) {
			criteria.setFirstResult(firstResult);
		}
	
		if (maxResults != null) {
			criteria.setMaxResults(maxResults);
		}
		
		return criteria.list();
	}
	
	private void filterConceptWords(Criteria criteria, boolean includeRetired, String filter) {
		criteria.createAlias("concept", "concept");
		if (!includeRetired) {
			criteria.add(Restrictions.eq("concept.retired", includeRetired));
		}
		
		if (!StringUtils.isEmpty(filter)) {
			Iterator<String> words = ConceptWord.getUniqueWords(filter).iterator();
			if (words.hasNext()) {
				criteria.add(Restrictions.like("word", words.next(), MatchMode.START));
				while (words.hasNext()) {
					DetachedCriteria crit = DetachedCriteria.forClass(ConceptWord.class)
					        .setProjection(Property.forName("concept"))
					        .add(Restrictions.eqProperty("concept", "conceptWord.concept"))
					        .add(Restrictions.like("word", words.next(), MatchMode.START));
					
					criteria.add(Subqueries.exists(crit));
				}
			}
		}
	}

	@Override
	public Integer getConceptsCount(boolean includeRetired, String filter) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConceptWord.class, "conceptWord");
		
		filterConceptWords(criteria, includeRetired, filter);
		
		criteria.setProjection(Projections.countDistinct("concept"));
		
		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	@Override
	public Blob createBlob(byte[] bytes) {
		return Hibernate.createBlob(bytes);
	}
}