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

package org.apache.tuscany.sca.endpoint.impl;

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
import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointReference2;
import org.apache.tuscany.sca.assembly.EndpointRegistry;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.EndpointReferenceBuilder;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.oasisopen.sca.SCARuntimeException;

/**
 * An builder that takes endpoint references and resolves them. It either finds local 
 * service endpoints if they are available or asks the domain. The main function here
 * is to perform binding and policy matching. 
 * This is a separate builder in case it is required by undresolved endpoints
 * once the runtime has started. 
 * 
 * @version $Rev$ $Date$
 */
public class EndpointReferenceBuilderImpl implements CompositeBuilder, EndpointReferenceBuilder {
    
    protected ExtensionPointRegistry extensionPoints;
    protected AssemblyFactory assemblyFactory;
    protected InterfaceContractMapper interfaceContractMapper;
    protected EndpointRegistry endpointRegistry;
    
    
    public EndpointReferenceBuilderImpl(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;
        
        FactoryExtensionPoint factories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);
        
        UtilityExtensionPoint utils = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        this.interfaceContractMapper = utils.getUtility(InterfaceContractMapper.class);
        
        this.endpointRegistry = utils.getUtility(EndpointRegistry.class);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.EndpointReferenceBuilder";
    }
    
    /**
     * Report a warning.
     * 
     * @param monitor
     * @param problems
     * @param message
     * @param model
     */
    protected void warning(Monitor monitor, String message, Object model, String... messageParameters) {
        if (monitor != null) {
            Problem problem = monitor.createProblem(this.getClass().getName(), "assembly-validation-messages", Severity.WARNING, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }
    
    /**
     * Report a error.
     * 
     * @param monitor
     * @param problems
     * @param message
     * @param model
     */
    protected void error(Monitor monitor, String message, Object model, String... messageParameters) {
        if (monitor != null) {
            Problem problem = monitor.createProblem(this.getClass().getName(), "assembly-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }  
    
    /**
     * Report a exception.
     * 
     * @param problems
     * @param message
     * @param model
     */
    protected void error(Monitor monitor, String message, Object model, Exception ex) {
        if (monitor != null) {
            Problem problem = null;
            problem = monitor.createProblem(this.getClass().getName(), "assembly-validation-messages", Severity.ERROR, model, message, ex);
            monitor.problem(problem);
        }
    }    

    /**
     * Build all the endpoint references
     * 
     * @param composite
     */
    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException 
    {
        // Not used now
    }

    /*
    private void populateEndpointRegistry(Composite composite, Monitor monitor) {
        
        // register endpoints (and endpoint references) for each component
        for (Component component : composite.getComponents()) {
            // recurse for composite implementations
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                populateEndpointRegistry((Composite)implementation, monitor);
            }
            
            // register endpoints  
            for (ComponentService service : component.getServices()) {
                for (Endpoint2 endpoint : service.getEndpoints()){
                    endpointRegistry.addEndpoint(endpoint);
                }
            }
            
            // register endpoint references  
            for (ComponentReference reference : component.getReferences()) {
                for (EndpointReference2 endpointReference : reference.getEndpointReferences()){
                    endpointRegistry.addEndpointReference(endpointReference);
                }
            }            
        }
    }
    */
    
    /**
     * Build a single endpoint reference
     * 
     * @param endpoint
     * @param monitor
     */
    public void build(EndpointReference2 endpointReference, Monitor monitor) {
        Endpoint2 endpoint = endpointReference.getTargetEndpoint();
      
        if (endpoint == null){
            // an error?
        } else {          
            if (endpoint.isUnresolved() == false){
                // Wired - service resolved - binding matched
                // The service is in the same composite
                return;
            }
            
            if (endpointReference.isUnresolved() == false ){  
                // Wired - service resolved - binding not matched 
                // The service is in the same composite
                // TODO - How do we get to here?
                matchForwardBinding(endpointReference, 
                                    monitor);
                
                matchCallbackBinding(endpointReference, 
                                     monitor);
            } else {
                // Wired - service specified but unresolved
                // The service is in a remote composite somewhere else in the domain
                
                // find the service in the endpoint registry
                List<Endpoint2> endpoints = endpointRegistry.findEndpoint(endpointReference);
                
                // TODO - do we exepect to find more than one endpoint in 
                //        anything other than the autowire case?
                if (endpoints.size() == 0) {
                    throw new SCARuntimeException("No endpoints found for EndpointReference " + endpointReference.toString());
                }
                
                if (endpoints.size() > 1) {
                    throw new SCARuntimeException("More than one endpoint found for EndpointReference" + endpointReference.toString());
                }
                
                endpointReference.setTargetEndpoint(endpoints.get(0));
                
                matchForwardBinding(endpointReference, 
                        monitor);
    
                matchCallbackBinding(endpointReference, 
                         monitor);
            } 
        }      
        
        if (endpointReference.isUnresolved()){
            throw new SCARuntimeException("EndpointReference can't be resolved");
        }
    }
    
    // TODO - EPR - In OASIS case there are no bindings to match with on the 
    //        reference side. 
    private void matchForwardBinding(EndpointReference2 endpointReference,
                                     Monitor monitor) {
        
        Endpoint2 endpoint = endpointReference.getTargetEndpoint();

        List<Binding> matchedReferenceBinding = new ArrayList<Binding>();
        List<Endpoint2> matchedServiceEndpoint = new ArrayList<Endpoint2>();

        // Find the corresponding bindings from the service side
        for (Binding referenceBinding : endpointReference.getReference().getBindings()) {
            for (Endpoint2 serviceEndpoint : endpoint.getService().getEndpoints()) {

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
            endpointReference.setUnresolved(true);
            warning(monitor, 
                    "NoMatchingBinding", 
                    endpointReference.getReference(),
                    endpointReference.getReference().getName(), 
                    endpoint.getService().getName());
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

                Binding clonedBinding = (Binding) referenceBinding.clone();

                // Set the binding URI to the URI of the target service
                // that has been matched
                if (referenceBinding.getURI() == null) {
                    clonedBinding.setURI(serviceEndpoint.getBinding().getURI());
                }
                
                // TODO - EPR can we remove this?
                if (clonedBinding instanceof OptimizableBinding) {
                    OptimizableBinding optimizableBinding = (OptimizableBinding)clonedBinding;
                    optimizableBinding.setTargetComponent(serviceEndpoint.getComponent());
                    optimizableBinding.setTargetComponentService(serviceEndpoint.getService());
                    optimizableBinding.setTargetBinding(serviceEndpoint.getBinding());
                }

                endpointReference.setBinding(clonedBinding);
                
                Endpoint2 clonedEndpoint = (Endpoint2)serviceEndpoint.clone();
                
                endpointReference.setTargetEndpoint(clonedEndpoint);
                endpointReference.setUnresolved(false);

            } catch (Exception ex) {
                // do nothing
            }
        }
    } 
    
    // TODO - EPR
    // Find the callback endpoint for the endpoint reference by matching
    // callback bindings between reference and service
    private void matchCallbackBinding(EndpointReference2 endpointReference, 
                                     Monitor monitor) {

        // if no callback on the interface or we are creating a self reference do nothing
        if (endpointReference.getReference().getInterfaceContract() == null || 
            endpointReference.getReference().getInterfaceContract().getCallbackInterface() == null ||
            endpointReference.getReference().getName().startsWith("$self$.")){
                return;
        }
        
        Endpoint2 endpoint = endpointReference.getTargetEndpoint();
        
        List<Endpoint2> callbackEndpoints = endpointReference.getReference().getCallbackService().getEndpoints();
        List<EndpointReference2> callbackEndpointReferences = endpoint.getCallbackEndpointReferences();
        
        List<Endpoint2> matchedEndpoint = new ArrayList<Endpoint2>();
        
        if ((callbackEndpoints != null) &&  (callbackEndpointReferences != null)){
            // Find the corresponding bindings from the service side
            for (EndpointReference2 epr : callbackEndpointReferences) {
                for (Endpoint2 ep : callbackEndpoints) {
    
                    if (epr.getBinding().getClass() == ep.getBinding().getClass() &&
                        hasCompatiblePolicySets(epr.getBinding(), ep.getBinding())) {
                        
                        matchedEndpoint.add(ep);              
                    }
                }
            }
        }
        
        if (matchedEndpoint.isEmpty()) {
            // No matching binding
            endpointReference.setCallbackEndpoint(null);
            endpointReference.setUnresolved(true);
            warning(monitor, 
                    "NoMatchingCallbackBinding", 
                    endpointReference.getReference(),
                    endpointReference.getReference().getName(), 
                    endpoint.getService().getName());
            return;
        } else {
            // default to using the first matched binding
            int selectedEndpoint = 0;
            
            for (int i = 0; i < matchedEndpoint.size(); i++){
                // If binding.sca is present, use it
                if (SCABinding.class.isInstance(matchedEndpoint.get(i).getBinding())) {
                    selectedEndpoint = i;
                }
            }
            
            endpointReference.setCallbackEndpoint(matchedEndpoint.get(selectedEndpoint));
            endpointReference.setUnresolved(false);
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
