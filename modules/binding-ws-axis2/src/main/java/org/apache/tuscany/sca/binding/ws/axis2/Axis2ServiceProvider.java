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
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.apache.axis2.deployment.util.Utils;
import org.apache.axis2.description.AxisEndpoint;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.description.WSDL11ToAxisServiceBuilder;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.description.WSDLToAxisServiceBuilder;
import org.apache.axis2.engine.ListenerManager;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.transport.http.server.HttpUtils;
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
import org.osoa.sca.ServiceRuntimeException;

public class Axis2ServiceProvider {
    
    private static final Logger logger = Logger.getLogger(Axis2ServiceProvider.class.getName());    

    private RuntimeComponent component;
    private AbstractContract contract;
    private WebServiceBinding wsBinding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
    private ConfigurationContext configContext;
    private JMSSender jmsSender;
    private JMSListener jmsListener;
    private Map<ClassLoader, List<PolicyHandlerTuple>> policyHandlerClassnames = null;
    private List<PolicyHandler> policyHandlerList = new ArrayList<PolicyHandler>();
    private Map<String, Port> urlMap = new HashMap<String, Port>();
    private Map<String, String> addressMap = new HashMap<String, String>();

    public static final QName QNAME_WSA_ADDRESS =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_ADDRESS);
    public static final QName QNAME_WSA_FROM =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_FROM);
    public static final QName QNAME_WSA_REFERENCE_PARAMETERS =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_REFERENCE_PARAMETERS);

    private static final String DEFAULT_QUEUE_CONNECTION_FACTORY = "TuscanyQueueConnectionFactory";

    public Axis2ServiceProvider(RuntimeComponent component,
                                AbstractContract contract,
                                WebServiceBinding wsBinding,
                                ServletHost servletHost,
                                MessageFactory messageFactory,
                                Map<ClassLoader, List<PolicyHandlerTuple>> policyHandlerClassnames) {

        this.component = component; 
        this.contract = contract; 
        this.wsBinding = wsBinding;
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;
        this.policyHandlerClassnames = policyHandlerClassnames;

        final boolean isRampartRequired = AxisPolicyHelper.isRampartRequired(wsBinding);
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
        } catch (AxisFault e) {
            throw new ServiceRuntimeException(e); // TODO: better exception
        } 

        configContext.setContextRoot(servletHost.getContextPath());

        /*
        // Look at all the Web Service bindings of the SCA service to see if any
        // of them have an existing generated WSDL definitions document.  If found,
        // use it for this binding as well.  If not found, generate a new document.
        Definition definition = null;
        for (Binding binding : contract.getBindings()) {
            if (binding instanceof WebServiceBinding) {
                definition = ((WebServiceBinding)binding).getWSDLDocument();
                if (definition != null) {
                    wsBinding.setWSDLDocument(definition);
                    break;
                }
            }
        }
        */
        // The above code is disabled temporarily.  Instead, we only look
        // for a WSDL definitions document in this binding and don't
        // attempt to share the same document across multiple bindings.
        Definition definition = wsBinding.getWSDLDocument();

        // generate a WSDL definitions document if needed
        if (definition == null) {
            definition = Axis2WSDLHelper.configureWSDLDefinition(wsBinding, component, contract, servletHost);
            wsBinding.setWSDLDocument(definition);
        }

        // The generated WSDL document maps ports to endpoint URLs.  Create a
        // reverse map that eliminates duplicate ports for the same endpoint.
        for (Object port : wsBinding.getService().getPorts().values()) {
            urlMap.put(getPortAddress((Port)port), (Port)port);
        }
    }
    
    public void start() {

        try {
            createPolicyHandlers();
            for (Map.Entry<String, Port> entry : urlMap.entrySet()) {
                AxisService axisService = createAxisService(entry.getKey(), entry.getValue());
                configContext.getAxisConfiguration().addService(axisService);
            }
            setupPolicyHandlers(policyHandlerList, configContext);
          
            Axis2ServiceServlet servlet = null;
            for (String endpointURL : urlMap.keySet()) {
                if (endpointURL.startsWith("http://") || endpointURL.startsWith("/")) {
                    if (servlet == null) {
                        servlet = new Axis2ServiceServlet();
                        servlet.init(configContext);
                    }
                    //[nash] configContext.setContextRoot(endpointURL);
                    servletHost.addServletMapping(endpointURL, servlet);
                } else if (endpointURL.startsWith("jms")) {
                    logger.log(Level.INFO,"Axis2 JMS URL=" + endpointURL);
                    
                    jmsListener = new JMSListener();
                    jmsSender = new JMSSender();
                    ListenerManager listenerManager = configContext.getListenerManager();
                    TransportInDescription trsIn = configContext.getAxisConfiguration().getTransportIn(Constants.TRANSPORT_JMS);
                                    
                    // get JMS transport parameters from the computed URL
                    Map<String, String> jmsProps = JMSUtils.getProperties(endpointURL);

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
            for (String endpointURL : urlMap.keySet()) {
                servletHost.removeServletMapping(endpointURL);
            }
        }

        if ( jmsSender != null )
            jmsSender.stop();

        try {
            for (String endpointURL : urlMap.keySet()) {
                // get the path to the service
                URI uriPath = new URI(endpointURL);
                String stringURIPath = uriPath.getPath();
            
                //[nash] Need a leading slash for WSDL imports to work with ?wsdl
                /*
                // remove any "/" from the start of the path
                if (stringURIPath.startsWith("/")) {
                    stringURIPath = stringURIPath.substring(1, stringURIPath.length());
                }
                */
            
                // remove it from the Axis context
                String modifiedURI = addressMap.remove(endpointURL);
                for (Object port : wsBinding.getService().getPorts().values()) {
                    if (modifiedURI.equals(getPortAddress((Port)port))) {
                        setPortAddress((Port)port, endpointURL);
                    }
                }
                configContext.getAxisConfiguration().removeService(stringURIPath);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (AxisFault e) {
            throw new RuntimeException(e);
        }
    }

    private AxisService createAxisService(String endpointURL, Port port) throws AxisFault {
        AxisService axisService;
        if (wsBinding.getWSDLDocument() != null) {
            axisService = createWSDLAxisService(endpointURL, port);
        } else {
            axisService = createJavaAxisService(endpointURL);
        }
        initAxisOperations(axisService);
        return axisService;
    }

    /**
     * Create an AxisService from the interface class from the SCA service interface
     */
    protected AxisService createJavaAxisService(String endpointURL) throws AxisFault {
        AxisService axisService = new AxisService();
        String path = URI.create(endpointURL).getPath();
        axisService.setName(path);
        axisService.setServiceDescription("Tuscany configured AxisService for service: " + endpointURL);
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
    protected AxisService createWSDLAxisService(String endpointURL, Port port) throws AxisFault {

        Definition definition = wsBinding.getWSDLDocument();
        QName serviceQName = wsBinding.getService().getQName();
        Definition def = getDefinition(definition, serviceQName);

        final WSDLToAxisServiceBuilder builder = new WSDL11ToAxisServiceBuilder(def, serviceQName, port.getName());
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

        String name = URI.create(endpointURL).getPath();
        //[nash] HTTP endpoints need a leading slash for WSDL imports to work with ?wsdl
        if (endpointURL.startsWith("jms")) {
            name = name.startsWith("/") ? name.substring(1) : name;
        }
        axisService.setName(name);
        axisService.setEndpointURL(endpointURL);
        axisService.setDocumentation("Tuscany configured AxisService for service: " + endpointURL);
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
        wsdlParam.setValue(definition);
        axisService.addParameter(wsdlParam);
        Parameter userWSDL = new Parameter("useOriginalwsdl", "true");
        axisService.addParameter(userWSDL);

        // Axis2 1.3 has a bug with returning incorrect values for the port
        // addresses.  To work around this, compute the values here.
        Parameter modifyAddr = new Parameter("modifyUserWSDLPortAddress", "false");
        axisService.addParameter(modifyAddr);
        String modifiedURL = setIPAddress(endpointURL);
        addressMap.put(endpointURL, modifiedURL);
        for (Object p : wsBinding.getService().getPorts().values()) {
            if (endpointURL.equals(getPortAddress((Port)p))) {
                setPortAddress((Port)p, modifiedURL);
            }
        }

        return axisService;
    }

    private String getPortAddress(Port port) {
        Object ext = port.getExtensibilityElements().get(0);
        if (ext instanceof SOAPAddress) {
            return ((SOAPAddress)ext).getLocationURI();
        }
        if (ext instanceof SOAP12Address) {
            return ((SOAP12Address)ext).getLocationURI();
        }
        return null;
    }

    private void setPortAddress(Port port, String locationURI) {
        Object ext = port.getExtensibilityElements().get(0);
        if (ext instanceof SOAPAddress) {
            ((SOAPAddress)ext).setLocationURI(locationURI);
        }
        if (ext instanceof SOAP12Address) {
            ((SOAP12Address)ext).setLocationURI(locationURI);
        }
    }

    private static String setIPAddress(String uriString) {
        try {
            URI uriObj = new URI(uriString);
            String ipAddr = HttpUtils.getIpAddress();
            String host = uriObj.getHost();
            return uriString.replace(host, ipAddr);
        } catch (Exception e) {
            // URI string not in expected format, so return it unmodified
            return uriString;
        }
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
