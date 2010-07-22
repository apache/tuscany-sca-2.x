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
package org.apache.tuscany.sca.binding.ws.axis2.provider;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReferenceHelper;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.util.threadpool.ThreadPool;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.axis2.transport.TransportReferenceInterceptor;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.policy.util.PolicyHelper;
import org.apache.tuscany.sca.provider.EndpointReferenceProvider;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceRuntimeException;

public class Axis2ReferenceBindingProvider extends Axis2BaseBindingProvider implements EndpointReferenceProvider {

    // the endpoint reference configuration that's driving this binding provider
    // and some convenience data retrieved from the endpoint reference
    private RuntimeEndpointReference endpointReference;
    private RuntimeComponent component;
    private RuntimeComponentReference reference;
    private WebServiceBinding wsBinding;
    
    // The Axis2 configuration that the binding creates
    private ServiceClient serviceClient;
    private AxisService axisClientSideService;
    

    public Axis2ReferenceBindingProvider(ExtensionPointRegistry extensionPoints,
                                         EndpointReference endpointReference) {
        
        super(extensionPoints);

        this.endpointReference = (RuntimeEndpointReference)endpointReference;
        
        this.wsBinding = (WebServiceBinding)endpointReference.getBinding();
        this.component = (RuntimeComponent)endpointReference.getComponent();
        this.reference = (RuntimeComponentReference)endpointReference.getReference();

        // A WSDL document should always be present in the binding
        if (wsBinding.getGeneratedWSDLDocument() == null) {
            throw new ServiceRuntimeException("No WSDL document for " + component.getName() + "/" + reference.getName());
        }

        // Set to use the Axiom data binding
        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract.getInterface() != null) {
            contract.getInterface().resetDataBinding(OMElement.class.getName());
        }
        
        isSOAP11Required = PolicyHelper.isIntentRequired(wsBinding, Constants.SOAP11_INTENT);
        isSOAP12Required = PolicyHelper.isIntentRequired(wsBinding, Constants.SOAP12_INTENT);
        
        isMTOMRequired = PolicyHelper.isIntentRequired(wsBinding, Axis2BindingProviderFactory.MTOM_INTENT);
        
        // TODO - this is not correct as there may be other, custom, policies that 
        // require rampart. For example this is not going to pick up the case
        // of external policy attachment
        isRampartRequired = PolicyHelper.isIntentRequired(wsBinding, Constants.AUTHENTICATION_INTENT) ||
                            PolicyHelper.isIntentRequired(wsBinding, Constants.CONFIDENTIALITY_INTENT) ||
                            PolicyHelper.isIntentRequired(wsBinding, Constants.INTEGRITY_INTENT);          

        // Apply the configuration from any other policies
        
        for (PolicyProvider pp : this.endpointReference.getPolicyProviders()) {
            pp.configureBinding(this);
        }
        
        // check the WSDL style as we currently only support some of them
        if (wsBinding.isRpcEncoded()){
            throw new ServiceRuntimeException("rpc/encoded WSDL style not supported. Component " + endpointReference.getComponent().getName() +
                                              " Reference " + endpointReference.getReference() +
                                              " Binding " + endpointReference.getBinding().getName());
        } 

        if (wsBinding.isDocEncoded()){
            throw new ServiceRuntimeException("doc/encoded WSDL style not supported. Component " + endpointReference.getComponent().getName() +
                                              " Reference " + endpointReference.getReference() +
                                              " Binding " + endpointReference.getBinding().getName());
        } 
        
        if (wsBinding.isDocLiteralUnwrapped()){
            //throw new ServiceRuntimeException("doc/literal/unwrapped WSDL style not supported for endpoint reference " + endpointReference);
        } 
        
        // Validate that the WSDL is not using SOAP v1.2 if requires="SOAP.v1_1" has been specified
        if ( isSOAP11Required ) {
        	Definition def = wsBinding.getGeneratedWSDLDocument();
        	Binding binding = def.getBinding(wsBinding.getBindingName());
        	for ( Object ext : binding.getExtensibilityElements() ) {
        		if ( ext instanceof SOAP12Binding )
        			throw new ServiceRuntimeException("WSDL document is using SOAP v1.2 but SOAP v1.1 " +
        					"is required by the specified policy intents");
        	}
        }
        
        // Validate that the WSDL is not using SOAP v1.1 if requires="SOAP.v1_2" has been specified
        if ( isSOAP12Required ) {
        	Definition def = wsBinding.getGeneratedWSDLDocument();
        	Binding binding = def.getBinding(wsBinding.getBindingName());
        	for ( Object ext : binding.getExtensibilityElements() ) {
        		if ( ext instanceof SOAPBinding )
        			throw new ServiceRuntimeException("WSDL document is using SOAP v1.1 but SOAP v1.2 " +
        					"is required by the specified policy intents");
        	}
        }
    }
    
    public void start() {
        configContext = Axis2EngineIntegration.getAxisConfigurationContext(extensionPoints.getServiceDiscovery());
        
        try {
            Definition definition = wsBinding.getGeneratedWSDLDocument();
            QName serviceQName = wsBinding.getService().getQName();
            Port port = wsBinding.getPort();
            if (port == null) {
                // service has multiple ports, select one port to use
                // TODO - it feels like there is much more to this than is
                //        here at the moment as need to match with the service side
                //        assuming that it's available
                Collection<Port> ports = wsBinding.getService().getPorts().values();
                for (Port p : ports) {
                    // look for a SOAP 1.1 port first
                    if (p.getExtensibilityElements().get(0) instanceof SOAPAddress) {
                        port = p;
                        break;
                    }
                }
                if (port == null) {
                    // no SOAP 1.1 port available, so look for a SOAP 1.2 port
                    for (Port p : ports) {
                        if (p.getExtensibilityElements().get(0) instanceof SOAP12Address) {
                            port = p;
                            break;
                        }
                    }
                }
            }
            
            axisClientSideService = Axis2EngineIntegration.createClientSideAxisService(definition, serviceQName, port.getName(), new Options());
    
            HttpClient httpClient = (HttpClient)configContext.getProperty(HTTPConstants.CACHED_HTTP_CLIENT);
            if (httpClient == null) {
                MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
                HttpConnectionManagerParams connectionManagerParams = new HttpConnectionManagerParams();
                connectionManagerParams.setDefaultMaxConnectionsPerHost(2);
                connectionManagerParams.setTcpNoDelay(true);
                connectionManagerParams.setStaleCheckingEnabled(true);
                connectionManagerParams.setLinger(0);
                connectionManager.setParams(connectionManagerParams);
                httpClient = new HttpClient(connectionManager);
                configContext.setThreadPool(new ThreadPool(1, 5));
                configContext.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);
                configContext.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
            }
    
            serviceClient = new ServiceClient(configContext, axisClientSideService);
    
        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO: better exception
        } 
    }

    public void stop() {
        if (serviceClient != null) {
            // close all connections that we have initiated, so that the jetty server
            // can be restarted without seeing ConnectExceptions
            HttpClient httpClient =
                (HttpClient)serviceClient.getServiceContext().getConfigurationContext()
                    .getProperty(HTTPConstants.CACHED_HTTP_CLIENT);
            if (httpClient != null)
                ((MultiThreadedHttpConnectionManager)httpClient.getHttpConnectionManager()).shutdown();

            serviceClient = null;
        }
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
        
    }

    public boolean supportsOneWayInvocation() {
        return true;
    }

    public Invoker createInvoker(Operation operation) {
        Options options = new Options();
        org.apache.axis2.addressing.EndpointReference epTo = getWSATOEPR(wsBinding);
        if (epTo != null) {
            options.setTo(epTo);
        }
        options.setProperty(HTTPConstants.CHUNKED, Boolean.FALSE);

        String operationName = operation.getName();

        String soapAction = getSOAPAction(operationName);
        if (soapAction != null && soapAction.length() > 1) {
            options.setAction(soapAction);
        }

        options.setTimeOutInMilliSeconds(30 * 1000); // 30 seconds

        // Allow privileged access to read properties. Requires PropertiesPermission read in
        // security policy.
        SOAPFactory soapFactory = AccessController.doPrivileged(new PrivilegedAction<SOAPFactory>() {
            public SOAPFactory run() {
                if (isSOAP12Required)
                    return OMAbstractFactory.getSOAP12Factory();
                else
                    return OMAbstractFactory.getSOAP11Factory();

            }
        });
        QName wsdlOperationQName = new QName(operationName);
        if (isMTOMRequired)
        {
            options.setProperty(org.apache.axis2.Constants.Configuration.ENABLE_MTOM, org.apache.axis2.Constants.VALUE_TRUE);
        }
        
        return new Axis2ReferenceBindingInvoker(endpointReference, serviceClient, wsdlOperationQName, options, soapFactory, wsBinding);
        
/*        
        if (operation.isNonBlocking()) {
            invoker = new Axis2OneWayBindingInvoker(this, wsdlOperationQName, options, soapFactory, wsBinding);
        } else {
            invoker = new Axis2BindingInvoker(endpointReference, serviceClient, wsdlOperationQName, options, soapFactory, wsBinding);
        }
        
        return invoker;
*/        
    }
    
    /*
     * set up the reference binding wire with the right set of ws reference
     * interceptors
     */
    public void configure() {
        InvocationChain bindingChain = endpointReference.getBindingInvocationChain();
         
        // add transport interceptor
        bindingChain.addInterceptor(Phase.REFERENCE_BINDING_TRANSPORT, 
                                    new TransportReferenceInterceptor());
        
    }
    
    // Reference specific utility operations
    
    protected org.apache.axis2.addressing.EndpointReference getWSATOEPR(WebServiceBinding binding) {
        org.apache.axis2.addressing.EndpointReference epr = getEPR(binding);
        if (epr == null) {
            epr = getPortLocationEPR(binding);
        } else if (epr.getAddress() == null || epr.getAddress().length() < 1) {
            org.apache.axis2.addressing.EndpointReference bindingEPR = getPortLocationEPR(binding);
            if (bindingEPR != null) {
                epr.setAddress(bindingEPR.getAddress());
            }
        }
        return epr;
    }

    protected org.apache.axis2.addressing.EndpointReference getPortLocationEPR(WebServiceBinding binding) {
        String ep = null;
        if (binding.getPort() != null) {
            List<?> wsdlPortExtensions = binding.getPort().getExtensibilityElements();
            for (final Object extension : wsdlPortExtensions) {
                if (extension instanceof SOAPAddress) {
                    ep = ((SOAPAddress)extension).getLocationURI();
                    break;
                }
                if (extension instanceof SOAP12Address) {
                    SOAP12Address address = (SOAP12Address)extension;
                    ep = address.getLocationURI();
                    break;
                }
            }
        }
        if(ep == null || ep.equals("")) {
            ep = binding.getURI();
        }
        return ep == null || "".equals(ep) ? null : new org.apache.axis2.addressing.EndpointReference(ep);
    }

    protected org.apache.axis2.addressing.EndpointReference getEPR(WebServiceBinding wsBinding) {
        if (wsBinding.getEndPointReference() == null) {
            return null;
        }
        try {

            XMLStreamReader parser =
                XMLInputFactory.newInstance().createXMLStreamReader(new DOMSource(wsBinding.getEndPointReference()));
            StAXOMBuilder builder = new StAXOMBuilder(parser);
            OMElement omElement = builder.getDocumentElement();
            org.apache.axis2.addressing.EndpointReference epr = EndpointReferenceHelper.fromOM(omElement);
            return epr;

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } catch (FactoryConfigurationError e) {
            throw new RuntimeException(e);
        }
    }

    protected String getSOAPAction(String operationName) {
        Binding binding = wsBinding.getBinding();
        if (binding != null) {
            for (Object o : binding.getBindingOperations()) {
                BindingOperation bop = (BindingOperation)o;
                if (bop.getName().equalsIgnoreCase(operationName)) {
                    for (Object o2 : bop.getExtensibilityElements()) {
                        if (o2 instanceof SOAPOperation) {
                            return ((SOAPOperation)o2).getSoapActionURI();
                        }
                    }
                }
            }
        }
        return null;
    }  
}
