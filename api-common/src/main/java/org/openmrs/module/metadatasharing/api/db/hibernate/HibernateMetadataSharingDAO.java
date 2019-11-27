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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.ExportedPackageSummary;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.api.db.MetadataSharingDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Hibernate specific database methods for {@link MetadataSharingDAO}
 */
@Repository
public class HibernateMetadataSharingDAO implements MetadataSharingDAO {
	
	@Autowired
	private DbSessionFactory sessionFactory;

	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataSharingDAO#getExportedPackage(java.lang.Integer)
	 */
	@Override
	public ExportedPackage getExportedPackage(Integer packageId) {
		return (ExportedPackage) sessionFactory.getCurrentSession().get(ExportedPackage.class, packageId);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataSharingDAO#purgeExportedPackage(org.openmrs.module.metadatasharing.ExportedPackage)
	 */
	@Override
	public void purgeExportedPackage(ExportedPackage pack) {
		sessionFactory.getCurrentSession().delete(pack);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataSharingDAO#saveExportedPackage(org.openmrs.module.metadatasharing.ExportedPackage)
	 */
	@Override
	public ExportedPackage saveExportedPackage(ExportedPackage pack) {
		sessionFactory.getCurrentSession().saveOrUpdate(pack);
		return pack;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataSharingDAO#getAllExportedPackages()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExportedPackage> getAllExportedPackages() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExportedPackage.class);
		List<ExportedPackage> list = criteria.list();
		return list;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataSharingDAO#getImportItem(Class,
	 *      java.lang.String)
	 */
	@Override
	public ImportedItem getImportItem(Class<?> type, String uuid) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ImportedItem.class);
			criteria.add(Restrictions.eq("uuid", uuid));
			criteria.add(Restrictions.eq("classname", type.getName()));
			ImportedItem result = (ImportedItem) criteria.uniqueResult();
			return result;
		}
		catch (RuntimeException e) {
			log.error("Error attempting to get import item: " + type.getSimpleName() + ": " + uuid, e);
			throw e;
		}
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataSharingDAO#persistImportItem(org.openmrs.module.metadatasharing.ImportedItem)
	 */
	@Override
	public ImportedItem persistImportItem(ImportedItem importItem) {
		sessionFactory.getCurrentSession().saveOrUpdate(importItem);
		return importItem;
	}
	
	@Override
	public Collection<ImportedItem> persistImportItems(Collection<ImportedItem> importedItems) {
		for (ImportedItem importedItem : importedItems) {
			sessionFactory.getCurrentSession().saveOrUpdate(importedItem);
		}
		return importedItems;
	}

	@Override
	public List<ExportedPackageSummary> getAllExportedPackageSummaries() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExportedPackageSummary.class);
		return criteria.list();
	}

	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataSharingDAO#getLatestExportedPackage(String)
	 */
	@Override
	public ExportedPackage getLatestExportedPackage(String group) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExportedPackage.class);
		criteria.add(Restrictions.eq("groupUuid", group));
		criteria.addOrder(Order.desc("version"));
		criteria.setMaxResults(1);
		return (ExportedPackage) criteria.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataSharingDAO#getExportedPackageGroup(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExportedPackage> getExportedPackageGroup(String group) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExportedPackage.class);
		criteria.add(Restrictions.eq("groupUuid", group));
		criteria.addOrder(Order.desc("version"));
		List<ExportedPackage> list = criteria.list();
		return list;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataSharingDAO#getLatestPublishedPackage(java.lang.String)
	 */
	@Override
	public ExportedPackage getLatestPublishedPackage(String group) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExportedPackage.class);
		criteria.add(Restrictions.eq("groupUuid", group));
		criteria.add(Restrictions.eq("published", true));
		criteria.addOrder(Order.desc("version"));
		criteria.setMaxResults(1);
		return (ExportedPackage) criteria.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataSharingDAO#getPublishedPackage(java.lang.String,
	 *      java.lang.Integer)
	 */
	@Override
	public ExportedPackage getPublishedPackage(String group, Integer version) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExportedPackage.class);
		criteria.add(Restrictions.eq("groupUuid", group));
		criteria.add(Restrictions.eq("published", true));
		criteria.add(Restrictions.eq("version", version));
		return (ExportedPackage) criteria.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.SubscriptionDAO#getImportedPackageByGroup(java.lang.Integer)
	 */
	@Override
	public ImportedPackage getImportedPackageById(Integer id) {
		return (ImportedPackage) sessionFactory.getCurrentSession().get(ImportedPackage.class, id);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.SubscriptionDAO#saveImportedPackage(org.openmrs.module.metadatasharing.subscription.Subscription)
	 */
	@Override
	public ImportedPackage saveImportedPackage(ImportedPackage importedPackage) {
		ImportedPackage existingImportedPackage = getImportedPackageByGroup(importedPackage.getGroupUuid());
		if (existingImportedPackage != null && existingImportedPackage != importedPackage) {
			importedPackage.setId(existingImportedPackage.getId());
			sessionFactory.getCurrentSession().evict(existingImportedPackage);
		}
		sessionFactory.getCurrentSession().saveOrUpdate(importedPackage);
		return importedPackage;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.SubscriptionDAO#getAllImportedPackages()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ImportedPackage> getAllImportedPackages() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ImportedPackage.class);
		List<ImportedPackage> list = criteria.list();
		return list;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.SubscriptionDAO#deleteImportedPackage(org.openmrs.module.metadatasharing.subscription.Subscription)
	 */
	@Override
	public void deleteImportedPackage(ImportedPackage subscription) {
		sessionFactory.getCurrentSession().delete(subscription);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.SubscriptionDAO#getImportedPackageByGroup(java.lang.String)
	 */
	@Override
	public ImportedPackage getImportedPackageByGroup(String group) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ImportedPackage.class);
		criteria.add(Restrictions.eq("groupUuid", group));
		return (ImportedPackage) criteria.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.api.db.MetadataSharingDAO#purgePreviousAssessments()
	 */
	@Override
	public void purgePreviousAssessments() {
		sessionFactory.getCurrentSession().createQuery("update ImportedItem set assessed = false").executeUpdate();
	}
}
