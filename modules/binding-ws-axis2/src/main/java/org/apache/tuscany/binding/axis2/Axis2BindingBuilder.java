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

import java.net.URI;
import java.util.List;
import java.util.ArrayList;

import javax.wsdl.Port;
import javax.wsdl.extensions.soap.SOAPAddress;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.binding.ws.WebServiceBinding;
import org.apache.tuscany.http.ServletHostExtensionPoint;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;

/**
 * Builds a {@link org.osoa.sca.annotations.Service} or {@link org.apache.tuscany.spi.component.ReferenceBinding} configured
 * with the Axis2 binding
 */
public class Axis2BindingBuilder extends BindingBuilderExtension<WebServiceBinding> {

    private ServletHostExtensionPoint servletHost;
    private ConfigurationContext configContext;

    // track reference bindings and service bindings so that resources can be released
    // needed because the stop methods in ReferenceImpl and ServiceImpl aren't being called
    // TODO: revisit this as part of the lifecycle work
    private List<ReferenceBinding> referenceBindings = new ArrayList<ReferenceBinding>();
    private List<ServiceBinding> serviceBindings = new ArrayList<ServiceBinding>();

    // TODO: what to do about the base URI?
    private static final String BASE_URI = "http://localhost:8080/";

    public Axis2BindingBuilder() {
        initAxis();
    }

    // release resources held by bindings
    // called by stop method of Axis2ModuleActivator
    // needed because the stop methods in ReferenceImpl and ServiceImpl aren't being called
    // TODO: revisit this as part of the lifecycle work
    protected void destroy() {
       for (ReferenceBinding binding : referenceBindings) {
          binding.stop();
       }
       for (ServiceBinding binding : serviceBindings) {
          binding.stop();
       }
    }

    public void setServletHost(ServletHostExtensionPoint servletHost) {
        this.servletHost = servletHost;
    }

    @Override
    protected Class<WebServiceBinding> getBindingType() {
        return WebServiceBinding.class;
    }

    @Override
    public ReferenceBinding build(CompositeReference compositeReference, WebServiceBinding wsBinding, DeploymentContext context) throws BuilderException {

        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract == null) {
            contract = compositeReference.getInterfaceContract();
            wsBinding.setBindingInterfaceContract(contract);
        }

        // Set to use the Axiom data binding 
        contract.getInterface().setDefaultDataBinding(OMElement.class.getName());        

        URI targetURI = wsBinding.getURI() != null ? URI.create(wsBinding.getURI()) : URI.create("foo");
        URI name = URI.create(context.getComponentId() + "#" + compositeReference.getName());

        ReferenceBinding referenceBinding = new Axis2ReferenceBinding(name, targetURI, wsBinding);
        referenceBindings.add(referenceBinding); // track binding so that its resources can be released
        return referenceBinding;
    }

    @Override
    public ServiceBinding build(CompositeService compositeService, WebServiceBinding wsBinding, DeploymentContext context) throws BuilderException {

        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract == null) {
            contract = compositeService.getInterfaceContract();
            wsBinding.setBindingInterfaceContract(contract);
        }

        // Set to use the Axiom data binding 
        contract.getInterface().setDefaultDataBinding(OMElement.class.getName());

        URI uri = computeActualURI(wsBinding, BASE_URI, compositeService).normalize();
        
        // TODO: if <binding.ws> specifies the wsdl service then should create a service for every port

        ServiceBinding serviceBinding = new Axis2ServiceBinding(uri, wsBinding, servletHost, configContext);
        serviceBindings.add(serviceBinding); // track binding so that its resources can be released
        return serviceBinding;
    }

    protected void initAxis() {
        try {
            this.configContext = new TuscanyAxisConfigurator().getConfigurationContext();
        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO better exception
        }
    }

    /**
     * Compute the endpoint URI based on section 2.1.1 of the WS binding spec
     * 1. The URIs in the endpoint(s) of the referenced WSDL, which may be relative
     * 2. The URI specified by the wsa:Address element of the wsa:EndpointReference, which may be relative
     * 3. The explicitly stated URI in the "uri" attribute of the binding.ws element, which may be relative,
     * 4. The implicit URI as defined by in section 1.7 in the SCA Assembly spec 
     * If the <binding.ws> has no wsdlElement but does have a uri attribute then the uri takes precidence
     * over any implicitly used WSDL.
     * @param parent 
     */
    protected URI computeActualURI(WebServiceBinding wsBinding, String baseURI, CompositeService compositeService) {
        
        // TODO: support wsa:Address

        URI wsdlURI = null;         
        if (wsBinding.getServiceName() != null && wsBinding.getBindingName() == null) {
            // <binding.ws> explicitly points at a wsdl port, may be a relative URI
            wsdlURI = getEndpoint(wsBinding.getPort());
        }
        if (wsdlURI != null && wsdlURI.isAbsolute()) {
            if (wsBinding.getURI() != null && (wsBinding.getServiceName() != null && wsBinding.getBindingName() == null)) {
                throw new IllegalArgumentException("binding URI cannot be used with absolute WSDL endpoint URI");
            }
            return URI.create(wsdlURI.toString());
        }
        
        // either there is no wsdl port endpoint URI or that URI is relative
        
        URI bindingURI = null;
        if (wsBinding.getURI() != null) {
            bindingURI = URI.create(wsBinding.getURI());
        }
        if (bindingURI != null && bindingURI.isAbsolute()) {
            // there is an absoulte uri specified on the binding: <binding.ws uri="xxx"
            if (wsdlURI != null) {
                // there is a relative URI in the wsdl port
                return URI.create(bindingURI + "/" + wsdlURI);
            } else {
                return bindingURI;
            }
        }
        
        // both the WSDL endpoint and binding uri are either unspecified or relative so
        // the endpoint is based on the component name and service binding URI
 
        // TODO: hack to get the component for the service
        SCABinding scaBinding = compositeService.getPromotedService().getBinding(SCABinding.class);
        Component component = scaBinding.getComponent();
        URI componentURI = URI.create(component.getName());
        
        String actualURI;
        if (componentURI.isAbsolute()) {
            actualURI = componentURI.toString();
        } else {
            actualURI = baseURI + "/" + componentURI;
        }
        
        // with multiple services the default binding URI is the binding name
        if (bindingURI == null && component.getServices().size() > 1) {
            // if the binding doesn't have a name use the name of the service (assumption, not in spec)
            if (wsBinding.getName() != null) {
                bindingURI = URI.create(wsBinding.getName());
            } else {
                bindingURI = URI.create(compositeService.getName());
            }
        }

        // add any relative binding URI
        if (bindingURI != null) {
            actualURI += "/" + bindingURI;
         }

        // add any relative WSDL port URI
        if (wsdlURI != null) {
            actualURI += "/" + wsdlURI.toString();
        }

        // Axis2 fails if the endpoint has a trailing slash
        if (actualURI.endsWith("/")) {
            actualURI = actualURI.substring(0, actualURI.length() -1);
        }
        
        return URI.create(actualURI);
    }

    /**
     * Returns the endpoint of a given port.
     */
    protected URI getEndpoint(Port wsdlPort) {
        if (wsdlPort != null) {
            List wsdlPortExtensions = wsdlPort.getExtensibilityElements();
            for (Object extension : wsdlPortExtensions) {
                if (extension instanceof SOAPAddress) {
                    return URI.create(((SOAPAddress) extension).getLocationURI());
                }
            }
        }
        return null;
    }
}
