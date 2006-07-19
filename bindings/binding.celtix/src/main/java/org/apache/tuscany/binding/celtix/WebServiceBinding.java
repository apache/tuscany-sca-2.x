/**
 *
 *  Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.celtix;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;

import org.apache.tuscany.spi.model.Binding;

/**
 * Represents a Celtix binding configuration in an assembly
 *
 * @version $Rev$ $Date$
 */
public class WebServiceBinding extends Binding {

    private Definition definition;
    private Port port;
    private Service service;
    //private String portURI;
    private String uri;

    public WebServiceBinding(Definition definition, Port port, String uri, String portURI, Service service) {
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
