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

import java.util.Collection;

import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.HibernateConceptDAO;

/**
 * It fixes {@link HibernateConceptDAO} to trigger inserts for new names in an existing concept in
 * {@link FlushMode#MANUAL}. It effectively overwrites HibernateConceptDAO from the OpenMRS core.
 * 
 * @see META-204 and META-205
 */
public class HibernateCustomConceptDAO extends HibernateConceptDAO {
	
	protected SessionFactory sessionFactory;
	
	/**
	 * @see org.openmrs.api.db.hibernate.HibernateConceptDAO#setSessionFactory(org.hibernate.SessionFactory)
	 */
	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		super.setSessionFactory(sessionFactory);
	}
	
	/**
	 * @see org.openmrs.api.db.hibernate.HibernateConceptDAO#updateConceptWord(org.openmrs.Concept)
	 */
	@Override
	public void updateConceptWord(Concept concept) throws DAOException {
		if (concept != null && concept.getId() != null) {
			Collection<ConceptName> names = concept.getNames();
			
			for (ConceptName name : names) {
				if (name.getId() == null) {
					//The id must be assigned before calling the updateConceptWord method, thus we force Hibernate to trigger the insert.
					sessionFactory.getCurrentSession().saveOrUpdate(name);
				}
			}
		}
		
		super.updateConceptWord(concept);
	}
}
