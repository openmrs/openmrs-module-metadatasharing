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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.type.TextType;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;
import org.openmrs.module.metadatasharing.serializer.MetadataSerializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Allows to persist objects as XML.
 */
@SuppressWarnings("rawtypes")
public class XMLSerializableType implements UserType, ParameterizedType {
	
	private static final String CLASSNAME = "class";
	
	private static final int[] SQL_TYPES = { Types.VARCHAR };
	
	private XStream xstream = new MetadataSerializer().getXStream();
	
	private Class returnedClass;
	
	private Properties parameters;
	
	/**
	 * @see org.hibernate.usertype.ParameterizedType#setParameterValues(java.util.Properties)
	 */
	@Override
	public void setParameterValues(Properties parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
	 */
	@Override
	public Object assemble(Serializable value, Object owner) throws HibernateException {
		return xstream.fromXML((String) value);
	}
	
	/**
	 * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
	 */
	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return xstream.fromXML(xstream.toXML(value));
	}
	
	/**
	 * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
	 */
	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return xstream.toXML(value);
	}
	
	/**
	 * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if ((x == null) && (y == null)) {
			return true;
		}
		if ((x == null) || (y == null)) {
			return false;
		}
		return x.equals(y);
	}
	
	/**
	 * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
	 */
	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}
	
	/**
	 * @see org.hibernate.usertype.UserType#isMutable()
	 */
	@Override
	public boolean isMutable() {
		return true;
	}
	
	/**
	 * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[],
	 *      java.lang.Object)
	 */
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
		String xmlString = (String) new TextType().nullSafeGet(rs, names[0]);
		return xstream.fromXML(xmlString);
	}
	
	/**
	 * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement,
	 *      java.lang.Object, int)
	 */
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
		String xmlString = xstream.toXML(value);
		new TextType().nullSafeSet(st, xmlString, index);
	}
	
	/**
	 * @see org.hibernate.usertype.UserType#replace(java.lang.Object, java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return deepCopy(original);
	}
	
	/**
	 * @see org.hibernate.usertype.UserType#returnedClass()
	 */
	@Override
	public Class returnedClass() {
		if (returnedClass == null) {
			String className = parameters.getProperty(CLASSNAME);
			if (className == null) {
				className = "";
			}
			try {
				returnedClass = Class.forName(className);
			}
			catch (ClassNotFoundException e) {
				IllegalArgumentException ex = new IllegalArgumentException("The parameter 'class' is not correct : "
				        + e.getMessage());
				ex.setStackTrace(e.getStackTrace());
				throw ex;
			}
		}
		
		return returnedClass;
	}
	
	/**
	 * @see org.hibernate.usertype.UserType#sqlTypes()
	 */
	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}
	
}
