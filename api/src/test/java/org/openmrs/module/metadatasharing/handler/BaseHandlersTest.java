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
package org.openmrs.module.metadatasharing.handler;

import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * A base test class which can be extended to facilitate testing handlers for different exportable
 * types.
 * 
 * @param <T>
 */
public abstract class BaseHandlersTest<T> extends BaseModuleContextSensitiveTest {
	
	public abstract T getNewItem();
	
	public abstract T getExistingItem();
	
	public abstract T getUpdatedItem();
	
	public abstract void assertExistingSameAsUpdated(T item);
	
	public abstract List<T> getAllItems();
	
	public int getNewItemPriorityDependenciesSize() {
		return Collections.emptyList().size();
	}
	
	public boolean hasIdProperty() {
		return true;
	}
	
	public boolean hasRetiredProperty() {
		return true;
	}
	
	public boolean hasPriorityDependenciesHandler() {
		return true;
	}
	
	@Test
	public void saveHandlerSaveItemShouldSaveANewItem() {
		T item = getNewItem();
		Handler.saveItem(item);
		Object savedItem = Handler.getItemByUuid(item.getClass(), Handler.getUuid(item));
		Assert.assertNotNull(savedItem);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void saveHandlerSaveItemShouldUpdateAnExisitingItem() {
		Object item = getUpdatedItem();
		Handler.saveItem(item);
		
		item = Handler.getItemByUuid(item.getClass(), Handler.getUuid(item));
		assertExistingSameAsUpdated((T) item);
	}
	
	@Test
	public void propertiesHandlerSetIdShouldSetId() {
		Integer id = 500;
		T item = getNewItem();
		
		if (!hasIdProperty()) {
			return;
		}
		
		Handler.setId(item, id);
		
		Assert.assertEquals(id, Handler.getId(item));
	}
	
	@Test
	public void propertiesHandlerSetUuidShouldSetUuid() {
		String uuid = "f1f091ec-7aca-11df-8120-7778f4620a0f";
		T item = getNewItem();
		Handler.setUuid(item, uuid);
		
		Assert.assertEquals(uuid, Handler.getUuid(item));
	}
	
	@Test
	public void propertiesHandlerSetRetiredShouldSetRetired() {
		if (!hasRetiredProperty()) {
			return;
		}
		
		T item = getNewItem();
		Handler.setRetired(item, true);
		
		Assert.assertTrue(Handler.getRetired(item));
	}
	
	@Test
	public void propertiesHandlerGetIdShouldGetNonNullId() {
		if (!hasIdProperty()) {
			return;
		}
		
		T item = getExistingItem();
		Assert.assertNotNull(Handler.getId(item));
	}
	
	@Test
	public void propertiesHandlerGetUuidShouldGetNonNullUuid() {
		T item = getExistingItem();
		Assert.assertNotNull(Handler.getUuid(item));
	}
	
	@Test
	public void propertiesHandlerGetNameShouldGetNonNullName() {
		T item = getExistingItem();
		Assert.assertNotNull(Handler.getName(item));
	}
	
	@Test
	public void propertiesHandlerGetDescriptionShouldGetNonNullDescription() {
		T item = getExistingItem();
		Assert.assertNotNull(Handler.getDescription(item));
	}
	
	@Test
	public void searchHandlerGetItemShouldGetItemByUuid() {
		String uuid = "f1f091ec-7aca-11df-8120-7778f4620a0f";
		T item = getNewItem();
		Handler.setUuid(item, uuid);
		Handler.saveItem(item);
		
		Object savedItem = Handler.getItemByUuid(item.getClass(), uuid);
		Assert.assertEquals(uuid, Handler.getUuid(savedItem));
	}
	
	@Test
	public void searchHandlerGetItemsCountShouldGetCorrectItemCount() {
		T item = getNewItem();
		int count = Handler.getItemsCount(item.getClass(), true, null);
		Assert.assertEquals(getAllItems().size(), count);
	}
	
	@Test
	public void searchHandlerGetItemsShouldGetCorrectItems() {
		T item = getNewItem();
		List<Object> items = Handler.getItems(item.getClass(), true, null, 0, 9);
		Assert.assertTrue(getAllItems().containsAll(items));
	}
	
	@Test
	public void priorityDependenciesHandlerGetPriorityDependenciesShouldGetAnEmptyList() {
		if (!hasPriorityDependenciesHandler()) {
			return;
		}
		
		T item = getNewItem();
		List<Object> items = Handler.getPriorityDependencies(item);
		Assert.assertEquals(getNewItemPriorityDependenciesSize(), items.size());
	}
}
