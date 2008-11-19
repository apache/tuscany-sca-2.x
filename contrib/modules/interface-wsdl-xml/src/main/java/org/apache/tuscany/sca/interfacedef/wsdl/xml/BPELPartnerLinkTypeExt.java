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
package org.apache.tuscany.sca.interfacedef.wsdl.xml;

import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;

/**
 * A class to serve as the extensibility element for BPEL partnerLinkType elements
 *
 * @version $Rev$ $Date$
 */
public class BPELPartnerLinkTypeExt implements ExtensibilityElement {

	private QName elementType = null;
	private String linkTypeName = null;
	private String[] roleNames = new String[2];
	private QName[] rolePortTypes = new QName[2];

	// -- methods required by the Extensibility Element interface
	public QName getElementType() {
		return elementType;
	}

	public Boolean getRequired() {
		return true;
	}

	public void setElementType(QName theName ) {
		elementType = theName;
	}

	public void setRequired(Boolean required) {
		// intentionally left blank
	}

	// -- other methods

	public void setName( String theName ) {
		linkTypeName = theName;
	}

	public String getName() {
		return linkTypeName;
	}

	public void setRole( int i, String name, QName portType ) {
		if( i > 1 ) return;
		roleNames[i] = name;
		rolePortTypes[i] = portType;
	}

	public String getRoleName( int i ) {
		if( i > 1 ) return null;
		return roleNames[i];
	}

	public QName getRolePortType( int i ) {
		if( i > 1 ) return null;
		return rolePortTypes[i];
	}

} // end BPELPartnerLinkTypeExt
