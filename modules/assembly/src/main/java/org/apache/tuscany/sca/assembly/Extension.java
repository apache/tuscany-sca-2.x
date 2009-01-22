/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.assembly;

import javax.xml.namespace.QName;

/**
 * Base interface for storing contents of extensible assembly model objects.
 * 
 * @version $Rev$ $Date$
 */
public interface Extension {

	/**
	 * Return QName for the extension
	 * @return the extension QName
	 */
	QName getQName();
	
	/**
	 * Set QName for the extension
	 * @param qName the extension QName
	 */
	void setQName(QName qName);
	
	/**
	 * Return the original extension value
	 * @return the extension value
	 */
	Object getValue();
	
	/**
	 * Set the original extension value
	 * @param value the extension value
	 */
	void setValue(Object value);
	
	/**
	 * Return whether or not the extension is an attribute
	 * @return 
	 */
	boolean isAttribute();
	
	/**
	 * Set whether or not the extension is an attribute
	 * @param value
	 */
	void setIsAttribute(boolean isAttribute);
}
