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
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointReference2;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.EndpointBuilder;
import org.apache.tuscany.sca.assembly.builder.EndpointReference2Builder;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * An builder that takes endpoint references and resolves them. It either finds local 
 * service endpoints if they are available or asks the domain. The main function here
 * is to perform binding and policy matching. 
 * This is a separate builder in case it is required by undresolved endpoints
 * once the runtime has started. 
 * 
 * @version $Rev$ $Date$
 */
public class EndpointReference2BuilderImpl extends BaseBuilderImpl implements CompositeBuilder, EndpointReference2Builder {
    
    
    public EndpointReference2BuilderImpl(AssemblyFactory assemblyFactory, InterfaceContractMapper interfaceContractMapper) {
        super(assemblyFactory, null, null, null, interfaceContractMapper);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.EndpointReference2Builder";
    }

    /**
     * Build all the endpoint references
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
            
            // build endpoint references 
            for (ComponentReference reference : component.getReferences()) {
                for (EndpointReference2 endpointReference : reference.getEndpointReferences()){
                    build(endpointReference, monitor);
                }
            }
        }
    }    

    /**
     * Build a single endpoint reference
     * 
     * @param endpoint
     * @param monitor
     */
    public void build(EndpointReference2 endpointReference, Monitor monitor) {
        Endpoint2 endpoint = endpointReference.getTargetEndpoint();
      
 
        // check if the endpoint was available locally
        if (endpoint == null){
            if (endpointReference.isUnresolved() == false){
                // this is a non-wired endpoint reference
                return;
            } else {
                // target service is available remotely
                
                // go look it up in the domain
            }
        } else {
            // target service is available locally
            
            // check for wired reference that's already resolved
            if (endpoint.isUnresolved() == false){
                return;
            }
            
            // find the real endpoint for this reference by matching bindings
            // and policy sets
            matchForwardBinding(endpointReference, 
                                endpointReference.getTargetEndpoint().getService(),
                                monitor);
            
            matchCallbackBinding(endpointReference, 
                                 endpointReference.getTargetEndpoint().getService(),
                                 monitor);
        }        
        
    }
    
    // TODO - In OASIS case there are no bindings to match with on the 
    //        reference side. This code will be factored out into a pluggable 
    //        piece
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
            endpointReference.setCallbackEndpoint(null);
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
            
            Binding selectedCallbackBinding = matchedReferenceBinding.get(selectedBinding);

            ComponentService callbackService = endpointReference.getReference().getCallbackService();
            
            if (callbackService != null) {
                // find the callback endpoint that has the selected binding
                for (Endpoint2 endpoint : callbackService.getEndpoints()){
                    if (endpoint.getBinding().getName().startsWith(selectedCallbackBinding.getName())){
                        endpointReference.setCallbackEndpoint(endpoint);
                        break;
                    }
                }
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
