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

package org.apache.tuscany.sca.builder.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.Messages;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.Intent;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Creates endpoint reference models.
 */
public class EndpointReferenceBuilderImpl {
    private final Logger logger = Logger.getLogger(EndpointReferenceBuilderImpl.class.getName());

    private AssemblyFactory assemblyFactory;
    private InterfaceContractMapper interfaceContractMapper;
    
    public EndpointReferenceBuilderImpl(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        interfaceContractMapper = utilities.getUtility(InterfaceContractMapper.class);

        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
    }

    /**
     * Create endpoint references for all component references.
     *
     * @param composite
     */  
    public Composite build(Composite composite, BuilderContext context)
            throws CompositeBuilderException {
        
        // create endpoint references for each reference
        createEndpointReferences(composite, context);  
               
        // validate component references
        // left until this stage, after all endpoints have been created, 
        // to to catch more complex cases caused by reference promotion
        validateComponentReferences(composite, context.getMonitor());

        return composite;
    }  
    
    /**
     * Iterate down through the composites creating end point references for
     * all component references
     * 
     * @param composite
     * @param context
     */
    private void createEndpointReferences(Composite composite, BuilderContext context){
        
        context.getMonitor().pushContext("Composite: " + composite.getName().toString());
        
        try {
            for (Component component : composite.getComponents()) {
                context.getMonitor().pushContext("Component: " + component.getName());

                try {
                    // recurse for composite implementations
                    Implementation implementation = component.getImplementation();
                    if (implementation instanceof Composite) {
                        createEndpointReferences((Composite)implementation, context);
                    }
                    
                    for (ComponentReference reference : component.getReferences()) {
                        // create the endpoint references for this component reference
                        processComponentReference(composite,
                                                  component,
                                                  reference,
                                                  context);
                        
                        // we assume that endpoints have already been created so we can now
                        // create the links between enpoint references and endpoints that 
                        // represent callbacks
                        fixUpCallbackLinks(component,
                                           reference);
                        
                        // push down endpoint references into the leaf component references
                        // in the case where this component reference promotes a reference from
                        // a composite implementation
                        pushDownEndpointReferences(composite,
                                                   component,
                                                   reference,
                                                   context.getMonitor());                
                                        
                    }
                    
                    // Validate that references are wired or promoted, according
                    // to their multiplicity. This validates as we go and catches cases
                    // where a reference has been configured directly incorrectly with its
                    // immediate multiplicity setting. We re-run this validation again later
                    // to catch to more complex cases where reference promotion causes 
                    // multiplicity errors. 
                    validateReferenceMultiplicity(composite, component, context.getMonitor());
                    
                } finally {
                    context.getMonitor().popContext();
                }                    
            } 
        } finally {
            context.getMonitor().popContext();
        }            
    }
      
    /**
     * Create endpoint references for a component references. Endpoint references can be 
     * implied by refrence targets, autowire or binding settings 
     * 
     * @param composite
     * @param component
     * @param reference
     * @param context
     */
    private void processComponentReference(Composite composite,
                                           Component component,
                                           ComponentReference reference,
                                           BuilderContext context){
        
        context.getMonitor().pushContext("Reference: " + reference.getName());
        
        try {
            // Get reference targets
            List<ComponentService> refTargets = getReferenceTargets(reference);
            
            // This autowire processing really needs to move to the matching 
            // algorithm but dependency problems means it has to stay here for now
            if (Boolean.TRUE.equals(reference.getAutowire()) && reference.getTargets().isEmpty()) {
                // Find suitable targets in the current composite for an
                // autowired reference
                Multiplicity multiplicity = reference.getMultiplicity();
                for (Component targetComponent : composite.getComponents()) {
    
                    // Tuscany specific selection of the first autowire reference
                    // when there are more than one (ASM_60025)
                    if ((multiplicity == Multiplicity.ZERO_ONE || 
                         multiplicity == Multiplicity.ONE_ONE) && 
                        (reference.getEndpointReferences().size() != 0)) {
                        break;
                    }
    
                    // Prevent autowire connecting to self
                    if (targetComponent == component)
                        continue;
    
                    for (ComponentService targetComponentService : targetComponent.getServices()) {
                        if (reference.getInterfaceContract() == null || 
                            interfaceContractMapper.isCompatibleSubset(reference.getInterfaceContract(), 
                                                                 targetComponentService.getInterfaceContract())) {
                            
                            if (intentsMatch(reference.getRequiredIntents(), targetComponentService.getRequiredIntents())) {                            
                                EndpointReference endpointRef = createEndpointRef(component, reference, false);
                                endpointRef.setTargetEndpoint(createEndpoint(targetComponent, targetComponentService, true));
                                endpointRef.setStatus(EndpointReference.Status.WIRED_TARGET_NOT_FOUND);
                                reference.getEndpointReferences().add(endpointRef);
        
                                // Stop with the first match for 0..1 and 1..1 references
                                if (multiplicity == Multiplicity.ZERO_ONE || multiplicity == Multiplicity.ONE_ONE) {
                                    break;
                                } // end if
                            }
                        } // end if
                    } // end for
                } // end for
    
                if (multiplicity == Multiplicity.ONE_N || multiplicity == Multiplicity.ONE_ONE) {
                    if (reference.getEndpointReferences().size() == 0) {
                        Monitor.error(context.getMonitor(),
                                      this,
                                      Messages.ASSEMBLY_VALIDATION,
                                      "NoComponentReferenceTarget",
                                      reference.getName());
                    }
                }
    
                setSingleAutoWireTarget(reference);
                
                // as this is the autowire case we ignore any further configuration
                return;
            } 
            
            // check to see if explicit reference targets have been specified
            if (!refTargets.isEmpty()) {
                // Check that the component reference does not mix the use of endpoint references
                // specified via the target attribute with the presence of binding elements
                if (bindingsIdentifyTargets(reference)) {
                    Monitor.error(context.getMonitor(),
                                  this,
                                  Messages.ASSEMBLY_VALIDATION,
                                  "ReferenceEndPointMixWithTarget",
                                  composite.getName().toString(),
                                  component.getName(),
                                  reference.getName());
                }            
    
                // create endpoint references for targets
                for (ComponentService target : refTargets) {
                    
                    EndpointReference endpointRef = createEndpointRef(component, reference, true);
                    endpointRef.setTargetEndpoint(createEndpoint(component, target.getName()));
                    endpointRef.setStatus(EndpointReference.Status.WIRED_TARGET_NOT_FOUND);
                    reference.getEndpointReferences().add(endpointRef);
                    
                    // There is a special case where the user has defined policies on a 
                    // non-targetted, i.e. no URI, binding.sca in order to control the 
                    // intended QoS of the wire when matching takes place. If any other 
                    // bindings are specified then the test later on will complain about
                    // mixing targets with bindings
                    if (reference.getBindings().size() == 1){
                        Binding binding = reference.getBindings().get(0);
                        if ((binding instanceof SCABinding) && (binding.getURI() == null)){
                            endpointRef.setBinding(binding);
                        }
                    }
                }
            } 
    
            // if no endpoints have been found so far the bindings hold the targets.
            if (reference.getEndpointReferences().isEmpty()) {
                for (Binding binding : reference.getBindings()) {
    
                    String uri = binding.getURI();
    
                    // user hasn't put a uri on the binding so it's not a target name and the assumption is that
                    // the target is established via configuration of the binding element itself
                    if (uri == null) {
                        // Regular forward references are UNWIRED with no endpoint if they have an SCABinding with NO targets
                        // and NO URI set - but Callbacks with an SCABinding are wired and need an endpoint
                        if (!reference.isForCallback() && (binding instanceof SCABinding))
                            continue;
    
                        // create endpoint reference for manually configured bindings with a resolved endpoint to
                        // signify that this reference is pointing at some unwired endpoint
                        EndpointReference endpointRef = createEndpointRef(component, reference, binding, null, false);
                        if (binding instanceof SCABinding) {
                            // Assume that the system needs to resolve this binding later as
                            // it's the SCA binding
                            endpointRef.setTargetEndpoint(createEndpoint(true));
                            endpointRef.setStatus(EndpointReference.Status.NOT_CONFIGURED);
                        } else {
                            // The user has configured a binding so assume they know what 
                            // they are doing and mark in as already resolved. 
                            endpointRef.setTargetEndpoint(createEndpoint(false));
                            endpointRef.setStatus(EndpointReference.Status.RESOLVED_BINDING);
                        }
                        
                        reference.getEndpointReferences().add(endpointRef);
                        continue;
                    } // end if
                    
                    // if it's an absolute URI then assume that it's a resolved binding
                    try {
                        URI tmpURI = new URI(uri);
                        if (tmpURI.isAbsolute()){
                            // The user has configured a binding with an absolute URI so assume 
                            // they know what they are doing and mark in as already resolved. 
                            EndpointReference endpointRef = createEndpointRef(component, reference, binding, null, false);
                            endpointRef.setTargetEndpoint(createEndpoint(false));
                            endpointRef.setStatus(EndpointReference.Status.RESOLVED_BINDING);
                            reference.getEndpointReferences().add(endpointRef);
                            continue;
                        }
                    } catch (Exception ex){
                        // do nothing and go to the next bit of code
                        // which assumes that the URI is an SCA URI
                    }
    
                    // The user has put something in the binding uri but we don't know if it's
                    // a real URI or a target name. We can't tell until we have access to the 
                    // fully populated registry at run time. The "createComponent()" call here
                    // will do its best to parse out component/service/binding elements assuming
                    // that the getSCATargetParts detects that there are three "/" separated
                    // parts in the uri. 
                    EndpointReference endpointRef = createEndpointRef(component, reference, binding, null, false);
                    Endpoint endpoint = null;
                    try {
                        getSCATargetParts(uri);
                        
                        // the target uri might be an SCA target so create an endpoint
                        // so that the binder can test it against the fully populated
                        // registry
                        endpoint = createEndpoint(component, uri);
                        endpointRef.setStatus(EndpointReference.Status.WIRED_TARGET_IN_BINDING_URI); 
                    } catch (Exception ex) {
                        // the target string definitely isn't an SCA target string
                        // so we can assume here that the user has configured a
                        // resolved binding
                        endpoint = createEndpoint(false);
                        endpoint.setURI(uri);
                        endpoint.setBinding(binding);
                        endpointRef.setStatus(EndpointReference.Status.RESOLVED_BINDING);
                    }
                    
                    endpointRef.setTargetEndpoint(endpoint);   
                    reference.getEndpointReferences().add(endpointRef);
                }
            }
        } finally {
            context.getMonitor().popContext();
        }             
    }    
    
    private boolean intentsMatch(List<Intent> referenceIntents, List<Intent> serviceIntents) {
        Set<Intent> referenceIntentSet = new HashSet<Intent>(referenceIntents);
        Set<Intent> serviceIntentSet = new HashSet<Intent>(serviceIntents);
        return referenceIntentSet.equals(serviceIntentSet);
    }    
    
    /**
     * The validate stage is separate from the process stage as endpoint references are
     * pushed down the hierarchy. We don't know the full set of endpoint references until
     * all processing is complete. Hence we can't validate as we go
     * 
     * @param composite
     * @param monitor
     */
    private void validateComponentReferences(Composite composite, Monitor monitor) {

        monitor.pushContext("Composite: " + composite.getName().toString());
        try {
            // create endpoint references for each component's references
            for (Component component : composite.getComponents()) {
                monitor.pushContext("Component: " + component.getName());

                try {

                    // recurse for composite implementations
                    Implementation implementation = component.getImplementation();
                    if (implementation instanceof Composite) {
                        validateComponentReferences((Composite)implementation, monitor);
                    }
                    // Validate that references are wired or promoted, according
                    // to their multiplicity   
                    validateReferenceMultiplicity(composite, component, monitor);

                } finally {
                    monitor.popContext();
                }
            }
        } finally {
            monitor.popContext();
        }
    }        

    /**
     * Reference targets have to be resolved in the context in which they are 
     * defined so they can't be pushed down the hierarchy during the static build.
     * So we wait until we have calculated the endpoint references before pushing them 
     * down. Muliplicity errors will be caught by the multiplicity validation check that
     * comes next 
     * 
     * @param composite
     * @param component
     * @param reference
     * @param monitor
     */
    private void pushDownEndpointReferences(Composite composite,
                                            Component component,
                                            ComponentReference componentReference,
                                            Monitor monitor) {
        Reference reference = componentReference.getReference();
        
        if (reference instanceof CompositeReference) {
            List<ComponentReference> leafComponentReferences = getPromotedComponentReferences((CompositeReference)reference);
            
            // for each leaf component reference copy in the endpoint references for this
            // higher level (promoting) reference
            // TODO - the elements are inserted starting at 0 here because the code allows references multiplicity 
            //        validation constraints to be broken if the reference is autowire. At runtime the 
            //        first one is chosen if max multiplicity is 1. We have an OSOA test that assumes that
            //        promoted references overwrite leaf references. This insert gives the same effect in the
            //        autowire case. We need to think about if there is a more correct answer. 
            for (ComponentReference leafRef : leafComponentReferences){
                int insertLocation = 0;
                if (!leafRef.isNonOverridable()) {
                    leafRef.getEndpointReferences().clear();
                }                
                for (EndpointReference epr : componentReference.getEndpointReferences()){
                    // copy the epr
                    EndpointReference eprCopy = copyHigherReference(epr, leafRef);
                    leafRef.getEndpointReferences().add(insertLocation, eprCopy);
                    insertLocation++;
                }
            }
        }
        
        // TODO - what to do about callbacks in the reference promotion case
    }
    
    /**
     * Follow a reference promotion chain down to the innermost (non composite)
     * component references.
     * 
     * @param compositeReference
     * @return
     */
    private List<ComponentReference> getPromotedComponentReferences(CompositeReference compositeReference) {
        List<ComponentReference> componentReferences = new ArrayList<ComponentReference>();
        collectPromotedComponentReferences(compositeReference, componentReferences);
        return componentReferences;
    }

    /**
     * Follow a reference promotion chain down to the innermost (non composite)
     * component references.
     * 
     * @param compositeReference
     * @param componentReferences
     * @return
     */
    private void collectPromotedComponentReferences(CompositeReference compositeReference,
                                                    List<ComponentReference> componentReferences) {
        for (ComponentReference componentReference : compositeReference.getPromotedReferences()) {
            // If the user has entered an incorrect promotion string an error will be reported to 
            // tell them but the processing will still reach here so only continue processing 
            // if the promotion chain is well formed
            if (componentReference != null){
                Reference reference = componentReference.getReference();
                if (reference instanceof CompositeReference) {
    
                    // Continue to follow the reference promotion chain
                    collectPromotedComponentReferences((CompositeReference)reference, componentReferences);
    
                } else if (reference != null) {
    
                    // Found a non-composite reference
                    componentReferences.add(componentReference);
                }
            }
        }
    }
    
    /**
     * Copy a higher level EndpointReference down to a lower level reference which it promotes 
     * @param epRef - the endpoint reference
     * @param promotedReference - the promoted reference
     * @return - a copy of the EndpointReference with data merged from the promoted reference
     */
    private  EndpointReference copyHigherReference(EndpointReference epRef, ComponentReference promotedReference) {
        EndpointReference epRefClone = null;
        try {
            epRefClone = (EndpointReference)epRef.clone();
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        } // end try
        // Copy across details of the inner reference
        //ComponentReference ref = epRefClone.getReference();
        //FIXME
        epRefClone.setReference(promotedReference);
        return epRefClone;
    }   

    /**
     * For all references in a component check that multiplicity is correct
     * 
     * @param composite
     * @param component
     * @param monitor
     */
    private void validateReferenceMultiplicity(Composite composite, Component component, Monitor monitor) {
        for (ComponentReference componentReference : component.getReferences()) {
            if (!validateMultiplicity(componentReference.getMultiplicity(),
                                      componentReference.getEndpointReferences())) {
                if (componentReference.getEndpointReferences().isEmpty()) {

                    // No error if the reference is promoted out of the current composite
                    boolean promoted = false;
                    for (Reference reference : composite.getReferences()) {
                        CompositeReference compositeReference = (CompositeReference)reference;
                        if (compositeReference.getPromotedReferences().contains(componentReference)) {
                            promoted = true;
                            break;
                        }
                    }
                    if (!promoted && !componentReference.isForCallback() && !componentReference.isWiredByImpl()) {
                        Monitor.error(monitor,
                                      this,
                                      Messages.ASSEMBLY_VALIDATION,
                                      "ReferenceWithoutTargets",
                                      composite.getName().toString(),
                                      componentReference.getName());
                    }
                } else {
                    // no error if reference is autowire and more targets
                    // than multiplicity have been found 
                    if (Boolean.TRUE.equals(componentReference.getAutowire())) {
                        break;
                    }

                    Monitor.error(monitor,
                                  this,
                                  Messages.ASSEMBLY_VALIDATION,
                                  "TooManyReferenceTargets",
                                  componentReference.getName());
                }
            }
        }

    }
    
    /**
     * For a single component reference check that multiplicity is correct
     * 
     * @param multiplicity
     * @param endpointReferences
     * @return
     */
    private boolean validateMultiplicity(Multiplicity multiplicity, List<EndpointReference> endpointReferences) {

        // In some tests multiplicity is not set
        if (multiplicity == null) {
            return true;
        }

        // Count targets
        int count = endpointReferences.size();

        switch (multiplicity) {
            case ZERO_N:
                break;
            case ZERO_ONE:
                if (count > 1) {
                    return false;
                }
                break;
            case ONE_ONE:
                if (count != 1) {
                    return false;
                }
                break;
            case ONE_N:
                if (count < 1) {
                    return false;
                }
                break;
        }
        return true;
    }    

    /**
     * Evaluates whether the bindings attached to a reference identify one or more target services.
     * @param reference - the reference
     * @return true if the bindings identify a target, false otherwise
     */
    private boolean bindingsIdentifyTargets(ComponentReference reference) {
        for (Binding binding : reference.getBindings()) {
            // <binding.sca without a URI does not identify a target
            if ((binding instanceof SCABinding) && (binding.getURI() == null))
                continue;
            // any other binding implies a target
            // TODO Processing for other binding types
            return true;
        } // end for
        return false;
    } // end bindingsIdentifyTargets

    /**
     * Helper method which obtains a list of targets for a reference
     * @param reference - Component reference
     * @return - the list of targets, which will be empty if there are no targets
     */
    private List<ComponentService> getReferenceTargets(ComponentReference reference) {
        List<ComponentService> theTargets = reference.getTargets();
        if (theTargets.isEmpty()) {
            // Component reference list of targets is empty, try the implementation reference
            if (reference.getReference() != null) {
                theTargets = reference.getReference().getTargets();
            } // end if
        } // end if
        return theTargets;
    } // end method getReferenceTargets 

    /**
     * Helper method to create an Endpoint Reference
     * @param component
     * @param reference
     * @param binding
     * @param endpoint
     * @param unresolved
     * @return the endpoint reference
     */
    private EndpointReference createEndpointRef(Component component,
                                                ComponentReference reference,
                                                Binding binding,
                                                Endpoint endpoint,
                                                boolean unresolved) {
        EndpointReference endpointRef = createEndpointRef(component, reference, unresolved);
        endpointRef.setBinding(binding);
        endpointRef.setTargetEndpoint(endpoint);
        return endpointRef;
    } // end method

    /**
     * Helper method to create an Endpoint Reference
     * @param component
     * @param reference
     * @param unresolved
     * @return the endpoint reference
     */
    private EndpointReference createEndpointRef(Component component, ComponentReference reference, boolean unresolved) {
        EndpointReference endpointRef = assemblyFactory.createEndpointReference();
        endpointRef.setComponent(component);
        endpointRef.setReference(reference);
        endpointRef.setUnresolved(unresolved);
        return endpointRef;
    } // end method createEndpointRef   

    /**
     * Helper method to create an Endpoint
     * @param unresolved
     * @return the endpoint
     */
    private Endpoint createEndpoint(boolean unresolved) {
        Endpoint endpoint = assemblyFactory.createEndpoint();
        endpoint.setUnresolved(unresolved);
        return endpoint;
    } // end method createEndpoint
    
    
    /**
     * Separates a target name into component/service/binding parts. Throws an exceptions
     * if the number of parts <1 or > 3
     * @param targetName
     * @return String[] the recovered target parts
     */
    private String[] getSCATargetParts(String targetName){
        String[] parts = targetName.split("/");
        if (parts.length < 1 || parts.length > 3) {
            throw new IllegalArgumentException("Invalid target URI: " + targetName);
        } 
        return parts;
    }

    /**
     * Helper method to create an Endpoint
     *
     * @param component The component that owns the reference
     * @param targetName It can be one of the following formats
     * <ul>
     * <li>componentName
     * <li>componentName/serviceName
     * <li>componentName/serviceName/bindingName
     * </ul>
     * @return the endpoint
     */
    private Endpoint createEndpoint(Component component, String targetName) {
        String[] parts = getSCATargetParts(targetName);

        // Find the parent uri
        String uri = component.getURI();
        int index = uri.lastIndexOf('/');
        if (index == -1) {
            uri = "";
        } else {
            uri = uri.substring(0, index);
        }

        if (parts.length >= 1) {
            // Append the target component name
            if (uri.length() == 0) {
                uri = parts[0];
            } else {
                uri = uri + "/" + parts[0];
            }
        }
        if (parts.length == 3) {
            // <componentURI>#service-binding(serviceName/bindingName)
            uri = uri + "#service-binding(" + parts[1] + "/" + parts[2] + ")";
        } else if (parts.length == 2) {
            // <componentURI>#service(serviceName)
            uri = uri + "#service(" + parts[1] + ")";
        }

        Endpoint endpoint = assemblyFactory.createEndpoint();
        endpoint.setUnresolved(true);
        endpoint.setURI(uri);
        return endpoint;
    } // end method createEndpoint
    
    /**
     * Helper method to create an endpoint
     * @param component
     * @param service
     * @param unresolved
     * @return the endpoint
     */ 
    private Endpoint createEndpoint(Component component, ComponentService service, boolean unresolved) {
        Endpoint endpoint = createEndpoint(unresolved);
        endpoint.setComponent(component);
        endpoint.setService(service);
        endpoint.setUnresolved(unresolved);
        return endpoint;
    } // end method createEndpoint    
    
    /**
     * ASM_5021: where a <reference/> of a <component/> has @autowire=true 
     * and where the <reference/> has a <binding/> child element which 
     * declares a single target service,  the reference is wired only to 
     * the single service identified by the <wire/> element
     */
    private void setSingleAutoWireTarget(ComponentReference reference) {
        if (reference.getEndpointReferences().size() > 1 && reference.getBindings() != null
            && reference.getBindings().size() == 1) {
            String uri = reference.getBindings().get(0).getURI();
            if (uri != null) {
                if (uri.indexOf('/') > -1) {
                    // TODO: must be a way to avoid this fiddling
                    int i = uri.indexOf('/');
                    String c = uri.substring(0, i);
                    String s = uri.substring(i + 1);
                    uri = c + "#service(" + s + ")";
                }
                for (EndpointReference er : reference.getEndpointReferences()) {
                    if (er.getTargetEndpoint() != null && uri.equals(er.getTargetEndpoint().getURI())) {
                        reference.getEndpointReferences().clear();
                        reference.getEndpointReferences().add(er);
                        return;
                    }
                }
            }
        }
    }   
    
    /**
     * The SCA callback model causes services and references to be automatically created
     * to present the callback services and references. These are identifiable as their names
     * will match the name of the forward reference or service to which they relate. In the general
     * endpoint reference and endpoint processing we will have created endpoints and endpoint references 
     * for these callback services and references. We now need to relate forward endpoint references with
     * callback endpoints and forward endpoints with callback endpoint references. Here's the model...
     * 
     *    Client Component                                     Target Component
     *        Reference (with callback iface)                      Service (with callback iface)
     *            EndpointReference ----------------------------------> Endpoint
     *                  |                                                    |
     *                  |                                                    |
     *        Service   \/ (for the callback)                      Reference \/  (for the callback)
     *            Endpoint <--------------------------------------------EndpointReference
     *  
     * TODO - there are issues here with callback binding multiplicities that the OASIS spec
     *        is not very clear on. We need to decide which callback endpoint is associated with 
     *        which endpointreference. For the time being we select the first one that matches the 
     *        forward binding type as we assume that the user is simply using the callback binding
     *        configuration to further configure the type of binding they used for the forward call.
     * 
     * @param reference
     * @param component
     */
    private void fixUpCallbackLinks (Component component, ComponentReference reference){
        
        // fix up the links between endpoint references and endpoints that represent callbacks
        // [rfeng] Populate the callback endpoints
        if (reference.getCallbackService() != null) {
            List<Endpoint> callbackEndpoints = reference.getCallbackService().getEndpoints();
            if (!callbackEndpoints.isEmpty()) {
                for (EndpointReference endpointReference : reference.getEndpointReferences()){
                    for(Endpoint callbackEndpoint : callbackEndpoints){
                        if((endpointReference.getBinding() != null) &&
                           (callbackEndpoint.getBinding() != null) &&
                           (callbackEndpoint.getBinding().getType().equals(endpointReference.getBinding().getType()))){
                            endpointReference.setCallbackEndpoint(callbackEndpoint);
                            break;
                        }
                    }
                }
            }
        }
        
        // fix up links between endpoints and endpoint references that represent callbacks
        for (ComponentService service : component.getServices()) {
            if ((service.getInterfaceContract() != null) && 
                (service.getInterfaceContract().getCallbackInterface() != null)) {
                if (reference.getName().equals(service.getName())) {
                    for (Endpoint endpoint : service.getEndpoints()) {
                        for ( EndpointReference callbackEndpointReference : reference.getEndpointReferences()){
                            if((endpoint.getBinding() == null) &&
                               (callbackEndpointReference.getBinding() == null) &&
                               (callbackEndpointReference.getBinding().getType().equals(endpoint.getBinding().getType()))){
                                endpoint.getCallbackEndpointReferences().add(callbackEndpointReference);
                                break;
                            }
                        }
                    }
                } 
            } 
        } 
    }  

}