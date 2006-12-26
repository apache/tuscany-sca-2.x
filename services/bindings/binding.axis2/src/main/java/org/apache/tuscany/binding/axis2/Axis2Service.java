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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Destroy;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.model.InteractionScope;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.WSDL11ToAxisServiceBuilder;
import org.apache.axis2.description.WSDLToAxisServiceBuilder;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.axis2.wsdl.WSDLConstants.WSDL20_2004Constants;
import org.apache.tuscany.binding.axis2.util.WebServicePortMetaData;

// org.apache.tuscany.spi.model
/**
 * An implementation of a {@link ServiceExtension} configured with the Axis2 binding
 *
 * @version $Rev$ $Date$
 */
public class Axis2Service extends ServiceExtension {
    private ServiceContract<?> serviceContract;

    private ServletHost servletHost;

    private ConfigurationContext configContext;

    private WebServiceBinding binding;

    private Map<Object, InvocationContext> invCtxMap = new HashMap<Object, InvocationContext>();

    private String serviceName;

    private WorkContext workContext;

    private Boolean conversational = null;

    private Set<String> seenConversations = Collections.synchronizedSet(new HashSet<String>());

    public Axis2Service(String theName,
                        ServiceContract<?> serviceContract,
                        CompositeComponent parent,
                        WebServiceBinding binding,
                        ServletHost servletHost,
                        ConfigurationContext configContext, WorkContext workContext) {

        super(theName, serviceContract.getInterfaceClass(), parent);

        this.serviceContract = serviceContract;
        this.binding = binding;
        this.servletHost = servletHost;
        this.configContext = configContext;
        this.serviceName = theName;
        this.workContext = workContext;
    }

    public void start() {
        super.start();

        try {
            configContext.getAxisConfiguration().addService(createAxisService(binding));
        } catch (AxisFault e) {
            throw new Axis2BindingRunTimeException(e);
        }

        Axis2ServiceServlet servlet = new Axis2ServiceServlet();
        servlet.init(configContext);
        configContext.setContextRoot(getName());
        servletHost.registerMapping("/" + getName(), servlet);
    }

    @Destroy
    public void stop() {
        servletHost.unregisterMapping("/" + getName());
        try {
            configContext.getAxisConfiguration().removeService(getName());
        } catch (AxisFault e) {
            throw new Axis2BindingRunTimeException(e);
        }
        super.stop();
    }

    private AxisService createAxisService(WebServiceBinding wsBinding) throws AxisFault {
        Definition definition = wsBinding.getWSDLDefinition();
        WebServicePortMetaData wsdlPortInfo =
            new WebServicePortMetaData(definition, wsBinding.getWSDLPort(), null, false);

        // TODO investigate if this is 20 wsdl what todo?
        WSDLToAxisServiceBuilder builder =
            new WSDL11ToAxisServiceBuilder(definition, wsdlPortInfo.getServiceName(), wsdlPortInfo.getPort()
                .getName());
        builder.setServerSide(true);
        AxisService axisService = builder.populateService();

        axisService.setName(this.getName());
        axisService.setServiceDescription("Tuscany configured AxisService for service: '" + this.getName()
            + "'");

        // Use the existing WSDL
        Parameter wsdlParam = new Parameter(WSDLConstants.WSDL_4_J_DEFINITION, null);
        wsdlParam.setValue(definition);
        axisService.addParameter(wsdlParam);
        Parameter userWSDL = new Parameter("useOriginalwsdl", "true");
        axisService.addParameter(userWSDL);

        PortType wsdlPortType = wsdlPortInfo.getPortType();
        for (Object o : wsdlPortType.getOperations()) {
            Operation wsdlOperation = (Operation) o;
            String operationName = wsdlOperation.getName();
            QName operationQN = new QName(definition.getTargetNamespace(), operationName);

            org.apache.tuscany.spi.model.Operation<?> op = serviceContract.getOperations().get(operationName);

            MessageReceiver msgrec = null;
            boolean opIsNonBlocking = op.isNonBlocking();
            if (serviceContract.getCallbackName() != null) {
                msgrec = new Axis2ServiceInOutAsyncMessageReceiver(this, op);
            } else if (opIsNonBlocking) {
                msgrec = new Axis2ServiceInMessageReceiver(this, op);
            } else {
                msgrec = new Axis2ServiceInOutSyncMessageReceiver(this, op);
            }

            AxisOperation axisOp = axisService.getOperation(operationQN);
            if (opIsNonBlocking) {
                axisOp.setMessageExchangePattern(WSDL20_2004Constants.MEP_URI_IN_ONLY);
            } else {
                axisOp.setMessageExchangePattern(WSDL20_2004Constants.MEP_URI_IN_OUT);
            }
            axisOp.setMessageReceiver(msgrec);
        }

        return axisService;
    }

    public Object invokeTarget(org.apache.tuscany.spi.model.Operation<?> op, Object[] args, Object messageId,
                               String conversationID)
        throws InvocationTargetException {
        InvocationChain chain = inboundWire.getInvocationChains().get(op);
        Interceptor headInterceptor = chain.getHeadInterceptor();
        String oldConversationID = (String) workContext.getIdentifier(Scope.CONVERSATION);
        if (isConversational() && conversationID != null) {
            workContext.setIdentifier(Scope.CONVERSATION, conversationID);
        } else {
            workContext.clearIdentifier(Scope.CONVERSATION);
        }
        try {
            if (headInterceptor == null) {
                // short-circuit the dispatch and invoke the target directly
                TargetInvoker targetInvoker = chain.getTargetInvoker();
                if (targetInvoker == null) {
                    throw new AssertionError("No target invoker [" + chain.getOperation().getName() + "]");
                }
                return targetInvoker.invokeTarget(args, TargetInvoker.NONE);
            } else {

                Message msg = new MessageImpl();
                msg.setTargetInvoker(chain.getTargetInvoker());
                msg.pushFromAddress(getFromAddress());
                if (messageId != null) {
                    msg.setMessageId(messageId);
                }
                msg.setBody(args);
                Message resp;

                if (isConversational()) {


                    int opSeq = op.getConversationSequence();
                    if (opSeq == org.apache.tuscany.spi.model.Operation.CONVERSATION_END) {
                        assert seenConversations
                            .contains(conversationID) : "End of conversation called when no conversation existed";
                        msg.setConversationSequence(TargetInvoker.END);
                        seenConversations.remove(conversationID); //if a fault occurs does the conversation end?
                        //how do I know if a component called locally another opeation that ended this conversation?

                    } else {
                        boolean ec = seenConversations.contains(conversationID);
                        if (ec) {

                            msg.setConversationSequence(TargetInvoker.CONTINUE);
                        } else {
                            seenConversations.add(conversationID);
                            msg.setConversationSequence(TargetInvoker.START);
                        }
                    }

                }
                // dispatch the wire down the chain and get the response
                // TODO http://issues.apache.org/jira/browse/TUSCANY-777
                ClassLoader oldtccl = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                    resp = headInterceptor.invoke(msg);
                } finally {
                    Thread.currentThread().setContextClassLoader(oldtccl);
                }
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

    protected Object getFromAddress() {
        return this.serviceName;
    }

    /**
     * Get the Method from an interface matching the WSDL operation name
     */
    protected Method getMethod(Class<?> serviceInterface, String operationName) throws BuilderConfigException {
        // Note: this doesn't support overloaded operations
        Method[] methods = serviceInterface.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(operationName)) {
                return m;
            }
            // tolerate WSDL with capatalized operation name
            StringBuilder sb = new StringBuilder(operationName);
            sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
            if (m.getName().equals(sb.toString())) {
                return m;
            }
        }
        throw new BuilderConfigException("no operation named " + operationName
            + " found on service interface: "
            + serviceInterface.getName());
    }

    public TargetInvoker createCallbackTargetInvoker(ServiceContract contract,
                                                     org.apache.tuscany.spi.model.Operation operation) {

        return new Axis2ServiceCallbackTargetInvoker(this);
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
                    for (Iterator cidI = rpE.getChildrenWithName(WebServiceBinding.CONVERSATION_ID_REFPARM_QN);
                         cidI.hasNext();) {
                        OMElement cidE = (OMElement) cidI.next();
                        conversationID = cidE.getText();
                    }
                }

            }

        }
        return conversationID;
    }

    protected class InvocationContext {
        public MessageContext inMessageContext;

        public org.apache.tuscany.spi.model.Operation<?> operation;

        public SOAPFactory soapFactory;

        public CountDownLatch doneSignal;

        public InvocationContext(MessageContext messageCtx,
                                 org.apache.tuscany.spi.model.Operation<?> operation,
                                 SOAPFactory soapFactory,
                                 CountDownLatch doneSignal) {
            this.inMessageContext = messageCtx;
            this.operation = operation;
            this.soapFactory = soapFactory;
            this.doneSignal = doneSignal;
        }
    }

    WorkContext getWorkContext() {
        return workContext;
    }

    boolean isConversational() {
        if (conversational == null) {
            conversational = serviceContract.getInteractionScope() == InteractionScope.CONVERSATIONAL;

        }
        return conversational;
    }
}
