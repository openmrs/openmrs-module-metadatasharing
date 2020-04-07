package org.openmrs.module.metadatasharing.web.bean;

import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleUtil;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

@Configuration("metadatasharing.dynamicBeanConfiguration")
public class DynamicBeanConfiguration {
	/**
	 * The DefaultAnnotationHandlerMapping class was deprecated and eventually
	 * removed in Spring 5 The recommended replacement class
	 * RequestMappingHandlerMapping was introduced in Spring 3.1.0 which is not
	 * available on OpenMRS platform versions 1.9.x and 1.1.0.x which run Spring
	 * 3.0.5 That's why we can't just statically replace this class in the
	 * webModuleApplicationContext.xml file.
	 */
	@Bean
	public AbstractHandlerMapping getHandlerMapping() throws Exception {

		Class<?> clazz;
		if (ModuleUtil.compareVersion(OpenmrsConstants.OPENMRS_VERSION_SHORT, "2.4") < 0) {
			clazz = Context.loadClass("org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping");
		} else {
			clazz = Context
					.loadClass("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping");
		}

		return (AbstractHandlerMapping) clazz.newInstance();
	}

}

