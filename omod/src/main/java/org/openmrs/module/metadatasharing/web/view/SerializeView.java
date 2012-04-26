package org.openmrs.module.metadatasharing.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

/**
 * An abstract view which requires an object which will be serialized to the client
 */
public abstract class SerializeView extends AbstractView {
	
	public SerializeView() {
		super();
	}
	
	/**
	 * Returns the object which will be serialized to the client. It will usually be implemented by
	 * getting the appropriate object form model map
	 * 
	 * @return the object to serialize to the client
	 */
	public abstract Object getObjectToSerialize(@SuppressWarnings("rawtypes") Map model, HttpServletRequest request,
	                                            HttpServletResponse response);
	
}
