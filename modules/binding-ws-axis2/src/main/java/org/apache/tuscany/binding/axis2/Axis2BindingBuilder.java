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

import javax.wsdl.Port;
import javax.wsdl.extensions.soap.SOAPAddress;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.binding.axis2.util.TuscanyAxisConfigurator;
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

    // TODO: what to do about the base URI?
    private static final String BASE_URI = "http://localhost:8080/";

    private ServletHostExtensionPoint servletHost;

    private ConfigurationContext configContext;
//
//    private WorkContext workContext;

    public Axis2BindingBuilder() {
        initAxis();
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

        // Set to use the Axiom data binding 
        compositeReference.getInterfaceContract().getInterface().setDefaultDataBinding(OMElement.class.getName());        

        URI targetURI = wsBinding.getURI() != null ? URI.create(wsBinding.getURI()) : URI.create("foo");
        URI name = URI.create(context.getComponentId() + "#" + wsBinding.getName());

        return new Axis2ReferenceBinding(name, targetURI, wsBinding);
    }

    @Override
    public ServiceBinding build(CompositeService compositeService, WebServiceBinding wsBinding, DeploymentContext context) throws BuilderException {

        InterfaceContract interfaceContract = compositeService.getInterfaceContract();
        
        // Set to use the Axiom data binding 
        interfaceContract.getInterface().setDefaultDataBinding(OMElement.class.getName());        

        URI name = URI.create(context.getComponentId() + "#" + wsBinding.getName());
        URI uri = computeActualURI(wsBinding, BASE_URI, name, compositeService.getName()).normalize();

        ServiceBinding serviceBinding = new Axis2ServiceBinding(uri, interfaceContract, null, wsBinding, servletHost, configContext, null);

        return serviceBinding;
    }

    protected void initAxis() {
        // TODO: consider having a system component wrapping the Axis2 ConfigContext
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
    protected URI computeActualURI(WebServiceBinding wsBinding, String baseURI, URI componentURI, String bindingName) {
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
        
        // there is no wsdl port endpoint URI or that URI is relative
        
        URI bindingURI = null;
        if (wsBinding.getURI() != null) {
            bindingURI = URI.create(wsBinding.getURI());
        }

        if (bindingURI != null && bindingURI.isAbsolute()) {
            if (wsdlURI != null) {
                return URI.create(bindingURI + "/" + wsdlURI);
            } else {
                return bindingURI;
            }
        }
        
        if (componentURI == null) { // null for references
            wsdlURI = getEndpoint(wsBinding.getPort());
            if (bindingURI != null) {
                return URI.create(wsdlURI + "/" + bindingURI);
            } else {
                return wsdlURI;
            }
        }

        // TODO: TUSCANY-xxx, how to tell if component has multiple services using <binding.ws>?
        //        boolean singleService = (parent != null) && (((Component)parent.getChild(componentURI.toString())).getInboundWires().size() == 1);
        //        if (bindingURI == null && !singleService) {

        if (bindingURI == null) {
            bindingURI = URI.create(bindingName);
        }

        if (componentURI.isAbsolute()) {
            if (bindingURI == null && wsdlURI == null) {
                return componentURI;
            } else if (wsdlURI == null) {
                return URI.create(componentURI + "/" + bindingURI);
            } else if (bindingURI == null) {
                return URI.create(componentURI + "/" + wsdlURI);
            } else {
                return URI.create(componentURI + "/" + bindingURI + "/" + wsdlURI);
            }
        }
                
        String actualURI = "";

        if (bindingURI == null) {
            actualURI = baseURI + "/" + componentURI + "/";
        } else {
            actualURI = baseURI + "/" + componentURI + "/" + bindingURI + "/";
        }

        if (wsdlURI != null) {
            actualURI = actualURI + wsdlURI.toString();
        }

        if (actualURI.endsWith("/")) {
            actualURI = actualURI.substring(0, actualURI.length() -1);
        }
        
        return URI.create(actualURI);
    }

    /**
     * Returns the endpoint of a given port.
     */
    protected URI getEndpoint(Port wsdlPort) {
        final List wsdlPortExtensions = wsdlPort.getExtensibilityElements();
        for (final Object extension : wsdlPortExtensions) {
            if (extension instanceof SOAPAddress) {
                return URI.create(((SOAPAddress) extension).getLocationURI());
            }
        }
        return null;
    }
}
