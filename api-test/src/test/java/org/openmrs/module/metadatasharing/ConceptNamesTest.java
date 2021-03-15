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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;

/**
 * Tests if concept names are correctly merged
 */
public class ConceptNamesTest extends BaseShareTest {
	
	@Override
	public boolean insertInitialDataSet() {
	    return false;
	}
	
	@Test
	public void shouldOverwritePreferredNameIfPreferTheirs() throws Exception {
		final String conceptUuid = UUID.randomUUID().toString();
		
		runShareTest(new ShareTestHelper() {
			
			@Override
			public List<?> prepareExportServer() throws Exception {
				Concept concept = new Concept();
				concept.setUuid(conceptUuid);
				
				ConceptName preferredName = new ConceptName("a", Locale.ENGLISH);
				concept.setPreferredName(preferredName);
				
				ConceptName synonym = new ConceptName("AA", Locale.ENGLISH);
				concept.addName(synonym);
				
				ConceptName synonym2 = new ConceptName("AAG", Locale.GERMAN);
				concept.addName(synonym2);
				
				Context.getConceptService().saveConcept(concept);
				return Arrays.asList(concept);
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#prepareImportServer()
			 */
			@Override
			public void prepareImportServer() throws Exception {
				Concept concept = new Concept();
				concept.setUuid(conceptUuid);
				
				ConceptName preferredName = new ConceptName("b", Locale.ENGLISH);
				concept.setPreferredName(preferredName);
				
				ConceptName synonym = new ConceptName("AA", Locale.ENGLISH);
				concept.addName(synonym);
				
				ConceptName synonym2 = new ConceptName("AAG", Locale.GERMAN);
				concept.addName(synonym2);
				
				Context.getConceptService().saveConcept(concept);
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#runOnImportServerBeforeImport(org.openmrs.module.metadatasharing.wrapper.PackageImporter)
			 */
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
			    for (ImportedItem importedItem: importer.getImportedItems(0)) {
	                if (importedItem.getIncoming() instanceof Concept) {
	                	importedItem.setImportType(ImportType.PREFER_THEIRS);
	                }
                }
			}
			
			@Override
			public void runOnImportServerAfterImport() throws Exception {
				Concept concept = Context.getConceptService().getConceptByUuid(conceptUuid);
				
				Collection<ConceptName> names = concept.getNames();
				Set<String> expectedNames = new HashSet<String>();
				expectedNames.addAll(Arrays.asList("AA", "AAG", "a", "b"));
				assertNull(diffConceptNameSets(concept.getNames(), expectedNames));
				
				ConceptName preferredName = concept.getPreferredName(Locale.ENGLISH);
				Assert.assertEquals("a must be preferred", "a", preferredName.getName());
			}
			
		});
	}
	
	@Test
	public void shouldRemoveExistingNamesIfNotInIncomingInMirrorMode() throws Exception {
		final String conceptUuid = UUID.randomUUID().toString();
		
		runShareTest(new ShareTestHelper() {
			
			@Override
			public List<?> prepareExportServer() throws Exception {
				Concept concept = new Concept();
				concept.setUuid(conceptUuid);
				
				ConceptName preferredName = new ConceptName("a", Locale.ENGLISH);
				concept.setPreferredName(preferredName);
				
				ConceptName synonym = new ConceptName("AA", Locale.ENGLISH);
				concept.addName(synonym);
				
				ConceptName synonym2 = new ConceptName("AAG", Locale.GERMAN);
				concept.addName(synonym2);
				
				Context.getConceptService().saveConcept(concept);
				return Arrays.asList(concept);
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#prepareImportServer()
			 */
			@Override
			public void prepareImportServer() throws Exception {
				Concept concept = new Concept();
				concept.setUuid(conceptUuid);
				
				ConceptName preferredName = new ConceptName("b", Locale.ENGLISH);
				concept.setPreferredName(preferredName);
				
				ConceptName synonym = new ConceptName("AA", Locale.ENGLISH);
				concept.addName(synonym);
				
				ConceptName synonym2 = new ConceptName("AAG", Locale.GERMAN);
				concept.addName(synonym2);
				
				Context.getConceptService().saveConcept(concept);
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#runOnImportServerBeforeImport(org.openmrs.module.metadatasharing.wrapper.PackageImporter)
			 */
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
				importer.setImportConfig(ImportConfig.valueOf(ImportMode.MIRROR));
			}
			
			@Override
			public void runOnImportServerAfterImport() throws Exception {				
				Concept concept = Context.getConceptService().getConceptByUuid(conceptUuid);
				
				Collection<ConceptName> names = concept.getNames();
				Set<String> expectedNames = new HashSet<String>();
				expectedNames.addAll(Arrays.asList("AA", "AAG", "a"));
				assertNull(diffConceptNameSets(concept.getNames(), expectedNames));
				
				ConceptName preferredName = concept.getPreferredName(Locale.ENGLISH);
				Assert.assertEquals("a must be preferred", "a", preferredName.getName());
			}
			
		});
	}
	
	@Test
	public void shouldNotOverwritePreferredNameIfPreferMine() throws Exception {
		final String conceptUuid = UUID.randomUUID().toString();
		
		runShareTest(new ShareTestHelper() {
			
			@Override
			public List<?> prepareExportServer() throws Exception {
				Concept concept = new Concept();
				concept.setUuid(conceptUuid);
				
				ConceptName preferredName = new ConceptName("a", Locale.ENGLISH);
				concept.setPreferredName(preferredName);
				
				ConceptName synonym = new ConceptName("AA", Locale.ENGLISH);
				concept.addName(synonym);
				
				ConceptName synonym2 = new ConceptName("AAG", Locale.GERMAN);
				concept.addName(synonym2);
				
				Context.getConceptService().saveConcept(concept);
				return Arrays.asList(concept);
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#prepareImportServer()
			 */
			@Override
			public void prepareImportServer() throws Exception {
				Concept concept = new Concept();
				concept.setUuid(conceptUuid);
				
				ConceptName preferredName = new ConceptName("b", Locale.ENGLISH);
				concept.setPreferredName(preferredName);
				
				ConceptName synonym = new ConceptName("AA", Locale.ENGLISH);
				concept.addName(synonym);
				
				ConceptName synonym2 = new ConceptName("AAG", Locale.GERMAN);
				concept.addName(synonym2);
				
				Context.getConceptService().saveConcept(concept);
			}
			
			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#runOnImportServerBeforeImport(org.openmrs.module.metadatasharing.wrapper.PackageImporter)
			 */
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
			    for (ImportedItem importedItem: importer.getImportedItems(0)) {
	                if (importedItem.getIncoming() instanceof Concept) {
	                	importedItem.setImportType(ImportType.PREFER_MINE);
	                }
                }
			}
			
			@Override
			public void runOnImportServerAfterImport() throws Exception {
				Concept concept = Context.getConceptService().getConceptByUuid(conceptUuid);
				
				Collection<ConceptName> names = concept.getNames();
				Set<String> expectedNames = new HashSet<String>();
				expectedNames.addAll(Arrays.asList("AA", "AAG","a", "b"));
				assertNull(diffConceptNameSets(concept.getNames(), expectedNames));

				ConceptName preferredName = concept.getPreferredName(Locale.ENGLISH);
				Assert.assertEquals("b must be preferred", "b", preferredName.getName());
			}
			
		});
	}

	@Test
	@Ignore("This doesn't work in the existing implementation.")
	public void shouldAllowChangingNameCapitalization() throws Exception {
		final String conceptUuid = UUID.randomUUID().toString();

		runShareTest(new ShareTestHelper() {

			@Override
			public List<?> prepareExportServer() throws Exception {
				Concept concept = new Concept();
				concept.setUuid(conceptUuid);

				ConceptName preferredName = new ConceptName("Aaa", Locale.ENGLISH);
				concept.setPreferredName(preferredName);

				ConceptName synonym = new ConceptName("Baa", Locale.ENGLISH);
				concept.addName(synonym);

				ConceptName synonym2 = new ConceptName("Aag", Locale.GERMAN);
				concept.addName(synonym2);

				Context.getConceptService().saveConcept(concept);
				return Arrays.asList(concept);
			}

			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#prepareImportServer()
			 */
			@Override
			public void prepareImportServer() throws Exception {
				Concept concept = new Concept();
				concept.setUuid(conceptUuid);

				ConceptName preferredName = new ConceptName("AAA", Locale.ENGLISH);
				concept.setPreferredName(preferredName);

				ConceptName synonym = new ConceptName("BAA", Locale.ENGLISH);
				concept.addName(synonym);

				ConceptName synonym2 = new ConceptName("AAG", Locale.GERMAN);
				concept.addName(synonym2);

				Context.getConceptService().saveConcept(concept);
			}

			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#runOnImportServerBeforeImport(org.openmrs.module.metadatasharing.wrapper.PackageImporter)
			 */
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
				for (ImportedItem importedItem: importer.getImportedItems(0)) {
					if (importedItem.getIncoming() instanceof Concept) {
						importedItem.setImportType(ImportType.PREFER_THEIRS);
					}
				}
			}

			@Override
			public void runOnImportServerAfterImport() throws Exception {
				Concept concept = Context.getConceptService().getConceptByUuid(conceptUuid);

				Collection<ConceptName> names = concept.getNames();
				Set<String> expectedNames = new HashSet<String>(Arrays.asList("Aaa", "Baa", "Aag"));
				assertNull(diffConceptNameSets(concept.getNames(), expectedNames));
			}

		});
	}

	@Test
	public void shouldChangeNameToPreferredNameIfPreferTheirs() throws Exception {
		final String conceptUuid = UUID.randomUUID().toString();

		runShareTest(new ShareTestHelper() {

			@Override
			public List<?> prepareExportServer() throws Exception {
				Concept concept = new Concept();
				concept.setUuid(conceptUuid);

				ConceptName preferredName = new ConceptName("AA", Locale.ENGLISH);
				concept.setPreferredName(preferredName);
				assertTrue(preferredName.isFullySpecifiedName());

				ConceptName synonym = new ConceptName("b", Locale.ENGLISH);
				concept.addName(synonym);

				Context.getConceptService().saveConcept(concept);
				return Arrays.asList(concept);
			}

			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#prepareImportServer()
			 */
			@Override
			public void prepareImportServer() throws Exception {
				Concept concept = new Concept();
				concept.setUuid(conceptUuid);

				ConceptName preferredName = new ConceptName("b", Locale.ENGLISH);
				concept.setPreferredName(preferredName);
				assertTrue(preferredName.isFullySpecifiedName());

				ConceptName synonym = new ConceptName("AA", Locale.ENGLISH);
				concept.addName(synonym);

				Context.getConceptService().saveConcept(concept);
			}

			/**
			 * @see org.openmrs.module.metadatasharing.ShareTestHelper#runOnImportServerBeforeImport(org.openmrs.module.metadatasharing.wrapper.PackageImporter)
			 */
			@Override
			public void runOnImportServerBeforeImport(PackageImporter importer) throws Exception {
				for (ImportedItem importedItem: importer.getImportedItems(0)) {
					if (importedItem.getIncoming() instanceof Concept) {
						importedItem.setImportType(ImportType.PREFER_THEIRS);
					}
				}
			}

			@Override
			public void runOnImportServerAfterImport() throws Exception {
				Concept concept = Context.getConceptService().getConceptByUuid(conceptUuid);

				Collection<ConceptName> names = concept.getNames();
				Set<String> expectedNames = new HashSet<String>();
				expectedNames.addAll(Arrays.asList("AA", "b"));
				assertNull(diffConceptNameSets(concept.getNames(), expectedNames));

				ConceptName preferredName = concept.getPreferredName(Locale.ENGLISH);
				Assert.assertEquals("AA must be preferred", "AA", preferredName.getName());
			}

		});
	}

	String diffConceptNameSets(Collection<ConceptName> actual, Collection<String> expected) {
		ArrayList<String> actualNames = new ArrayList<String>();
		for (ConceptName name : actual) {
			actualNames.add(name.getName());
		}
		ArrayList<String> unexpected = new ArrayList<String>();
		for (String name : actualNames) {
			if (!expected.contains(name)) {
				unexpected.add(name);
			}
		}
		ArrayList<String> notFound = new ArrayList<String>();
		for (String name : expected) {
			if (!actualNames.contains(name)) {
				notFound.add(name);
			}
		}
		StringBuilder result = new StringBuilder();
		if (!unexpected.isEmpty()) {
			result.append("Unexpected name(s) found: ");
			for (int i = 0; i < unexpected.size(); i++) {
				result.append(unexpected.get(i));
				if (i != unexpected.size() - 1) {
					result.append(", ");
				} else {
					result.append(".  ");
				}
			}
		}
		if (!notFound.isEmpty()) {
			result.append("Result is missing name(s): ");
			for (int i = 0; i < notFound.size(); i++) {
				result.append(notFound.get(i));
				if (i != notFound.size() - 1) {
					result.append(", ");
				} else {
					result.append(".  ");
				}
			}
		}
		if (result.toString().equals("")) {
			return null;
		} else {
			return result.toString();
		}
	}
}
