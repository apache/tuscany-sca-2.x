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
package org.apache.tuscany.sca.assembly.impl;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Extension;

public class ExtensionImpl implements Extension {
	private QName qName;
	private Object value;
	boolean isAttribute = false;

	public ExtensionImpl() {
		
	}
	
	public ExtensionImpl(QName qName, Object value, boolean isAttribute) {
		this.qName = qName;
		this.value = value;
		this.isAttribute = isAttribute;
	}
	
	public QName getQName() {
		return qName;
	}

	public void setQName(QName qName) {
		this.qName = qName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isAttribute() {
		return isAttribute;
	}

	public void setIsAttribute(boolean isAttribute) {
		this.isAttribute = isAttribute;
	}
}
