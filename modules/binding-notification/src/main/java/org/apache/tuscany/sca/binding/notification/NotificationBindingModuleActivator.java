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

import java.net.InetAddress;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.binding.notification.encoding.BrokerConsumerReferenceEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.BrokerEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.BrokerIDEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.BrokerProducerReferenceEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.BrokersEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.ConnectionOverrideEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.ConnectionOverrideResponseEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.ConsumerReferenceEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.DefaultEncodingRegistry;
import org.apache.tuscany.sca.binding.notification.encoding.EncodingRegistry;
import org.apache.tuscany.sca.binding.notification.encoding.EndConsumersEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.EndProducersEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.EndpointAddressEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.EndpointReferenceEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.NeighborBrokerConsumersEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.NeighborsEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.NewBrokerAckEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.NewBrokerEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.NewBrokerResponseEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.NewConsumerEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.NewConsumerResponseEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.NewProducerEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.NewProducerResponseEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.ReferencePropertiesEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.RemoveBrokerEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.RemovedBrokerEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.ReplaceBrokerConnectionEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.SubscribeEnDeCoder;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.http.ExtensibleServletHost;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;

/**
 * @version $Rev$ $Date$
 */
public class NotificationBindingModuleActivator implements ModuleActivator {

    private NotificationBindingProcessor bindingProcessor;
    
    private NotificationTypeManagerImpl ntm;
    
    private SubscribeEnDeCoder subscribeEnDeCoder;
    private ConsumerReferenceEnDeCoder consumerReferenceEnDeCoder;
    private EndpointAddressEnDeCoder endpointAddressEnDeCoder;
    private NewConsumerEnDeCoder newConsumerEnDeCoder;
    private NewProducerEnDeCoder newProducerEnDeCoder;
    private NewConsumerResponseEnDeCoder newConsumerResponseEnDeCoder;
    private NewProducerResponseEnDeCoder newProducerResponseEnDeCoder;
    private NewBrokerEnDeCoder newBrokerEnDeCoder;
    private BrokerConsumerReferenceEnDeCoder brokerConsumerReferenceEnDeCoder;
    private BrokerProducerReferenceEnDeCoder brokerProducerReferenceEnDeCoder;
    private NewBrokerResponseEnDeCoder newBrokerResponseEnDeCoder;
    private BrokersEnDeCoder brokersEnDeCoder;
    private BrokerEnDeCoder brokerEnDeCoder;
    private EndConsumersEnDeCoder endConsumersEnDeCoder; 
    private EndProducersEnDeCoder endProducersEnDeCoder; 
    private EndpointReferenceEnDeCoder endpointReferenceEnDeCoder;;
    private ReferencePropertiesEnDeCoder referencePropertiesEnDeCoder;
    private BrokerIDEnDeCoder brokerIDEnDeCoder;
    private ConnectionOverrideEnDeCoder connectionOverrideEnDeCoder;
    private ConnectionOverrideResponseEnDeCoder connectionOverrideResponseEnDeCoder;
    private NewBrokerAckEnDeCoder newBrokerAckEnDeCoder;
    private NeighborBrokerConsumersEnDeCoder neighborBrokerConsumersEnDeCoder;
    private RemoveBrokerEnDeCoder removeBrokerEnDeCoder;
    private RemovedBrokerEnDeCoder removedBrokerEnDeCoder;
    private NeighborsEnDeCoder neighborsEnDeCoder;
    private ReplaceBrokerConnectionEnDeCoder replaceBrokerConnectionEnDeCoder;
    
    private static final String DEFAULT_PORT = "8086";

    public void start(ExtensionPointRegistry registry) {
        
        EncodingRegistry encodingRegistry = new DefaultEncodingRegistry();
        subscribeEnDeCoder = new SubscribeEnDeCoder(encodingRegistry);
        subscribeEnDeCoder.start();
        consumerReferenceEnDeCoder = new ConsumerReferenceEnDeCoder(encodingRegistry);
        consumerReferenceEnDeCoder.start();
        endpointAddressEnDeCoder = new EndpointAddressEnDeCoder(encodingRegistry);
        endpointAddressEnDeCoder.start();
        newConsumerEnDeCoder = new NewConsumerEnDeCoder(encodingRegistry);
        newConsumerEnDeCoder.start();
        newProducerEnDeCoder = new NewProducerEnDeCoder(encodingRegistry);
        newProducerEnDeCoder.start();
        newConsumerResponseEnDeCoder = new NewConsumerResponseEnDeCoder(encodingRegistry);
        newConsumerResponseEnDeCoder.start();
        newProducerResponseEnDeCoder = new NewProducerResponseEnDeCoder(encodingRegistry);
        newProducerResponseEnDeCoder.start();
        newBrokerEnDeCoder = new NewBrokerEnDeCoder(encodingRegistry);
        newBrokerEnDeCoder.start();
        brokerConsumerReferenceEnDeCoder = new BrokerConsumerReferenceEnDeCoder(encodingRegistry);
        brokerConsumerReferenceEnDeCoder.start();
        brokerProducerReferenceEnDeCoder = new BrokerProducerReferenceEnDeCoder(encodingRegistry);
        brokerProducerReferenceEnDeCoder.start();
        newBrokerResponseEnDeCoder = new NewBrokerResponseEnDeCoder(encodingRegistry);
        newBrokerResponseEnDeCoder.start();
        brokersEnDeCoder = new BrokersEnDeCoder(encodingRegistry);
        brokersEnDeCoder.start();
        brokerEnDeCoder = new BrokerEnDeCoder(encodingRegistry);
        brokerEnDeCoder.start();
        endConsumersEnDeCoder = new EndConsumersEnDeCoder(encodingRegistry);
        endConsumersEnDeCoder.start();
        endProducersEnDeCoder = new EndProducersEnDeCoder(encodingRegistry);
        endProducersEnDeCoder.start();
        endpointReferenceEnDeCoder = new EndpointReferenceEnDeCoder(encodingRegistry);
        endpointReferenceEnDeCoder.start();
        referencePropertiesEnDeCoder = new ReferencePropertiesEnDeCoder(encodingRegistry); 
        referencePropertiesEnDeCoder.start();
        brokerIDEnDeCoder = new BrokerIDEnDeCoder(encodingRegistry);
        brokerIDEnDeCoder.start();
        connectionOverrideEnDeCoder = new ConnectionOverrideEnDeCoder(encodingRegistry);
        connectionOverrideEnDeCoder.start();
        connectionOverrideResponseEnDeCoder = new ConnectionOverrideResponseEnDeCoder(encodingRegistry);
        connectionOverrideResponseEnDeCoder.start();
        newBrokerAckEnDeCoder = new NewBrokerAckEnDeCoder(encodingRegistry);
        newBrokerAckEnDeCoder.start();
        neighborBrokerConsumersEnDeCoder = new NeighborBrokerConsumersEnDeCoder(encodingRegistry);
        neighborBrokerConsumersEnDeCoder.start();
        removeBrokerEnDeCoder = new RemoveBrokerEnDeCoder(encodingRegistry);
        removeBrokerEnDeCoder.start();
        removedBrokerEnDeCoder = new RemovedBrokerEnDeCoder(encodingRegistry);
        removedBrokerEnDeCoder.start();
        neighborsEnDeCoder = new NeighborsEnDeCoder(encodingRegistry);
        neighborsEnDeCoder.start();
        replaceBrokerConnectionEnDeCoder = new ReplaceBrokerConnectionEnDeCoder(encodingRegistry);
        replaceBrokerConnectionEnDeCoder.start();
        
        String httpPort = System.getProperty("notification.httpPort");
        if (httpPort == null) {
            httpPort = DEFAULT_PORT;
        }

        ServletHost servletHost = new ExtensibleServletHost(registry.getExtensionPoint(ServletHostExtensionPoint.class));

        ntm = new NotificationTypeManagerImpl();
        ntm.setServletHost(servletHost);
        ntm.setEncodingRegistry(encodingRegistry);
        ntm.init();

        String localHost = null;
        try {
            localHost = InetAddress.getLocalHost().getCanonicalHostName();
        } catch(Exception e) {
            e.printStackTrace();
            localHost = "localhost";
        }
        String localBaseUrl = "http://" + localHost + ((httpPort != null) ? (":" + httpPort) : "");

        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        DefaultNotificationBindingFactory bindingFactory = new DefaultNotificationBindingFactory();
        bindingProcessor = new NotificationBindingProcessor(assemblyFactory, policyFactory, bindingFactory);
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processors.addArtifactProcessor(bindingProcessor);
        
        NotificationBindingProviderFactory nbpf = new NotificationBindingProviderFactory(servletHost,
                                                                                         ntm,
                                                                                         encodingRegistry,
                                                                                         localBaseUrl);
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.addProviderFactory(nbpf);
    }

    public void stop(ExtensionPointRegistry registry) {
        subscribeEnDeCoder.stop();
        consumerReferenceEnDeCoder.stop();
        endpointAddressEnDeCoder.stop();
        newConsumerEnDeCoder.stop();
        newProducerEnDeCoder.stop();
        newConsumerResponseEnDeCoder.stop();
        newProducerResponseEnDeCoder.stop();
        newBrokerEnDeCoder.stop();
        brokerConsumerReferenceEnDeCoder.stop();
        brokerProducerReferenceEnDeCoder.stop();
        newBrokerResponseEnDeCoder.stop();
        brokersEnDeCoder.stop();
        brokerEnDeCoder.stop();
        endConsumersEnDeCoder.stop();
        endProducersEnDeCoder.stop();
        endpointReferenceEnDeCoder.stop();
        referencePropertiesEnDeCoder.stop();
        brokerIDEnDeCoder.stop();
        connectionOverrideEnDeCoder.stop();
        connectionOverrideResponseEnDeCoder.stop();
        newBrokerAckEnDeCoder.stop();
        neighborBrokerConsumersEnDeCoder.stop();
        removeBrokerEnDeCoder.stop();
        removedBrokerEnDeCoder.stop();
        neighborsEnDeCoder.stop();
        replaceBrokerConnectionEnDeCoder.stop();

        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processors.removeArtifactProcessor(bindingProcessor);
    }

}
