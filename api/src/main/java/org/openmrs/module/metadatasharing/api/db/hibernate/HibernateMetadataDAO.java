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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.StringType;
import org.openmrs.Concept;
import org.openmrs.ConceptWord;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.Privilege;
import org.openmrs.RelationshipType;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.metadatasharing.api.db.MetadataDAO;
import org.openmrs.module.metadatasharing.reflection.ClassUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Hibernate specific database methods for {@link MetadataDAO}
 */
@Repository
public class HibernateMetadataDAO implements MetadataDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public <T> List<T> getItems(Class<? extends T> type, boolean includeRetired, String filter, Integer first, Integer max)
	    throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(type);
		
		if (first != null) {
			criteria.setFirstResult(first);
		}
		
		if (max != null) {
			criteria.setMaxResults(max);
		}
		
		filter(type, criteria, includeRetired, filter);
		
		criteria.addOrder(Order.asc("uuid"));
		
		@SuppressWarnings("unchecked")
		List<T> list = criteria.list();
		return list;
	}
	
	@Override
	public <T> int getItemsCount(Class<? extends T> type, boolean includeRetired, String filter) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(type);
		
		filter(type, criteria, includeRetired, filter);
		
		criteria.setProjection(Projections.rowCount());
		
		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	@Override
	public <T> T getItemByUuid(Class<? extends T> type, String uuid) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(type);
		criteria.add(Restrictions.eq("uuid", uuid));
		
		@SuppressWarnings("unchecked")
		T result = (T) criteria.uniqueResult();
		return result;
	}
	
	@Override
	public <T> T getItemById(Class<? extends T> type, String id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(type);
		criteria.add(Restrictions.idEq(id));
		
		@SuppressWarnings("unchecked")
		T result = (T) criteria.uniqueResult();
		return result;
	}
	
	private void filter(Class<?> type, Criteria criteria, boolean includeRetired, String filter) {
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", includeRetired));
		}
		
		if (filter != null && !filter.isEmpty()) {
			Disjunction or = Restrictions.disjunction();
			criteria.add(or);
			
			or.add(Restrictions.like("uuid", filter, MatchMode.START));
			
			or.add(Restrictions.idEq(asItemId(filter)));
			
			type = ClassUtil.getDeproxiedClass(type);
			
			if (Role.class.isAssignableFrom(type)) {
				or.add(Restrictions.like("role", filter, MatchMode.START));
				
			} else if (Privilege.class.isAssignableFrom(type)) {
				or.add(Restrictions.like("privilege", filter, MatchMode.START));
				
			} else if (RelationshipType.class.isAssignableFrom(type)) {
				or.add(Restrictions.sqlRestriction("CONCAT(a_Is_To_B, CONCAT('/', b_Is_To_A)) like (?)", "%" + filter,
				    new StringType()));
			} else if (type.getSimpleName().equals("HtmlForm")) {
				criteria.createAlias("form", "form");
				or.add(Restrictions.like("form.name", filter, MatchMode.START));
			} else if (OpenmrsMetadata.class.isAssignableFrom(type)) {
				//It may happen that the name property is not defined for the specific metadata type so we need to test it.
				String[] propertyNames = sessionFactory.getClassMetadata(type).getPropertyNames();
				if (Arrays.asList(propertyNames).contains("name")) {
					or.add(Restrictions.like("name", filter, MatchMode.START));
				}
			}
		}
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataDAO#getConcepts(boolean,
	 *      java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<Concept> getConcepts(boolean includeRetired, String filter, Integer firstResult, Integer maxResults) {
		List<Concept> result = new ArrayList<Concept>();
		if (!StringUtils.isEmpty(filter)) {
			Concept concept = Context.getConceptService().getConceptByUuid(filter);
			if (concept != null) {
				return Arrays.asList(concept);
			}
			concept = filterById(filter);
			if (concept != null) {
				// if the first page with concepts was requested, we just add found concept 
				if (firstResult == 0) {
					result.add(concept);
					maxResults -= 1;
				} else {
					// otherwise, we need to shift down the lower bound of concepts
					firstResult -= 1;
				}
			}
		}
		
		Criteria criteria = null;
		if (StringUtils.isEmpty(filter)) {
			criteria = sessionFactory.getCurrentSession().createCriteria(Concept.class);
			
			if (!includeRetired) {
				criteria.add(Restrictions.eq("retired", includeRetired));
			}
			
			criteria.addOrder(Order.asc("conceptId"));
		} else {
			criteria = sessionFactory.getCurrentSession().createCriteria(ConceptWord.class, "conceptWord");
			
			filterConceptWords(criteria, includeRetired, filter);
			
			criteria.addOrder(Order.asc("concept"));
			criteria.setProjection(Projections.distinct(Projections.property("concept")));
		}
		
		if (firstResult != null) {
			criteria.setFirstResult(firstResult);
		}
		
		if (maxResults != null) {
			criteria.setMaxResults(maxResults);
		}
		
		@SuppressWarnings("unchecked")
		List<Concept> concepts = criteria.list();
		
		if (concepts != null && !concepts.isEmpty()) {
			result.addAll(concepts);
		}
		
		return result;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataDAO#getConceptsCount(boolean,
	 *      java.lang.String)
	 */
	@Override
	public int getConceptsCount(boolean includeRetired, String filter) {
		if (StringUtils.isEmpty(filter)) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Concept.class);
			
			if (!includeRetired) {
				criteria.add(Restrictions.eq("retired", includeRetired));
			}
			
			criteria.setProjection(Projections.rowCount());
			
			return ((Number) criteria.uniqueResult()).intValue();
		}
		
		boolean incrementNeeded = false;
		if (!StringUtils.isEmpty(filter)) {
			Concept concept = Context.getConceptService().getConceptByUuid(filter);
			if (concept != null) {
				if (concept.isRetired() && includeRetired) {
					return 1;
				} else {
					return 0;
				}
			}
			concept = filterById(filter);
			if (concept != null) {
				incrementNeeded = true;
			}
		}
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConceptWord.class, "conceptWord");
		
		filterConceptWords(criteria, includeRetired, filter);
		
		criteria.setProjection(Projections.countDistinct("concept"));
		
		Integer resultsCount = ((Number) criteria.uniqueResult()).intValue();
		if (incrementNeeded) {
			return resultsCount + 1;
		} else {
			return resultsCount;
		}
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
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataDAO#getItemById(Class, Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getItemById(Class<? extends T> type, Integer id) {
		return (T) sessionFactory.getCurrentSession().get(type, id);
	}
	
	/**
	 * Tries to filter concept by given string, assuming that it's an ID
	 * 
	 * @param filter the filtering string for concept
	 * @return concept object if there is matching item for given filter or null otherwise
	 */
	private Concept filterById(String filter) {
		Concept concept = null;
		Integer conceptId = asItemId(filter);
		if (conceptId != null) {
			concept = (Concept) getItemById(Concept.class, conceptId);
		}
		return concept;
	}
	
	/**
	 * Represents given filter as meta-data item's id
	 * 
	 * @param filter the filtering string to cast to
	 * @return integer value of given filter if it can be cast to otherwise null
	 */
	private Integer asItemId(String filter) {
		Integer itemId = null;
		try {
			itemId = Integer.parseInt(filter);
		}
		catch (NumberFormatException e) {
			// do nothing
		}
		return itemId;
	}
}
