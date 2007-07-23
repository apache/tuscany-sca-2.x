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

package org.apache.tuscany.sca.binding.axis2;

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
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.core.runtime.EndpointReferenceImpl;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

public class Axis2ServiceProvider {

    private RuntimeComponent component;
    private AbstractContract contract;
    private WebServiceBinding wsBinding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
    private ConfigurationContext configContext;

    // TODO: what to do about the base URI?
    //FIXME: changed from 8080 to 8085 as a hack to work around current limitation that
    // the base URI must be the same for all servlet mappings in a single ServletHost.
    // It appears that the code in both http-tomcat and http-jetty has this restriction.
    // This port number may be used to construct callback URIs.  The value 8085 is used
    // beacuse it matches the service port number used by the simple-callback-ws sample.
    private static final String BASE_URI = "http://localhost:8085/";

    public Axis2ServiceProvider(RuntimeComponent component,
                                AbstractContract contract,
                                WebServiceBinding wsBinding,
                                ServletHost servletHost,
                                MessageFactory messageFactory) {

        this.component = component;
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

    protected void start() {

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
        configContext.setContextRoot(servletURI);
        servletHost.addServletMapping(servletURI, servlet);
    }

    protected void stop() {
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
            // <binding.ws> explicitly points at a wsdl port, may be a relative
            // URI
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
            // there is an absoulte uri specified on the binding: <binding.ws
            // uri="xxx"
            if (wsdlURI != null) {
                // there is a relative URI in the wsdl port
                return URI.create(bindingURI + "/" + wsdlURI);
            } else {
                return bindingURI;
            }
        }

        // both the WSDL endpoint and binding uri are either unspecified or
        // relative so
        // the endpoint is based on the component name and service binding URI

        URI componentURI = URI.create(component.getName());

        String actualURI;
        if (componentURI.isAbsolute()) {
            actualURI = componentURI.toString();
        } else {
            actualURI = baseURI + "/" + componentURI;
        }

        // for service bindings with multiple services, the default binding URI is the binding name
        // for callback reference bindings, add a prefix "$callback$." to ensure uniqueness
        if (bindingURI == null && (wsBinding.isCallback() || component.getServices().size() > 1)) {
            if (!wsBinding.isCallback()) {
                bindingURI = URI.create(wsBinding.getName());
            } else {
                bindingURI = URI.create("$callback$." + wsBinding.getName());
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
     * Create an AxisService from the interface class from the SCA service
     * interface
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
        for (Iterator i = axisService.getOperations(); i.hasNext();) {
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
        Interface iface =
            wsBinding.isCallback() ? wsBinding.getBindingInterfaceContract().getCallbackInterface() : wsBinding
                .getBindingInterfaceContract().getInterface();
        for (Operation op : iface.getOperations()) {
            if (op.getName().equalsIgnoreCase(operationName)) {
                return op;
            }
        }
        return null;
    }

    // methods for Axis2 message receivers

    //FIXME: can we use the Axis2 addressing support for this?
    /**
     * @param inMC
     * @return conversationID
     */
    protected String getConversationID(MessageContext inMC) {
        String conversationID = null;
        if (isConversational()) {
            SOAPHeader header = inMC.getEnvelope().getHeader();
            if (header != null) {
                Iterator i = header.getChildrenWithName(new QName("http://www.w3.org/2005/08/addressing", "From"));
                for (; i.hasNext();) {
                    Object a = i.next();
                    if (a instanceof OMElement) {
                        OMElement ao = (OMElement)a;
                        for (Iterator rpI =
                            ao.getChildrenWithName(new QName("http://www.w3.org/2005/08/addressing",
                                                             "ReferenceParameters")); rpI.hasNext();) {
                            OMElement rpE = (OMElement)rpI.next();
                            for (Iterator cidI =
                                rpE.getChildrenWithName(Axis2BindingInvoker.CONVERSATION_ID_REFPARM_QN); cidI.hasNext();) {
                                OMElement cidE = (OMElement)cidI.next();
                                conversationID = cidE.getText();
                            }
                        }

                    }
                }
            }
        }
        return conversationID;
    }

    //FIXME: can we use the Axis2 addressing support for this?
    /**
     * @param inMC
     * @return fromEPR
     */
    protected String getFromEPR(MessageContext inMC) {
        String fromEPR = null;
        if (contract instanceof RuntimeComponentService && contract.getInterfaceContract().getCallbackInterface() != null) {
            SOAPHeader header = inMC.getEnvelope().getHeader();
            if (header != null) {
                Iterator i = header.getChildrenWithName(new QName("http://www.w3.org/2005/08/addressing", "From"));
                for (; i.hasNext();) {
                    Object a = i.next();
                    if (a instanceof OMElement) {
                        OMElement ao = (OMElement)a;
                        for (Iterator adI =
                            ao.getChildrenWithName(new QName("http://www.w3.org/2005/08/addressing", "Address")); adI
                            .hasNext();) {
                            OMElement adE = (OMElement)adI.next();
                            fromEPR = adE.getText();
                        }
                    }
                }
            }
        }
        return fromEPR;
    }

    public Object invokeTarget(Operation op,
                               Object[] args,
                               Object messageId,
                               String conversationID,
                               String callbackAddress) throws InvocationTargetException {

        Message requestMsg = messageFactory.createMessage();

        if (messageId != null) {
            requestMsg.setMessageID(messageId);
        }
        requestMsg.setBody(args);

        if (contract instanceof RuntimeComponentService)
            requestMsg.setFrom(((RuntimeComponentService)contract).getRuntimeWire(wsBinding).getSource());
        else
            requestMsg.setFrom(((RuntimeComponentReference)contract).getRuntimeWire(wsBinding).getSource());
        if (callbackAddress != null) {
            requestMsg.setTo(new EndpointReferenceImpl(callbackAddress));
        }

        Message workContext = ThreadMessageContext.getMessageContext();

        ThreadMessageContext.setMessageContext(requestMsg);
        try {
            if (isConversational() && conversationID != null) {
                requestMsg.setConversationID(conversationID);
            } else {
                requestMsg.setConversationID(null);
            }

            Message responseMsg =
                contract instanceof RuntimeComponentService ? ((RuntimeComponentService)contract).getInvoker(wsBinding,
                                                                                                             op)
                    .invoke(requestMsg) : ((RuntimeComponentReference)contract).getCallbackInvocationChain(wsBinding,
                                                                                                           op)
                    .getHeadInvoker().invoke(requestMsg);

            if (responseMsg.isFault()) {
                throw new InvocationTargetException((Throwable)responseMsg.getBody());
            }
            return responseMsg.getBody();

        } finally {
            ThreadMessageContext.setMessageContext(workContext);
        }
    }

    public boolean isConversational() {
        if (!wsBinding.isCallback()) {
            return wsBinding.getBindingInterfaceContract().getInterface().isConversational();
        } else {
            return wsBinding.getBindingInterfaceContract().getCallbackInterface().isConversational();
        }
    }

}
