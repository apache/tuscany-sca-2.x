/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.tuscany.sca.binding.axis2;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.WSDL11ToAxisServiceBuilder;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.description.WSDLToAxisServiceBuilder;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider2;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

public class Axis2ServiceBindingProvider implements ServiceBindingProvider2 {

    private RuntimeComponent component;
    private RuntimeComponentService service;
    private WebServiceBinding wsBinding;
    private ServletHost servletHost;
    private ConfigurationContext configContext;
    private MessageFactory messageFactory;
    private Axis2ServiceBindingProvider callbackProvider;
    private InterfaceContract bindingInterfaceContract;

    // TODO: what to do about the base URI?
    private static final String BASE_URI = "http://localhost:8080/";

    public Axis2ServiceBindingProvider(RuntimeComponent component,
                                       RuntimeComponentService service,
                                       WebServiceBinding wsBinding,
                                       ServletHost servletHost,
                                       MessageFactory messageFactory) {

        this.component = component;
        this.service = service;
        this.wsBinding = wsBinding;
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;

        try {
            TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
            configContext = tuscanyAxisConfigurator.getConfigurationContext();
        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO: better exception
        }

        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract == null) {
            contract = service.getInterfaceContract().makeUnidirectional(wsBinding.isCallback());
            wsBinding.setBindingInterfaceContract(contract);
        }

        // Set to use the Axiom data binding
        if (contract.getInterface() != null) {
            contract.getInterface().setDefaultDataBinding(OMElement.class.getName());
        }
        if (contract.getCallbackInterface() != null) {
            contract.getCallbackInterface().setDefaultDataBinding(OMElement.class.getName());
        }

        // FIXME: only needed for the current tactical solution
        // connect forward providers with matching callback providers
        if (!wsBinding.isCallback()) {
            // this is a forward binding, so look for a matching callback binding
            if (service.getCallback() != null) {
                for (Binding binding : service.getCallback().getBindings()) {
                    if (service.getBindingProvider(binding) instanceof Axis2ServiceBindingProvider) {
                        // use the first compatible callback binding provider for this service
                        setCallbackProvider((Axis2ServiceBindingProvider)service.getBindingProvider(binding));
                        continue;
                    }
                }
            }
        } else {
            // this is a callback binding, so look for all matching forward bindings
            for (Binding binding : service.getBindings()) {
                if (service.getBindingProvider(binding) instanceof Axis2ServiceBindingProvider) {
                    // set all compatible forward binding providers for this service
                    ((Axis2ServiceBindingProvider)service.getBindingProvider(binding)).setCallbackProvider(this);
                }
            }
        }
    }

    // FIXME: only needed for the current tactical solution
    public void setCallbackProvider(Axis2ServiceBindingProvider callbackProvider) {
        if (this.callbackProvider == null) {
            this.callbackProvider = callbackProvider;
        }
    }

    public void start() {
        if (!wsBinding.isCallback()) {
            String uri = computeActualURI(BASE_URI, component, service).normalize().toString();
            if (uri.endsWith("/")) {
                uri = uri.substring(0, uri.length() - 1);
            }
            wsBinding.setURI(uri.toString());

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
    }

    public void stop() {
        if (!wsBinding.isCallback()) {
            servletHost.removeServletMapping(wsBinding.getURI());
            try {
                configContext.getAxisConfiguration().removeService(wsBinding.getURI());
            } catch (AxisFault e) {
                throw new RuntimeException(e);
            }
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
    protected URI computeActualURI(String baseURI, RuntimeComponent component, RuntimeComponentService service) {

        // TODO: support wsa:Address

        URI wsdlURI = null;
        if (wsBinding.getServiceName() != null && wsBinding.getBindingName() == null) {
            // <binding.ws> explicitly points at a wsdl port, may be a relative
            // URI
            wsdlURI = getEndpoint(wsBinding.getPort());
        }
        if (wsdlURI != null && wsdlURI.isAbsolute()) {
//            if (wsBinding.getURI() != null && (wsBinding.getServiceName() != null && wsBinding.getBindingName() == null)) {
//                throw new IllegalArgumentException("binding URI cannot be used with absolute WSDL endpoint URI");
//            }
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

        // with multiple services the default binding URI is the binding name
        if (bindingURI == null && component.getServices().size() > 1) {
            // if the binding doesn't have a name use the name of the service
            // (assumption, not in spec)
            if (wsBinding.getName() != null) {
                bindingURI = URI.create(wsBinding.getName());
            } else {
                bindingURI = URI.create(service.getName());
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
        Definition definition = wsBinding.getWSDLDefinition().getDefinition();

        // WSDLToAxisServiceBuilder only uses the service and port to find the
        // wsdl4J Binding
        // An SCA service with binding.ws does not require a service or port so
        // we may not have these
        // but

        WSDLToAxisServiceBuilder builder = new WSDL11ToAxisServiceBuilder(definition, wsBinding.getServiceName(),
                                                                          wsBinding.getPortName());
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
                if (service.getInterfaceContract().getCallbackInterface() != null) {
                    msgrec = new Axis2ServiceInOutAsyncMessageReceiver(this, callbackProvider, op);
                } else if (op.isNonBlocking()) {
                    msgrec = new Axis2ServiceInMessageReceiver(this, op);
                } else {
                    msgrec = new Axis2ServiceInOutSyncMessageReceiver(this, op);
                }
                axisOp.setMessageReceiver(msgrec);
            }
        }

        return axisService;
    }

    protected Operation getOperation(AxisOperation axisOp) {
        String operationName = axisOp.getName().getLocalPart();
        for (Operation op : wsBinding.getBindingInterfaceContract().getInterface().getOperations()) {
            if (op.getName().equalsIgnoreCase(operationName)) {
                return op;
            }
        }
        return null;
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public Invoker createCallbackInvoker(Operation operation) {
        return new Axis2ServiceCallbackTargetInvoker(this);
    }

    public boolean supportsAsyncOneWayInvocation() {
        return true;
    }

    // methods for Axis2 message receivers

    /**
     * @param inMC
     * @return
     */
    protected static String getConversationID(MessageContext inMC) {
        String conversationID = null;
        Iterator i = inMC.getEnvelope().getHeader()
            .getChildrenWithName(new QName("http://www.w3.org/2005/08/addressing", "From"));
        for (; i.hasNext();) {
            Object a = i.next();
            if (a instanceof OMElement) {
                OMElement ao = (OMElement)a;
                for (Iterator rpI = ao.getChildrenWithName(new QName("http://www.w3.org/2005/08/addressing",
                                                                     "ReferenceParameters")); rpI.hasNext();) {
                    OMElement rpE = (OMElement)rpI.next();
                    for (Iterator cidI = rpE.getChildrenWithName(Axis2BindingInvoker.CONVERSATION_ID_REFPARM_QN); cidI
                        .hasNext();) {
                        OMElement cidE = (OMElement)cidI.next();
                        conversationID = cidE.getText();
                    }
                }

            }

        }
        return conversationID;
    }

    public Object invokeTarget(Operation op, Object[] args, Object messageId, String conversationID)
        throws InvocationTargetException {

        Message requestMsg = messageFactory.createMessage();

        if (messageId != null) {
            requestMsg.setMessageID(messageId);
        }
        requestMsg.setBody(args);

        Message workContext = ThreadMessageContext.getMessageContext();

        ThreadMessageContext.setMessageContext(requestMsg);
        try {
            if (isConversational() && conversationID != null) {
                requestMsg.setConversationID(conversationID);
            } else {
                requestMsg.setConversationID(null);
            }

            Message responseMsg = service.getInvoker(wsBinding, op).invoke(requestMsg);

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

    // methods for handling callbacks

    private Map<Object, InvocationContext> invCtxMap = new HashMap<Object, InvocationContext>();

    public void addMapping(Object msgId, InvocationContext invCtx) {
        this.invCtxMap.put(msgId, invCtx);
    }

    public InvocationContext retrieveMapping(Object msgId) {
        return this.invCtxMap.get(msgId);
    }

    public void removeMapping(Object msgId) {
        this.invCtxMap.remove(msgId);
    }

    protected class InvocationContext {
        public MessageContext inMessageContext;

        public Operation operation;

        public SOAPFactory soapFactory;

        public CountDownLatch doneSignal;

        public InvocationContext(MessageContext messageCtx,
                                 Operation operation,
                                 SOAPFactory soapFactory,
                                 CountDownLatch doneSignal) {
            this.inMessageContext = messageCtx;
            this.operation = operation;
            this.soapFactory = soapFactory;
            this.doneSignal = doneSignal;
        }
    }

}
