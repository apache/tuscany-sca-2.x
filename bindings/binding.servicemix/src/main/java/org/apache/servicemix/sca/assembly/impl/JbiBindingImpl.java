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
package org.apache.servicemix.sca.assembly.impl;

import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.apache.servicemix.sca.assembly.JbiBinding;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.impl.BindingImpl;

/**
 * An implementation of the model object '<em><b>Web Service Binding</b></em>'.
 */
public class JbiBindingImpl extends BindingImpl implements JbiBinding {

    private String portURI;
    private QName serviceName;
    private String endpointName;
    private QName interfaceName;
    private Definition definition;
    private Service service;
    private PortType portType;
    private Port port;


    /**
     * Constructor
     */
    protected JbiBindingImpl() {
    }

    /* (non-Javadoc)
     * @see org.apache.servicemix.sca.assembly.JbiBinding#getPortURI()
     */
    public String getPortURI() {
        return portURI;
    }

    /* (non-Javadoc)
     * @see org.apache.servicemix.sca.assembly.JbiBinding#setPortURI(java.lang.String)
     */
    public void setPortURI(String portURI) {
        this.portURI = portURI;
    }

    /* (non-Javadoc)
     * @see org.apache.servicemix.sca.assembly.JbiBinding#getServiceName()
     */
    public QName getServiceName() {
        return serviceName;
    }

    /* (non-Javadoc)
     * @see org.apache.servicemix.sca.assembly.JbiBinding#getEndpointName()
     */
    public String getEndpointName() {
        return endpointName;
    }

    /* (non-Javadoc)
     * @see org.apache.servicemix.sca.assembly.JbiBinding#getInterfaceName()
     */
    public QName getInterfaceName() {
        return interfaceName;
    }

    /* (non-Javadoc)
     * @see org.apache.servicemix.sca.assembly.JbiBinding#getDefinition()
     */
    public Definition getDefinition() {
        return definition;
    }

    /* (non-Javadoc)
     * @see org.apache.servicemix.sca.assembly.JbiBinding#getService()
     */
    public Service getService() {
        return service;
    }

    /* (non-Javadoc)
     * @see org.apache.servicemix.sca.assembly.JbiBinding#getPort()
     */
    public Port getPort() {
        return port;
    }

    /* (non-Javadoc)
     * @see org.apache.servicemix.sca.assembly.JbiBinding#getPortType()
     */
    public PortType getPortType() {
        return portType;
    }

    /**
     * @see org.apache.tuscany.model.assembly.impl.BindingImpl#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);

        // Get the service name and endpoint name
        String[] parts = split(portURI);
        serviceName = new QName(parts[0], parts[1]);
        endpointName = parts[2];
        
        // Load the WSDL definitions for the given namespace
        List<Definition> definitions = modelContext.getAssemblyLoader().loadDefinitions(parts[0]);
        if (definitions != null) {
            for (Definition definition : definitions) {
                Service service = definition.getService(serviceName);
                if (service != null) {
                    Port port = service.getPort(endpointName);
                    if (port != null) {
                        this.service = service;
                        this.port = port;
                        this.portType = port.getBinding().getPortType();
                        this.interfaceName = portType.getQName();
                        this.definition = definition;
                        return;
                    }
                }
            }
        }
    }

    protected String[] split(String uri) {
        char sep;
        uri = uri.trim();
        if (uri.indexOf('/') > 0) {
            sep = '/';
        } else {
            sep = ':';
        }
        int idx1 = uri.lastIndexOf(sep);
        int idx2 = uri.lastIndexOf(sep, idx1 - 1);
        String epName = uri.substring(idx1 + 1);
        String svcName = uri.substring(idx2 + 1, idx1);
        String nsUri   = uri.substring(0, idx2);
        return new String[] { nsUri, svcName, epName };
    }
    
}
