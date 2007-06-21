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

package org.apache.tuscany.sca.distributed.core;



import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.core.runtime.CompositeActivatorImpl;
import org.apache.tuscany.sca.distributed.assembly.DistributedSCABinding;
import org.apache.tuscany.sca.distributed.host.SCADomainNode;
import org.apache.tuscany.sca.distributed.node.ComponentRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.scope.ScopeRegistry;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * A distributed version of the composite activator that ensures
 * that components running in this node have the correct bindings
 * 
 * @version $Rev$ $Date$
 */
public class DistributedCompositeActivatorImpl extends CompositeActivatorImpl {

    private SCADomainNode domainNode;
    private ComponentRegistry componentRegistry;    
    
    /**
     * @param assemblyFactory
     * @param interfaceContractMapper
     * @param workContext
     * @param workScheduler
     * @param wirePostProcessorRegistry
     */
    public DistributedCompositeActivatorImpl(AssemblyFactory assemblyFactory,
                                     SCABindingFactory scaBindingFactory,
                                     InterfaceContractMapper interfaceContractMapper,
                                     ScopeRegistry scopeRegistry,
                                     WorkScheduler workScheduler,
                                     RuntimeWireProcessor wireProcessor,
                                     ProviderFactoryExtensionPoint providerFactories,
                                     SCADomainNode domainNode) {
        super(assemblyFactory,
              scaBindingFactory,
              interfaceContractMapper,
              scopeRegistry,
              workScheduler,
              wireProcessor,
              providerFactories);
        
        this.domainNode = domainNode;
        
        // if the domain node is available find the component registry 
        if (domainNode != null) {
            // get the ComponentRegistry
            componentRegistry = domainNode.getNodeService(ComponentRegistry.class, "ComponentRegistry");
        }      
        
    }


    protected void buildComposite(Composite composite) throws CompositeBuilderException {
        super.buildComposite(composite);
        
        // now we have a build composite look at which SCABindings
        // need updating based on which components are present
        // in this node
        assignComponentsToNode(composite);

    }
    
    /**
     * Looks for components that either have no node assignment
     * or have a node id that matches the id of the node 
     * we are currently in. These components have their services and 
     * references tested. Any services or references that only have an 
     * SCA binding and are wired to components in other runtimes have 
     * their SCA bindings marked as distributed. The SCABiding will
     * then choose a remote binding that allows the wire to cross 
     * the VM boundary. 
     * 
     * @param composite
     * @throws CompositeBuilderException
     */
    protected void assignComponentsToNode(Composite composite) 
      throws CompositeBuilderException {

        for (Component sourceComponent : composite.getComponents()) {          

            // Look at all the reference/service pairs looking for examples
            // where the target and source are bound to different nodes
            for (ComponentReference reference : sourceComponent.getReferences()) {
                for (ComponentService service : reference.getTargets()) {
                    
                    Component targetComponent = null;
                    
                    DistributedSCABinding serviceSCABinding = (DistributedSCABinding)service.getBinding(SCABinding.class);
                    
                    if (serviceSCABinding != null) {
                        targetComponent = serviceSCABinding.getComponent();
                    } else {
                        throw new CompositeBuilderException ("No SCABinding on service " +
                                                             service.getName()); 
                    }
                    
                    if (componentRegistry != null) {
                        // get the information about the reference and associated services
                        
                        // reference
                        String referenceNode = componentRegistry.getComponentNode(sourceComponent.getName());
                        
                        // service 
                        String serviceNode = componentRegistry.getComponentNode(targetComponent.getName());
                        
                        // check if reference and service are operating on 
                        // different nodes 
                        if ( referenceNode == null ) {
                            // if the reference component hasn't been assigned to a particular
                            // node then it may be valid to let it run everywhere. For now
                            // raise an exception
                            throw new CompositeBuilderException("Component " +
                                                                sourceComponent.getName() + 
                                                                " is not assigned to a node");
                        } else if ( serviceNode == null) {
                            // if the service component hasn't been assigned to a particular
                            // node then it may be valid to let it run everywhere. For now
                            // raise an exception
                            throw new CompositeBuilderException("Component " +
                                                                targetComponent.getName() + 
                                                                " is not assigned to a node");                            
                        } else if ( !referenceNode.equals(serviceNode) ) {
                            // TODO - need to check if the service
                            // interface is remoteable
                           
                            // if the reference is operating on this node
                            // mark the binding as distributed
                            if (referenceNode.equals(domainNode.getNodeName())) {
                                DistributedSCABinding referenceSCABinding = 
                                    (DistributedSCABinding)reference.getBinding(SCABinding.class);
                                
                                // not used at the moment but maintain this 
                                // flag in case the SCABinding becomes more 
                                // functional
                                referenceSCABinding.setIsDisitributed(true);
                                
                                Binding newBinding = referenceSCABinding.getRemoteReferenceBinding(reference, 
                                                                                                   service);
                                reference.getBindings().add(0,newBinding);
                                
                                System.out.println(">>> reference " +
                                                   reference.getName() +
                                                   " SCABinding is set remote" );
                            }
                            
                            if (serviceNode.equals(domainNode.getNodeName())) { 
                                // not used at the moment but maintain this 
                                // flag in case the SCABinding becomes more 
                                // functional                                
                                serviceSCABinding.setIsDisitributed(true);  
                                
                                Binding newBinding = serviceSCABinding.getRemoteReferenceBinding(reference, 
                                                                                                 service);
                                service.getBindings().add(0,newBinding);
                                
                                System.out.println(">>> service " +
                                                   service.getName() +
                                                   " SCABinding set remote" );                                
                            }
                        }
                    }
                }
            }
        }    
    }
}

