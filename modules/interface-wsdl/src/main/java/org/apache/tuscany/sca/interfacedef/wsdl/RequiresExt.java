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
package org.apache.tuscany.sca.interfacedef.wsdl;

import java.util.ArrayList;
import java.util.List;

import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;

/**
 * A class to serve as the extensibility element for policy requires elements
 */
public class RequiresExt implements ExtensibilityElement {

	private QName elementType = null;
	private List<QName> intents = new ArrayList<QName>();

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
	public List<QName> getIntents(){
	    return intents;
	}
}
