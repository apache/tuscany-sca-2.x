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
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that creates endpoint reference models.
 *
 * @version $Rev$ $Date$
 */
public class ComponentReferenceEndpointReferenceBuilderImpl extends BaseBuilderImpl implements CompositeBuilder {


    public ComponentReferenceEndpointReferenceBuilderImpl(AssemblyFactory assemblyFactory, InterfaceContractMapper interfaceContractMapper) {
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

                // fix up links between endpoints and endpoint references that represent callbacks
                for (ComponentService service : component.getServices()){
                    if ((service.getInterfaceContract() != null) &&
                        (service.getInterfaceContract().getCallbackInterface() != null)){
                        if ( reference.getName().equals(service.getName())){
                            for ( Endpoint2 endpoint : service.getEndpoints()){
                                endpoint.getCallbackEndpointReferences().addAll(reference.getEndpointReferences());
                            }
                            break;
                        } // end if
                    } // end if
                } // end for
            } // end for
        } // end for
    } // end method processCompoenntReferences

    private void createReferenceEndpointReferences(Composite composite,
                                                   Component component,
                                                   ComponentReference reference,
                                                   Map<String, Component> components,
                                                   Map<String, ComponentService> componentServices,
                                                   Monitor monitor)
    {
        // Get reference targets
    	List<ComponentService> refTargets = getReferenceTargets( reference );
    	if (reference.getAutowire() == Boolean.TRUE &&
            reference.getTargets().isEmpty()) {

            // Find suitable targets in the current composite for an
            // autowired reference
            Multiplicity multiplicity = reference.getMultiplicity();
            for (Component targetComponent : composite.getComponents()) {

                // Prevent autowire connecting to self
            	if( targetComponent == component ) continue;

                for (ComponentService targetComponentService : targetComponent.getServices()) {
                    if (reference.getInterfaceContract() == null ||
                        interfaceContractMapper.isCompatible(reference.getInterfaceContract(),
                                                             targetComponentService.getInterfaceContract())) {
                        // create endpoint reference - with a dummy endpoint which will be replaced when policies
                        // are matched and bindings are configured later
                        EndpointReference2 endpointRef = createEndpointRef( component, reference, false  );
                        endpointRef.setTargetEndpoint( createEndpoint(targetComponent, targetComponentService, true) );
                        reference.getEndpointReferences().add(endpointRef);

                        // Stop with the first match for 0..1 and 1..1 references
                        if (multiplicity == Multiplicity.ZERO_ONE ||
                            multiplicity == Multiplicity.ONE_ONE) {
                            break;
                        } // end if
                    } // end if
                } // end for
            } // end for

            if (multiplicity == Multiplicity.ONE_N ||
                multiplicity == Multiplicity.ONE_ONE) {
                if (reference.getEndpointReferences().size() == 0) {
                    warning(monitor, "NoComponentReferenceTarget",
                            reference,
                            reference.getName());
                }
            }

        } else if (!refTargets.isEmpty()) {
            // Check that the component reference does not mix the use of endpoint references
        	// specified via the target attribute with the presence of binding elements
            if( bindingsIdentifyTargets( reference ) ) {
                warning(monitor, "ReferenceEndPointMixWithTarget",
                        composite, composite.getName().toString(), component.getName(), reference.getName());
            }

            // Resolve targets specified on the component reference
            for (ComponentService target : refTargets) {

                String targetName = target.getName();
                ComponentService targetComponentService = componentServices.get(targetName);

                Component targetComponent = getComponentFromTargetName( components, targetName );

                if (targetComponentService != null) {
                    // Check that target component service provides a superset of the component reference interface
                    if (reference.getInterfaceContract() == null ||
                        interfaceContractMapper.isCompatible(reference.getInterfaceContract(),
                                                             targetComponentService.getInterfaceContract())) {

                        // create endpoint reference -  with dummy endpoint which will be replaced when policies
                        // are matched and bindings are configured later
                        EndpointReference2 endpointRef = createEndpointRef( component, reference, false  );
                        endpointRef.setTargetEndpoint(createEndpoint(targetComponent, targetComponentService, true));
                        reference.getEndpointReferences().add(endpointRef);
                    } else {
                        warning(monitor, "ReferenceIncompatibleInterface",
                                composite,
                                composite.getName().toString(),
                                component.getName() + "." + reference.getName(),
                                targetName);
                    }
                } else {
                    // add an unresolved endpoint reference with an unresolved endpoint to go with it
                    EndpointReference2 endpointRef = createEndpointRef( component, reference, true  );
                    endpointRef.setTargetEndpoint(createEndpoint(true));
                    reference.getEndpointReferences().add(endpointRef);
                    warning(monitor, "ComponentReferenceTargetNotFound",
                            composite,
                            composite.getName().toString(),
                            targetName);
                } // end if
            } // end for
        } // end if

        // if no endpoints have found so far the bindings hold the targets.
        if (reference.getEndpointReferences().isEmpty()) {
            for (Binding binding : reference.getBindings()) {

                String uri = binding.getURI();

                // user hasn't put a uri on the binding so it's not a target name and the assumption is that
                // the target is established via configuration of the binding element itself
                if (uri == null) {
                	// Regular forward references are UNWIRED with no endpoint if they have an SCABinding with NO targets
                	// and NO URI set - but Callbacks with an SCABinding are wired and need an endpoint
                	if( !reference.isCallback() && (binding instanceof SCABinding) ) continue;

                    // create endpoint reference for manually configured bindings with a resolved endpoint to
                	// signify that this reference is pointing at some unwired endpoint
                    EndpointReference2 endpointRef = createEndpointRef( component, reference,
                    		binding, null, false  );
                    endpointRef.setTargetEndpoint(createEndpoint(false));
                    reference.getEndpointReferences().add(endpointRef);
                    continue;
                } // end if

                // user might have put a local target name in the uri - see if it refers to a target we know about
                // - if it does the reference binding will be matched with a service binding
                // - if it doesn't it is assumed to be an external reference
                if (uri.startsWith("/")) {
                    uri = uri.substring(1);
                }

                // Resolve the target component and service
                ComponentService targetComponentService = componentServices.get(uri);
                Component targetComponent = getComponentFromTargetName( components, uri );

                // If the binding URI matches a component in the composite, configure an endpoint reference with
                // this component as the target.
                // If not, the binding URI is assumed to reference an external service
                if (targetComponentService != null) {

                    // Check that the target component service provides
                    // a superset of the component reference interface
                    if (reference.getInterfaceContract() == null ||
                        interfaceContractMapper.isCompatible(reference.getInterfaceContract(),
                                                             targetComponentService.getInterfaceContract())) {
                        // create endpoint reference with dummy endpoint which will be replaced when policies
                        // are matched and bindings are configured later
                        EndpointReference2 endpointRef = createEndpointRef( component, reference, binding, null, false  );
                        endpointRef.setTargetEndpoint(createEndpoint(targetComponent, targetComponentService, true));
                        reference.getEndpointReferences().add(endpointRef);
                    } else {
                        warning(monitor, "ReferenceIncompatibleInterface",
                                composite,
                                composite.getName().toString(),
                                reference.getName(),
                                uri);
                    }
                } else {
                    // create endpoint reference for manually configured bindings with resolved endpoint
                	// to signify that this reference is pointing at some unwired endpoint
                    EndpointReference2 endpointRef = createEndpointRef( component, reference, binding, null, false  );
                    endpointRef.setTargetEndpoint(createEndpoint( false ));
                    reference.getEndpointReferences().add(endpointRef);
                } // end if
            }
        }
    } // end method

    /**
     * Evaluates whether the bindings attached to a reference indentify one or more target services.
     * @param reference - the reference
     * @return true if the bindings identify a target, false otherwise
     */
    private boolean bindingsIdentifyTargets( ComponentReference reference ) {
    	for( Binding binding : reference.getBindings() ) {
    		// <binding.sca without a URI does not identify a target
        	if( (binding instanceof SCABinding) && (binding.getURI() == null) ) continue;
        	// any other binding implies a target
        	return true;
    	} // end for
    	return false;
    } // end bindingsIdentifyTargets

    /**
     * Helper method which obtains a list of targets for a reference
     * @param reference - Component reference
     * @return - the list of targets, which will be empty if there are no targets
     */
    private List<ComponentService> getReferenceTargets( ComponentReference reference ) {
    	List<ComponentService> theTargets = reference.getTargets();
    	if( theTargets.isEmpty() ) {
    		// Component reference list of targets is empty, try the implementation reference
    		if( reference.getReference() != null ) {
    			theTargets = reference.getReference().getTargets();
    		} // end if
    	} // end if
    	return theTargets;
    } // end method getReferenceTargets

    /**
     * Helper method that finds the Component given a target name
     * @param components
     * @param targetName
     * @return the component
     */
    private Component getComponentFromTargetName( Map<String, Component> components, String targetName ) {
    	Component theComponent;
        int s = targetName.indexOf('/');
        if (s == -1) {
        	theComponent = components.get(targetName);
        } else {
        	theComponent = components.get(targetName.substring(0, s));
        }
    	return theComponent;
    } // end method getComponentFromTargetName

    /**
     * Helper method to create an Endpoint Reference
     * @param component
     * @param reference
     * @param binding
     * @param endpoint
     * @param unresolved
     * @return the endpoint reference
     */
    private EndpointReference2 createEndpointRef( Component component, ComponentReference reference,
    		Binding binding, Endpoint2 endpoint, boolean unresolved  ) {
    	EndpointReference2 endpointRef = createEndpointRef( component, reference, unresolved  );
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
    private EndpointReference2 createEndpointRef( Component component, ComponentReference reference, boolean unresolved  ) {
	    EndpointReference2 endpointRef = assemblyFactory.createEndpointReference();
	    endpointRef.setComponent(component);
	    endpointRef.setReference(reference);
	    endpointRef.setUnresolved(unresolved);
	    return endpointRef;
    } // end method createEndpointRef


    /**
     * Helper method to create an endpoint
     * @param component
     * @param service
     * @param unresolved
     * @return the endpoint
     */
    private Endpoint2 createEndpoint( Component component, ComponentService service, boolean unresolved) {
	    Endpoint2 endpoint = createEndpoint( unresolved);
	    endpoint.setComponent(component);
	    endpoint.setService(service);
	    endpoint.setUnresolved(unresolved);
	    return endpoint;
    } // end method createEndpoint

    /**
     * Helper method to create an Endpoint
     * @param unresolved
     * @return the endpoint
     */
    private Endpoint2 createEndpoint( boolean unresolved) {
    	Endpoint2 endpoint = assemblyFactory.createEndpoint();
	    endpoint.setUnresolved(unresolved);
	    return endpoint;
    } // end method createEndpoint

} // end class
