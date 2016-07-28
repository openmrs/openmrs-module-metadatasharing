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

import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.converter.ConverterEngine;
import org.openmrs.module.metadatasharing.handler.HandlerEngine;
import org.openmrs.module.metadatasharing.mapper.ConceptMapper;
import org.openmrs.module.metadatasharing.model.validator.PackageContainerValidator;
import org.openmrs.module.metadatasharing.model.validator.PackageValidator;
import org.openmrs.module.metadatasharing.reflection.ReplaceMethodInovker;
import org.openmrs.module.metadatasharing.resolver.ResolverEngine;
import org.openmrs.module.metadatasharing.serializer.MetadataSerializer;
import org.openmrs.module.metadatasharing.task.TaskEngine;
import org.openmrs.module.metadatasharing.versioner.PackageVersioner;
import org.openmrs.module.metadatasharing.versioner.impl.PackageVersionerImpl;
import org.openmrs.module.metadatasharing.visitor.ObjectVisitor;
import org.openmrs.module.metadatasharing.wrapper.PackageExporter;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.module.metadatasharing.wrapper.impl.PackageExporterImpl;
import org.openmrs.module.metadatasharing.wrapper.impl.PackageImporterImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service("metadatasharing")
public class MetadataSharing implements ApplicationContextAware {
	
	private static MetadataSharing instance;
	
	@Autowired
	private HandlerEngine handlerEngine;
	
	@Autowired
	private ReplaceMethodInovker replaceMethodInvoker;
	
	@Autowired
	private MetadataSerializer metadataSerializer;
	
	@Autowired
	private ResolverEngine resolverEngine;
	
	@Autowired
	private ObjectVisitor objectVisitor;
	
	@Autowired
	private ConceptMapper conceptMapper;
	
	@Autowired
	private TaskEngine taskEngine;
	
	@Autowired
	private PackageContainerValidator containerValidator;
	
	@Autowired
	private PackageValidator packageValidator;
	
	@Autowired
	private ConverterEngine converterEngine;
	
	/**
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		instance = (MetadataSharing) context.getBean("metadatasharing");
	}
	
	/**
	 * @return the instance
	 */
	public static MetadataSharing getInstance() {
		return instance;
	}
	
	public static MetadataSharingService getService() {
		return Context.getService(MetadataSharingService.class);
	}
	
	/**
	 * @return the taskEngine
	 */
	public TaskEngine getTaskEngine() {
		return taskEngine;
	}
	
	/**
	 * @return the handlerEngine
	 */
	public HandlerEngine getHandlerEngine() {
		return handlerEngine;
	}
	
	/**
	 * @return the metadataSerializer
	 */
	public MetadataSerializer getMetadataSerializer() {
		return metadataSerializer;
	}
	
	/**
	 * @return the replaceMethodInvoker
	 */
	public ReplaceMethodInovker getReplaceMethodInvoker() {
		return replaceMethodInvoker;
	}
	
	/**
	 * @return the resolverEngine
	 */
	public ResolverEngine getResolverEngine() {
		return resolverEngine;
	}
	
	/**
	 * @return the objectVisitor
	 */
	public ObjectVisitor getObjectVisitor() {
		return objectVisitor;
	}
	
	/**
	 * @return the conceptMapper
	 */
	public ConceptMapper getConceptMapper() {
		return conceptMapper;
	}
	
	/**
	 * @return the containerValidator
	 */
	public PackageContainerValidator getContainerValidator() {
		return containerValidator;
	}
	
	/**
	 * @return the packageValidator
	 */
	public PackageValidator getPackageValidator() {
		return packageValidator;
	}
	
	/**
	 * @return the converterEngine
	 */
	public ConverterEngine getConverterEngine() {
		return converterEngine;
	}
	
	/**
	 * @return the boolean value of global property with name GP_CONFIGURED
	 * @deprecated since 1.1 first time configuration no longer required
	 */
	public boolean isConfigured() {
		return true;
	}
	
	/**
	 * @return boolean stating if user has properly configured the URL prefix
	 */
	public boolean isPublishConfigured() {
		String prefix = Context.getAdministrationService().getGlobalProperty(MetadataSharingConsts.GP_URL_PREFIX);
		if (prefix == null || prefix == "") {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * @return
	 * @deprecated since 1.1, use {@link MetadataMappingService#isAddLocalMappingToConceptOnExport()}
	 */
	@Deprecated
	public boolean isAddLocalMappings() {
		return Context.getService(MetadataMappingService.class).isAddLocalMappingToConceptOnExport();
	}
	
	/**
	 * Defines if system concept source has been already configured
	 * 
	 * @return true if concept source is configured and exists, false otherwise
	 * @deprecated since 1.1, use {@link MetadataMappingService#isLocalConceptSourceConfigured()}
	 */
	public boolean isConceptSourceConfigured() {
		return Context.getService(MetadataMappingService.class).isLocalConceptSourceConfigured();
	}
	
	/**
	 * @return the packageExporter
	 */
	public PackageExporter newPackageExporter() {
		return new PackageExporterImpl();
	}
	
	public PackageImporter newPackageImporter() {
		return new PackageImporterImpl();
	}
	
	/**
	 * Creates new {@link PackageVersioner} instance associated with the given package
	 * 
	 * @param pack the package to be associated with versioner
	 * @return new versioner instance
	 */
	public PackageVersioner getPackageVersioner(ExportedPackage pack) {
		return new PackageVersionerImpl(pack);
	}
	
	/**
	 * @return the Module
	 */
	public Module getModule() {
		return ModuleFactory.getModuleById(MetadataSharingConsts.MODULE_ID);
	}
}
