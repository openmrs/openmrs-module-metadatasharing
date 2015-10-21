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

import org.hibernate.FlushMode;
import org.openmrs.api.db.hibernate.DbSession;  

/**
 * Allows to execute code in a custom session flush mode.
 * <p>
 * It can be used as follows.
 * 
 * <pre>
 * <code>
 * User user = new SessionFlushMode&lt;User&gt;() {
 * 	
 * 	&#064;Override
 * 	protected User task() {
 * 		return Context.getAuthenticatedUser();
 * 	}
 * }.executeInManualFlushMode();
 * </code>
 * </pre>
 * 
 * @param T the result
 */
public abstract class CustomSessionFlushTask<T> {
	
	/**
	 * The task to execute.
	 * 
	 * @return the result or null
	 */
	protected abstract T task();
	
	/**
	 * Executes the task method preventing flushes.
	 * 
	 * @return the result or <code>null</code>
	 */
	public final T executeInManualFlushMode() {
		DbSession currentSession = HibernateSessionFactory.getSessionFactory().getCurrentSession();		
		FlushMode previousFlushMode = currentSession.getFlushMode();
		currentSession.setFlushMode(FlushMode.MANUAL);
		
		try {
			return task();
		}
		finally {
			currentSession.setFlushMode(previousFlushMode);
		}
	}
	
	/**
	 * Executes the task method with flushes.
	 * 
	 * @return the result or <code>null<code>
	 */
	public final T executeInAutoFlushMode() {
		DbSession currentSession = HibernateSessionFactory.getSessionFactory().getCurrentSession();		
		FlushMode previousFlushMode = currentSession.getFlushMode();
		currentSession.setFlushMode(FlushMode.AUTO);
		
		try {
			return task();
		}
		finally {
			currentSession.setFlushMode(previousFlushMode);
		}
	}
}
