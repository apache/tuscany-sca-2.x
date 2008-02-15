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
package org.apache.tuscany.sca.binding.ws.axis2;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
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
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.addressing.EndpointReferenceHelper;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisEndpoint;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.WSDL11ToAxisServiceBuilder;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.util.threadpool.ThreadPool;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.neethi.Policy;
import org.apache.tuscany.sca.assembly.AbstractContract;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.XMLDocumentHelper;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.security.ws.Axis2ConfigParamPolicy;
import org.apache.tuscany.sca.policy.util.PolicyHandler;
import org.apache.tuscany.sca.policy.util.PolicyHandlerTuple;
import org.apache.tuscany.sca.policy.util.PolicyHandlerUtils;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.ws.commons.schema.resolver.URIResolver;

public class Axis2ServiceClient {

    private WebServiceBinding wsBinding;
    private ServiceClient serviceClient;
    Map<ClassLoader, List<PolicyHandlerTuple>> policyHandlerClassnames = null;
    private static final QName SOAP12_INTENT = new QName("http://www.osoa.org/xmlns/sca/1.0", "soap12");
    

    public Axis2ServiceClient(RuntimeComponent component,
                              AbstractContract contract,
                              WebServiceBinding wsBinding,
                              ServletHost servletHost,
                              MessageFactory messageFactory, 
                              Map<ClassLoader, List<PolicyHandlerTuple>> policyHandlerClassnames) {

        this.wsBinding = wsBinding;
        this.policyHandlerClassnames = policyHandlerClassnames;
    }

    protected void start() {
        if (serviceClient == null){
            this.serviceClient = createServiceClient();
        }
    }
    
    public ServiceClient getServiceClient() {
        return serviceClient;
    }    

    /**
     * Create an Axis2 ServiceClient
     */
    protected ServiceClient createServiceClient() {
        try {
            TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
            ConfigurationContext configContext = tuscanyAxisConfigurator.getConfigurationContext();

            //configureSecurity(configContext);
            setupPolicies(configContext);
            
            Definition wsdlDefinition = wsBinding.getWSDLDefinition().getDefinition();
            setServiceAndPort(wsBinding);
            QName serviceQName = wsBinding.getServiceName();
            String portName = wsBinding.getPortName();
            AxisService axisService =
                createClientSideAxisService(wsdlDefinition, serviceQName, portName, new Options());

            HttpClient httpClient = (HttpClient) configContext.getProperty(HTTPConstants.CACHED_HTTP_CLIENT); 
            if (httpClient == null)
            {
                MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
                HttpConnectionManagerParams connectionManagerParams = new HttpConnectionManagerParams();
                connectionManagerParams.setDefaultMaxConnectionsPerHost(2);
                connectionManagerParams.setTcpNoDelay(true);
                connectionManagerParams.setStaleCheckingEnabled(true);
                connectionManagerParams.setLinger(0);
                connectionManager.setParams(connectionManagerParams);
                httpClient  = new HttpClient(connectionManager);
                configContext.setThreadPool(new ThreadPool(1, 5));
                configContext.setProperty(HTTPConstants.REUSE_HTTP_CLIENT,
                                          Boolean.TRUE);
                configContext.setProperty(HTTPConstants.CACHED_HTTP_CLIENT,
                                          httpClient);
            }

            return new ServiceClient(configContext, axisService);
           
        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO: better exception
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * URI resolver implementation for xml schema
     */
    public static class URIResolverImpl implements URIResolver {
        private Definition definition;

        public URIResolverImpl(Definition definition) {
            this.definition = definition;
        }

        public org.xml.sax.InputSource resolveEntity(java.lang.String targetNamespace,
                                                     java.lang.String schemaLocation,
                                                     java.lang.String baseUri) {
            try {
                if (baseUri == null) {
                    baseUri = definition.getDocumentBaseURI();
                }
                URL url = new URL(new URL(baseUri), schemaLocation);
                return XMLDocumentHelper.getInputSource(url);
            } catch (IOException e) {
                return null;
            }
        }
    }
    
    /**
     * Workaround for https://issues.apache.org/jira/browse/AXIS2-3205
     * @param definition
     * @param serviceName
     * @return
     */
    private static Definition getDefinition(Definition definition, QName serviceName) {
        
        if (serviceName == null){
            return definition;
        }
        
        if (definition == null) {
            return null;
        }
        Object service = definition.getServices().get(serviceName);
        if (service != null) {
            return definition;
        }
        for (Object i : definition.getImports().values()) {
            List<Import> imports = (List<Import>)i;
            for (Import imp : imports) {
                Definition d = getDefinition(imp.getDefinition(), serviceName);
                if (d != null) {
                    return d;
                }
            }
        }
        return null;
    }
    
    /**
     * This method is copied from AxisService.createClientSideAxisService to
     * work around http://issues.apache.org/jira/browse/WSCOMMONS-228
     * 
     * @param wsdlDefinition
     * @param wsdlServiceName
     * @param portName
     * @param options
     * @return
     * @throws AxisFault
     */
    @Deprecated
    public static AxisService createClientSideAxisService(Definition wsdlDefinition,
                                                          QName wsdlServiceName,
                                                          String portName,
                                                          Options options) throws AxisFault {
        Definition def = getDefinition(wsdlDefinition, wsdlServiceName);
        WSDL11ToAxisServiceBuilder serviceBuilder =
                new WSDL11ToAxisServiceBuilder(def, wsdlServiceName, portName);
        serviceBuilder.setServerSide(false);
        // [rfeng] Add a custom resolver to work around WSCOMMONS-228
        serviceBuilder.setCustomResolver(new URIResolverImpl(def));
        serviceBuilder.setBaseUri(def.getDocumentBaseURI());
        // [rfeng]
        AxisService axisService = serviceBuilder.populateService();
        AxisEndpoint axisEndpoint = (AxisEndpoint) axisService.getEndpoints()
                .get(axisService.getEndpointName());
        options.setTo(new EndpointReference(axisEndpoint.getEndpointURL()));
        if (axisEndpoint != null) {
            options.setSoapVersionURI((String) axisEndpoint.getBinding()
                    .getProperty(WSDL2Constants.ATTR_WSOAP_VERSION));
        }
        return axisService;
    }


    /**
     * Ensure the WSDL definition contains a suitable service and port
     */
    protected static void setServiceAndPort(WebServiceBinding wsBinding) {
        Definition wsdlDefinition = wsBinding.getWSDLDefinition().getDefinition();
        QName serviceQName = wsBinding.getServiceName();
        String portName = wsBinding.getPortName();

        // If no service is specified in the binding element, allow for WSDL that
        // only contains a portType and not a service and port.  Synthesize a
        // service and port using WSDL4J and add them to the wsdlDefinition to
        // keep Axis happy.
        //FIXME: it would be better to do this for all WSDLs to explictly control the
        // service and port that Axis will use, rather than just hoping the user has
        // placed a suitable service and/or port first in the WSDL.
        if (serviceQName == null && wsBinding.getBinding() != null) {
            QName bindingQName = wsBinding.getBindingName();
            Port port = wsdlDefinition.createPort();
            portName = "$port$." + bindingQName.getLocalPart();
            port.setName(portName);
            wsBinding.setPortName(portName);
            port.setBinding(wsBinding.getBinding());
            Service service = wsdlDefinition.createService();
            serviceQName = new QName(bindingQName.getNamespaceURI(),
                                     "$service$." + bindingQName.getLocalPart());
            service.setQName(serviceQName);
            wsBinding.setServiceName(serviceQName);
            service.addPort(port);
            wsdlDefinition.addService(service);
        }
    }

    protected void stop() {
        if (serviceClient != null){
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

    /**
     * Create and configure an Axis2BindingInvoker for each operation
     */
    protected Invoker createInvoker(Operation operation) {
        Options options = new Options();
        EndpointReference epTo = getWSATOEPR(wsBinding);
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

        SOAPFactory soapFactory = requiresSOAP12() ? OMAbstractFactory.getSOAP12Factory() : OMAbstractFactory.getSOAP11Factory();
        QName wsdlOperationQName = new QName(operationName);

        Axis2BindingInvoker invoker;
        if (operation.isNonBlocking()) {
            invoker = new Axis2OneWayBindingInvoker(this, wsdlOperationQName, options, soapFactory);
        } else {
            invoker = new Axis2BindingInvoker(this, wsdlOperationQName, options, soapFactory);
        }
        return invoker;
    }

    private boolean requiresSOAP12() {
        if (wsBinding instanceof IntentAttachPoint) {
            List<Intent> intents = ((IntentAttachPoint)wsBinding).getRequiredIntents();
            for (Intent intent : intents) {
                if (SOAP12_INTENT.equals(intent.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected EndpointReference getWSATOEPR(WebServiceBinding binding) {
        EndpointReference epr = getEPR(binding);
        if (epr == null) {
            epr = getPortLocationEPR(binding);
        } else if (epr.getAddress() == null || epr.getAddress().length() < 1) {
            EndpointReference bindingEPR = getPortLocationEPR(binding);
            if (bindingEPR != null) {
                epr.setAddress(bindingEPR.getAddress());
            }
        }
        return epr;
    }

    protected EndpointReference getPortLocationEPR(WebServiceBinding binding) {
        String ep = binding.getURI();
        if (ep == null && binding.getPort() != null) {
            List<?> wsdlPortExtensions = binding.getPort().getExtensibilityElements();
            for (final Object extension : wsdlPortExtensions) {
                if (extension instanceof SOAPAddress) {
                    ep = ((SOAPAddress)extension).getLocationURI();
                    break;
                }
            }
        }
        return ep != null ? new EndpointReference(ep) : null;
    }

    protected org.apache.axis2.addressing.EndpointReference getEPR(WebServiceBinding wsBinding) {
        if (wsBinding.getEndPointReference() == null) {
            return null;
        }
        try {

            XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(new DOMSource(wsBinding.getEndPointReference()));
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
    
    private void setupPolicies(ConfigurationContext configContext) throws IllegalAccessException,
        InstantiationException, ClassNotFoundException {
        if (wsBinding instanceof PolicySetAttachPoint) {
            PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)wsBinding;
            PolicyHandler policyHandler = null;
            for (PolicySet policySet : policiedBinding.getPolicySets()) {
                policyHandler =
                    PolicyHandlerUtils.findPolicyHandler(policySet, policyHandlerClassnames);
                if (policyHandler != null) {
                    policyHandler.setApplicablePolicySet(policySet);
                    policyHandler.setUp(configContext);
                }
            }
        }
    }
    
    /*private void configureSecurity(ConfigurationContext configContext) throws AxisFault {
        if ( wsBinding instanceof PolicySetAttachPoint ) {
            PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)wsBinding;
            Parameter configParam = null;
            Axis2ConfigParamPolicy axis2ConfigParamPolicy = null;
            for ( PolicySet policySet : policiedBinding.getPolicySets() ) {
                for ( Object policy : policySet.getPolicies() ) {
                    if ( policy instanceof Axis2ConfigParamPolicy ) {
                        axis2ConfigParamPolicy = (Axis2ConfigParamPolicy)policy;
                        for ( String paramName : axis2ConfigParamPolicy.getParamElements().keySet() ) {
                            configParam = new Parameter(paramName, 
                                                        axis2ConfigParamPolicy.getParamElements().get(paramName).getFirstElement());
                            configParam.setParameterElement(axis2ConfigParamPolicy.getParamElements().get(paramName));
                            configContext.getAxisConfiguration().addParameter(configParam);
                        }
                    } else if ( policy instanceof Policy ) {
                        Policy wsPolicy = (Policy)policy;
                        configContext.getAxisConfiguration().applyPolicy(wsPolicy);
                    }
                }
            }
        }
    }*/


}
