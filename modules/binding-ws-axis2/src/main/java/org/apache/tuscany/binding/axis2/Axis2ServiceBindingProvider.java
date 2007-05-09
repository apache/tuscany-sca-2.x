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

package org.apache.tuscany.binding.axis2;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.apache.tuscany.binding.ws.WebServiceBinding;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.http.ServletHost;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.ConversationSequence;
import org.apache.tuscany.invocation.InvocationChain;
import org.apache.tuscany.invocation.Invoker;
import org.apache.tuscany.invocation.Message;
import org.apache.tuscany.invocation.MessageImpl;
import org.apache.tuscany.provider.ServiceBindingProvider;
import org.apache.tuscany.scope.Scope;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.WorkContextTunnel;

public class Axis2ServiceBindingProvider implements ServiceBindingProvider<WebServiceBinding> {

    private RuntimeComponent component;
    private RuntimeComponentService service;
    private WebServiceBinding wsBinding;
    private ServletHost servletHost;
    private ConfigurationContext configContext;
    private RuntimeWire serviceWire;

    public Axis2ServiceBindingProvider(RuntimeComponent component, RuntimeComponentService service,
                                       WebServiceBinding wsBinding, ServletHost servletHost) {

        // TODO: before the SPI changes, a composite service was passed to the builder.
        // Is the change to a component service OK?

        this.component = component;
        this.service = service;
        this.wsBinding = wsBinding;
        this.servletHost = servletHost;

        try {
            TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
            configContext = tuscanyAxisConfigurator.getConfigurationContext();
        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO: better exception
        }
    }

    // methods for ServiceBindingActivator

    // TODO: what to do about the base URI?
    private static final String BASE_URI = "http://localhost:8080/";

    public void start() {

        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract == null) {
            contract = service.getInterfaceContract();
            wsBinding.setBindingInterfaceContract(contract);
        }

        // Set to use the Axiom data binding 
        contract.getInterface().setDefaultDataBinding(OMElement.class.getName());

        URI uri = computeActualURI(BASE_URI, component, service).normalize();
        wsBinding.setURI(uri.toString());
        
        // ??? following line was in Axis2BindingBuilder before the SPI changes and code reorg
        //
        // TODO: if <binding.ws> specifies the wsdl service then should create a service for every port
        //
        // is this still a valid to-do?

        serviceWire = service.getRuntimeWire(wsBinding);

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

    public void stop() {
        servletHost.removeServletMapping(wsBinding.getURI());
        try {
            configContext.getAxisConfiguration().removeService(wsBinding.getURI());
        } catch (AxisFault e) {
            throw new RuntimeException(e);
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
    protected URI computeActualURI(String baseURI, RuntimeComponent component, RuntimeComponentService service) {

        // TODO: before the SPI changes, a CompositeService was passed to the builder.
        // Is the change to ComponentService OK?

        // TODO: support wsa:Address

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
        
        // either there is no wsdl port endpoint URI or that URI is relative
        
        URI bindingURI = null;
        if (wsBinding.getURI() != null) {
            bindingURI = URI.create(wsBinding.getURI());
        }
        if (bindingURI != null && bindingURI.isAbsolute()) {
            // there is an absoulte uri specified on the binding: <binding.ws uri="xxx"
            if (wsdlURI != null) {
                // there is a relative URI in the wsdl port
                return URI.create(bindingURI + "/" + wsdlURI);
            } else {
                return bindingURI;
            }
        }
        
        // both the WSDL endpoint and binding uri are either unspecified or relative so
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
            // if the binding doesn't have a name use the name of the service (assumption, not in spec)
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

        // Axis2 fails if the endpoint has a trailing slash
        if (actualURI.endsWith("/")) {
            actualURI = actualURI.substring(0, actualURI.length() -1);
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
                    return URI.create(((SOAPAddress) extension).getLocationURI());
                }
            }
        }
        return null;
    }

    private AxisService createAxisService() throws AxisFault {
        Definition definition = wsBinding.getWSDLDefinition().getDefinition();

        // WSDLToAxisServiceBuilder only uses the service and port to find the wsdl4J Binding
        // An SCA service with binding.ws does not require a service or port so we may not have these
        // but 

        WSDLToAxisServiceBuilder builder = new WSDL11ToAxisServiceBuilder(definition, wsBinding.getServiceName(), wsBinding.getPortName());
        builder.setServerSide(true);
        AxisService axisService = builder.populateService();

        String path = wsBinding.getURI();
        if (path != null && path.length() > 1 && path.startsWith("/")) {
            path = path.substring(1);
        }
        axisService.setName(path);
        axisService.setServiceDescription("Tuscany configured AxisService for service: " + wsBinding.getURI());

        // Use the existing WSDL
        Parameter wsdlParam = new Parameter(WSDLConstants.WSDL_4_J_DEFINITION, null);
        wsdlParam.setValue(definition);
        axisService.addParameter(wsdlParam);
        Parameter userWSDL = new Parameter("useOriginalwsdl", "true");
        axisService.addParameter(userWSDL);

        for (Iterator i = axisService.getOperations(); i.hasNext();) {
            AxisOperation axisOp = (AxisOperation) i.next();
            Operation op = getOperation(axisOp);
            if (op != null) {

                if (op.isNonBlocking()) {
                    axisOp.setMessageExchangePattern(WSDL2Constants.MEP_URI_IN_ONLY);
                } else {
                    axisOp.setMessageExchangePattern(WSDL2Constants.MEP_URI_IN_OUT);
                }

                MessageReceiver msgrec = null;
                if (wsBinding.getBindingInterfaceContract().getCallbackInterface() != null) {
                    msgrec = new Axis2ServiceInOutAsyncMessageReceiver(this, op);
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

    // methods for ServiceBindingProvider

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    // other methods that were previously in Axis2ServiceBinding
    // TODO: are these still needed?

    private Map<Object, InvocationContext> invCtxMap = new HashMap<Object, InvocationContext>();
    private Set<String> seenConversations = Collections.synchronizedSet(new HashSet<String>());

    public Invoker createTargetInvoker(InterfaceContract contract, Operation operation) {
//        if (!operation.isCallback()) { TODO: no isCallback methjod yet?
//            throw new UnsupportedOperationException();
//        } else {
            return new Axis2ServiceCallbackTargetInvoker(this);
//        }
    }

    public void addMapping(Object msgId, InvocationContext invCtx) {
        this.invCtxMap.put(msgId, invCtx);
    }

    public InvocationContext retrieveMapping(Object msgId) {
        return this.invCtxMap.get(msgId);
    }

    public void removeMapping(Object msgId) {
        this.invCtxMap.remove(msgId);
    }

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
                OMElement ao = (OMElement) a;
                for (Iterator rpI =
                    ao.getChildrenWithName(new QName("http://www.w3.org/2005/08/addressing", "ReferenceParameters"));
                     rpI.hasNext();) {
                    OMElement rpE = (OMElement) rpI.next();
                    for (
                        Iterator cidI = rpE.getChildrenWithName(Axis2BindingInvoker.CONVERSATION_ID_REFPARM_QN);
                        cidI.hasNext();) {
                        OMElement cidE = (OMElement) cidI.next();
                        conversationID = cidE.getText();
                    }
                }

            }

        }
        return conversationID;
    }

    public Object invokeTarget(Operation op, Object[] args, Object messageId, String conversationID) throws InvocationTargetException {
        InvocationChain chain = null;
        for (InvocationChain ic : serviceWire.getInvocationChains()) {
            if (ic.getSourceOperation().equals(op)) {
                chain = ic;
            }
        }
        if (chain == null) {
            throw new IllegalStateException("no InvocationChain on wire for operation " + op);
        }
        
        Invoker headInvoker = chain.getHeadInvoker();
        WorkContext workContext = WorkContextTunnel.getThreadWorkContext();
        String oldConversationID = (String) workContext.getIdentifier(Scope.CONVERSATION);
        if (isConversational() && conversationID != null) {
            workContext.setIdentifier(Scope.CONVERSATION, conversationID);
        } else {
            workContext.clearIdentifier(Scope.CONVERSATION);
        }
        try {
            if (headInvoker == null) {
                // can no longer occur because TargetInvoker has been merged with Interceptor   
                throw new AssertionError("No target invoker interceptor [" + chain.getTargetOperation().getName() + "]");

                // short-circuit the dispatch and invoke the target directly
                // TargetInvoker targetInvoker = chain.getTargetInvoker();
                // if (targetInvoker == null) {
                //     throw new AssertionError("No target invoker [" + chain.getTargetOperation().getName() + "]");
                // }
                // return targetInvoker.invokeTarget(args, TargetInvoker.NONE, null);
            } else {

                Message msg = new MessageImpl();
                // no target invokers any more
                // msg.setTargetInvoker(chain.getTargetInvoker());
                // msg.pushFromAddress(getFromAddress()); // TODO : method gone in the TRUNK???
                if (messageId != null) {
                    msg.setMessageID(messageId);
                }
                msg.setBody(args);
                msg.setWorkContext(workContext);

                Message resp;

                if (isConversational()) {


                    Operation.ConversationSequence opSeq = op.getConversationSequence();
                    if (opSeq == Operation.ConversationSequence.CONVERSATION_END) {
                        assert seenConversations
                            .contains(conversationID) : "End of conversation called when no conversation existed";
                        msg.setConversationSequence(ConversationSequence.END);
                        seenConversations.remove(conversationID); //if a fault occurs does the conversation end?
                        //how do I know if a component called locally another opeation that ended this conversation?

                    } else {
                        boolean ec = seenConversations.contains(conversationID);
                        if (ec) {

                            msg.setConversationSequence(ConversationSequence.CONTINUE);
                        } else {
                            seenConversations.add(conversationID);
                            msg.setConversationSequence(ConversationSequence.START);
                        }
                    }

                }
                // dispatch the wire down the chain and get the response
                resp = headInvoker.invoke(msg);
                Object body = resp.getBody();
                if (resp.isFault()) {
                    throw new InvocationTargetException((Throwable) body);
                }
                return body;
            }
        } finally {
            if (null != oldConversationID) {
                workContext.setIdentifier(Scope.CONVERSATION, conversationID);
            } else {
                workContext.clearIdentifier(Scope.CONVERSATION);
            }
        }
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

    public boolean isConversational() {
        return wsBinding.getBindingInterfaceContract().getInterface().isConversational();
    }

}
