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

import static org.apache.tuscany.sca.binding.ws.axis2.AxisPolicyHelper.SOAP12_INTENT;
import static org.apache.tuscany.sca.binding.ws.axis2.AxisPolicyHelper.isIntentRequired;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Port;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
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
import org.apache.tuscany.sca.assembly.AbstractContract;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.axis2.policy.configuration.Axis2ConfigParamPolicy;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.util.PolicyHandler;
import org.apache.tuscany.sca.policy.util.PolicyHandlerTuple;
import org.apache.tuscany.sca.policy.util.PolicyHandlerUtils;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.xsd.xml.XMLDocumentHelper;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.osoa.sca.ServiceRuntimeException;

public class Axis2ServiceClient {

    private RuntimeComponent component;
    private AbstractContract contract;
    private WebServiceBinding wsBinding;
    private ServiceClient serviceClient;
    List<PolicyHandlerTuple> policyHandlerClassnames = null;
    private List<PolicyHandler> policyHandlerList = new ArrayList<PolicyHandler>();

    public Axis2ServiceClient(RuntimeComponent component,
                              AbstractContract contract,
                              WebServiceBinding wsBinding,
                              MessageFactory messageFactory,
                              List<PolicyHandlerTuple> policyHandlerClassnames) {

        this.component = component;
        this.contract = contract;
        this.wsBinding = wsBinding;
        this.policyHandlerClassnames = policyHandlerClassnames;
    }

    protected void start() {
        if (serviceClient == null) {
            this.serviceClient = createServiceClient();
        }
    }

    public ServiceClient getServiceClient() {
        return serviceClient;
    }
    
    protected void configurePolicy(ConfigurationContext context, PolicySet ps) throws AxisFault {
        if (ps == null) {
            return;
        }
        for (Object policy : ps.getPolicies()) {
            if (policy instanceof Axis2ConfigParamPolicy) {
                Axis2ConfigParamPolicy axis2ConfigParamPolicy = (Axis2ConfigParamPolicy)policy;
                for (Map.Entry<String, OMElement> param : axis2ConfigParamPolicy.getParamElements().entrySet()) {
                    Parameter configParam = new Parameter(param.getKey(), param.getValue().getFirstElement());
                    configParam.setParameterElement(param.getValue());
                    context.getAxisConfiguration().addParameter(configParam);
                }
            }
        }
    }

    /**
     * Create an Axis2 ServiceClient
     */
    protected ServiceClient createServiceClient() {
        try {
            final boolean isRampartRequired = AxisPolicyHelper.isRampartRequired(wsBinding);
            ConfigurationContext configContext;
            
            try {
                // TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
                // Allow privileged access to read properties. Requires PropertyPermission read in
                // security policy.
                TuscanyAxisConfigurator tuscanyAxisConfigurator =
                    AccessController.doPrivileged(new PrivilegedExceptionAction<TuscanyAxisConfigurator>() {
                        public TuscanyAxisConfigurator run() throws AxisFault {
                            return new TuscanyAxisConfigurator(isRampartRequired);
                        }
                    });
                configContext = tuscanyAxisConfigurator.getConfigurationContext();
                // deployRampartModule();
                // configureSecurity();
            } catch (PrivilegedActionException e) {
                throw new ServiceRuntimeException(e.getException());
            }

            createPolicyHandlers();
            setupPolicyHandlers(policyHandlerList, configContext);

            Definition definition = wsBinding.getWSDLDocument();
            QName serviceQName = wsBinding.getService().getQName();
            Port port = wsBinding.getPort();
            if (port == null) {
                // service has multiple ports, select one port to use
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
            AxisService axisService =
                createClientSideAxisService(definition, serviceQName, port.getName(), new Options());

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
     * URI resolver implementation for XML schema
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

        if (serviceName == null) {
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
    public static AxisService createClientSideAxisService(Definition definition,
                                                          QName serviceName,
                                                          String portName,
                                                          Options options) throws AxisFault {
        Definition def = getDefinition(definition, serviceName);
        final WSDL11ToAxisServiceBuilder serviceBuilder = new WSDL11ToAxisServiceBuilder(def, serviceName, portName);
        serviceBuilder.setServerSide(false);
        // [rfeng] Add a custom resolver to work around WSCOMMONS-228
        serviceBuilder.setCustomResolver(new URIResolverImpl(def));
        serviceBuilder.setBaseUri(def.getDocumentBaseURI());
        // [rfeng]
        // Allow access to read properties. Requires PropertiesPermission in security policy.
        AxisService axisService;         
        try {        
            axisService = AccessController.doPrivileged(new PrivilegedExceptionAction<AxisService>() {
                public AxisService run() throws AxisFault {
                    return serviceBuilder.populateService();
                }
            });
            } catch ( PrivilegedActionException e ) {
               throw (AxisFault) e.getException();
            }

        AxisEndpoint axisEndpoint = (AxisEndpoint)axisService.getEndpoints().get(axisService.getEndpointName());
        options.setTo(new EndpointReference(axisEndpoint.getEndpointURL()));
        if (axisEndpoint != null) {
            options.setSoapVersionURI((String)axisEndpoint.getBinding().getProperty(WSDL2Constants.ATTR_WSOAP_VERSION));
        }
        return axisService;
    }

    /*
    private static <T extends ExtensibilityElement> T getExtensibilityElement(List elements, Class<T> type) {
        for (Object e : elements) {
            if (type.isInstance(e)) {
                return type.cast(e);
            }
        }
        return null;
    }
    */

    protected void stop() {
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

        // Allow privileged access to read properties. Requires PropertiesPermission read in
        // security policy.
        SOAPFactory soapFactory = AccessController.doPrivileged(new PrivilegedAction<SOAPFactory>() {
            public SOAPFactory run() {
                if (requiresSOAP12())
                    return OMAbstractFactory.getSOAP12Factory();
                else
                    return OMAbstractFactory.getSOAP11Factory();

            }
        });
        QName wsdlOperationQName = new QName(operationName);
        if (requiresMTOM())
        {
        	options.setProperty(org.apache.axis2.Constants.Configuration.ENABLE_MTOM, org.apache.axis2.Constants.VALUE_TRUE);
        }
        Axis2BindingInvoker invoker;
        if (operation.isNonBlocking()) {
            invoker = new Axis2OneWayBindingInvoker(this, wsdlOperationQName, options, soapFactory, policyHandlerList, wsBinding);
        } else {
            invoker = new Axis2BindingInvoker(this, wsdlOperationQName, options, soapFactory, policyHandlerList, wsBinding);
        }
        
        return invoker;
    }

    private boolean requiresSOAP12() {
        return isIntentRequired(wsBinding, SOAP12_INTENT);
    }

    private boolean requiresMTOM() {
        return isIntentRequired(wsBinding, AxisPolicyHelper.MTOM_INTENT);
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
        return ep == null || "".equals(ep) ? null : new EndpointReference(ep);
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

    private void createPolicyHandlers() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (wsBinding instanceof PolicySetAttachPoint) {
            PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)wsBinding;
            PolicyHandler policyHandler = null;
            for (PolicySet policySet : policiedBinding.getPolicySets()) {
                policyHandler = PolicyHandlerUtils.findPolicyHandler(policySet, policyHandlerClassnames);
                if (policyHandler != null) {
                    policyHandler.setApplicablePolicySet(policySet);
                    policyHandlerList.add(policyHandler);
                }
            }
            
            // code to create policy handlers using the new policy SPI based
            // on policy providers
/*            
            List<PolicyProvider> policyProviders = ((RuntimeComponentReference)contract).getPolicyProviders(wsBinding);
            
            for (PolicyProvider policyProvider : policyProviders){
                policyHandler = policyProvider.createHandler();
                if (policyHandler != null) {
                    policyHandlerList.add(policyHandler);
                } 
            }
*/             
        }
    }

    private void setupPolicyHandlers(List<PolicyHandler> policyHandlers, ConfigurationContext configContext) {
        for (PolicyHandler aHandler : policyHandlers) {
            aHandler.setUp(configContext);
        }
    }

}
