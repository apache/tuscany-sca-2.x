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

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.binding.notification.encoding.Broker;
import org.apache.tuscany.sca.binding.notification.encoding.ConnectionOverride;
import org.apache.tuscany.sca.binding.notification.encoding.Constants;
import org.apache.tuscany.sca.binding.notification.encoding.EncodingObject;
import org.apache.tuscany.sca.binding.notification.encoding.EncodingRegistry;
import org.apache.tuscany.sca.binding.notification.encoding.EncodingUtils;
import org.apache.tuscany.sca.binding.notification.encoding.EndpointReference;
import org.apache.tuscany.sca.binding.notification.encoding.ReplaceBrokerConnection;
import org.apache.tuscany.sca.binding.notification.encoding.Subscribe;
import org.apache.tuscany.sca.binding.notification.util.NotificationServlet;
import org.apache.tuscany.sca.binding.notification.util.URIUtil;
import org.apache.tuscany.sca.binding.notification.util.NotificationServlet.NotificationServletStreamHandler;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

/**
 * The runtime representaion of the notification reference binding
 *
 * @version $Rev$ $Date$
 */
public class NotificationReferenceBindingProvider
        implements ReferenceBindingProvider, NotificationServletStreamHandler {

    private static final String producerPathBase = "/producer";
    private NotificationReferenceBindingInvoker invoker;
    private RuntimeComponentReference reference;
    private NotificationBinding notificationBinding;
    private ServletHost servletHost;
    private NotificationTypeManager ntm;
    private EncodingRegistry encodingRegistry;
    private URI notificationType;
    private URL myUrl;
    private URL remoteNtmUrl;
    private boolean started;
    private NotificationBrokerManager brokerManager;

    public NotificationReferenceBindingProvider(NotificationBinding notificationBinding,
                                                RuntimeComponent component,
                                                RuntimeComponentReference reference,
                                                ServletHost servletHost,
                                                NotificationTypeManager ntm,
                                                EncodingRegistry encodingRegistry,
                                                String httpUrl,
                                                NotificationBrokerManager brokerManager) {
        this.invoker = null;
        this.notificationBinding = notificationBinding;
        this.reference = reference;
        this.servletHost = servletHost;
        this.ntm = ntm;
        this.encodingRegistry = encodingRegistry;
        this.notificationType = notificationBinding.getNotificationType();
        String ntmAddress = notificationBinding.getNtmAddress();
        String notificationTypePath = URIUtil.getPath(notificationType);
        try {
            this.myUrl = new URL(httpUrl + producerPathBase + notificationTypePath);
            this.remoteNtmUrl = null;
            if (ntmAddress != null && notificationType != null) {
                remoteNtmUrl = new URL(ntmAddress + notificationTypePath);
            }
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        this.started = false;
        this.brokerManager = brokerManager;
        
        URI uri = URI.create(component.getURI() + "/" + notificationBinding.getName());
        notificationBinding.setURI(uri.toString());
        Interface interfaze = reference.getInterfaceContract().getInterface();
        interfaze.setDefaultDataBinding(OMElement.class.getName());
        for (Operation operation : interfaze.getOperations()) {
            operation.setNonBlocking(false);
        }
    }
    
    public NotificationBinding getBinding() {
        return notificationBinding;
    }
    
    public URL getURL() {
        return myUrl;
    }

    public boolean isStarted() {
        return started;
    }

    public Invoker createInvoker(Operation operation, boolean isCallback) {
        if (isCallback) {
            throw new UnsupportedOperationException();
        }
        if (invoker == null) {
            invoker = new NotificationReferenceBindingInvoker(operation);
        }
        return invoker;
    }

    public InterfaceContract getBindingInterfaceContract() {
        return reference.getInterfaceContract();
    }

    public void start() {
        if (started) {
            return;
        }

        brokerManager.referenceProviderStarted(notificationType, this, remoteNtmUrl);
        started = true;
    }

    public void stop() {
    }
    
    public void deployProducer() {
        List<URL> consumerList = new ArrayList<URL>();
        String sequenceType;
        try {
            sequenceType = ntm.newProducer(notificationType, myUrl, remoteNtmUrl, consumerList);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        if (Constants.EndConsumers.equals(sequenceType)) {
            for (URL consumerUrl : consumerList) {
                invoker.addSubscriberUrl(consumerUrl);
            }
        }
        else if (Constants.BrokerConsumers.equals(sequenceType)) {
            // Pick a broker consumer, for now the first one
            URL consumerUrl = consumerList.get(0);
            invoker.addSubscriberUrl(consumerUrl);
        }

        servletHost.addServletMapping(myUrl.toString(), new NotificationServlet(this));
    }
    
    public void deployBroker(String brokerID, EndpointReference brokerConsumerEPR, List<EndpointReference> consumerList) {
        if (brokerConsumerEPR != null) {
            invoker.addSubscriber(brokerConsumerEPR);            
        }
        if (consumerList != null && !consumerList.isEmpty()) {
            for (EndpointReference consumerEPR : consumerList) {
                invoker.addSubscriber(consumerEPR);
            }
        }
        invoker.setBrokerID(brokerID);
        servletHost.addServletMapping(myUrl.toString(), new NotificationServlet(this));
    }
    
    public void undeployBroker(URL brokerConsumerUrl) {
        EndpointReference brokerConsumerEpr = EncodingUtils.createEndpointReference(brokerConsumerUrl, invoker.getBrokerID());
        ntm.removeBroker(brokerConsumerEpr, invoker.getNeighborBrokerConsumerEprs(), remoteNtmUrl);
        invoker.removeBrokerSubscribers();
    }
    
    public void handle(Map<String, String> headers, ServletInputStream istream, int contentLength, ServletOutputStream ostream) {

        try {
            EncodingObject eo = EncodingUtils.decodeFromStream(encodingRegistry, istream);
            if (eo instanceof Subscribe) {
                Subscribe sub = (Subscribe)eo;
                invoker.addSubscriber(sub.getConsumerReference().getReference());
            }
            else if (eo instanceof ConnectionOverride) {
                ConnectionOverride co = (ConnectionOverride)eo;
                invoker.replaceSubscribers(co.getBrokerConsumerReference().getReference());
            }
            else if (eo instanceof ReplaceBrokerConnection) {
                ReplaceBrokerConnection rbc = (ReplaceBrokerConnection)eo;
                URL removedBrokerConsumerEpr = rbc.getRemovedBroker().getReference().getEndpointAddress().getAddress();
                if (rbc.getNeighbors() != null) {
                    int choice = rbc.getNeighbors().getBrokerSequence().size() - 1;
                    Broker chosenBroker = rbc.getNeighbors().getBrokerSequence().get(choice);
                    invoker.replaceBrokerSubscriber(removedBrokerConsumerEpr,
                                                    chosenBroker.getBrokerConsumerReference().getReference());
                    brokerManager.replaceConsumersBrokerConnection(notificationType,
                                                                   chosenBroker.getBrokerProducerReference().getReference());
                }
                else {
                    invoker.replaceBrokerSubscriber(removedBrokerConsumerEpr, null);
                }
            }
            else {
                throw new RuntimeException("Unknown encoding object");
            }
        } catch(Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
