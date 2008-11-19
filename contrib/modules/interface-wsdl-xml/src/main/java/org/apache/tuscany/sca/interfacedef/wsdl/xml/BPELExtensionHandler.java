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

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A WSDL extension processor for extension elements introduced by BPEL - in particular
 * the <partnerLinkType.../> elements
 *
 * @version $Rev$ $Date$
 */
public class BPELExtensionHandler implements ExtensionSerializer, ExtensionDeserializer {

	private final String localName = "partnerLinkType";
	private final String roleName = "role";

	/**
	 * Marshals the BPEL partner link type extension element to XML
	 * See (@link javax.wsdl.extensions.ExtensionSerializer)
	 */
	@SuppressWarnings("unchecked")
	public void marshall(Class parentType, QName elementType, ExtensibilityElement theElement,
			PrintWriter writer, Definition def, ExtensionRegistry extReg)
			throws WSDLException {
		// The format of the Partner Link Type in XML is as follows:
		// <foo:partnerLinkType name="bar">
		//    <foo:role name="somename" portType="xyz:portTypeName"/>
		//    <foo:role name="othername" portType="xyz:portTypeName2"/>
		// <foo:partnerLinkType>
		BPELPartnerLinkTypeExt thePLinkType = (BPELPartnerLinkTypeExt) theElement;
		QName theType = thePLinkType.getElementType();

		writer.println("<" + theType.toString() +
				       " name=\"" + thePLinkType.getName() + "\">");
		for( int i = 0; i < 2; i++ ) {
			if( thePLinkType.getRoleName( i ) != null ) {
				writer.println( "<" + theType.getPrefix() + ":role"
						       + " name=\"" + thePLinkType.getRoleName(i) + "\" portType=\""
						       + thePLinkType.getRolePortType(i) + "\">");
			} // end if
		} // end for
		writer.println("</" + theType.toString() + ">");
	} // end marshall

	/**
	 * Unmarshals the BPEL partner link type element from XML
	 * See (@link javax.wsdl.extensions.ExtensionDeserializer)
	 * The format of the Partner Link Type in XML is as follows:
	 *   <foo:partnerLinkType name="bar">
	 *       <foo:role name="somename" portType="xyz:portTypeName"/>
	 *       <foo:role name="othername" portType="xyz:portTypeName2"/>
	 *   <foo:partnerLinkType>
	 *
	 *   One role is mandatory, the second is optional.
	 */
	@SuppressWarnings("unchecked")
	public ExtensibilityElement unmarshall(Class theClass, QName elementType,
			Element theElement, Definition def, ExtensionRegistry extReg)
			throws WSDLException {

		// Check that this elementType really is a partnerLinkType element
		if( !elementType.getLocalPart().equals(localName) ) return null;
		BPELPartnerLinkTypeExt theExtension = new BPELPartnerLinkTypeExt();
		theExtension.setElementType(elementType);
		theExtension.setName( theElement.getAttribute("name") );

		// Fetch the child "role" elements
		NodeList theRoles = theElement.getElementsByTagNameNS("*", roleName);
		for ( int i=0; i < theRoles.getLength(); i++ ) {
			if( i > 1 ) break;
			Element roleNode = (Element)theRoles.item(i);
			String roleName = roleNode.getAttribute("name");
			String portType = roleNode.getAttribute("portType");
			if (portType == null || portType.length() == 0) {
			    // Fetch the child "portType" element
			    NodeList portTypesNodes = roleNode.getElementsByTagNameNS("*", "portType");
			    for (int p = 0; p < portTypesNodes.getLength(); p++) {
			        Element portTypeNode = (Element)portTypesNodes.item(p);
	                        portType = portTypeNode.getAttribute("name");
			        break;
			    }
			}
                        // The PortType attribute is a QName in prefix:localName format - convert to a QName
                        QName rolePortType = getQNameValue( def, portType );
			theExtension.setRole( i, roleName, rolePortType );
		} // end for
		return theExtension;
	} // end unmarshall


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
    } // end getQNameValue

} // end BPELExtensionHandler
