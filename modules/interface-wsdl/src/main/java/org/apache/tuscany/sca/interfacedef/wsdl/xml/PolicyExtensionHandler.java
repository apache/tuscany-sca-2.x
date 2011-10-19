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

import java.io.PrintWriter;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.interfacedef.wsdl.RequiresExt;
import org.w3c.dom.Element;

/**
 * A WSDL extension processor for extension policy elements of the form:
 * 
 * <sca:requires intents="sca:SOAP.v1_1"/>
 * 
 */
public class PolicyExtensionHandler implements ExtensionSerializer, ExtensionDeserializer {

	/**
	 * Marshals the requires extension element to XML
	 * See (@link javax.wsdl.extensions.ExtensionSerializer)
	 */
	@SuppressWarnings("unchecked")
	public void marshall(Class parentType, 
	                     QName elementType, 
	                     ExtensibilityElement theElement,
			             PrintWriter writer, 
			             Definition def, 
			             ExtensionRegistry extReg)
			throws WSDLException {

		RequiresExt requires = (RequiresExt) theElement;
		QName theType = requires.getElementType();

		writer.println("<" + theType.toString() +
				       " intents=\"");
		
		for(QName intentName : requires.getIntents()){
		    writer.println(intentName + " ");
		}
		        
		writer.println("\">");
	} 

	/**
	 * Unmarshals the requires extension element from XML
	 */
	@SuppressWarnings("unchecked")
	public ExtensibilityElement unmarshall(Class theClass, 
	                                       QName elementType,
			                               Element theElement, 
			                               Definition def, 
			                               ExtensionRegistry extReg)
			throws WSDLException {

		// Check that this elementType really is a requires element
		if( !elementType.getLocalPart().equals("requires") ){
		    return null;
		}
		
		RequiresExt requires = new RequiresExt();
		requires.setElementType(elementType);
		
		String intents = theElement.getAttribute("intents");
        String[] intentArray = intents.split(" +");
        
        for (int i=0; i < intentArray.length; i++){
            String intentNameString = intentArray[i];
            QName intentQName = getQNameValue( def, intentNameString);
            requires.getIntents().add(intentQName);
        }
        
		return requires;
	} 

    /**
     * Returns a QName from a string.
     * @param definition - a WSDL Definition
     * @param value - the String from which to form the QName in the form "pref:localName"
     * @return
     */
    protected QName getQNameValue(Definition definition, String value) {
        if (value != null && definition != null) {
            int index = value.indexOf(':');
            String prefix = index == -1 ? "" : value.substring(0, index);
            String localName = index == -1 ? value : value.substring(index + 1);
            String ns = definition.getNamespace(prefix);
            if (ns == null) {
                ns = "";
            }
            return new QName(ns, localName, prefix);
        } else {
            return null;
        }
    }
}
