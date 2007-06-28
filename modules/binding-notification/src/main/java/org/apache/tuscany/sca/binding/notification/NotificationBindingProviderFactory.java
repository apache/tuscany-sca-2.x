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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.binding.notification.encoding.BrokerID;
import org.apache.tuscany.sca.binding.notification.encoding.EncodingRegistry;
import org.apache.tuscany.sca.binding.notification.encoding.EndpointReference;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * The runtime representaion of the notification reference binding
 *
 * @version $Rev$ $Date$
 */
public class NotificationBindingProviderFactory implements BindingProviderFactory<NotificationBinding>, NotificationBrokerManager {

    private ServletHost servletHost;
    private NotificationTypeManager notificationTypeManager;
    private EncodingRegistry encodingRegistry;
    private String httpUrl;
    private Map<URI, NotificationReferenceBindingProvider> referenceBindingProviders;
    private Map<URI, NotificationServiceBindingProvider> serviceBindingProviders;
    
    private static NotificationBindingProviderFactory factoryInstance = null;
    
    public NotificationBindingProviderFactory(ServletHost servletHost,
                                              NotificationTypeManager notificationTypeManager,
                                              EncodingRegistry encodingRegistry,
                                              String httpUrl) {
        this.servletHost = servletHost;
        this.notificationTypeManager = notificationTypeManager;
        this.encodingRegistry = encodingRegistry;
        this.httpUrl = httpUrl;
        this.referenceBindingProviders = new HashMap<URI, NotificationReferenceBindingProvider>();
        this.serviceBindingProviders = new HashMap<URI, NotificationServiceBindingProvider>();
        
        factoryInstance = this;
    }
    
    public Class<NotificationBinding> getModelType() {
        return NotificationBinding.class;
    }
    
    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component,
                                                                   RuntimeComponentReference reference,
                                                                   NotificationBinding binding) {
        URI notificationType = binding.getNotificationType();
        if (!validReferenceBinding(binding)) {
            throw new RuntimeException("Binding not valid");
        }
        NotificationReferenceBindingProvider referenceBindingProvider = referenceBindingProviders.get(notificationType);
        if (referenceBindingProvider == null) {
            referenceBindingProvider = new NotificationReferenceBindingProvider(binding,
                                                                                component,
                                                                                reference,
                                                                                servletHost,
                                                                                notificationTypeManager,
                                                                                encodingRegistry,
                                                                                httpUrl,
                                                                                this);
            referenceBindingProviders.put(notificationType, referenceBindingProvider);
        }
        return referenceBindingProvider;
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component,
                                                               RuntimeComponentService service,
                                                               NotificationBinding binding) {
        URI notificationType = binding.getNotificationType();
        if (!validServiceBinding(binding)) {
            throw new RuntimeException("Binding not valid");
        }
        NotificationServiceBindingProvider serviceBindingProvider = serviceBindingProviders.get(notificationType);
        if (serviceBindingProvider == null) {
            serviceBindingProvider =  new NotificationServiceBindingProvider(binding,
                                                                             component,
                                                                             service,
                                                                             servletHost,
                                                                             notificationTypeManager,
                                                                             encodingRegistry,
                                                                             httpUrl,
                                                                             this);
            serviceBindingProviders.put(notificationType, serviceBindingProvider);
        }
        return serviceBindingProvider;
    }
    
    private boolean validServiceBinding(NotificationBinding binding) {
        URI notificationType = binding.getNotificationType();
        NotificationReferenceBindingProvider referenceBindingProvider = referenceBindingProviders.get(notificationType);
        if (referenceBindingProvider != null) {
            return validBinding(binding, referenceBindingProvider.getBinding());
        }
        return true;
    }
    
    private boolean validReferenceBinding(NotificationBinding binding) {
        URI notificationType = binding.getNotificationType();
        NotificationServiceBindingProvider serviceBindingProvider = serviceBindingProviders.get(notificationType);
        if (serviceBindingProvider != null) {
            return validBinding(binding, serviceBindingProvider.getBinding());
        }
        return true;
    }
    
    private boolean validBinding(NotificationBinding binding1, NotificationBinding binding2) {
        String binding1NtmAddress = binding1.getNtmAddress();
        String binding2NtmAddress = binding2.getNtmAddress();
        if (binding1NtmAddress == null && binding2NtmAddress == null) {
            return true;
        }
        else if (binding1NtmAddress == null || binding2NtmAddress == null) {
            return false;
        }
        else {
            return binding1NtmAddress.equals(binding2NtmAddress);
        }
    }
    
    /*
     * These methods are intended to be called by the binding providers' start methods. By the time this
     * happens, both referenceBindingProvider != null && serviceBindingProvider != null, if they are
     * ever going to be
     */
    public void serviceProviderStarted(URI notificationType,
                                       NotificationServiceBindingProvider serviceBindingProvider,
                                       URL remoteNtmUrl) {
        NotificationReferenceBindingProvider referenceBindingProvider = referenceBindingProviders.get(notificationType);
        if (referenceBindingProvider == null) {
            serviceBindingProvider.deployConsumer();
        }
        else if (referenceBindingProvider.isStarted()) {
            String brokerID = BrokerID.generate();
            deployBroker(notificationType, serviceBindingProvider, referenceBindingProvider, brokerID, remoteNtmUrl);
        }
    }
    
    public void referenceProviderStarted(URI notificationType,
                                         NotificationReferenceBindingProvider referenceBindingProvider,
                                         URL remoteNtmUrl) {
        NotificationServiceBindingProvider serviceBindingProvider = serviceBindingProviders.get(notificationType);
        if (serviceBindingProvider == null) {
            referenceBindingProvider.deployProducer();
        }
        else if (serviceBindingProvider.isStarted()) {
            String brokerID = BrokerID.generate();
            deployBroker(notificationType, serviceBindingProvider, referenceBindingProvider, brokerID, remoteNtmUrl);
        }
    }
    
    private void deployBroker(URI notificationType,
                              NotificationServiceBindingProvider serviceBindingProvider,
                              NotificationReferenceBindingProvider referenceBindingProvider,
                              String brokerID,
                              URL remoteNtmUrl) {
        URL consumerUrl = serviceBindingProvider.getURL();
        URL producerUrl = referenceBindingProvider.getURL();
        List<EndpointReference> consumerList = new ArrayList<EndpointReference>();
        List<EndpointReference> producerList = new ArrayList<EndpointReference>();
        boolean firstBroker = notificationTypeManager.newBroker(notificationType,
                                                                consumerUrl,
                                                                producerUrl,
                                                                brokerID,
                                                                remoteNtmUrl,
                                                                consumerList,
                                                                producerList);
        if (firstBroker) {
            serviceBindingProvider.deployBroker(brokerID, null, producerList);
            referenceBindingProvider.deployBroker(brokerID, null, consumerList);
            if (!consumerList.isEmpty() || !producerList.isEmpty()) {
                notificationTypeManager.newBrokerAck(remoteNtmUrl);
            }
        }
        else {
            // returned lists contain broker consumers and producers and are the same length
            int index = consumerList.size() - 1;
            // establish connection with picked broker
            EndpointReference brokerConsumerEPR = consumerList.get(index);
            EndpointReference brokerProducerEPR = producerList.get(index);
            serviceBindingProvider.deployBroker(brokerID, brokerProducerEPR, null);
            referenceBindingProvider.deployBroker(brokerID, brokerConsumerEPR, null);
        }
    }
    
    public void replaceConsumersBrokerConnection(URI notificationType, EndpointReference chosenBrokerProducerEpr) {
        NotificationServiceBindingProvider serviceBindingProvider = serviceBindingProviders.get(notificationType);
        if (serviceBindingProvider == null) {
            throw new RuntimeException("Missing service binding provider for [" + notificationType + "]");
        }
        serviceBindingProvider.replaceBrokerConnection(chosenBrokerProducerEpr);
    }
    
    public static void removeBroker(URI notificationType) {
        if (factoryInstance == null) {
            throw new RuntimeException("Missing factory instance");
        }
        NotificationReferenceBindingProvider referenceBindingProvider = factoryInstance.referenceBindingProviders.get(notificationType);
        NotificationServiceBindingProvider serviceBindingProvider = factoryInstance.serviceBindingProviders.get(notificationType);
        if (referenceBindingProvider == null || serviceBindingProvider == null) {
            throw new RuntimeException("Not a broker for [" + notificationType + "]");
        }
        referenceBindingProvider.undeployBroker(serviceBindingProvider.getURL());
    }
}
