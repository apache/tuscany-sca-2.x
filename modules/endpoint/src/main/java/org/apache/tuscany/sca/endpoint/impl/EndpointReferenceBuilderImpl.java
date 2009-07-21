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
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
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
import org.apache.tuscany.sca.runtime.EndpointRegistry;
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

    /**
     * Build a single endpoint reference
     *
     * @param endpoint
     * @param monitor
     */
    public void build(EndpointReference endpointReference, Monitor monitor) {
        Endpoint endpoint = endpointReference.getTargetEndpoint();

        if (endpoint == null){
            // an error?
        } else {
            if (endpoint.isUnresolved() == false){
                // Wired - service resolved - binding matched
                // The service is in the same composite or the
                // binding is remote and has a full URI

                // still need to check that the callback endpoint is set correctly
                if ((endpointReference.getCallbackEndpoint() != null) &&
                    (endpointReference.getCallbackEndpoint().isUnresolved() == false)){
                    return;
                }

                matchCallbackBinding(endpointReference,
                                     monitor);

                return;
            }

            if (endpointReference.isUnresolved() == false ){
                // Wired - service resolved - binding not matched
                // The service is in the same composite
                // TODO - How do we get to here?
                matchForwardBinding(endpointReference,
                                    true,
                                    monitor);

                matchCallbackBinding(endpointReference,
                                     monitor);
            } else {
                // Wired - service specified but unresolved
                // The service is in a remote composite somewhere else in the domain

                // find the service in the endpoint registry
                List<Endpoint> endpoints = endpointRegistry.findEndpoint(endpointReference);

                // TODO - do we expect to find more than one endpoint in
                //        anything other than the autowire case?
                if (endpoints.size() == 0) {
                    throw new SCARuntimeException("No endpoints found for EndpointReference " + endpointReference.toString());
                }

                if (endpoints.size() > 1) {
                    throw new SCARuntimeException("More than one endpoint found for EndpointReference" + endpointReference.toString());
                }

                endpointReference.setTargetEndpoint(endpoints.get(0));

                matchForwardBinding(endpointReference, false, monitor);

                matchCallbackBinding(endpointReference, monitor);
            }
        }

        if (endpointReference.isUnresolved()){
            throw new SCARuntimeException("Can't resolve " + endpointReference.toString());
        }
    }

    // TODO - EPR - In OASIS case there are no bindings to match with on the
    //        reference side.
    private void matchForwardBinding(EndpointReference endpointReference,
                                     boolean local,
                                     Monitor monitor) {

        Endpoint endpoint = endpointReference.getTargetEndpoint();

        List<Binding> matchedReferenceBinding = new ArrayList<Binding>();
        List<Endpoint> matchedServiceEndpoint = new ArrayList<Endpoint>();

        // Find the corresponding bindings from the service side
        if ((endpointReference.getReference().getBindings().size() == 0) ||
             ((endpointReference.getReference().getBindings().size() == 1) &&
              (endpointReference.getReference().getBindings().get(0) instanceof SCABinding))){
            // OAISIS - choose a binding from the service side
            //          (could have been specified as part of the target string)
            //           last part of this test that is looking for binding SCA is
            //          bogus. Just a temporary fix until we get rid of the OSOA
            //          style reference side bindings.

            // retrieve the user specified binding name.
            // TODO - EPR - we don't support this yet

            // otherwise pick the first binding from the service
            if (local) {
                endpointReference.setTargetEndpoint(endpoint.getService().getEndpoints().get(0));
            } else {
                endpointReference.setTargetEndpoint(endpoint);
            }
            endpointReference.setBinding(endpointReference.getTargetEndpoint().getBinding());
            endpointReference.setUnresolved(false);
            return;

        } else {
            // OAISIS - this is an error
            //          (for now let it match bindings while we rewrite OSOA tests)
            for (Binding referenceBinding : endpointReference.getReference().getBindings()) {
                if (local) {
                    for (Endpoint serviceEndpoint : endpoint.getService().getEndpoints()) {

                        if (referenceBinding.getType().equals(serviceEndpoint.getBinding().getType()) && hasCompatiblePolicySets(referenceBinding,
                                                                                                                              serviceEndpoint
                                                                                                                                  .getBinding())) {

                            matchedReferenceBinding.add(referenceBinding);
                            matchedServiceEndpoint.add(serviceEndpoint);
                        }
                    }
                } else {
                    Endpoint serviceEndpoint = endpoint;
                    if (referenceBinding.getType().equals(serviceEndpoint.getBinding().getType()) && hasCompatiblePolicySets(referenceBinding,
                                                                                                                          serviceEndpoint
                                                                                                                              .getBinding())) {

                        matchedReferenceBinding.add(referenceBinding);
                        matchedServiceEndpoint.add(serviceEndpoint);

                    }
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
            Endpoint serviceEndpoint = matchedServiceEndpoint.get(selectedBinding);

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

                Endpoint clonedEndpoint = (Endpoint)serviceEndpoint.clone();

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
    private void matchCallbackBinding(EndpointReference endpointReference,
                                     Monitor monitor) {

        // if no callback on the interface or we are creating a self reference do nothing
        if (endpointReference.getReference().getInterfaceContract() == null ||
            endpointReference.getReference().getInterfaceContract().getCallbackInterface() == null ||
            endpointReference.getReference().getName().startsWith("$self$.")){
                return;
        }

        Endpoint endpoint = endpointReference.getTargetEndpoint();

        List<Endpoint> callbackEndpoints = endpointReference.getReference().getCallbackService().getEndpoints();
        List<EndpointReference> callbackEndpointReferences = endpoint.getCallbackEndpointReferences();

        List<Endpoint> matchedEndpoint = new ArrayList<Endpoint>();

        // Find the corresponding bindings from callback service side
        if ((callbackEndpointReferences.size() ==0) ||
            (callbackEndpointReferences.get(0).getReference().getBindings().size() == 0) ||
            ((callbackEndpointReferences.get(0).getReference().getBindings().size() == 1) &&
             (callbackEndpointReferences.get(0).getReference().getBindings().get(0) instanceof SCABinding))){
            // OAISIS - choose a binding from the service side
            //          (could have been specified as part of the target string)
            //           last part of this test that is looking for binding SCA is
            //          bogus. Just a temporary fix until we get rid of the OSOA
            //          style reference side bindings.

            // retrieve the user specified binding name.
            // TODO - EPR - we don't support this yet

            // otherwise pick the first binding from the service
            //endpointReference.setTargetEndpoint(endpoint.getService().getEndpoints().get(0));
            //endpointReference.setBinding(endpointReference.getTargetEndpoint().getBinding());
            endpointReference.setCallbackEndpoint(callbackEndpoints.get(0));
            endpointReference.setUnresolved(false);
            return;

        } else {
            // OAISIS - this is an error
            //          (for now let it match bindings while we rewrite OSOA tests)

            if ((callbackEndpoints != null) &&  (callbackEndpointReferences != null)){
                // Find the corresponding bindings from the service side
                for (EndpointReference epr : callbackEndpointReferences) {
                    for (Endpoint ep : callbackEndpoints) {

                        if (epr.getBinding().getType().equals(ep.getBinding().getType()) &&
                            hasCompatiblePolicySets(epr.getBinding(), ep.getBinding())) {

                            matchedEndpoint.add(ep);
                        }
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
