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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.openmrs.Concept;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.Privilege;
import org.openmrs.RelationshipType;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.metadatasharing.api.db.HibernateCompatibility;
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
	private DbSessionFactory sessionFactory;

	@Autowired
	private HibernateCompatibility compatibility;

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

	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataDAO#saveItem(java.lang.Object)
	 */
	@Override
	public <T> T saveItem(T item) {
		sessionFactory.getCurrentSession().saveOrUpdate(item);
	    return item;
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
				or.add(Restrictions.ilike("role", filter, MatchMode.START));

			} else if (Privilege.class.isAssignableFrom(type)) {
				or.add(Restrictions.ilike("privilege", filter, MatchMode.START));

			} else if (RelationshipType.class.isAssignableFrom(type)) {
				or.add(Restrictions.sqlRestriction("CONCAT(a_Is_To_B, CONCAT('/', b_Is_To_A)) like (?)", "%" + filter,
				    new StringType()));
			} else if (type.getSimpleName().equals("HtmlForm")) {
				criteria.createAlias("form", "form");
				or.add(Restrictions.ilike("form.name", filter, MatchMode.START));
			} else if (OpenmrsMetadata.class.isAssignableFrom(type)) {
				//It may happen that the name property is not defined for the specific metadata type so we need to test it.
				if (sessionFactory.getHibernateSessionFactory().getClassMetadata(type) != null) {
					String[] propertyNames = sessionFactory.getHibernateSessionFactory().getClassMetadata(type).getPropertyNames();
					if (Arrays.asList(propertyNames).contains("name")) {
						or.add(Restrictions.ilike("name", filter, MatchMode.START));
					}
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
				if (firstResult != null && firstResult == 0) {
					if (maxResults == null || maxResults != 0) {
						result.add(concept);
					}
				}
				if (maxResults != null && maxResults != 0) {
					maxResults -= 1;
				}

			}
		}

		List<Concept> concepts = null;
		Criteria criteria = null;
		if (StringUtils.isEmpty(filter)) {
			criteria = sessionFactory.getCurrentSession().createCriteria(Concept.class);

			if (!includeRetired) {
				criteria.add(Restrictions.eq("retired", includeRetired));
			}

			criteria.addOrder(Order.asc("conceptId"));

			if (firstResult != null) {
				criteria.setFirstResult(firstResult);
			}

			if (maxResults != null) {
				criteria.setMaxResults(maxResults);
			}

			concepts = criteria.list();

		} else {
			concepts = compatibility.getConcepts(includeRetired, filter, firstResult, maxResults);
		}

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

		Integer resultsCount = compatibility.getConceptsCount(includeRetired, filter);
		if (incrementNeeded) {
			return resultsCount + 1;
		} else {
			return resultsCount;
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

	@Override
	public Blob createBlob(byte[] bytes) {
		return compatibility.createBlob(bytes);
	}
}
