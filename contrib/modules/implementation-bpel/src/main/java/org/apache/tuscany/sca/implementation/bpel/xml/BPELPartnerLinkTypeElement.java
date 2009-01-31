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
 */package org.apache.tuscany.sca.implementation.bpel.xml;

 import javax.xml.namespace.QName; 
 
 import javax.wsdl.extensions.ExtensibilityElement;
 import javax.wsdl.PortType;
 
/**
 * Represents a <partnerLinkType.../> element related to a BPEL process
 * - this has attributes:
 *      name
 *      Role1 name
 *      Role1 portType
 *      Role2 name
 *         Role2 portType
 *
 * - in the XML the 2 roles are child elements of the partnerLinkType element, but there
 * seems little point in reflecting this back into this model - it is simpler to include
 * both roles within the representation of the partnerLinkType itself
 *
 * @version $Rev$ $Date$
 */
public class BPELPartnerLinkTypeElement implements ExtensibilityElement {
	
    private QName name;
    private String Role1name = null;
    private QName Role1porttype = null;
    private PortType Role1pType = null;
    private String Role2name = null;
    private QName Role2porttype = null;
    private PortType Role2pType = null;
    private QName elementType = null;
    private Boolean required = false;

    public BPELPartnerLinkTypeElement(QName name) {
        this.name = name;
    }

    public QName getName() {
        return name;
    }

    public void setRole1(String name, QName portType, PortType pType) {
        Role1name = name;
        Role1porttype = portType;
        Role1pType = pType;
    }

    public void setRole2(String name, QName portType, PortType pType) {
        Role2name = name;
        Role2porttype = portType;
        Role2pType = pType;
    }

    public String getRole1Name() {
        return Role1name;
    }

    public String getRole2Name() {
        return Role2name;
    }

    public QName getRole1PortType() {
        return Role1porttype;
    }

    public QName getRole2PortType() {
        return Role2porttype;
    }

    public PortType getRole1pType() {
        return Role1pType;
    }

    public PortType getRole2pType() {
        return Role2pType;
    }

    public QName getElementType() {
        return elementType;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setElementType(QName elementType) {
        this.elementType = elementType;
    }

    public void setRequired(java.lang.Boolean required) {
        this.required = required;
    }

} // end BPELPartnerLinkType
