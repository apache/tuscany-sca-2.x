/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.binding.axis2.assembly.impl;

import java.util.Collection;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;

import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.impl.BindingImpl;
import org.apache.tuscany.common.resource.ResourceLoader;
import commonj.sdo.helper.TypeHelper;

/**
 * An implementation of WebServiceBinding.
 */
public class WebServiceBindingImpl extends BindingImpl implements WebServiceBinding {

    private Definition definition;
    private Port port;
    private String portURI;
    private TypeHelper typeHelper;
    private ResourceLoader resourceLoader;

    /**
     * Constructor
     */
    protected WebServiceBindingImpl() {
    }

    public TypeHelper getTypeHelper() {
        return typeHelper;
    }

    public void setTypeHelper(TypeHelper typeHelper) {
        this.typeHelper = typeHelper;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * @see org.apache.tuscany.binding.axis2.assembly.WebServiceBinding#getWSDLPort()
     */
    public Port getWSDLPort() {
        return port;
    }

    /**
     * @see org.apache.tuscany.binding.axis2.assembly.WebServiceBinding#setWSDLPort(javax.wsdl.Port)
     */
    public void setWSDLPort(Port value) {
        checkNotFrozen();
        this.port = value;
    }

    /**
     * @see org.apache.tuscany.binding.axis2.assembly.WebServiceBinding#getWSDLDefinition()
     */
    public Definition getWSDLDefinition() {
        return definition;
    }

    /**
     */
    public void setWSDLDefinition(Definition pdefinition) {
        checkNotFrozen();
        this.definition = pdefinition;
    }

    /**
     * @param portURI The portURI to set.
     */
    public void setPortURI(String portURI) {
        this.portURI = portURI;
    }

    /**
     */
    @SuppressWarnings("unchecked")
    public void initialize(AssemblyContext modelContext) {
        if (isInitialized()) {
            return;
        }
        super.initialize(modelContext);

        // Get the WSDL port namespace and name
        if (port == null && portURI != null) {
            int h = portURI.indexOf('#');
            String portNamespace = portURI.substring(0, h);
            String portName = portURI.substring(h + 1);

            // Load the WSDL definitions for the given namespace
            List<Definition> definitions = modelContext.getAssemblyLoader()
                    .loadDefinitions(portNamespace);
            if (definitions == null) {
                throw new IllegalArgumentException("Cannot find WSDL definition for " + portNamespace);
            }
            for (Definition def : definitions) {

                // Find the port with the given name
                for (Service service : (Collection<Service>)def.getServices().values()) {
                    Port prt = service.getPort(portName);
                    if (prt != null) {
                        this.definition = def;
                        this.port = prt;
                        return;
                    }
                }
            }
            throw new IllegalArgumentException("Cannot find WSDL port " + portURI);
        }
    }

}
