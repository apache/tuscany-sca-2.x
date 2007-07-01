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

package org.apache.tuscany.sca.binding.sca;



import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingFactory;
import org.apache.tuscany.sca.binding.jms.JMSBindingFactoryImpl;
import org.apache.tuscany.sca.binding.jms.JMSBindingProviderFactory;
import org.apache.tuscany.sca.binding.jms.JMSBindingServiceBindingProvider;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.distributed.assembly.DistributedSCABinding;
import org.apache.tuscany.sca.distributed.host.DistributedSCADomain;
import org.apache.tuscany.sca.distributed.node.ComponentRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Implementation of the JMS service binding provider.
 * 
 * @version $Rev$ $Date$
 */
public class SCABindingServiceBindingProvider implements ServiceBindingProvider {

    private ExtensionPointRegistry registry;      
    private DistributedSCADomain domain;
    private RuntimeComponent component;
    private RuntimeComponentService service;
    private DistributedSCABinding binding;
    private ComponentRegistry componentRegistry;
    private JMSBinding jmsBinding;
    private JMSBindingServiceBindingProvider jmsServiceProvider = null;    
  
    public SCABindingServiceBindingProvider(ExtensionPointRegistry registry,
                                            DistributedSCADomain domain,
                                            RuntimeComponent component,
                                            RuntimeComponentService service,
                                            DistributedSCABinding binding) {
        this.registry  = registry;
        this.domain    = domain;        
        this.component = component;
        this.service   = service;
        this.binding   = binding;
        
        /*  All of this has been replaced by the code to create the 
           JMS binding in SCABindingImpl. 

        
        // if the domain node is available find the component registry 
        if (domainNode != null) {
            // get the ComponentRegistry
            componentRegistry = domainNode.getNodeService(ComponentRegistry.class, "ComponentRegistry");
        }         
        
        // if this SCA binding crosses a node boundary fire up an alternative
        // binding that supports this
        if (this.binding.getIsDistributed()) {
            // first invent a model on the fly
            JMSBindingFactory jmsBindingFactory = new JMSBindingFactoryImpl();
            jmsBinding = jmsBindingFactory.createJMSBinding();  
          
            // it's at this point with a point to point binding we 
            // would go and get the base URLs etc. I'm just using the
            // node name here as JMS doesn't really need an endpoint
            
            // get the service information
            
            String serviceName = service.getName();            
            String serviceNode = componentRegistry.getComponentNode(component.getName());
            
          
            // set the destination queue to the target service name
            jmsBinding.setDestinationName(serviceNode + 
                                          "." +
                                          component.getName() + 
                                          "." + 
                                          serviceName);
            jmsBinding.setDestinationCreate(JMSBindingConstants.CREATE_ALLWAYS);            
            
         
            // create the reference provider based on this binding
            ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
            JMSBindingProviderFactory providerFactory = 
               (JMSBindingProviderFactory)providerFactories.getProviderFactory(JMSBinding.class);
         
            jmsServiceProvider = 
               (JMSBindingServiceBindingProvider) providerFactory.createServiceBindingProvider(component, service, jmsBinding); 
        }   
        
           */
                  
    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    public void start() {
        if (jmsServiceProvider != null) {
            jmsServiceProvider.start();
        }
    }

    public void stop() {
        if (jmsServiceProvider != null) {
            jmsServiceProvider.stop();
        }
    }
}
