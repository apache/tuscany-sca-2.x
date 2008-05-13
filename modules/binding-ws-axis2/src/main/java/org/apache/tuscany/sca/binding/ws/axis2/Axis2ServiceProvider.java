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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Port;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.EndpointReferenceHelper;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.deployment.DeploymentErrorMsgs;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.deployment.ModuleBuilder;
import org.apache.axis2.deployment.util.Utils;
import org.apache.axis2.description.AxisEndpoint;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.description.WSDL11ToAxisServiceBuilder;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.description.WSDLToAxisServiceBuilder;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.ListenerManager;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.i18n.Messages;
import org.apache.axis2.transport.jms.JMSConstants;
import org.apache.axis2.transport.jms.JMSListener;
import org.apache.axis2.transport.jms.JMSSender;
import org.apache.axis2.transport.jms.JMSUtils;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.tuscany.sca.assembly.AbstractContract;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.axis2.Axis2ServiceClient.URIResolverImpl;
import org.apache.tuscany.sca.core.assembly.EndpointReferenceImpl;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.security.ws.Axis2ConfigParamPolicy;
import org.apache.tuscany.sca.policy.util.PolicyHandler;
import org.apache.tuscany.sca.policy.util.PolicyHandlerTuple;
import org.apache.tuscany.sca.policy.util.PolicyHandlerUtils;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.ReferenceParameters;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;

public class Axis2ServiceProvider {
    
    private static final Logger logger = Logger.getLogger(Axis2ServiceProvider.class.getName());    

    private AbstractContract contract;
    private WebServiceBinding wsBinding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
    private ConfigurationContext configContext;
    private JMSSender jmsSender;
    private JMSListener jmsListener;
    private Map<ClassLoader, List<PolicyHandlerTuple>> policyHandlerClassnames = null;
    private List<PolicyHandler> policyHandlerList = new ArrayList<PolicyHandler>();

    public static final QName QNAME_WSA_ADDRESS =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_ADDRESS);
    public static final QName QNAME_WSA_FROM =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_FROM);
    public static final QName QNAME_WSA_REFERENCE_PARAMETERS =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_REFERENCE_PARAMETERS);

    private static final String DEFAULT_QUEUE_CONNECTION_FACTORY = "TuscanyQueueConnectionFactory";
    
    private static final QName TRANSPORT_JMS_QUALIFIED_INTENT = new QName("http://www.osoa.org/xmlns/sca/1.0","transport.jms");
    
    private PolicySet transportJmsPolicySet = null;
        

    public Axis2ServiceProvider(RuntimeComponent component,
                                AbstractContract contract,
                                WebServiceBinding wsBinding,
                                ServletHost servletHost,
                                MessageFactory messageFactory,
                                Map<ClassLoader, List<PolicyHandlerTuple>> policyHandlerClassnames) {

        this.contract = contract; 
        this.wsBinding = wsBinding;
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;
        this.policyHandlerClassnames = policyHandlerClassnames;

        
        try {
            TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
            configContext = tuscanyAxisConfigurator.getConfigurationContext();
            //deployRampartModule();
            //configureSecurity();
        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO: better exception
        } catch ( Exception e ) {
            throw new RuntimeException(e);
        }

        configContext.setContextRoot(servletHost.getContextPath());

        // pull out the binding intents to see what sort of transport is required
        transportJmsPolicySet = getPolicySet(TRANSPORT_JMS_QUALIFIED_INTENT);
        
        String uri;
        
        if (transportJmsPolicySet != null){
            uri = computeActualURI(component, contract);
            
            if (!uri.startsWith("jms:/")) {
                uri = "jms:" + uri;
            }
            
            // construct the rest of the URI based on the policy. All the details are put
            // into the URI here rather than being place directly into the Axis configuration 
            // as the Axis JMS sender relies on parsing the target URI      
            Axis2ConfigParamPolicy axis2ConfigParamPolicy = null;
            for ( Object policy : transportJmsPolicySet.getPolicies() ) {
                if ( policy instanceof Axis2ConfigParamPolicy ) {
                    axis2ConfigParamPolicy = (Axis2ConfigParamPolicy)policy;
                    Iterator paramIterator = axis2ConfigParamPolicy.getParamElements().get(DEFAULT_QUEUE_CONNECTION_FACTORY).getChildElements();
                    
                    if (paramIterator.hasNext()){
                        StringBuffer uriParams = new StringBuffer("?");
                       
                        while (paramIterator.hasNext()){
                            OMElement parameter = (OMElement)paramIterator.next();
                            uriParams.append(parameter.getAttributeValue(new QName("","name")));
                            uriParams.append("=");
                            uriParams.append(parameter.getText());
                            
                            if (paramIterator.hasNext()){
                                uriParams.append("&");
                            }
                        }
                        
                        uri = uri + uriParams;
                    }
                }
            }                     
        } else {
            uri = computeActualURI(component, contract);
            if (!uri.startsWith("jms:")) {
                uri = servletHost.getURLMapping(uri).toString();
            }
        }
        
        wsBinding.setURI(uri);
    }
    
    private void engageModules() throws AxisFault {
        if ( wsBinding instanceof PolicySetAttachPoint ) {
            PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)wsBinding;
            if ( policiedBinding.getPolicySets().size() > 0 ) {
                //TODO: need to verify if one of the policies are concerned with security
                AxisModule m = new AxisModule("rampart");
                m.setFileName(wsBinding.getClass().getClassLoader().getResource("rampart-1.2.mar"));
                configContext.getAxisConfiguration().addModule(m);
                configContext.getAxisConfiguration().engageModule(m, configContext.getAxisConfiguration());
            }
        }
    }

    public void start() {

        // TODO: if <binding.ws> specifies the WSDL service then should create a
        // service for every port

        try {
            createPolicyHandlers();
            AxisService axisService = createAxisService();
            configContext.getAxisConfiguration().addService( axisService );
            
            setupPolicyHandlers(policyHandlerList, configContext);
          
            String endpointURL = axisService.getEndpointURL();
            if ( endpointURL.startsWith( "http://")  || endpointURL.startsWith("/")) {
                Axis2ServiceServlet servlet = new Axis2ServiceServlet();
                servlet.init(configContext);
                String servletURI = wsBinding.getURI();
                configContext.setContextRoot(servletURI);
                servletHost.addServletMapping(servletURI, servlet);
            } else if ( axisService.getEndpointURL().startsWith( "jms" ) ) {
                logger.log(Level.INFO,"Axis2 JMS URL=" + axisService.getEndpointURL() );
                
                jmsListener = new JMSListener();
                jmsSender = new JMSSender();
                ListenerManager listenerManager = configContext.getListenerManager();
                TransportInDescription trsIn = configContext.getAxisConfiguration().getTransportIn(Constants.TRANSPORT_JMS);
                                
                // get JMS transport parameters from the binding URI
                Map<String, String> jmsProps = JMSUtils.getProperties( wsBinding.getURI() );

                // collect the parameters used to configure the JMS transport
                OMFactory fac = OMAbstractFactory.getOMFactory();
                OMElement parms = fac.createOMElement(DEFAULT_QUEUE_CONNECTION_FACTORY, null);                    

                for ( String key : jmsProps.keySet() ) {
                    OMElement param = fac.createOMElement("parameter", null);
                    param.addAttribute( "name", key, null );
                    param.addChild(fac.createOMText(param, jmsProps.get(key)));
                    parms.addChild(param);
                }
                
                Parameter queueConnectionFactory = new Parameter(DEFAULT_QUEUE_CONNECTION_FACTORY, parms);
                trsIn.addParameter( queueConnectionFactory );
                
                trsIn.setReceiver(jmsListener);

                configContext.getAxisConfiguration().addTransportIn( trsIn );
                TransportOutDescription trsOut = configContext.getAxisConfiguration().getTransportOut(Constants.TRANSPORT_JMS);
                //configContext.getAxisConfiguration().addTransportOut( trsOut );
                trsOut.setSender(jmsSender);

                if (listenerManager == null) {
                    listenerManager = new ListenerManager();
                    listenerManager.init(configContext);
                }
                listenerManager.addListener(trsIn, true);
                jmsSender.init(configContext, trsOut);
                jmsListener.init(configContext, trsIn);
                jmsListener.start();
            }
        } catch (AxisFault e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if ( jmsListener != null ) {
            jmsListener.stop();
            jmsListener.destroy();
        }
        else {
            servletHost.removeServletMapping(wsBinding.getURI());
        }

        if ( jmsSender != null )
            jmsSender.stop();

        try {
            // get the path to the service
            URI uriPath = new URI(wsBinding.getURI());
            String stringURIPath = uriPath.getPath();
            
            // remove any "/" from the start of the path
            if (stringURIPath.startsWith("/")) {
                stringURIPath = stringURIPath.substring(1, stringURIPath.length());
            } 
            
            // remove it from the Axis context
            configContext.getAxisConfiguration().removeService(stringURIPath);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (AxisFault e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Compute the endpoint URI based on section 2.1.1 of the WS binding Specification 1.
     * The URIs in the endpoint(s) of the referenced WSDL, which may be relative
     * 2. The URI specified by the wsa:Address element of the
     * wsa:EndpointReference, which may be relative 3. The explicitly stated URI
     * in the "uri" attribute of the binding.ws element, which may be relative,
     * 4. The implicit URI as defined by in section 1.7 in the SCA Assembly Specification
     * If the <binding.ws> has no wsdlElement but does have a uri attribute then
     * the uri takes precedence over any implicitly used WSDL.
     * 
     */
    protected String computeActualURI(RuntimeComponent component, AbstractContract contract) {

        org.apache.axis2.addressing.EndpointReference epr = null;
        URI eprURI = null;
        if (wsBinding.getEndPointReference() != null) {
            epr = getEPR(); 
            if (epr.getAddress() != null) {
                eprURI = URI.create(epr.getAddress());
            }
        }

        URI wsdlURI = null;
        if (wsBinding.getServiceName() != null && wsBinding.getBindingName() == null) {
            // <binding.ws> explicitly points at a WSDL port, may be a relative URI
            wsdlURI = getEndpoint(wsBinding.getPort());
        }

        // if the WSDL port/endpoint has an absolute URI use that
        if (wsdlURI != null && wsdlURI.isAbsolute()) {
            return wsdlURI.toString();
        }

        // if the wsa:EndpointReference has an address element with an absolute URI use that
        if (eprURI != null && eprURI.isAbsolute()) {
            return eprURI.toString();
        }
        
        // either there is no WSDL port endpoint URI or that URI is relative
        String actualURI = wsBinding.getURI();
        if (eprURI != null && eprURI.toString().length() != 0) {
            // there is a relative URI in the binding EPR
            actualURI = actualURI + "/" + eprURI;
        }

        if (wsdlURI != null && wsdlURI.toString().length() != 0) {
            // there is a relative URI in the WSDL port
            actualURI = actualURI + "/" + wsdlURI;
        }
        
        actualURI = URI.create(actualURI).normalize().toString();
        
        return actualURI;
    }

    private org.apache.axis2.addressing.EndpointReference getEPR() {
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

    /**
     * Returns the endpoint of a given port.
     */
    protected URI getEndpoint(Port wsdlPort) {
        if (wsdlPort != null) {
            List<?> wsdlPortExtensions = wsdlPort.getExtensibilityElements();
            for (Object extension : wsdlPortExtensions) {
                if (extension instanceof SOAPAddress) {
                    String uri = ((SOAPAddress)extension).getLocationURI();
                    return (uri == null || "".equals(uri)) ? null : URI.create(uri);
                }
                if (extension instanceof SOAP12Address) {
                    SOAP12Address address = (SOAP12Address)extension;
                    String uri = address.getLocationURI();
                    return (uri == null || "".equals(uri)) ? null : URI.create(uri);
                }
            }
        }
        return null;
    }

    private AxisService createAxisService() throws AxisFault {
        AxisService axisService;
        if (wsBinding.getWSDLDefinition() != null) {
            axisService = createWSDLAxisService();
        } else {
            axisService = createJavaAxisService();
        }
        initAxisOperations(axisService);
        return axisService;
    }

    /**
     * Create an AxisService from the interface class from the SCA service interface
     */
    protected AxisService createJavaAxisService() throws AxisFault {
        AxisService axisService = new AxisService();
        String path = URI.create(wsBinding.getURI()).getPath();
        axisService.setName(path);
        axisService.setServiceDescription("Tuscany configured AxisService for service: " + wsBinding.getURI());
        axisService.setClientSide(false);
        Parameter classParam =
            new Parameter(Constants.SERVICE_CLASS, ((JavaInterface)contract.getInterfaceContract().getInterface())
                .getJavaClass().getName());
        axisService.addParameter(classParam);
        try {
            Utils.fillAxisService(axisService, configContext.getAxisConfiguration(), null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return axisService;
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
     * Create an AxisService from the WSDL doc used by ws binding
     */
    protected AxisService createWSDLAxisService() throws AxisFault {
        Definition definition = wsBinding.getWSDLDefinition().getDefinition();

        // WSDLToAxisServiceBuilder only uses the service and port to find the wsdl4J Binding
        // An SCA service with binding.ws does not require a service or port so we may not have
        // these but ...

        Axis2ServiceClient.setServiceAndPort(wsBinding);
        // The service and port can be set by the above call
        QName serviceQName =
            wsBinding.getService() != null ? wsBinding.getService().getQName() : wsBinding.getServiceName();
        String portName = wsBinding.getPort() != null ? wsBinding.getPort().getName() : wsBinding.getPortName();
        
        Definition def = getDefinition(definition, serviceQName);

        final WSDLToAxisServiceBuilder builder = new WSDL11ToAxisServiceBuilder(def, serviceQName, portName);
        builder.setServerSide(true);
        // [rfeng] Add a custom resolver to work around WSCOMMONS-228
        builder.setCustomResolver(new URIResolverImpl(def));
        builder.setBaseUri(def.getDocumentBaseURI());
        // [rfeng]        
        // AxisService axisService = builder.populateService();
        // Allow privileged access to read properties. Requires PropertiesPermission read in
        // security policy.
        AxisService axisService;
        try {
            axisService = AccessController.doPrivileged(new PrivilegedExceptionAction<AxisService>() {
                public AxisService run() throws AxisFault {
                    return builder.populateService();
                }
            });
        } catch (PrivilegedActionException e) {
            throw (AxisFault)e.getException();
        }
        String path = URI.create(wsBinding.getURI()).getPath();
        String name = ( path.startsWith( "/") ? path.substring(1) : path );
        axisService.setName(name);
        String endpointURL = wsBinding.getURI();
        axisService.setEndpointURL(endpointURL );
        axisService.setDocumentation("Tuscany configured AxisService for service: " + wsBinding.getURI());
        for ( Iterator i = axisService.getEndpoints().values().iterator(); i.hasNext(); ) {
            AxisEndpoint ae = (AxisEndpoint)i.next();
            if (endpointURL.startsWith("jms") ) {
                Parameter qcf = new Parameter(JMSConstants.CONFAC_PARAM, null);
                qcf.setValue(DEFAULT_QUEUE_CONNECTION_FACTORY);
                axisService.addParameter(qcf);
                break;
            }
        }
        // Use the existing WSDL
        Parameter wsdlParam = new Parameter(WSDLConstants.WSDL_4_J_DEFINITION, null);
        wsdlParam.setValue(def);
        axisService.addParameter(wsdlParam);
        Parameter userWSDL = new Parameter("useOriginalwsdl", "true");
        axisService.addParameter(userWSDL);

        return axisService;
    }

    protected void initAxisOperations(AxisService axisService) {
        for (Iterator<?> i = axisService.getOperations(); i.hasNext();) {
            AxisOperation axisOp = (AxisOperation)i.next();
            Operation op = getOperation(axisOp);
            if (op != null) {

                if (op.isNonBlocking()) {
                    axisOp.setMessageExchangePattern(WSDL2Constants.MEP_URI_IN_ONLY);
                } else {
                    axisOp.setMessageExchangePattern(WSDL2Constants.MEP_URI_IN_OUT);
                }

                MessageReceiver msgrec = null;
                if (op.isNonBlocking()) {
                    msgrec = new Axis2ServiceInMessageReceiver(this, op, policyHandlerList);
                } else {
                    msgrec = new Axis2ServiceInOutSyncMessageReceiver(this, op, policyHandlerList);
                }
                axisOp.setMessageReceiver(msgrec);
            }
        }
    }

    protected Operation getOperation(AxisOperation axisOp) {
        String operationName = axisOp.getName().getLocalPart();
        Interface iface = wsBinding.getBindingInterfaceContract().getInterface();
        for (Operation op : iface.getOperations()) {
            if (op.getName().equalsIgnoreCase(operationName)) {
                return op;
            }
        }
        return null;
    }

    // methods for Axis2 message receivers

    public Object invokeTarget(Operation op, Object[] args, MessageContext inMC) throws InvocationTargetException {
        String callbackAddress = null;
        String callbackID = null;
        Object conversationID = null;

        //FIXME: can we use the Axis2 addressing support for this?
        SOAPHeader header = inMC.getEnvelope().getHeader();
        if (header != null) {
            OMElement from = header.getFirstChildWithName(QNAME_WSA_FROM);
            if (from != null) {
                OMElement callbackAddrElement = from.getFirstChildWithName(QNAME_WSA_ADDRESS);
                if (callbackAddrElement != null) {
                    if (contract.getInterfaceContract().getCallbackInterface() != null) {
                        callbackAddress = callbackAddrElement.getText();
                    }
                }
                OMElement params = from.getFirstChildWithName(QNAME_WSA_REFERENCE_PARAMETERS);
                if (params != null) {
                    OMElement convIDElement =
                        params.getFirstChildWithName(Axis2BindingInvoker.CONVERSATION_ID_REFPARM_QN);
                    if (convIDElement != null) {
                        if (isConversational()) {
                            conversationID = convIDElement.getText();
                        }
                    }
                    OMElement callbackIDElement =
                        params.getFirstChildWithName(Axis2BindingInvoker.CALLBACK_ID_REFPARM_QN);
                    if (callbackIDElement != null) {
                        callbackID = callbackIDElement.getText();
                    }
                }
            }
        }

        // create a message object and set the args as its body
        Message msg = messageFactory.createMessage();
        msg.setBody(args);
        msg.setOperation(op);
        
        //fill message with QoS context info 
        fillQoSContext(msg, inMC);
        
        // if reference parameters are needed, create a new "From" EPR to hold them
        EndpointReference from = null;
        ReferenceParameters parameters = null;
        if (callbackAddress != null ||
            callbackID != null ||
            conversationID != null) {
            from = new EndpointReferenceImpl(null);
            parameters = from.getReferenceParameters();
            msg.setFrom(from);
        }

        // set the reference parameters into the "From" EPR
        if (callbackAddress != null) {
            parameters.setCallbackReference(new EndpointReferenceImpl(callbackAddress));
        }
        if (callbackID != null) {
            parameters.setCallbackID(callbackID);
        }
        if (conversationID != null) {
            parameters.setConversationID(conversationID);
        }

        // find the runtime wire and invoke it with the message
        RuntimeWire wire = ((RuntimeComponentService)contract).getRuntimeWire(getBinding());
        return wire.invoke(op, msg);
    }

    public boolean isConversational() {
        return wsBinding.getBindingInterfaceContract().getInterface().isConversational();
    }

    /**
     * Return the binding for this provider as a primitive binding type
     * For use when looking up wires registered against the binding.
     * 
     * @return the binding
     */
    protected Binding getBinding() {
        return wsBinding;
    }
    
    private PolicySet getPolicySet(QName intentName){
        PolicySet returnPolicySet = null;
        
        if ( wsBinding instanceof PolicySetAttachPoint ) {
            PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)wsBinding; 
            for ( PolicySet policySet : policiedBinding.getPolicySets() ) {
                for (Intent intent : policySet.getProvidedIntents()){
                    if ( intent.getName().equals(intentName) ){
                        returnPolicySet = policySet;
                        break;
                    }
                }
            }
        }
        
        return returnPolicySet;
    } 
    
    private void setupPolicyHandlers(List<PolicyHandler> policyHandlers, ConfigurationContext configContext)  {
        for ( PolicyHandler aHandler : policyHandlers ) {
            aHandler.setUp(configContext);
        }
    }
    
    private void createPolicyHandlers() throws IllegalAccessException,
                                                              InstantiationException, 
                                                              ClassNotFoundException {
        if (wsBinding instanceof PolicySetAttachPoint) {
            PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)wsBinding;
            PolicyHandler policyHandler = null;
            for (PolicySet policySet : policiedBinding.getPolicySets()) {
                policyHandler =
                    PolicyHandlerUtils.findPolicyHandler(policySet, policyHandlerClassnames);
                if (policyHandler != null) {
                    policyHandler.setApplicablePolicySet(policySet);
                    policyHandlerList.add(policyHandler);
                }
            }
        }
    }
     
    
    private void deployRampartModule()  throws DeploymentException, AxisFault {
    	ClassLoader tccl = (ClassLoader) org.apache.axis2.java.security.AccessController
        .doPrivileged(new PrivilegedAction() {
            public Object run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });


        AxisModule module = new AxisModule();
        module.setParent(configContext.getAxisConfiguration());
		String moduleName = "rampart-1.2";
		URL moduleurl = TuscanyAxisConfigurator.class.getResource("/org/apache/tuscany/sca/binding/ws/axis2/engine/config/rampart-1.2.mar");
		module.setName(moduleName);
		ClassLoader deploymentClassloader = Utils.createClassLoader(new URL[]{moduleurl},
													tccl,
													true,
													(File)configContext.getAxisConfiguration().getParameterValue(Constants.Configuration.ARTIFACTS_TEMP_DIR));
													
		module.setModuleClassLoader(deploymentClassloader);
		populateModule(module, moduleurl,configContext.getAxisConfiguration());
		module.setFileName(moduleurl);
		TuscanyAxisConfigurator.addNewModule(module, configContext.getAxisConfiguration());
		org.apache.axis2.util.Utils.calculateDefaultModuleVersion(
				configContext.getAxisConfiguration().getModules(), configContext.getAxisConfiguration());
    }
    
    private void populateModule(AxisModule module, URL moduleUrl, AxisConfiguration axisConfig) throws DeploymentException {
        try {
            ClassLoader classLoadere = module.getModuleClassLoader();
            InputStream moduleStream = classLoadere.getResourceAsStream("META-INF/module.xml");
            if (moduleStream == null) {
                moduleStream = classLoadere.getResourceAsStream("meta-inf/module.xml");
            }
            if (moduleStream == null) {
                throw new DeploymentException(
                        Messages.getMessage(
                                DeploymentErrorMsgs.MODULE_XML_MISSING, moduleUrl.toString()));
            }
            ModuleBuilder moduleBuilder = new ModuleBuilder(moduleStream, module, axisConfig);
            moduleBuilder.populateModule();
        } catch (IOException e) {
            throw new DeploymentException(e);
        }
    }
    
    private void fillQoSContext(Message message, MessageContext axis2MsgCtx) {
        if ( axis2MsgCtx.getProperty(WSHandlerConstants.RECV_RESULTS) != null &&
            axis2MsgCtx.getProperty(WSHandlerConstants.RECV_RESULTS) instanceof Vector ) {
            Vector recvResults = (Vector)axis2MsgCtx.getProperty(WSHandlerConstants.RECV_RESULTS);
            for ( int count1 = 0 ; count1 < recvResults.size() ; ++count1 ) {
                if ( recvResults.elementAt(count1) instanceof WSHandlerResult ) {
                    WSHandlerResult wshr = (WSHandlerResult)recvResults.elementAt(count1);
                    Vector results = wshr.getResults();
                    for ( int count2 = 0 ; count2 < results.size() ; ++count2 ) {
                        if ( results.elementAt(count2) instanceof WSSecurityEngineResult ) {
                            WSSecurityEngineResult securityResult = 
                                (WSSecurityEngineResult)wshr.getResults().elementAt(count2);
                            if ( securityResult.get("principal") != null ) {
                                message.getQoSContext().put(Message.QOS_CTX_SECURITY_PRINCIPAL, securityResult.get("principal"));
                            }
                        }
                    }
                }
            }
            
        }
    }
}
