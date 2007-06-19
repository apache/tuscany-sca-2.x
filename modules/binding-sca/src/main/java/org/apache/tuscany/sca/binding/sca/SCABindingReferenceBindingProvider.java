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

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingFactory;
import org.apache.tuscany.sca.binding.jms.JMSBindingFactoryImpl;
import org.apache.tuscany.sca.binding.jms.JMSBindingProviderFactory;
import org.apache.tuscany.sca.binding.jms.JMSBindingReferenceBindingProvider;
import org.apache.tuscany.sca.binding.jms.JMSBindingServiceBindingProvider;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.distributed.assembly.DistributedSCABinding;
import org.apache.tuscany.sca.distributed.host.SCADomainNode;
import org.apache.tuscany.sca.distributed.node.ComponentRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * An reference provider for the SCA binding. Doesn't do anything over and 
 * above the default version but I have left it here for now as it has some
 * potentially useful plumbing
 * 
 * @version $Rev$ $Date$
 */
public class SCABindingReferenceBindingProvider implements ReferenceBindingProvider {
    
    private ExtensionPointRegistry registry;      
    private SCADomainNode domainNode;
    private RuntimeComponent component;
    private RuntimeComponentReference reference;
    private DistributedSCABinding binding;
    private ComponentRegistry componentRegistry;
    private JMSBinding jmsBinding;
    private JMSBindingReferenceBindingProvider jmsReferenceProvider = null;
    
    public SCABindingReferenceBindingProvider(ExtensionPointRegistry registry,
                                              SCADomainNode domainNode,
                                              RuntimeComponent component, 
                                              RuntimeComponentReference reference, 
                                              DistributedSCABinding binding) {
        this.registry  = registry;
        this.domainNode= domainNode;
        this.component = component;
        this.reference = reference;
        this.binding   = binding;
        
        /* All of this has been replaced by the code to create the 
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
            
            //I'm only checking the first target here - this is bad but 
            //there is no way of knowing which target is being processed
            // TODO - sort out multiple targets
            ComponentService service = reference.getTargets().get(0); 
            SCABinding serviceSCABinding = service.getBinding(SCABinding.class);
            Component targetComponent = serviceSCABinding.getComponent();
            String serviceName = targetComponent.getName();            
            String serviceNode = componentRegistry.getComponentNode(targetComponent.getName());
            
          
            // set the destination queue to the target service name
            jmsBinding.setDestinationName(serviceNode + 
                                          "." +
                                          serviceName + 
                                          "." + 
                                          service.getName());
            jmsBinding.setDestinationCreate(JMSBindingConstants.CREATE_ALLWAYS);
            
            // get the reference information
            String referenceNode = componentRegistry.getComponentNode(component.getName());
          
            // set the response queue name to this reference
            jmsBinding.setResponseDestinationName(referenceNode +
                                                  "." +
                                                  component.getName() +
                                                  "." +
                                                  reference.getName() );
            jmsBinding.setResponseDestinationCreate(JMSBindingConstants.CREATE_ALLWAYS);
          
            // create the reference provider based on this binding
            ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
            JMSBindingProviderFactory providerFactory = 
               (JMSBindingProviderFactory)providerFactories.getProviderFactory(JMSBinding.class);
         
            jmsReferenceProvider = 
               (JMSBindingReferenceBindingProvider) providerFactory.createReferenceBindingProvider(component, reference, jmsBinding); 
        }    
        */
    }
    
    public InterfaceContract getBindingInterfaceContract() {
        return reference.getInterfaceContract();
    }

    public Invoker createInvoker(Operation operation, boolean isCallback) {
        
        Invoker invoker = null;
        
        if (jmsReferenceProvider != null) {
            invoker = jmsReferenceProvider.createInvoker(operation, isCallback);
        }

        return invoker;
    }

    public void start() {
        if (jmsReferenceProvider != null) {
            jmsReferenceProvider.start();
        }
    }

    public void stop() {
        if (jmsReferenceProvider != null) {
            jmsReferenceProvider.stop();
        }
    }

}
