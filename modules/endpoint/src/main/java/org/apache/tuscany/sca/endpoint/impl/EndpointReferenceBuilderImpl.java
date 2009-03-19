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
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.EndpointReferenceBuilder;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
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
public class EndpointReferenceBuilderImpl implements CompositeBuilder, EndpointReferenceBuilder {
    
    protected ExtensionPointRegistry extensionPoints;
    protected AssemblyFactory assemblyFactory;
    protected InterfaceContractMapper interfaceContractMapper;
    
    
    public EndpointReferenceBuilderImpl(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;
    }
    
    public EndpointReferenceBuilderImpl(FactoryExtensionPoint factories, InterfaceContractMapper mapper) {
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);
        this.interfaceContractMapper = mapper;
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
     * Index components inside a composite
     * 
     * @param composite
     * @param componentServices

     */
    protected void indexComponents(Composite composite,
                                 Map<String, Component> components) {
        for (Component component : composite.getComponents()) {
            // Index components by name
            components.put(component.getName(), component);
        }    
    }
    
    /**
     * Index services inside a composite
     * 
     * @param composite
     * @param componentServices
     */
    protected void indexServices(Composite composite,
                                 Map<String, ComponentService> componentServices) {

        for (Component component : composite.getComponents()) {
            
            ComponentService nonCallbackService = null;
            int nonCallbackServiceCount = 0;
            
            for (ComponentService componentService : component.getServices()) {                 
                // Index component services by component name / service name
                String uri = component.getName() + '/' + componentService.getName();
                componentServices.put(uri, componentService);
                
                // count how many non-callback there are
                if (!componentService.isCallback()) {                            
                    
                    if (nonCallbackServiceCount == 0) {
                        nonCallbackService = componentService;
                    }
                    nonCallbackServiceCount++;
                }
            }
            if (nonCallbackServiceCount == 1) {
                // If we have a single non callback service, index it by
                // component name as well
                componentServices.put(component.getName(), nonCallbackService);
            }
        }    
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
      
        if (endpoint == null){
            // an error?
        } else {          
            if (endpoint.isUnresolved() == false){
                // everything is resolved
                return;
            }
            
            if (endpointReference.isUnresolved() == false ){  
                // TODO - bring resolution and binding matching together
                // just do binding matching
                matchForwardBinding(endpointReference, 
                                    monitor);
                
                matchCallbackBinding(endpointReference, 
                                     monitor);
            } else {
                // resolve the endpoint reference in the domain and then 
                // match bindings
            } 
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
            endpointReference.setTargetEndpoint(null);
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

                Binding cloned = (Binding) referenceBinding.clone();

                // Set the binding URI to the URI of the target service
                // that has been matched
                if (referenceBinding.getURI() == null) {
                    cloned.setURI(serviceEndpoint.getBinding().getURI());
                }
                
                // TODO - EPR can we remove this?
                if (cloned instanceof OptimizableBinding) {
                    OptimizableBinding optimizableBinding = (OptimizableBinding)cloned;
                    optimizableBinding.setTargetComponent(serviceEndpoint.getComponent());
                    optimizableBinding.setTargetComponentService(serviceEndpoint.getService());
                    optimizableBinding.setTargetBinding(serviceEndpoint.getBinding());
                }

                endpointReference.setBinding(cloned);
                endpointReference.setTargetEndpoint(serviceEndpoint);

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
