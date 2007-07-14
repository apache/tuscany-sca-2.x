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

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingFactory;
import org.apache.tuscany.sca.binding.jms.JMSBindingFactoryImpl;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.distributed.assembly.DistributedSCABinding;
import org.apache.tuscany.sca.distributed.host.DistributedSCADomain;
import org.apache.tuscany.sca.distributed.host.impl.DistributedSCADomainImpl;
import org.apache.tuscany.sca.distributed.node.ComponentRegistry;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Represents an SCA binding.
 * 
 * @version $Rev$ $Date$
 */
public class SCABindingImpl implements DistributedSCABinding {
    private String name;
    private String uri;
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<Object> extensions = new ArrayList<Object>();
    private boolean isDistributed = false;
    private DistributedSCADomain domain;
    private ExtensionPointRegistry registry;
    private ComponentRegistry componentRegistry;
    
    private Component component;
    
    /**
     * Constructs a new SCA binding.
     */
    public SCABindingImpl(DistributedSCADomain domain,
                          ExtensionPointRegistry registry) {
        this.domain = domain;
        this.registry = registry;
        
        if ((domain != null) && (domain.getNodeDomain() != null)) {
            // get the ComponentRegistry
            this.componentRegistry = domain.getNodeDomain().getService(ComponentRegistry.class, "ComponentRegistry");
        }
    }
    
    public Component getComponent() {
        return component;
    }
    
    public void setComponent(Component component) {
        this.component = component;
    }

    public String getName() {
        return name;
    }

    public String getURI() {
        return uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public List<Object> getExtensions() {
        return extensions;
    }
    
    public boolean isUnresolved() {
        return false;
    }
    
    public void setUnresolved(boolean unresolved) {
    }
    
    /**
     * Gets the flag which tells you if this SCABinding is distributed across nodes
     * 
     * @return isDistributed flag
     */
    public boolean isDistributed() {
        return isDistributed;
    }

    /**
     * Gets the flag which tells you if this SCABinding is distributed across nodes
     * 
     * @param isDistributed true if this binding is distributed across nodes
     */
    public void setDistributed(boolean isDistributed) {
        this.isDistributed = isDistributed;
    }

    /**
     * Return the remote reference binding that the SCABinding deems is appropriate
     * between the provided service and reference. 
     * 
     * @param reference
     * @param service
     * @return the remote binding
     */
    public Binding getRemoteReferenceBinding(ComponentReference reference,
                                             ComponentService service) {
        
        // invent a model on the fly
        JMSBindingFactory jmsBindingFactory = new JMSBindingFactoryImpl();
        JMSBinding jmsBinding = jmsBindingFactory.createJMSBinding();  
      
        // it's at this point with a point to point binding we 
        // would go and get the base URLs etc. I'm just using the
        // node name here as JMS doesn't really need an endpoint
        
        // get the service information
        SCABinding serviceSCABinding = service.getBinding(SCABinding.class);
        Component targetComponent = serviceSCABinding.getComponent();
        String serviceName = targetComponent.getName();            
        String serviceNode = componentRegistry.getComponentNode(domain.getURI(), targetComponent.getName());
        
      
        // set the destination queue to the target service name
        jmsBinding.setDestinationName(serviceNode + 
                                      "." +
                                      serviceName + 
                                      "." + 
                                      service.getName());
        jmsBinding.setDestinationCreate(JMSBindingConstants.CREATE_ALLWAYS);
        
        // get the reference information
        String referenceNode = componentRegistry.getComponentNode(domain.getURI(), component.getName());
      
        // set the response queue name to this reference
        jmsBinding.setResponseDestinationName(referenceNode +
                                              "." +
                                              component.getName() +
                                              "." +
                                              reference.getName() );
        jmsBinding.setResponseDestinationCreate(JMSBindingConstants.CREATE_ALLWAYS);

        return jmsBinding;
    }
    
    /**
     * Return the remote service binding that the SCABinding deems is appropriate
     * between the provided service and reference. 
     * 
     * @param reference
     * @param service
     * @return
     */
    public Binding getRemoteServiceBinding(ComponentReference reference,
                                           ComponentService service) {
    
        // first invent a model on the fly
        JMSBindingFactory jmsBindingFactory = new JMSBindingFactoryImpl();
        JMSBinding jmsBinding = jmsBindingFactory.createJMSBinding();  
      
        // it's at this point with a point to point binding we 
        // would go and get the base URLs etc. I'm just using the
        // node name here as JMS doesn't really need an endpoint
        
        // get the service information
        
        String serviceName = service.getName();            
        String serviceNode = componentRegistry.getComponentNode(domain.getURI(), component.getName());
        
      
        // set the destination queue to the target service name
        jmsBinding.setDestinationName(serviceNode + 
                                      "." +
                                      component.getName() + 
                                      "." + 
                                      serviceName);
        jmsBinding.setDestinationCreate(JMSBindingConstants.CREATE_ALLWAYS);            
        
        return jmsBinding;     
    }      
}
