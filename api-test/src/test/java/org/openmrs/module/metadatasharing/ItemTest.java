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
package org.openmrs.module.metadatasharing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ItemTest {
	
	@Test
	public void isRetired_shouldReturnFalseIfRetiredIsNull() {
		Item item = new Item();
		item.setRetired(null);
		assertFalse(item.isRetired());
	}
	
	@Test
	public void isRetired_shouldReturnTheRetiredValue() {
		Item item = new Item();
		item.setRetired(true);
		assertTrue(item.isRetired());
		item.setRetired(false);
		assertFalse(item.isRetired());
	}
}
