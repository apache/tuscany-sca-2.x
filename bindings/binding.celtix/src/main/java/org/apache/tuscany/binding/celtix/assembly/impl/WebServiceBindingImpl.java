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
package org.apache.tuscany.binding.celtix.assembly.impl;

import java.util.Collection;
import java.util.List;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;

import commonj.sdo.helper.TypeHelper;
import org.apache.tuscany.binding.celtix.assembly.WebServiceBinding;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.loader.WSDLDefinitionRegistry;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.impl.BindingImpl;
import org.objectweb.celtix.Bus;

/**
 * An implementation of WebServiceBinding.
 */
public class WebServiceBindingImpl extends BindingImpl implements WebServiceBinding {

    private WSDLDefinitionRegistry wsdlRegistry;
    
    private Definition definition;
    private Port port;
    private Service service;
    private String portURI;
    private TypeHelper typeHelper;
    private ResourceLoader resourceLoader;
    private Bus bus;

    /**
     * Constructor
     */
    protected WebServiceBindingImpl(WSDLDefinitionRegistry reg) {
        wsdlRegistry = reg;
    }

    /**
     * @see org.apache.tuscany.binding.celtix.assembly.WebServiceBinding#getWSDLPort()
     */
    public Port getWSDLPort() {
        return port;
    }

    public Service getWSDLService() {
        return service;
    }
    


    /**
     * @see org.apache.tuscany.binding.celtix.assembly.WebServiceBinding#setWSDLPort(javax.wsdl.Port)
     */
    public void setWSDLPort(Port value) {
        checkNotFrozen();
        port = value;
    }

    /**
     * @see org.apache.tuscany.binding.celtix.assembly.WebServiceBinding#getWSDLDefinition()
     */
    public Definition getWSDLDefinition() {
        return definition;
    }

    /**
     * @see WebServiceBinding#setWSDLDefinition(javax.wsdl.Definition)
     */
    public void setWSDLDefinition(Definition def) {
        checkNotFrozen();
        definition = def;
    }

    /**
     * @param uri The portURI to set.
     */
    public void setPortURI(String uri) {
        portURI = uri;
    }

    public TypeHelper getTypeHelper() {
        return typeHelper;
    }

    public void setTypeHelper(TypeHelper pTypeHelper) {
        this.typeHelper = pTypeHelper;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    /**
     * @see BindingImpl#initialize(org.apache.tuscany.model.assembly.AssemblyContext)
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
            //FIXME pass the current application resource loader
            List<Definition> definitions = wsdlRegistry.getDefinitionsForNamespace(portNamespace, null);
            if (definitions == null) {
                throw new IllegalArgumentException("Cannot find WSDL definition for " + portNamespace);
            }
            for (Definition def : definitions) {

                // Find the port with the given name
                for (Service serv : (Collection<Service>)def.getServices().values()) {
                    Port p = serv.getPort(portName);
                    if (p != null) {
                        service = serv;
                        definition = def;
                        port = p;
                        return;
                    }
                }
            }
            throw new IllegalArgumentException("Cannot find WSDL port " + portURI);
        }
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus b) {
        bus = b;
    }

}
