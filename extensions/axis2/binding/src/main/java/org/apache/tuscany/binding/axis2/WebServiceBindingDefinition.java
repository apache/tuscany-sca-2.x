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
package org.apache.tuscany.binding.axis2;


import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.model.BindingDefinition;
import org.osoa.sca.Version;

/**
 * Represents a Celtix binding configuration in an assembly
 *
 * @version $Rev$ $Date$
 */
public class WebServiceBindingDefinition extends BindingDefinition {
    public static final QName CONVERSATION_ID_REFPARM_QN = new QName(Version.XML_NAMESPACE_1_0,"conversationID");
    private Definition definition;
    private Port port;
    private Service service;
    //private String portURI;
    private String uri;
    public WebServiceBindingDefinition(Definition definition, Port port, String uri, String portURI, Service service) {
        this.definition = definition;
        this.port = port;
        this.uri = uri;
        //this.portURI = portURI;
        this.service = service;
    }

    public Port getWSDLPort() {
        return port;
    }

    public Service getWSDLService() {
        return service;
    }

    public void setWSDLPort(Port value) {
        port = value;
    }

    public Definition getWSDLDefinition() {
        return definition;
    }

    public void setWSDLDefinition(Definition def) {
        definition = def;
    }

    //    public void setPortURI(String uri) {
    //        portURI = uri;
    //    }

    public String getURI() {
        return uri;
    }

    public void setURI(String theUri) {
        this.uri = theUri;
    }
}
