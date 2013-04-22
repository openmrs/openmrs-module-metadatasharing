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
package org.openmrs.module.metadatasharing.util;

import java.util.Collection;

import org.hibernate.proxy.HibernateProxy;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.handler.Handler;


/**
 *
 */
public class ImportUtil {
	
	public static void reloadExistingItems(Collection<ImportedItem> importedItems) {
		Context.flushSession();
		Context.clearSession();
		
		for (ImportedItem importedItem : importedItems) {
			if (importedItem.getExisting() != null) {
				Object item = Handler.getItemByUuid(importedItem.getExisting().getClass(),
				    Handler.getUuid(importedItem.getExisting()));
				
				//Get rid of HibernateProxy
				if (item instanceof HibernateProxy) {
					item = ((HibernateProxy) item).getHibernateLazyInitializer().getImplementation();
				}
				
				importedItem.setExisting(item);
			}
		}
	}
}
