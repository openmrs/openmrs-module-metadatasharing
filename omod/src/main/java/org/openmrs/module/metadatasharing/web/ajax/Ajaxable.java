package org.openmrs.module.metadatasharing.web.ajax;

import org.openmrs.module.metadatasharing.serializer.JSONSerializer;

public abstract class Ajaxable {
	
	public String toJSON() {
		return new JSONSerializer().serialize(this);
	}
	
	@Override
	public String toString() {
		return toJSON();
	}
}
