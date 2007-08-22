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
package org.apache.tuscany.sca.binding.notification;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.sca.binding.notification.encoding.BrokerConsumerReference;
import org.apache.tuscany.sca.binding.notification.encoding.BrokerID;
import org.apache.tuscany.sca.binding.notification.encoding.ConnectionOverride;
import org.apache.tuscany.sca.binding.notification.encoding.Constants;
import org.apache.tuscany.sca.binding.notification.encoding.ConsumerReference;
import org.apache.tuscany.sca.binding.notification.encoding.EncodingRegistry;
import org.apache.tuscany.sca.binding.notification.encoding.EncodingUtils;
import org.apache.tuscany.sca.binding.notification.encoding.EndpointAddress;
import org.apache.tuscany.sca.binding.notification.encoding.EndpointReference;
import org.apache.tuscany.sca.binding.notification.encoding.ReferenceProperties;
import org.apache.tuscany.sca.binding.notification.encoding.Subscribe;
import org.apache.tuscany.sca.binding.notification.util.IOUtils;
import org.apache.tuscany.sca.binding.notification.util.NotificationServlet;
import org.apache.tuscany.sca.binding.notification.util.URIUtil;
import org.apache.tuscany.sca.binding.notification.util.IOUtils.IOUtilsException;
import org.apache.tuscany.sca.binding.notification.util.IOUtils.Writeable;
import org.apache.tuscany.sca.binding.notification.util.NotificationServlet.NotificationServletStreamHandler;
import org.apache.tuscany.sca.core.invocation.MessageImpl;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * The runtime representaion of the local service binding
 *
 * @version $Rev$ $Date$
 */
public class NotificationServiceBindingProvider
        implements ServiceBindingProvider, NotificationServletStreamHandler {
    
    private RuntimeWire wire;
    private NotificationBinding notificationBinding;
    private RuntimeComponentService service;
    private ServletHost servletHost;
    private NotificationTypeManager ntm;
    private EncodingRegistry encodingRegistry;
    private URI notificationType;
    private URL myUrl;
    private URL remoteNtmUrl;
    private static final String consumerPathBase = "/consumer";
    private boolean started;
    private NotificationBrokerManager brokerManager;
    private String brokerID;
    
    public NotificationServiceBindingProvider(NotificationBinding notificationBinding,
                                              RuntimeComponent component,
                                              RuntimeComponentService service,
                                              ServletHost servletHost,
                                              NotificationTypeManager ntm,
                                              EncodingRegistry encodingRegistry,
                                              String httpUrl,
                                              NotificationBrokerManager brokerManager) {
        this.notificationBinding = notificationBinding;
        this.service = service;
        this.servletHost = servletHost;
        this.ntm = ntm;
        this.encodingRegistry = encodingRegistry;
        this.notificationType = notificationBinding.getNotificationType();
        String ntmAddress = notificationBinding.getNtmAddress();
        String notificationTypePath = URIUtil.getPath(notificationType);
        try {
            this.myUrl = new URL(httpUrl + consumerPathBase + notificationTypePath);
            remoteNtmUrl = null;
            if (ntmAddress != null && notificationType != null) {
                remoteNtmUrl = new URL(ntmAddress + notificationTypePath);
            }
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        this.started = false;
        this.brokerManager = brokerManager;
        this.brokerID = null;

        URI uri = URI.create(component.getURI() + "/" + notificationBinding.getName());
        notificationBinding.setURI(uri.toString());
        Interface interfaze = service.getInterfaceContract().getInterface();
        interfaze.setDefaultDataBinding(OMElement.class.getName());
        for (Operation operation : interfaze.getOperations()) {
            operation.setNonBlocking(false);
        }
    }
    
    public NotificationBinding getBinding() {
        return notificationBinding;
    }
    
    public boolean isStarted() {
        return started;
    }
    
    public URL getURL() {
        return myUrl;
    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    public void start() {
        if (started) {
            return;
        }
        
        RuntimeComponentService componentService = (RuntimeComponentService) service;
        wire = componentService.getRuntimeWire(notificationBinding);
        
        brokerManager.serviceProviderStarted(notificationType, this, remoteNtmUrl);
        started = true;
    }

    public void stop() {
    }
    
    public void deployConsumer() {
        WriteableSubscribe ws = new WriteableSubscribe(myUrl, null);
        List<URL> producerList = new ArrayList<URL>();
        String sequenceType = ntm.newConsumer(notificationType, myUrl, remoteNtmUrl, producerList);
        if (Constants.EndProducers.equals(sequenceType)) {
            for (URL producerUrl : producerList) {
                subscribeWithProducer(producerUrl, null, ws);
            }
        }
        else if (Constants.BrokerProducers.equals(sequenceType)) {
            // Pick a broker producer, for now the first one
            URL producerUrl = producerList.get(0);
            subscribeWithProducer(producerUrl, null, ws);
        }

        servletHost.addServletMapping(myUrl.toString(), new NotificationServlet(this));
    }
    
    protected void subscribeWithProducer(URL producerUrl, String brokerID, WriteableSubscribe ws) {
        if (ws == null) {
            ws = new WriteableSubscribe(myUrl, brokerID);
        }
        try {
            IOUtils.sendHttpRequest(producerUrl, Constants.SUBSCRIBE_OP, ws, null);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void deployBroker(String brokerID, EndpointReference brokerProducerEPR, List<EndpointReference> producerList) {
        if (brokerProducerEPR != null) {
            subscribeWithProducer(brokerProducerEPR.getEndpointAddress().getAddress(), brokerID, null);
        }
        this.brokerID = brokerID;
        if (producerList != null && !producerList.isEmpty()) {
            WriteableConnectionOverride wco = new WriteableConnectionOverride(myUrl, brokerID);
            for (EndpointReference producerEPR : producerList) {
                try {
                    IOUtils.sendHttpRequest(producerEPR.getEndpointAddress().getAddress(), Constants.CONNECTION_OVERRIDE_OP, wco, null);
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        servletHost.addServletMapping(myUrl.toString(), new NotificationServlet(this));
    }
    
    public void replaceBrokerConnection(EndpointReference chosenBrokerProducerEpr) {
        if (brokerID == null) {
            throw new RuntimeException("Missing broker id");
        }
        URL producerUrl = chosenBrokerProducerEpr.getEndpointAddress().getAddress();
        subscribeWithProducer(producerUrl, brokerID, null);
    }

    public void handle(Map<String, String> headers, ServletInputStream istream, int contentLength, ServletOutputStream ostream) {
        String opHeader = headers.get(IOUtils.Notification_Operation);
        String incomingBrokerID = headers.get(Constants.Broker_ID);
        if (opHeader == null) {
            throw new RuntimeException("Missing operation header");
        }
        if (wire == null) {
            throw new RuntimeException("Missing wire");
        }
        InvocationChain chain = null;
        for (InvocationChain ch : wire.getInvocationChains()) {
            // We may want to use more than just the op name
            if(ch.getTargetOperation().getName().equals(opHeader)) {
                chain = ch;
                break;
            }
        }
        if (chain == null) {
            throw new RuntimeException("Can't find invocation chain match for [" + opHeader + "]");
        }
        byte[] payload = null;
        try {
            payload = IOUtils.readFully(istream, contentLength);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
        Object[] args = getArgsFromByteArray(payload, incomingBrokerID);
        
        invoke(chain, args);
        
        // Doing nothing to ostream is equivalent to returning null
    }
    
    private Object[] getArgsFromByteArray(byte[] payload, String incomingBrokerID) {
        try {
            StAXOMBuilder builder = new StAXOMBuilder(new ByteArrayInputStream(payload));
            OMElement element = builder.getDocumentElement();
            return new Object[] { element, payload, incomingBrokerID };
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void invoke(InvocationChain chain, Object[] args) {
        Message msg = new MessageImpl();
        msg.setBody(args);
        chain.getHeadInvoker().invoke(msg);
    }
    
    class WriteableSubscribe implements Writeable {
        
        private Subscribe sub;
        
        public WriteableSubscribe(URL url, String brokerID) {
            EndpointAddress epa = new EndpointAddress();
            epa.setAddress(url);
            EndpointReference epr = new EndpointReference();
            epr.setEndpointAddress(epa);
            if (brokerID != null) {
                BrokerID cbi = new BrokerID();
                cbi.setID(brokerID);
                ReferenceProperties crp = new ReferenceProperties();
                crp.addProperty(cbi);
                epr.setReferenceProperties(crp);
            }
            ConsumerReference cr = new ConsumerReference();
            cr.setReference(epr);
            sub = new Subscribe();
            sub.setConsumerReference(cr);
        }
        
        public void write(OutputStream os) throws IOUtilsException {
            EncodingUtils.encodeToStream(encodingRegistry, sub, os);
        }
    }
    
    class WriteableConnectionOverride implements Writeable {
        
        private ConnectionOverride connectionOverride;
        
        public WriteableConnectionOverride(URL brokerConsumerUrl, String brokerID) {
            EndpointAddress epa = new EndpointAddress();
            epa.setAddress(brokerConsumerUrl);
            EndpointReference brokerConsumerEPR = new EndpointReference();
            brokerConsumerEPR.setEndpointAddress(epa);
            BrokerID cbi = new BrokerID();
            cbi.setID(brokerID);
            ReferenceProperties crp = new ReferenceProperties();
            crp.addProperty(cbi);
            brokerConsumerEPR.setReferenceProperties(crp);
            BrokerConsumerReference brokerConsumerReference = new BrokerConsumerReference();
            brokerConsumerReference.setReference(brokerConsumerEPR);
            connectionOverride = new ConnectionOverride();
            connectionOverride.setBrokerConsumerReference(brokerConsumerReference);
        }
        
        public void write(OutputStream os) throws IOUtilsException {
            EncodingUtils.encodeToStream(encodingRegistry, connectionOverride, os);
        }
    }
}
