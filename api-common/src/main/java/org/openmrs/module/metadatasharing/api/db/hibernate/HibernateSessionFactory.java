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

import org.hibernate.SessionFactory;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Exposes {@link SessionFactory} to classes in this package.
 */
@Repository(MetadataSharingConsts.MODULE_ID + ".HibernateSessionFactory")
public class HibernateSessionFactory {
	
	private static SessionFactory sessionFactory;
	
	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		HibernateSessionFactory.sessionFactory = sessionFactory;
	}
	
	/**
	 * @return the sessionFactory
	 */
	static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
