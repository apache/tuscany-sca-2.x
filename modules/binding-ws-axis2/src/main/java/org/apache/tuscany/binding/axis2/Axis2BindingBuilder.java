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

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.binding.axis2.util.TuscanyAxisConfigurator;
import org.apache.tuscany.binding.ws.WebServiceBinding;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;

/**
 * Builds a {@link org.osoa.sca.annotations.Service} or {@link org.apache.tuscany.spi.component.ReferenceBinding} configured
 * with the Axis2 binding
 */
public class Axis2BindingBuilder extends BindingBuilderExtension<WebServiceBinding> {
    private static final String OM_DATA_BINDING = OMElement.class.getName();

    // TODO: what to do about the base URI?
    private static final String BASE_URI = "http://localhost:8080/";

//    private ServletHost servletHost;

    private ConfigurationContext configContext;
//
//    private WorkContext workContext;

    public Axis2BindingBuilder() {
        initAxis();
    }

//    public void setServletHost(ServletHost servletHost) {
//        this.servletHost = servletHost;
//    }
//
//    public void setWorkContext(WorkContext workContext) {
//        this.workContext = workContext;
//    }

    @Override
    protected Class<WebServiceBinding> getBindingType() {
        return WebServiceBinding.class;
    }

    public ReferenceBinding build(CompositeReference boundReferenceDefinition,
                                  WebServiceBinding bindingDefinition,
                                  DeploymentContext context) throws BuilderException {
      return new Axis2WSReference(null, null);
    }
    
//    @SuppressWarnings("unchecked")
//    public ServiceBinding build(
//        ServiceDefinition serviceDefinition,
//        WebServiceBindingDefinition wsBinding, DeploymentContext deploymentContext) {
//
//        try {
//            // Set the default databinding
//            ServiceContract outboundContract = serviceDefinition.getServiceContract();
//            if (outboundContract instanceof WSDLServiceContract) {
//                outboundContract.setDataBinding(OM_DATA_BINDING);
//            }
//
//            // TODO: TUSCANY-1148, <binding.ws> with no wsdl only works with <interface.wsdl>
//            if (wsBinding.getWSDLDefinition() == null) {
//                if (outboundContract instanceof WSDLServiceContract) {
//                    String ns = ((WSDLServiceContract)outboundContract).getPortType().getQName().getNamespaceURI();
//                    wsBinding.setWSDLDefinition(wsdlReg.getDefinition(ns));
//                } else {
//                    throw new IllegalStateException("<binding.ws> with no WSDL requires using <interface.wsdl>");
//                }
//            }
//
//            // FIXME: We need to define how the WSDL PortType is honored in the case that
//            // both the service.ws and interface.wsdl are in place.
//            // The WSDL portType from the WSDL Port decides the incoming SOAP message format
//            // There are also cases that interface.java is used.
//
//            ServiceContract<?> inboundContract;
//            Port port = wsBinding.getWSDLPort();
//            if (port == null) {
//                // FIXME: [rfeng] No WSDL is referenced by service.ws, we need to create one from
//                // the outbound service contract if it's JavaServiceContract
//                inboundContract = outboundContract;
//            }
//
//            PortType portType = wsBinding.getWSDLPort().getBinding().getPortType();
//            inboundContract = introspector.introspect(portType);
//
//            // FIXME:  
//            inboundContract.setInterfaceClass(serviceDefinition.getServiceContract().getInterfaceClass());
//            inboundContract.setDataBinding(OM_DATA_BINDING);
//            inboundContract.setCallbackName(serviceDefinition.getServiceContract().getCallbackName());
//            
////            inboundContract.setInteractionScope(serviceDefinition.getServiceContract().getInteractionScope()); // TODO: gone
//
//// TODO: gone            
////            try {
////                wireService.checkCompatibility(inboundContract, outboundContract, true);
////            } catch (IncompatibleServiceContractException e) {
////                throw new Axis2BindingBuilderRuntimeException(e);
////            }
//
//            URI axisServiceName;
//            if (wsBinding.isSpec10Compliant()) {
//                wsBinding.setActualURI(computeActualURI(wsBinding, BASE_URI, serviceDefinition.getTarget(), serviceDefinition.getUri()));
//                String name = wsBinding.getActualURI().getPath();
//                if (name != null && name.length() > 1 && name.startsWith("/")) {
//                    name = name.substring(1);
//                }
//                axisServiceName = URI.create(name);
//            } else {
//                axisServiceName = serviceDefinition.getUri();  // TODO: verify name
//            }
//
//            ServiceBinding serviceBinding =
//                new Axis2ServiceBinding(axisServiceName, outboundContract, inboundContract, wsBinding,
//                    servletHost, configContext, workContext);
//            return serviceBinding;
//
//        } catch (InvalidServiceContractException e) {
//            throw new Axis2BindingBuilderRuntimeException(e);
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public ReferenceBinding buildx(
//        ReferenceDefinition boundReferenceDefinition,
//        WebServiceBinding wsBinding,
//        DeploymentContext deploymentContext) {
//
//        // Set the default binding
//        ServiceContract inboundContract = boundReferenceDefinition.getServiceContract();
//        if (inboundContract instanceof WSDLServiceContract) {
//            inboundContract.setDataBinding(OM_DATA_BINDING);
//        }
//
//        // TODO: TUSCANY-1148, <binding.ws> with no wsdl only works with <interface.wsdl>
//        if (wsBinding.getWSDLDefinition() == null) {
//            if (inboundContract instanceof WSDLServiceContract) {
//                String ns = ((WSDLServiceContract)inboundContract).getPortType().getQName().getNamespaceURI();
//                wsBinding.setWSDLDefinition(wsdlReg.getDefinition(ns));
//            } else {
//                throw new IllegalStateException("<binding.ws> with no WSDL requires using <interface.wsdl>");
//            }
//        }
//
//        // FIXME: We need to define how the WSDL PortType is honored in the case that
//        // both the binding.ws and interface.wsdl are in place
//        // The WSDL portType from the WSDL Port decides the incoming SOAP message format
//
//        ServiceContract<?> outboundContract = inboundContract;
//        Port port = wsBinding.getWSDLPort();
//        if (port == null) {
//            // FIXME: [rfeng] No WSDL is referenced by binding.ws, we need to create one from
//            // the inbound service contract if it's JavaServiceContract
//            outboundContract = inboundContract;
//        }
//        PortType portType = port.getBinding().getPortType();
//        try {
//            outboundContract = introspector.introspect(portType);
//        } catch (InvalidServiceContractException e) {
//            new Axis2BindingBuilderRuntimeException(e);
//        }
//
//        // Set the default databinding
//        outboundContract.setDataBinding(OM_DATA_BINDING);
//        //FIXME ... need to figure out how to specify scope on wsdl.
////        outboundContract.setInteractionScope(inboundContract.getInteractionScope()); // methdod gone
//
//// TODO: gone        
////        try {
////            wireService.checkCompatibility(inboundContract, outboundContract, true);
////        } catch (IncompatibleServiceContractException e) {
////            throw new Axis2BindingBuilderRuntimeException(e);
////        }
//
//        if (wsBinding.isSpec10Compliant()) {
//            wsBinding.setActualURI(computeActualURI(wsBinding, BASE_URI, null, boundReferenceDefinition.getUri()));
//        }
//
//        return new Axis2ReferenceBinding(boundReferenceDefinition.getUri(), wsBinding,
//            inboundContract, outboundContract, workContext);
//
//    }
//
//    protected Class<WebServiceBindingDefinition> getBindingType() {
//        return WebServiceBindingDefinition.class;
//    }
//
    protected void initAxis() {
        // TODO: consider having a system component wrapping the Axis2 ConfigContext
        try {
            this.configContext = new TuscanyAxisConfigurator().getConfigurationContext();
        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO better exception
        }
    }
//    
//    /**
//     * Compute the endpoint URI based on section 2.1.1 of the WS binding spec
//     * 1. The URIs in the endpoint(s) of the referenced WSDL, which may be relative
//     * 2. The URI specified by the wsa:Address element of the wsa:EndpointReference, which may be relative
//     * 3. The explicitly stated URI in the "uri" attribute of the binding.ws element, which may be relative,
//     * 4. The implicit URI as defined by in section 1.7 in the SCA Assembly spec 
//     * If the <binding.ws> has no wsdlElement but does have a uri attribute then the uri takes precidence
//     * over any implicitly used WSDL.
//     * @param parent 
//     */
//    protected URI computeActualURI(WebServiceBindingDefinition wsBinding, String baseURI, URI componentURI, URI bindingName) {
//        URI wsdlURI = null;         
//        if (wsBinding.getServiceName() != null && wsBinding.getBindingName() == null) {
//            // <binding.ws> explicitly points at a wsdl port, may be a relative URI
//            wsdlURI = wsBinding.getPortURI();
//        }
//        if (wsdlURI != null && wsdlURI.isAbsolute()) {
//            if (wsBinding.getURI() != null && (wsBinding.getServiceName() != null && wsBinding.getBindingName() == null)) {
//                throw new IllegalArgumentException("binding URI cannot be used with absolute WSDL endpoint URI");
//            }
//            return URI.create(wsdlURI.toString());
//        }
//        
//        // there is no wsdl port endpoint URI or that URI is relative
//        
//        URI bindingURI = null;
//        if (wsBinding.getURI() != null) {
//            bindingURI = URI.create(wsBinding.getURI());
//        }
//
//        if (bindingURI != null && bindingURI.isAbsolute()) {
//            if (wsdlURI != null) {
//                return URI.create(bindingURI + "/" + wsdlURI).normalize();
//            } else {
//                return bindingURI;
//            }
//        }
//        
//        if (componentURI == null) { // null for references
//            wsdlURI = wsBinding.getPortURI();
//            if (bindingURI != null) {
//                return URI.create(wsdlURI + "/" + bindingURI).normalize();
//            } else {
//                return wsdlURI;
//            }
//        }
//        
//
//        // TODO: TUSCANY-xxx, how to tell if component has multiple services using <binding.ws>?
//        //        boolean singleService = (parent != null) && (((Component)parent.getChild(componentURI.toString())).getInboundWires().size() == 1);
//        //        if (bindingURI == null && !singleService) {
//
//        if (bindingURI == null) {
//            bindingURI = bindingName;
//        }
//
//        if (componentURI.isAbsolute()) {
//            if (bindingURI == null && wsdlURI == null) {
//                return componentURI;
//            } else if (wsdlURI == null) {
//                return URI.create(componentURI + "/" + bindingURI).normalize();
//            } else if (bindingURI == null) {
//                return URI.create(componentURI + "/" + wsdlURI).normalize();
//            } else {
//                return URI.create(componentURI + "/" + bindingURI + "/" + wsdlURI).normalize();
//            }
//        }
//                
//        String actualURI = "";
//
//        if (bindingURI == null) {
//            actualURI = baseURI + "/" + componentURI + "/";
//        } else {
//            actualURI = baseURI + "/" + componentURI + "/" + bindingURI + "/";
//        }
//
//        if (wsdlURI != null) {
//            actualURI = actualURI + wsdlURI.toString();
//        }
//
//        if (actualURI.endsWith("/")) {
//            actualURI = actualURI.substring(0, actualURI.length() -1);
//        }
//        
//        // normalize to handle any . or .. occurances 
//        return URI.create(actualURI).normalize();
//    }

}
