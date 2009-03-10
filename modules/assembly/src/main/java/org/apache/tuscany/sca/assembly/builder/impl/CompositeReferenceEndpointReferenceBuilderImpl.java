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

package org.apache.tuscany.sca.assembly.builder.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointFactory;
import org.apache.tuscany.sca.assembly.EndpointReference2;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.DefaultEndpointBuilder;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * A composite builder that creates endpoint reference models.
 *
 * @version $Rev$ $Date$
 */
public class CompositeReferenceEndpointReferenceBuilderImpl extends BaseBuilderImpl implements CompositeBuilder {


    public CompositeReferenceEndpointReferenceBuilderImpl(AssemblyFactory assemblyFactory, InterfaceContractMapper interfaceContractMapper) {
        super(assemblyFactory, null, null, null, interfaceContractMapper);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.ComponentReferenceEndpointReferenceBuilder";
    }

    /**
     * Create endpoint references for all component references.
     * 
     * @param composite
     */
    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException 
    {
        // process top level composite references
        // TODO - I don't think OASIS allows for these
        //
        //processCompositeReferences(composite);
        
        // process component services
        processComponentReferences(composite, monitor);  
    }
    
    private void processCompositeReferences(Composite composite) {
        // TODO do we need this for OASIS?
    }
    
    private void processComponentReferences(Composite composite, Monitor monitor) {
        
        // index all of the components in the composite
        Map<String, Component> components = new HashMap<String, Component>();
        indexComponents(composite, components);
        
        // index all of the services in the composite
        Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
        indexServices(composite, componentServices);
        
        // create endpoint references for each component's references
        for (Component component : composite.getComponents()) {
            // recurse for composite implementations
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                processComponentReferences((Composite)implementation, monitor);
            }
            
            // create endpoint references to represent the component reference
            for (ComponentReference reference : component.getReferences()) {
                createReferenceEndpointReferences(composite, component, reference, components, componentServices, monitor);
            }
        }
    }    
    
    private void createReferenceEndpointReferences(Composite composite, 
                                                   Component component, 
                                                   ComponentReference reference, 
                                                   Map<String, Component> components,
                                                   Map<String, ComponentService> componentServices, 
                                                   Monitor monitor)
    {
        if (reference.getAutowire() == Boolean.TRUE && 
            reference.getTargets().isEmpty()) {

            // Find suitable targets in the current composite for an
            // autowired reference
            Multiplicity multiplicity = reference.getMultiplicity();
            for (Component targetComponent : composite.getComponents()) {
                
                // prevent autowire connecting to self
                boolean skipSelf = false;
                for (ComponentReference targetComponentReference : targetComponent.getReferences()) {
                    if (reference == targetComponentReference) {
                        skipSelf = true;
                    }
                }

                if (!skipSelf) {
                    for (ComponentService targetComponentService : targetComponent.getServices()) {
                        if (reference.getInterfaceContract() == null || 
                            interfaceContractMapper.isCompatible(reference.getInterfaceContract(),
                                                                 targetComponentService.getInterfaceContract())) {

                            EndpointReference2 endpointRef = assemblyFactory.createEndpointReference();
                            endpointRef.setComponent(component);
                            endpointRef.setReference(reference);
                            endpointRef.setTargetName(targetComponentService.getName());
                            endpointRef.setUnresolved(false);
                            matchForwardBinding(endpointRef, targetComponentService, monitor);
                            matchCallbackBinding(endpointRef, targetComponentService, monitor);
                            
                            reference.getEndpointReferences().add(endpointRef);

                            if (multiplicity == Multiplicity.ZERO_ONE || 
                                multiplicity == Multiplicity.ONE_ONE) {
                                break;
                            }
                        }
                    }
                }
            }

            if (multiplicity == Multiplicity.ONE_N || 
                multiplicity == Multiplicity.ONE_ONE) {
                if (reference.getEndpointReferences().size() == 0) {
                    warning(monitor, 
                            "NoComponentReferenceTarget",
                            reference, 
                            reference.getName());
                }
            }

        } else if (!reference.getTargets().isEmpty()) {

            // Check that the component reference does not mix the use of
            // endpoint references specified via the target attribute with 
            // the presence of binding elements 
            if (reference.getBindings().size() > 0) {
                warning(monitor, "ReferenceEndPointMixWithTarget",
                        composite, reference.getName());
            }

            // Resolve targets specified on the component reference
            for (ComponentService target : reference.getTargets()) {               
                
                String targetName = target.getName();
                ComponentService targetComponentService = componentServices.get(targetName);
                
                Component targetComponent;
                int s = targetName.indexOf('/');
                if (s == -1) {
                    targetComponent = components.get(targetName);
                } else {
                    targetComponent = components.get(targetName.substring(0, s));
                }

                if (targetComponentService != null) {

                    // Check that the target component service provides
                    // a superset of the component reference interface
                    if (reference.getInterfaceContract() == null || 
                        interfaceContractMapper.isCompatible(reference.getInterfaceContract(),
                                                             targetComponentService.getInterfaceContract())) {

                        EndpointReference2 endpointRef = assemblyFactory.createEndpointReference();
                        endpointRef.setComponent(component);
                        endpointRef.setReference(reference);
                        endpointRef.setTargetName(targetComponentService.getName());
                        endpointRef.setUnresolved(false);
                        matchForwardBinding(endpointRef, targetComponentService, monitor);
                        matchCallbackBinding(endpointRef, targetComponentService, monitor);
                        
                        reference.getEndpointReferences().add(endpointRef);
                    } else {
                        warning(monitor, 
                                "ReferenceIncompatibleInterface",
                                composite, 
                                composite.getName().toString(),
                                reference.getName(), 
                                targetName);
                    }
                } else {
                    // add an unresolved endpoint
                    EndpointReference2 endpointRef = assemblyFactory.createEndpointReference();
                    endpointRef.setComponent(component);
                    endpointRef.setReference(reference);
                    endpointRef.setTargetName(targetName);
                    endpointRef.setUnresolved(true);
                    
                    warning(monitor, 
                            "ComponentReferenceTargetNotFound",
                            composite, 
                            composite.getName().toString(),
                            targetName);
                }
            }
        } else if ((reference.getReference() != null)
                && (!reference.getReference().getTargets().isEmpty())) {

            // Resolve targets from the corresponding reference in the
            // componentType
            for (ComponentService target : reference.getReference().getTargets()) {

                String targetName = target.getName();
                ComponentService targetComponentService = componentServices.get(targetName);
                
                Component targetComponent;
                int s = targetName.indexOf('/');
                if (s == -1) {
                    targetComponent = components.get(targetName);
                } else {
                    targetComponent = components.get(targetName.substring(0, s));
                }

                if (targetComponentService != null) {

                    // Check that the target component service provides
                    // a superset of the component reference interface
                    if (reference.getInterfaceContract() == null || 
                        interfaceContractMapper.isCompatible(reference.getInterfaceContract(),
                                                             targetComponentService.getInterfaceContract())) {

                        EndpointReference2 endpointRef = assemblyFactory.createEndpointReference();
                        endpointRef.setComponent(component);
                        endpointRef.setReference(reference);
                        endpointRef.setTargetName(targetComponentService.getName());
                        endpointRef.setUnresolved(false);
                        matchForwardBinding(endpointRef, targetComponentService, monitor);
                        matchCallbackBinding(endpointRef, targetComponentService, monitor);
                        
                        reference.getEndpointReferences().add(endpointRef);
                    } else {
                        warning(monitor, 
                                "ReferenceIncompatibleInterface",
                                composite, 
                                composite.getName().toString(),
                                reference.getName(), 
                                targetName);
                    }
                } else {
                    // add an unresolved endpoint
                    EndpointReference2 endpointRef = assemblyFactory.createEndpointReference();
                    endpointRef.setComponent(component);
                    endpointRef.setReference(reference);
                    endpointRef.setTargetName(targetName);
                    endpointRef.setUnresolved(true);
                    
                    warning(monitor, 
                            "ComponentReferenceTargetNotFound",
                            composite, 
                            composite.getName().toString(),
                            targetName);
                }
            }
        } 


        // if no endpoints have found so far the bindings become targets. 
        if (reference.getEndpointReferences().isEmpty()) {
            for (Binding binding : reference.getBindings()) {

                String uri = binding.getURI();

                // user hasn't put a uri on the binding so it's not a target
                // name
                if (uri == null) {
                    continue;
                }

                // user might have put a local target name in the uri so get
                // the path part and see if it refers to a target we know about
                // - if it does the reference binding will be matched with a
                // service binding
                // - if it doesn't it is assumed to be an external reference
                Component targetComponent = null;
                ComponentService targetComponentService = null;
                String path = null;

                try {
                    path = URI.create(uri).getPath();
                } catch (Exception ex) {
                    // just assume that no target is identified if
                    // a URI related exception is thrown
                }

                if (path != null) {
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }

                    // Resolve the target component and service
                    targetComponentService = componentServices.get(path);
                    int s = path.indexOf('/');
                    if (s == -1) {
                        targetComponent = components.get(path);
                    } else {
                        targetComponent = components.get(path.substring(0, s));
                    }
                }

                // if the path of the binding URI matches a component in the
                // composite then configure an endpoint reference with this component as
                // the target
                // if not then the binding URI will be assumed to reference an
                // external service
                if (targetComponentService != null) {

                    // Check that the target component service provides
                    // a superset of the component reference interface
                    if (reference.getInterfaceContract() == null || 
                        interfaceContractMapper.isCompatible(reference.getInterfaceContract(),
                                                             targetComponentService.getInterfaceContract())) {
                        EndpointReference2 endpointRef = assemblyFactory.createEndpointReference();
                        endpointRef.setComponent(component);
                        endpointRef.setReference(reference);
                        endpointRef.setBinding(binding);
                        endpointRef.setTargetName(targetComponentService.getName());
                        endpointRef.setUnresolved(false);
                        matchForwardBinding(endpointRef, targetComponentService, monitor);
                        matchCallbackBinding(endpointRef, targetComponentService, monitor);
                        
                        reference.getEndpointReferences().add(endpointRef);
                    } else {
                        warning(monitor, 
                                "ReferenceIncompatibleInterface",
                                composite, 
                                composite.getName().toString(),
                                reference.getName(), 
                                uri);
                    }
                } else {
                    // create endpoint reference for manually configured bindings
                    EndpointReference2 endpointRef = assemblyFactory.createEndpointReference();
                    endpointRef.setComponent(component);
                    endpointRef.setReference(reference);
                    endpointRef.setBinding(binding);
                    endpointRef.setTargetName(null);
                    endpointRef.setTargetEndpoint(null);
                    endpointRef.setUnresolved(false);                       
                    reference.getEndpointReferences().add(endpointRef);
                }
            }
        }
    }
    
    private void matchForwardBinding(EndpointReference2 endpointReference,
                                     ComponentService service, 
                                     Monitor monitor) {

        List<Binding> matchedReferenceBinding = new ArrayList<Binding>();
        List<Endpoint2> matchedServiceEndpoint = new ArrayList<Endpoint2>();

        // Find the corresponding bindings from the service side
        for (Binding referenceBinding : endpointReference.getReference().getBindings()) {
            for (Endpoint2 serviceEndpoint : service.getEndpoints()) {

                if (referenceBinding.getClass() == serviceEndpoint.getBinding().getClass() && 
                    hasCompatiblePolicySets(referenceBinding, serviceEndpoint.getBinding())) {

                    matchedReferenceBinding.add(referenceBinding);
                    matchedServiceEndpoint.add(serviceEndpoint);
                }
            }
        }

        if (matchedReferenceBinding.isEmpty()) {
            // No matching binding
            endpointReference.setBinding(null);
            endpointReference.setTargetEndpoint(null);
            warning(monitor, 
                    "NoMatchingBinding", 
                    endpointReference.getReference(),
                    endpointReference.getReference().getName(), 
                    service.getName());
            return;
        } else {
            // default to using the first matched binding
            int selectedBinding = 0;

            for (int i = 0; i < matchedReferenceBinding.size(); i++) {
                // If binding.sca is present, use it
                if (SCABinding.class.isInstance(matchedReferenceBinding.get(i))) {
                    selectedBinding = i;
                }
            }

            Binding referenceBinding = matchedReferenceBinding.get(selectedBinding);
            Endpoint2 serviceEndpoint = matchedServiceEndpoint.get(selectedBinding);

            // populate the endpoint reference
            try {

                Binding cloned = (Binding) referenceBinding.clone();

                // Set the binding URI to the URI of the target service
                // that has been matched
                if (referenceBinding.getURI() == null) {
                    cloned.setURI(serviceEndpoint.getBinding().getURI());
                }

                endpointReference.setBinding(referenceBinding);
                endpointReference.setTargetEndpoint(serviceEndpoint);

            } catch (Exception ex) {
                // do nothing
            }
        }
    } 
    
    // TODO
    // Pretty much a duplicate of matchForwardBinding to handle callback bindings
    // will rationalize when I understand what we need to do with callbacks
    private void matchCallbackBinding(EndpointReference2 endpointReference, 
                                     ComponentService service, 
                                     Monitor monitor) {

        // if no callback on the interface do nothing
        if (endpointReference.getReference().getInterfaceContract() == null || 
            endpointReference.getReference().getInterfaceContract().getCallbackInterface() == null){
                return;
        }
        
        List<Binding> matchedReferenceBinding = new ArrayList<Binding>();
        List<Binding> matchedServiceBinding = new ArrayList<Binding>();
        
        // Find the corresponding bindings from the service side
        for (Binding referenceBinding : endpointReference.getReference().getCallback().getBindings()) {
            for (Binding serviceBinding : service.getCallback().getBindings()) {

                if (referenceBinding.getClass() == serviceBinding.getClass() && 
                    hasCompatiblePolicySets(referenceBinding, serviceBinding)) {
                    
                    matchedReferenceBinding.add(referenceBinding);
                    matchedServiceBinding.add(serviceBinding);                 
                }
            }
        }
        
        if (matchedReferenceBinding.isEmpty()) {
            // No matching binding
            endpointReference.setBinding(null);
            endpointReference.setTargetEndpoint(null);
            warning(monitor, 
                    "NoMatchingCallbackBinding", 
                    endpointReference.getReference(),
                    endpointReference.getReference().getName(), 
                    service.getName());
            return;
        } else {
            // default to using the first matched binding
            int selectedBinding = 0;
            
            for (int i = 0; i < matchedReferenceBinding.size(); i++){
                // If binding.sca is present, use it
                if (SCABinding.class.isInstance(matchedReferenceBinding.get(i))) {
                    selectedBinding = i;
                }
            }
            
            Binding referenceBinding = matchedReferenceBinding.get(selectedBinding);
            Binding serviceBinding = matchedServiceBinding.get(selectedBinding);
            
            // populate the endpoint reference
            try {
                
                Binding cloned = (Binding)referenceBinding.clone();
                
                // Set the binding URI to the URI of the target service
                // that has been matched
                if (referenceBinding.getURI() == null) {
                    cloned.setURI(serviceBinding.getURI());
                }
                
                endpointReference.setCallbackBinding(referenceBinding);
                
            } catch (Exception ex) {
                // do nothing 
            }  
        }
    }     
    
    private boolean hasCompatiblePolicySets(Binding refBinding, Binding svcBinding) {
        boolean isCompatible = true;
        if ( refBinding instanceof PolicySubject && svcBinding instanceof PolicySubject ) {
            //TODO : need to add more compatibility checks at the policy attachment levels
            for ( PolicySet svcPolicySet : ((PolicySubject)svcBinding).getPolicySets() ) {
                isCompatible = false;
                for ( PolicySet refPolicySet : ((PolicySubject)refBinding).getPolicySets() ) {
                    if ( svcPolicySet.equals(refPolicySet) ) {
                        isCompatible = true;
                        break;
                    }
                }
                //if there exists no matching policy set in the reference binding
                if ( !isCompatible ) {
                    return isCompatible;
                }
            }
        }
        return isCompatible;
    } 
}
