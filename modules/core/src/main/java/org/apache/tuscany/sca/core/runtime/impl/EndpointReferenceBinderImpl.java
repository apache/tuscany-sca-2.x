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

package org.apache.tuscany.sca.core.runtime.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.BindingBuilder;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.PolicyBuilder;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeEndpointImpl;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeEndpointReferenceImpl;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.util.Audit;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.policy.BindingType;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentMap;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.Qualifier;
import org.apache.tuscany.sca.provider.EndpointReferenceAsyncProvider;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.CompositeActivator;
import org.apache.tuscany.sca.runtime.DomainRegistry;
import org.apache.tuscany.sca.runtime.EndpointReferenceBinder;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.apache.tuscany.sca.runtime.UnknownEndpointHandler;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * A builder that takes endpoint references and resolves them. It either finds local
 * service endpoints if they are available or asks the domain. The main function here
 * is to perform binding and policy matching.
 * 
 * This is a separate from the builders so that the mechanism for reference/service matching 
 * can be used at runtime as well as build time and can also be replaced independently
 *
 * @version $Rev$ $Date$
 */
public class EndpointReferenceBinderImpl implements EndpointReferenceBinder {
    private static final Logger logger = Logger.getLogger(EndpointReferenceBinderImpl.class.getName());

    protected ExtensionPointRegistry extensionPoints;
    protected AssemblyFactory assemblyFactory;
    protected InterfaceContractMapper interfaceContractMapper;
    protected BuilderExtensionPoint builders;
    protected CompositeActivator compositeActivator;
    protected Monitor monitor;
    protected UnknownEndpointHandler unknownEndpointHandler;
    protected CompositeBuilder policyAppliesToBuilder;


    public EndpointReferenceBinderImpl(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;

        FactoryExtensionPoint factories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);

        UtilityExtensionPoint utils = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        this.interfaceContractMapper = utils.getUtility(InterfaceContractMapper.class);
        
        MonitorFactory monitorFactory = utils.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();

        this.unknownEndpointHandler = utils.getUtility(UnknownEndpointHandler.class);
        
        this.builders = extensionPoints.getExtensionPoint(BuilderExtensionPoint.class);
        
        this.compositeActivator = extensionPoints.getExtensionPoint(CompositeActivator.class);
    }
    
    /**
     * Bind a single endpoint reference at build time. Here we only expect the
     * registry to have a record of local endpoints
     *
     * @param domainRegistry
     * @param endpointReference
     */
    public void bindBuildTime(DomainRegistry domainRegistry, 
                              EndpointReference endpointReference, 
                              BuilderContext builderContext) {
       bind(domainRegistry, endpointReference, builderContext, false);
    }
    
    /**
     * Bind a single endpoint reference at run time. Here we expect the
     * registry to be populated with endpoints from across the domain
     *
     * @param domainRegistry
     * @param endpointReference
     */
    public void bindRunTime(DomainRegistry domainRegistry,
                            EndpointReference endpointReference) {
        bind(domainRegistry, endpointReference, null, true);
    }
    
    /**
     * Bind a reference to a service endpoint
     * 
     * @param domainRegistry
     * @param endpointReference
     * @param runtime set true if called from the runtime 
     */
    public void bind(DomainRegistry domainRegistry,  
                     EndpointReference endpointReference,
                     BuilderContext builderContext,
                     boolean runtime){
        
        logger.fine("Binding " + endpointReference.toString());
        
        Audit matchAudit = new Audit();
             
        // This logic does post build autowire matching but isn't actually used at the moment
        // as problems with dependencies mean we still do this during build
        if (endpointReference.getStatus() == EndpointReference.Status.AUTOWIRE_PLACEHOLDER){ 
           
            // do autowire matching
            // will only be called at build time at the moment
            Multiplicity multiplicity = endpointReference.getReference().getMultiplicity();
            for (Endpoint endpoint : domainRegistry.getEndpoints()){
//              if (endpoint is in the same composite as endpoint reference){
                    if ((multiplicity == Multiplicity.ZERO_ONE || 
                         multiplicity == Multiplicity.ONE_ONE) && 
                        (endpointReference.getReference().getEndpointReferences().size() > 1)) {
                        break;
                    }

                    // Prevent autowire connecting to self
                    if (endpointReference.getComponent() == 
                        endpoint.getComponent()) {
                        continue;
                    }
                    
                    if (haveMatchingPolicy(endpointReference, endpoint, matchAudit, builderContext) &&
                        haveMatchingInterfaceContracts(endpointReference, endpoint, matchAudit)){
                        // matching service so find if this reference already has 
                        // an endpoint reference for this endpoint
                        Endpoint autowireEndpoint = null;
                        
                        for (EndpointReference epr : endpointReference.getReference().getEndpointReferences()){
                            if (epr.getTargetEndpoint() == endpoint){
                                autowireEndpoint = endpoint;
                                break;
                            }
                        }
                        
                        if (autowireEndpoint == null){
                            // create new EPR for autowire
                            EndpointReference autowireEndpointRefrence = null;
                            try {
                                autowireEndpointRefrence = (EndpointReference)endpointReference.clone();
                            } catch (Exception ex){
                                // won't happen as clone is supported
                            }
                            
                            autowireEndpointRefrence.setTargetEndpoint(endpoint);
                            autowireEndpointRefrence.setBinding(endpoint.getBinding());
                            autowireEndpointRefrence.setStatus(EndpointReference.Status.WIRED_TARGET_FOUND_AND_MATCHED);
                            endpointReference.getReference().getEndpointReferences().add(autowireEndpointRefrence);  
                        }
                    }
//              }
            }
            
            if (multiplicity == Multiplicity.ONE_N || multiplicity == Multiplicity.ONE_ONE) {
                if (endpointReference.getReference().getEndpointReferences().size() == 1) {
                    Monitor.error(monitor,
                                  this,
                                  "endpoint-validation-messages",
                                  "NoComponentReferenceTarget",
                                  endpointReference.getReference().getName());
                    throw new ServiceRuntimeException("Unable to bind " +
                                                      monitor.getLastProblem().toString());
                }
            }
            
            setSingleAutoWireTarget(endpointReference.getReference());
            
        } else if ( endpointReference.getStatus() == EndpointReference.Status.WIRED_TARGET_FOUND_AND_MATCHED||
                    endpointReference.getStatus() == EndpointReference.Status.RESOLVED_BINDING ) {
            // The endpoint reference is already resolved to either
            // a service endpoint local to this composite or it has
            // a remote binding
            
            // still need to check that the callback endpoint is set correctly
            if (hasCallback(endpointReference) && 
                (endpointReference.getCallbackEndpoint() == null 
                    || endpointReference.getCallbackEndpoint().isUnresolved())) {
                selectCallbackEndpoint(endpointReference,
                                       endpointReference.getReference().getCallbackService(),
                                       matchAudit, 
                                       builderContext, 
                                       runtime);
            } 
        } else if (endpointReference.getStatus() == EndpointReference.Status.WIRED_TARGET_FOUND_READY_FOR_MATCHING ){
            // The endpoint reference is already resolved to either
            // a service endpoint but no binding was specified in the 
            // target URL and/or the policies have yet to be matched.
            // Used when $self references are resolved
            
            selectForwardEndpoint(endpointReference,
                                  endpointReference.getTargetEndpoint().getService().getEndpoints(),
                                  matchAudit,
                                  builderContext);

            if (hasCallback(endpointReference)){
                selectCallbackEndpoint(endpointReference,
                                       endpointReference.getReference().getCallbackService(),
                                       matchAudit,
                                       builderContext, 
                                       runtime);
            }             
        } else if (endpointReference.getStatus() == EndpointReference.Status.WIRED_TARGET_IN_BINDING_URI ||
                   endpointReference.getStatus() == EndpointReference.Status.WIRED_TARGET_NOT_FOUND ||
                   endpointReference.getStatus() == EndpointReference.Status.NOT_CONFIGURED){
            // The reference is not yet matched to a service
          
            // find the service in the endpoint registry
            List<Endpoint> endpoints = domainRegistry.findEndpoint(endpointReference);
            
            if (endpoints.size() > 0){
                selectForwardEndpoint(endpointReference,
                        endpoints,
                        matchAudit,
                        builderContext);

                // If the reference was matched try to match the callback
                if (endpointReference.getStatus().equals(EndpointReference.Status.WIRED_TARGET_FOUND_AND_MATCHED) &&
                    hasCallback(endpointReference)){
                    selectCallbackEndpoint(endpointReference,
                                           endpointReference.getReference().getCallbackService(),
                                           matchAudit,
                                           builderContext, 
                                           runtime);
                } 
            } else if (runtime) {
                // tweak to test if this could be a resolved binding. This is the back end of the test
                // in the builder that pulls the URI out of the binding if there are no targets
                // on the reference. have to wait until here to see if the binding uri matches any
                // available services. If not we assume here that it's a resolved binding
                if (endpointReference.getStatus() == EndpointReference.Status.WIRED_TARGET_IN_BINDING_URI){
                    endpointReference.getTargetEndpoint().setBinding(endpointReference.getBinding());
                    endpointReference.setStatus(EndpointReference.Status.RESOLVED_BINDING);
                } else {
                    processUnknownEndpoint(endpointReference, matchAudit);
                    
                    if (!endpointReference.getStatus().equals(EndpointReference.Status.WIRED_TARGET_FOUND_AND_MATCHED)){
                        Monitor.error(monitor, 
                                      this, 
                                      "endpoint-validation-messages", 
                                      "NoEndpointsFound", 
                                      endpointReference.toString()); 
                        throw new ServiceRuntimeException("Unable to bind " + 
                                                          monitor.getLastProblem().toString());
                    }
                }
            } else {
                // it's build time so just give the UnknownEndpoint code a chance
                // without regard for the result
                processUnknownEndpoint(endpointReference, matchAudit);
            }
        } 
        
        logger.fine(matchAudit.toString());

        if (endpointReference.getStatus() != EndpointReference.Status.WIRED_TARGET_FOUND_AND_MATCHED &&
            endpointReference.getStatus() != EndpointReference.Status.RESOLVED_BINDING){
            
            if (runtime){
                Monitor.error(monitor, 
                              this, 
                              "endpoint-validation-messages", 
                              "EndpointReferenceCantBeMatched", 
                              endpointReference.toString(),
                              matchAudit);
                throw new ServiceRuntimeException("Unable to bind " + 
                                                  monitor.getLastProblem().toString());
            } else {
                Monitor.warning(monitor, 
                                this, 
                                "endpoint-validation-messages", 
                                "ComponentReferenceTargetNotFound", 
                                endpointReference.toString());
                return;
            }
               

        }
        
        // Now the endpoint reference is resolved check that the binding interfaces contract
        // and the reference contract are compatible
        try {
            ((RuntimeEndpointReference)endpointReference).validateReferenceInterfaceCompatibility();
        } catch (ServiceRuntimeException ex) {
            // don't re-throw this exception at build time just record the
            // error. If it's thrown here is messes up the order in which
            // build time errors are reported and that in turn messes
            // up the output of the compliance tests. 
            if (runtime){
                throw ex;
            } else {
                Monitor.error(monitor, 
                        this, 
                        "endpoint-validation-messages", 
                        "EndpointReferenceCantBeMatched", 
                        endpointReference.toString(),
                        ex.getMessage());
            }
        }
        
        // TUSCANY-3783
        // if the reference is an async reference and the binding doesn't support
        // async natively fluff up the response service/endpoint
        ReferenceBindingProvider referenceBindingProvider = ((RuntimeEndpointReference)endpointReference).getBindingProvider();
        if ( referenceBindingProvider instanceof EndpointReferenceAsyncProvider &&
             !((EndpointReferenceAsyncProvider)referenceBindingProvider).supportsNativeAsync() &&
             endpointReference.isAsyncInvocation() && 
             endpointReference.getCallbackEndpoint() == null) {
            ((RuntimeEndpointReference)endpointReference).createAsyncCallbackEndpoint();
        }
    
        // System.out.println("MATCH AUDIT:" + matchAudit.toString());
    }       
        
    private void processUnknownEndpoint(EndpointReference endpointReference, Audit matchAudit){
        Binding b = null;
        if (unknownEndpointHandler != null) {
            b = unknownEndpointHandler.handleUnknownEndpoint(endpointReference);
        }
        if (b != null) {
            Endpoint matchedEndpoint = new RuntimeEndpointImpl(extensionPoints);
            matchedEndpoint.setBinding(b);
            matchedEndpoint.setRemote(true);
            endpointReference.setTargetEndpoint(matchedEndpoint);
            endpointReference.setBinding(b);
            endpointReference.setUnresolved(false);
            endpointReference.setStatus(EndpointReference.Status.WIRED_TARGET_FOUND_AND_MATCHED);
            matchAudit.append("Match because the UnknownEndpointHandler provided a binding: " + b.getType() + " uri: " + b.getURI());
            matchAudit.appendSeperator();
        }
    }
   
    /**
     * Returns true if the reference has a callback
     */
    private boolean hasCallback(EndpointReference endpointReference){
        if (endpointReference.getReference().getInterfaceContract() == null ||
            endpointReference.getReference().getInterfaceContract().getCallbackInterface() == null ||
            endpointReference.getReference().getName().startsWith("$self$.")){
            return false;
        } else {
            return true;
        }
    }

    /**
     * Selects a forward endpoint from a list of possible candidates
     * 
     * @param endpointReference
     * @param endpoints
     */
    private void selectForwardEndpoint(EndpointReference endpointReference, List<Endpoint> endpoints, Audit matchAudit, BuilderContext builderContext) {    
             
        Endpoint matchedEndpoint = null;
        
        if (endpointReference.getReference().getName().startsWith("$self$.")){
            // just select the first one and don't do any policy matching
            if (endpointReference.getTargetEndpoint() != null && !endpointReference.getTargetEndpoint().isUnresolved()) {
                matchedEndpoint = endpointReference.getTargetEndpoint();
            } else {
                matchedEndpoint = endpoints.get(0);
            }
        } else {
            // find the first endpoint that matches this endpoint reference
            for (Endpoint endpoint : endpoints){
                if (haveMatchingPolicy(endpointReference, endpoint, matchAudit, builderContext) &&
                    haveMatchingInterfaceContracts(endpointReference, endpoint, matchAudit)){
                    matchedEndpoint = endpoint;
                    break;
                }
            }
        }
        
        if (matchedEndpoint == null){
            return;
        } else {
            endpointReference.setTargetEndpoint(matchedEndpoint);
            Binding binding = matchedEndpoint.getBinding();
            endpointReference.setBinding(binding);
            // TUSCANY-3873 - add policy from the service
            //                we don't care about intents at this stage
            endpointReference.getPolicySets().addAll(matchedEndpoint.getPolicySets());
            // TODO - we need to re-run the appliesTo processing here but there is some question about what 
            //        appliesTo means. It's also difficult to get to the PolicyAppliesToBuilder from here and
            //        need a new EntensionInterface to support access. So for now I'm just cheating and looking to 
            //        see if the XPath expression contains the binding type as a string while we discuss appliesTo
            
            List<PolicySet> psToRemove = new ArrayList<PolicySet>();
            
            for (PolicySet ps : endpointReference.getPolicySets() ) {
                if (!ps.getAppliesTo().contains(endpointReference.getBinding().getType().getLocalPart())){
                    psToRemove.add(ps);
                }
            }
            
            endpointReference.getPolicySets().removeAll(psToRemove);
            
            build(endpointReference);
            endpointReference.setStatus(EndpointReference.Status.WIRED_TARGET_FOUND_AND_MATCHED);
            endpointReference.setUnresolved(false);
        }
    }

    private void build(EndpointReference endpointReference) {
        BindingBuilder builder = builders.getBindingBuilder(endpointReference.getBinding().getType());
        if (builder != null) {
            builder.build(endpointReference.getComponent(),
                          endpointReference.getReference(),
                          endpointReference.getBinding(),
                          new BuilderContext(extensionPoints),
                          false);
        }
    }

    /**
     * This code is very similar to the code in EnpointReferenceBuilder.fixUpCallbackLinks()
     * but here we are selecting endpoint/endpoint references at runtime. 
     * 
     * To recap the general model is as follows:
     * 
     *  Forward EPR  -----------> Forward EP (target EP)
     *     |                          |
     *     \/                         \/
     *  Callback EP <------------ Callback EPR 
     * 
     * The question then becomes how to construct this model given that
     * target resolution can happen at runtime and EPR and EP can be 
     * distributed across the domain. 
     * 
     * The forward link is resolved by searching in the registry for an EP 
     * that matches the EPR. 
     * 
     * At build time we've made the assumption that, if the user configures
     * a callback binding at all, they will choose the same binding as the
     * forward call (TODO - this may be a bold assumption). So here we just
     * assume that the callback endpoint that is set on the reference
     * at build time matches. This may not be true if the callback bindings
     * are configured differently on the reference and service. 
     * 
     * @param endpointReference
     * @param endpoints
     */
    private void selectCallbackEndpoint(EndpointReference endpointReference, ComponentService callbackService, Audit matchAudit, BuilderContext builderContext, boolean runtime) {
        
        // A simplifying assumption here is that the user will only specify a callback binding
        // of the same type as the forward binding in order to specify further configuration
        // We can then assume that if there is no callback endpoint that matches already then we have 
        // picked up binding config from the service and we can create a callback endpoint to match
        
        // So having said this we look in three places for the callback binding. 
        // 1/ in callback endpoints at the reference
        //      - will find it here if the user has manually configured a callback binding at the reference
        // 2/ in the service callback structure 
        //      - will find it here if the user has manually configured a callback binding at the service
        // 3/ in the service callback reference structure
        //      - will find it here if the system has constructed the callback binding based on the forward binding
        //
        // 2 and 3 are conflated in the distributed case as be write any derived callback bindings out into the
        // callback structure as the endpoint is serialized across the domain
        
        RuntimeEndpoint callbackEndpoint = null;
        
        // 1/ look in callback endpoints at the reference
        //      - exploiting the assumption that the user will specific callback bindings of 
        //        the same type as the forward binding
        match1:
        for(Endpoint loopCallbackEndpoint : callbackService.getEndpoints()){
            if(loopCallbackEndpoint.getBinding().getType().equals(endpointReference.getBinding().getType())){
                callbackEndpoint = (RuntimeEndpoint)loopCallbackEndpoint;
                break match1;
            }
        }   
        
        // if no callback endpoint was found then create a new callback endpoint
        if (callbackEndpoint == null ){
            callbackEndpoint = (RuntimeEndpoint)assemblyFactory.createEndpoint();
            callbackEndpoint.setComponent(endpointReference.getComponent());
            callbackEndpoint.setService(callbackService);
            
            Binding forwardBinding = endpointReference.getBinding();
            Binding callbackBinding = null;
            
            // 2/  look in the service callback structure 
            Callback serviceCallback = endpointReference.getTargetEndpoint().getService().getCallback();
            
            if (serviceCallback != null){
                for(Binding loopCallbackBinding : serviceCallback.getBindings()){
                    if(loopCallbackBinding.getType().equals(endpointReference.getBinding().getType())){
                        callbackBinding = loopCallbackBinding;
                        break;
                    }
                }
            }

            // 3/ look in the service endpoint callback reference structure
            ComponentReference callbackReference = endpointReference.getTargetEndpoint().getService().getCallbackReference();
            
            if (callbackReference != null){
                for (EndpointReference loopEndpointReference : callbackReference.getEndpointReferences()){
                    if (loopEndpointReference.getBinding().getType().equals(endpointReference.getBinding().getType())){
                        callbackBinding = loopEndpointReference.getBinding();
                        break;
                    }
                }
            }
            
            // if all else fails clone the forward binding
            // TODO - do we ever get here?
            if (callbackBinding == null){
                try {
                    callbackBinding = (Binding)forwardBinding.clone();
                } catch (CloneNotSupportedException ex){
                }  
            }

            // get the callback binding URI by looking at the SCA binding 
            // that will have been added at build time
            callbackBinding.setURI(null);
            for (Endpoint endpoint : callbackService.getEndpoints()){
                if (endpoint.getBinding().getType().equals(SCABinding.TYPE)){
                    callbackBinding.setURI(endpoint.getBinding().getURI());
                }
            }
            
            callbackEndpoint.setBinding(callbackBinding);
            callbackService.getBindings().add(callbackBinding);
            
            callbackEndpoint.setUnresolved(false);
            callbackService.getEndpoints().add(callbackEndpoint);
            
            // build it
            build(callbackEndpoint);
            
            // Only activate the callback endpoint if the bind is being done at runtime
            // and hence everything else is running. If it's build time then the
            // endpoint will be activated at the same time as all the other endpoints
            if (runtime) {
                // activate it
                compositeActivator.activate(((RuntimeEndpointReferenceImpl)endpointReference).getCompositeContext(), 
                                            callbackEndpoint);
                
                // start it
                compositeActivator.start(((RuntimeEndpointReferenceImpl)endpointReference).getCompositeContext(), 
                                         callbackEndpoint);  
            }
        } 
        
        // finally set the callback endpoint into the forward reference
        endpointReference.setCallbackEndpoint(callbackEndpoint);       
    }
    
    private void build(Endpoint endpoint) {
        
        BindingBuilder builder = builders.getBindingBuilder(endpoint.getBinding().getType());
        if (builder != null) {
            builder.build(endpoint.getComponent(),
                          endpoint.getService(),
                          endpoint.getBinding(),
                          new BuilderContext(extensionPoints), 
                          true);
        }
    }    

    /**
     * Determine if endpoint reference and endpoint policies match. We know by this stage
     * that 
     *   - a given policy set will only contain expressions from a single language
     *   - a given endpoint or endpoint reference's policy sets will only contain
     *     expressions from a single language
     *     
     * Matching algorithm (read from the top down):
     *   - FAIL if there are intents that are mutually exclusive between reference and service
     *   - PASS if there are no intents or policies present at reference and service
     *   - FAIL if there are unresolved intents (intents with no policy set) at the reference (service should have been checked previously)
     *          the wrinkle here is that we need to adopt policy from the service if the reference doesn't define a binding
     *   - PASS if there are no policies at reference and service (now we know all intents are resolved)
     *   - FAIL if there are some policies on one side but not on the other
     *   - PASS if the QName of the policy sets on each side match
     *   - FAIL if the policy languages on both sides are different
     *   - Perform policy specific match
     *   
     */
    private boolean haveMatchingPolicy(EndpointReference endpointReference, Endpoint endpoint, Audit matchAudit, BuilderContext builderContext){
        matchAudit.append("Match policy of " + endpointReference.toString() + " to " + endpoint.toString() + " ");
        
        List<PolicySet> referencePolicySets = new ArrayList<PolicySet>();
        Binding binding = null;
        
        if (endpointReference.getBinding() == null){
            binding = endpoint.getBinding();
        } else {
            binding = endpointReference.getBinding();
        }
        
        // if there are any intents that are mutually exclusive between 
        // service and reference then they don't match
        for (Intent eprIntent : endpointReference.getRequiredIntents()){
            for (Intent epIntent : endpoint.getRequiredIntents()){ 
                if (eprIntent.getExcludedIntents().contains(epIntent) ||
                    epIntent.getExcludedIntents().contains(eprIntent) ||
                    checkQualifiedMutualExclusion(eprIntent.getExcludedIntents(), epIntent) ||
                    checkQualifiedMutualExclusion(epIntent.getExcludedIntents(), eprIntent)){
                    matchAudit.append("No match because the following intents are mutually exclusive " + 
                                      eprIntent.toString() +
                                      " " +
                                      epIntent.toString() +
                                      " ");
                    matchAudit.appendSeperator();
                    return false;
                }
            }
        }
        
        // Find the set of policy sets from this reference. This includes 
        // the policy sets that are specific to the service binding and 
        // any policy sets that are not binding specific    
        for (PolicySet policySet : endpointReference.getPolicySets()){
            PolicyBuilder policyBuilder = null;
            
            if (policySet.getPolicies().size() > 0){
                QName policyType = policySet.getPolicies().get(0).getName();
                policyBuilder = builders.getPolicyBuilder(policyType);
            }
            
            if ((policyBuilder == null) ||
                (policyBuilder != null && policyBuilder.getSupportedBindings() == null) ||
                (policyBuilder != null && policyBuilder.getSupportedBindings().contains(binding.getType()))){
                referencePolicySets.add(policySet);
            }
        }
        
        // if there are no policy sets on the reference take the policy sets from the
        // service binding we are matching against
        if (referencePolicySets.isEmpty()) {
            for (PolicySet policySet : endpoint.getPolicySets()){
                PolicyBuilder policyBuilder = null;
                
                if (policySet.getPolicies().size() > 0){
                    QName policyType = policySet.getPolicies().get(0).getName();
                    policyBuilder = builders.getPolicyBuilder(policyType);
                }
                
                if ((policyBuilder == null) ||
                    (policyBuilder != null && policyBuilder.getSupportedBindings() == null) ||
                    (policyBuilder != null && policyBuilder.getSupportedBindings().contains(binding.getType()))){
                    referencePolicySets.add(policySet);
                }
            }   
        }
        
        // the "appliesTo" algorithm to remove any policy sets that 
        // don't apply to the service binding will already have been 
        // run during the build phase
        
        // Determine if there are any reference policies
        boolean noEndpointReferencePolicies = true;
        
        for (PolicySet policySet : referencePolicySets){
            if (policySet.getPolicies().size() > 0){
                noEndpointReferencePolicies = false;
                break;
            }
        }
        
        // Determine of there are any service policies
        boolean noEndpointPolicies = true;
        
        for (PolicySet policySet : endpoint.getPolicySets()){
            if (policySet.getPolicies().size() > 0){
                noEndpointPolicies = false;
                break;
            }
        }        
        
        // if no policy sets or intents are present then they match
        if ((endpointReference.getRequiredIntents().size() == 0) &&
            (endpoint.getRequiredIntents().size() == 0) &&
            (noEndpointReferencePolicies) &&
            (noEndpointPolicies)) {
            matchAudit.append("Match because there are no intents or policies ");
            matchAudit.appendSeperator();
            return true;
        }        
        
        // check that the intents on the reference side are resolved 
        // can't do this until this point as the service binding
        // may come into play. Intents may be satisfied by the default
        // or optional intents that the binding type provides. Failing
        // this they must be satisfied by reference policy sets
        // Failing this the intent is unresolved and the reference and 
        // service don't match
        
        // TODO - seems that we should do this loop on a binding by binding basis
        //        rather than each time we do matching
        BindingType bindingType = null;
        
        Definitions systemDefinitions = null;
        if (builderContext != null){
            systemDefinitions = builderContext.getDefinitions();
        } else {
            systemDefinitions = ((RuntimeEndpoint)endpoint).getCompositeContext().getSystemDefinitions();
        }
        
        for (BindingType loopBindingType : systemDefinitions.getBindingTypes()){
            if (loopBindingType.getType().equals(binding.getType())){
                bindingType = loopBindingType;
                break;
            }
        }
        
        // Before we start examining intents, remove any whose constrained
        // types don't include the binding type
        removeConstrainedIntents(endpointReference, bindingType);
        
        List<Intent> eprIntents = new ArrayList<Intent>();
        eprIntents.addAll(endpointReference.getRequiredIntents());
        
        // first check the binding type
        for (Intent intent : endpointReference.getRequiredIntents()){ 
            if (bindingType != null && 
                bindingType.getAlwaysProvidedIntents().contains(intent)){
                eprIntents.remove(intent);
            } else if (bindingType != null &&
                       bindingType.getMayProvidedIntents().contains(intent)){
                eprIntents.remove(intent);
            } else {
               // TODO - this code also appears in the ComponentPolicyBuilder
               //        so should rationalize
               loop: for (PolicySet policySet : referencePolicySets){
                    if (policySet.getProvidedIntents().contains(intent)){
                        eprIntents.remove(intent);
                        break;
                    }
                    
                    for (Intent psProvidedIntent : policySet.getProvidedIntents()){
                        if (isQualifiedBy(psProvidedIntent, intent)){
                            eprIntents.remove(intent);
                            break loop;
                        }
                    }

                    for (IntentMap map : policySet.getIntentMaps()) {
                        for (Qualifier q : map.getQualifiers()) {
                            if (intent.equals(q.getIntent())) {
                                eprIntents.remove(intent);
                                break loop;
                            }
                        }
                    }                    
                }          
            }                
        }
        
        // if there are unresolved intents the service and reference don't match
        if (eprIntents.size() > 0){
            matchAudit.append("No match because there are unresolved intents " + eprIntents.toString() + " ");
            matchAudit.appendSeperator();
            return false;
        }   
        
        // if there are no policies on epr or ep side then 
        // they match
        if (noEndpointPolicies && noEndpointReferencePolicies){
            matchAudit.append("Match because the intents are resolved and there are no policy sets ");
            matchAudit.appendSeperator();
            return true;
        }
        
        // if there are some policies on one side and not the other then 
        // the don't match
        if (noEndpointPolicies && !noEndpointReferencePolicies) {
            matchAudit.append("No match because there are policy sets at the endpoint reference but not at the endpoint ");
            matchAudit.appendSeperator();
            return false;
        }
        
        if (!noEndpointPolicies && noEndpointReferencePolicies){
            matchAudit.append("No match because there are policy sets at the endpoint but not at the endpoint reference ");
            matchAudit.appendSeperator();
            return false;
        }
        
        // If policy set QNames from epr and er match exactly then the reference and 
        // service policies are compatible
        Set<PolicySet> referencePolicySet = new HashSet<PolicySet>(referencePolicySets);
        Set<PolicySet> servicePolicySet = new HashSet<PolicySet>(endpoint.getPolicySets());
        if(referencePolicySet.equals(servicePolicySet)){
            matchAudit.append("Match because the policy sets on both sides are eactly the same ");
            matchAudit.appendSeperator();
            return true;
        }
        
        // if policy set language at ep and epr are not the same then there is no
        // match. We get the policy language by looking at the first expression
        // of the first policy set. By this stage we know that all the policy sets
        // in an endpoint or endpoint reference will use a single language and we know 
        // that there is at least one policy set with at least one policy
        QName eprLanguage = null;
        
        for (PolicySet policySet : referencePolicySets){
            if (policySet.getPolicies().size() > 0){
                eprLanguage = policySet.getPolicies().get(0).getName();
                break;
            }
        }
        
        QName epLanguage = null;
          
        for (PolicySet policySet : endpoint.getPolicySets()){
            if (policySet.getPolicies().size() > 0){
                epLanguage = policySet.getPolicies().get(0).getName();
                break;
            }
        }
        
        if(!eprLanguage.getNamespaceURI().equals(epLanguage.getNamespaceURI())){
            matchAudit.append("No match because the policy sets on either side have policies in differnt languages " + 
                              eprLanguage + 
                              " and " +
                              epLanguage +
                              " ");
            matchAudit.appendSeperator();
            return false;
        }
        
        // now do a policy specific language match 
        PolicyBuilder builder = builders.getPolicyBuilder(eprLanguage);
        boolean match = false;
        
        // switch the derived list of policy sets into the reference
        // it will be left there if there is a match
        List<PolicySet> originalPolicySets = endpointReference.getPolicySets();
        endpointReference.getPolicySets().clear();
        endpointReference.getPolicySets().addAll(referencePolicySets);
        
        if (builder != null) {
            if (builderContext == null){
                builderContext = new BuilderContext(monitor);
            }
            
            match = builder.build(endpointReference, endpoint, builderContext);
        } 
                
        if (!match){
            matchAudit.append("No match because the language specific matching failed ");
            matchAudit.appendSeperator();
            endpointReference.getPolicySets().clear();
            endpointReference.getPolicySets().addAll(originalPolicySets);
        } else {
            matchAudit.append("Match because the language specific matching succeeded ");
            matchAudit.appendSeperator();
        }
        
        return match;
    }
    
    // Copied from ComponentPolicyBuilder, should probably be refactored
    protected void removeConstrainedIntents(EndpointReference subject, BindingType bindingType) {
        List<Intent> intents = subject.getRequiredIntents();
        
        // Remove the intents whose @contrains do not include the current element       
        if(bindingType != null){
            List<Intent> copy = new ArrayList<Intent>(intents);
            for (Intent i : copy) {
            	List<ExtensionType> constrainedTypes = i.getConstrainedTypes();
            	if (( constrainedTypes.size() == 0 ) && ( i.getQualifiableIntent() != null ) )  
            		constrainedTypes = i.getQualifiableIntent().getConstrainedTypes();
            	
                if (constrainedTypes.size() > 0){               
                    boolean constraintFound = false;
                    for (ExtensionType constrainedType : i.getConstrainedTypes()){
                        if (constrainedType.getType().equals(bindingType.getType()) ||
                            constrainedType.getType().equals(bindingType.getBaseType())){
                            constraintFound = true;
                            break;
                        }
                    }
                    if(!constraintFound){
                        intents.remove(i);
                    }
                }
            }
        }
    }
  

	protected boolean isQualifiedBy(Intent qualifiableIntent, Intent qualifiedIntent){
        if (qualifiedIntent.getQualifiableIntent() == qualifiableIntent){
            return true;
        } else {
            return false;
        }
    }
    
    protected boolean checkQualifiedMutualExclusion(List<Intent> excludedIntentList, Intent intent){
        for (Intent excludedIntent : excludedIntentList){
            if (intent.getQualifiableIntent() != null &&
                excludedIntent != null &&
                intent.getQualifiableIntent().equals(excludedIntent)){
                return true;
            }
        }
        return false;
    }      
    
    /**
     * Determine if endpoint reference and endpoint interface contracts match 
     */
    private boolean haveMatchingInterfaceContracts(EndpointReference endpointReference, Endpoint endpoint, Audit matchAudit){
        matchAudit.append("Match interface of " + endpointReference.toString() + " to " + endpoint.toString() + " ");
        
        InterfaceContract endpointReferenceContract = endpointReference.getReference().getInterfaceContract();
        InterfaceContract endpointContract = endpoint.getComponentServiceInterfaceContract();
        
        if (endpointReferenceContract == null){
            matchAudit.append("Match because there is no interface contract on the reference ");
            matchAudit.appendSeperator();
            return true;
        }     
        
        if (endpoint.isRemote()){
            matchAudit.append("Match because endpoint is remote");
            matchAudit.appendSeperator();
            return true;
        }
        
        // If the contracts are not of the same type use normailized interfaces
        if (endpointReferenceContract.getClass() != endpointContract.getClass() ||
            endpointReferenceContract.getNormalizedWSDLContract() != null ||
            endpointContract.getNormalizedWSDLContract() != null) {
            endpointReferenceContract = ((RuntimeEndpointReference)endpointReference).getGeneratedWSDLContract(endpointReferenceContract);
            endpointContract = ((RuntimeEndpoint)endpoint).getGeneratedWSDLContract(endpointContract);
        }        
             
        boolean match = false;
        match = interfaceContractMapper.isCompatibleSubset(endpointReferenceContract, 
                                                           endpointContract, 
                                                           matchAudit);
        
        if (!match){
            matchAudit.append("Match failed because the interface contract mapper failed ");
        } else {
            matchAudit.append("Match because the interface contract mapper succeeded ");
        }
        
        matchAudit.appendSeperator();
        
        return match;
    }
    
    /**
     * Checks to see if the registry has been updated since the reference was last matched
     * 
     * @return true is the registry has changed
     */
    public boolean isOutOfDate(DomainRegistry domainRegistry, EndpointReference endpointReference) {
        Endpoint te = endpointReference.getTargetEndpoint();
        if (te != null && !te.isUnresolved()
            && te.getURI() != null
            && endpointReference.getStatus() != EndpointReference.Status.RESOLVED_BINDING) {
            return domainRegistry.isOutOfDate(endpointReference);
        }
        return false;
    }

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
    
     
}
