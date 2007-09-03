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

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.deployment.util.Utils;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.WSDL11ToAxisServiceBuilder;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.description.WSDLToAxisServiceBuilder;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.tuscany.sca.assembly.AbstractContract;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.core.assembly.EndpointReferenceImpl;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

public class Axis2ServiceProvider {

    private AbstractContract contract;
    private WebServiceBinding wsBinding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
    private ConfigurationContext configContext;

    public static final QName QNAME_WSA_ADDRESS =
            new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_ADDRESS);
    public static final QName QNAME_WSA_FROM =
            new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_FROM);
    public static final QName QNAME_WSA_REFERENCE_PARAMETERS =
            new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_REFERENCE_PARAMETERS);

    // TODO: what to do about the base URI?
    // This port number may be used to construct callback URIs.  The value 8085 is used
    // beacuse it matches the service port number used by the simple-callback-ws sample.
    private static final String BASE_URI = "http://localhost:8085/";

    public Axis2ServiceProvider(RuntimeComponent component,
                                AbstractContract contract,
                                WebServiceBinding wsBinding,
                                ServletHost servletHost,
                                MessageFactory messageFactory) {

        this.contract = contract;
        this.wsBinding = wsBinding;
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;

        try {
            TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
            configContext = tuscanyAxisConfigurator.getConfigurationContext();
        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO: better exception
        }

        String uri = computeActualURI(BASE_URI, component, contract).normalize().toString();
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        wsBinding.setURI(uri);
    }

    public void start() {

        // TODO: if <binding.ws> specifies the wsdl service then should create a
        // service for every port

        try {
            configContext.getAxisConfiguration().addService(createAxisService());
        } catch (AxisFault e) {
            throw new RuntimeException(e);
        }

        Axis2ServiceServlet servlet = new Axis2ServiceServlet();
        servlet.init(configContext);
        String servletURI = wsBinding.getURI();
        servletHost.addServletMapping(servletURI, servlet);
    }

    public void stop() {
        servletHost.removeServletMapping(wsBinding.getURI());
        try {
            configContext.getAxisConfiguration().removeService(wsBinding.getURI());
        } catch (AxisFault e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Compute the endpoint URI based on section 2.1.1 of the WS binding spec 1.
     * The URIs in the endpoint(s) of the referenced WSDL, which may be relative
     * 2. The URI specified by the wsa:Address element of the
     * wsa:EndpointReference, which may be relative 3. The explicitly stated URI
     * in the "uri" attribute of the binding.ws element, which may be relative,
     * 4. The implicit URI as defined by in section 1.7 in the SCA Assembly spec
     * If the <binding.ws> has no wsdlElement but does have a uri attribute then
     * the uri takes precidence over any implicitly used WSDL.
     * 
     * @param parent
     */
    protected URI computeActualURI(String baseURI, RuntimeComponent component, AbstractContract contract) {

        // TODO: support wsa:Address

        URI wsdlURI = null;
        if (wsBinding.getServiceName() != null && wsBinding.getBindingName() == null) {
            // <binding.ws> explicitly points at a wsdl port, may be a relative URI
            wsdlURI = getEndpoint(wsBinding.getPort());
        }
        if (wsdlURI != null && wsdlURI.isAbsolute()) {
            return URI.create(wsdlURI.toString());
        }

        // either there is no wsdl port endpoint URI or that URI is relative

        URI bindingURI = URI.create(wsBinding.getURI());
        if (bindingURI.isAbsolute()) {
            // there is an absoulte uri specified on the binding: <binding.ws
            // uri="xxx"
            if (wsdlURI != null) {
                // there is a relative URI in the wsdl port
                return URI.create(bindingURI + "/" + wsdlURI);
            } else {
                return bindingURI;
            }
        } else {
            bindingURI = URI.create(baseURI + "/" + wsBinding.getURI());
            return bindingURI;
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
                    return URI.create(((SOAPAddress)extension).getLocationURI());
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
     * Create an AxisService from the WSDL doc used by ws binding
     */
    protected AxisService createWSDLAxisService() throws AxisFault {
        Definition definition = wsBinding.getWSDLDefinition().getDefinition();

        // WSDLToAxisServiceBuilder only uses the service and port to find the wsdl4J Binding
        // An SCA service with binding.ws does not require a service or port so we may not have
        // these but ...

        Axis2ServiceClient.setServiceAndPort(wsBinding);
        QName serviceQName = wsBinding.getServiceName();
        String portName = wsBinding.getPortName();

        WSDLToAxisServiceBuilder builder = new WSDL11ToAxisServiceBuilder(definition, serviceQName, portName);
        builder.setServerSide(true);
        AxisService axisService = builder.populateService();

        String path = URI.create(wsBinding.getURI()).getPath();
        axisService.setName(path);
        axisService.setServiceDescription("Tuscany configured AxisService for service: " + wsBinding.getURI());

        // Use the existing WSDL
        Parameter wsdlParam = new Parameter(WSDLConstants.WSDL_4_J_DEFINITION, null);
        wsdlParam.setValue(definition);
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
                    msgrec = new Axis2ServiceInMessageReceiver(this, op);
                } else {
                    msgrec = new Axis2ServiceInOutSyncMessageReceiver(this, op);
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

    public Object invokeTarget(Operation op,
                               Object[] args,
                               MessageContext inMC) throws InvocationTargetException {

        String callbackAddress = null;
        String callbackID = null;
        Object conversationID = null;

        //FIXME: can we use the Axis2 addressing support for this?
        SOAPHeader header = inMC.getEnvelope().getHeader();
        if (header != null) {
            Iterator<?> i = header.getChildrenWithName(QNAME_WSA_FROM);
            Object a = null;
            for (; i.hasNext();) {
                if (a != null) {
                    throw new IllegalArgumentException("Duplicate wsa:From element");
                }
                a = i.next();
                if (a instanceof OMElement) {
                    OMElement ao = (OMElement)a;

                    // process required Address element
                    Iterator<?> adI = ao.getChildrenWithName(QNAME_WSA_ADDRESS);
                    OMElement adE = null;
                    for (; adI.hasNext();) {
                        if (adE != null) {
                            throw new IllegalArgumentException("Duplicate wsa:Address element");
                        }
                        adE = (OMElement)adI.next();
                        if (contract.getInterfaceContract().getCallbackInterface() != null) {
                            callbackAddress = adE.getText();
                            if (callbackAddress.equals(AddressingConstants.Final.WSA_ANONYMOUS_URL)) {
                                throw new IllegalArgumentException("Anonymous wsa:Address passed for callback");
                            }
                        }
                    }
                    if (adE == null) {
                        throw new IllegalArgumentException("Missing wsa:Address element");
                    }

                    // process optional ReferenceParameters element
                    Iterator<?> rpI = ao.getChildrenWithName(QNAME_WSA_REFERENCE_PARAMETERS);
                    OMElement rpE = null;
                    for (; rpI.hasNext();) {
                        if (rpE != null) {
                            throw new IllegalArgumentException("Duplicate wsa:ReferenceParameters element");
                        }
                        rpE = (OMElement)rpI.next();
                        Iterator<?> cidI = rpE.getChildrenWithName(Axis2BindingInvoker.CONVERSATION_ID_REFPARM_QN);
                        OMElement cidE = null;
                        for (; cidI.hasNext();) {
                            if (cidE != null) {
                                throw new IllegalArgumentException("Duplicate SCA conversation ID element");
                            }
                            cidE = (OMElement)cidI.next();
                            if (isConversational()) {
                                conversationID = cidE.getText();
                            }
                        }
                        Iterator<?> cbidI = rpE.getChildrenWithName(Axis2BindingInvoker.CALLBACK_ID_REFPARM_QN);
                        OMElement cbidE = null;
                        for (; cbidI.hasNext();) {
                            if (cbidE != null) {
                                throw new IllegalArgumentException("Duplicate SCA callback ID element");
                            }
                            cbidE = (OMElement)cbidI.next();
                            if (contract.getInterfaceContract().getCallbackInterface() != null) {
                                callbackID = cbidE.getText();
                            }
                        }
                    }

                }
            }
        }

        Message requestMsg = messageFactory.createMessage();
        requestMsg.setBody(args);
        requestMsg.setTo(((RuntimeComponentService)contract).getRuntimeWire(getBinding()).getTarget());
        if (callbackAddress != null) {
            requestMsg.setFrom(new EndpointReferenceImpl(callbackAddress));
        }
        if (callbackID != null) {
            requestMsg.setCorrelationID(callbackID);
        }
        if (conversationID != null) {
            requestMsg.setConversationID(conversationID);
        }

        Message workContext = ThreadMessageContext.setMessageContext(requestMsg);
        try {
            Message responseMsg = ((RuntimeComponentService)contract).getInvoker(getBinding(), op).invoke(requestMsg);
            if (responseMsg.isFault()) {
                throw new InvocationTargetException((Throwable)responseMsg.getBody());
            }
            return responseMsg.getBody();
        } finally {
            ThreadMessageContext.setMessageContext(workContext);
        }
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
    protected Binding getBinding(){
        return wsBinding;
    }

}
