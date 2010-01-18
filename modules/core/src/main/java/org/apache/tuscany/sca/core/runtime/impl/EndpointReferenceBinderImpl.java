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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.PolicyBuilder;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.runtime.EndpointReferenceBinder;
import org.apache.tuscany.sca.runtime.EndpointRegistry;

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

    protected ExtensionPointRegistry extensionPoints;
    protected AssemblyFactory assemblyFactory;
    protected InterfaceContractMapper interfaceContractMapper;
    protected BuilderExtensionPoint builders;
    private Monitor monitor;


    public EndpointReferenceBinderImpl(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;

        FactoryExtensionPoint factories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);

        UtilityExtensionPoint utils = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        this.interfaceContractMapper = utils.getUtility(InterfaceContractMapper.class);
        
        MonitorFactory monitorFactory = utils.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();
        
        this.builders = extensionPoints.getExtensionPoint(BuilderExtensionPoint.class);
    }
    
    /**
     * Bind a single endpoint reference at build time. Here we only expect the
     * registry to have a record of local endpoints
     *
     * @param endpointRegistry
     * @param endpointReference
     */
    public boolean bindBuildTime(EndpointRegistry endpointRegistry, 
                                 EndpointReference endpointReference) {
       return bind(endpointRegistry, endpointReference, false);
    }
    
    /**
     * Bind a single endpoint reference at build time. Here we expect the
     * registry to be populated with endpoints from across the domain
     *
     * @param endpointRegistry
     * @param endpointReference
     */
    public boolean bindRunTime(EndpointRegistry endpointRegistry,
                               EndpointReference endpointReference) {
        return bind(endpointRegistry, endpointReference, true);
    }
    
    /**
     * Bind a reference to a service endpoint
     * 
     * @param endpointRegistry
     * @param endpointReference
     * @param runtime set true if called from the runtime 
     */
    public boolean bind(EndpointRegistry endpointRegistry,  
                         EndpointReference endpointReference,
                         boolean runtime){
        
        Problem problem = null;
             
        // This logic does post build autowire matching but isn't actually used at the moment
        // as problems with dependencies mean we still do this during build
        if (endpointReference.getStatus() == EndpointReference.AUTOWIRE_PLACEHOLDER){ 
           
            // do autowire matching
            // will only be called at build time at the moment
            Multiplicity multiplicity = endpointReference.getReference().getMultiplicity();
            for (Endpoint endpoint : endpointRegistry.getEndpoints()){
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
                    
                    if (haveMatchingPolicy(endpointReference, endpoint) &&
                        haveMatchingInterfaceContracts(endpointReference, endpoint)){
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
                            autowireEndpointRefrence.setStatus(EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED);
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
                }
            }
            
            setSingleAutoWireTarget(endpointReference.getReference());
            
        } else if ( endpointReference.getStatus() == EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED||
                    endpointReference.getStatus() == EndpointReference.RESOLVED_BINDING ) {
            // The endpoint reference is already resolved to either
            // a service endpoint local to this composite or it has
            // a remote binding
            
            // still need to check that the callback endpoint is set correctly
            if (hasCallback(endpointReference) &&
                endpointReference.getCallbackEndpoint() != null &&
                endpointReference.getCallbackEndpoint().isUnresolved() == true ){
                problem = selectCallbackEndpoint(endpointReference,
                                                 endpointReference.getReference().getCallbackService().getEndpoints());
            } 
        } else if (endpointReference.getStatus() == EndpointReference.WIRED_TARGET_FOUND_READY_FOR_MATCHING ){
            // The endpoint reference is already resolved to either
            // a service endpoint but no binding was specified in the 
            // target URL and/or the policies have yet to be matched.
            // TODO - is this really required now
            
            problem = selectForwardEndpoint(endpointReference,
                                            endpointReference.getTargetEndpoint().getService().getEndpoints());

            if (problem == null && hasCallback(endpointReference)){
                problem = selectCallbackEndpoint(endpointReference,
                                                 endpointReference.getReference().getCallbackService().getEndpoints());
            }             
        } else if (endpointReference.getStatus() == EndpointReference.WIRED_TARGET_IN_BINDING_URI ||
                   endpointReference.getStatus() == EndpointReference.WIRED_TARGET_NOT_FOUND ||
                   endpointReference.getStatus() == EndpointReference.NOT_CONFIGURED){
            // The reference is not yet matched to a service
          
            // find the service in the endpoint registry
            List<Endpoint> endpoints = endpointRegistry.findEndpoint(endpointReference);
            
            if ((endpoints.size() == 0) && 
                (runtime == true)     ) {
                
                // tweak to test if this could be a resolve binding. This is the back end of the test
                // in the builder that pulls the URI out of the binding if there are no targets
                // on the reference. have to wait until here to see if the binding uri matches any
                // available services. If not we assume here that it's a resolved binding
                if (endpointReference.getStatus() == EndpointReference.WIRED_TARGET_IN_BINDING_URI){
                    endpointReference.getTargetEndpoint().setBinding(endpointReference.getBinding());
                    endpointReference.setRemote(true);
                    endpointReference.setStatus(EndpointReference.RESOLVED_BINDING);
                } else {
                    problem = monitor.createProblem(this.getClass().getName(), 
                                                    "endpoint-validation-messages", 
                                                    Problem.Severity.ERROR, 
                                                    this, 
                                                    "NoEndpointsFound", 
                                                    endpointReference.toString());
                }
            }            

            problem = selectForwardEndpoint(endpointReference,
                                            endpoints);

            if (problem == null && hasCallback(endpointReference)){
                problem = selectCallbackEndpoint(endpointReference,
                                                 endpointReference.getReference().getCallbackService().getEndpoints());
            }             
        } 
        
        if (problem != null){
            monitor.problem(problem);
            return false;
        }

        if (endpointReference.getStatus() != EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED &&
            endpointReference.getStatus() != EndpointReference.RESOLVED_BINDING){
            
            if (runtime){
                problem = monitor.createProblem(this.getClass().getName(), 
                                                "endpoint-validation-messages", 
                                                Problem.Severity.ERROR, 
                                                this, 
                                                "EndpointReferenceCantBeMatched", 
                                                endpointReference.toString());
            } else {
                problem = monitor.createProblem(this.getClass().getName(), 
                                                "endpoint-validation-messages", 
                                                Problem.Severity.WARNING, 
                                                this, 
                                                "ComponentReferenceTargetNotFound",
                                                "NEED COMPOSITE NAME",
                                                endpointReference.toString());
            }
            
            monitor.problem(problem);
            return false;
        }
       
        return true;
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
    private Problem selectForwardEndpoint(EndpointReference endpointReference, List<Endpoint> endpoints) {    
             
        Endpoint matchedEndpoint = null;
        
        if (endpointReference.getReference().getName().startsWith("$self$.")){
            // just select the first one and don't do any policy matching
            matchedEndpoint = endpoints.get(0);
        } else {
            // find the first endpoint that matches this endpoint reference
            for (Endpoint endpoint : endpoints){
                if (haveMatchingPolicy(endpointReference, endpoint) &&
                    haveMatchingInterfaceContracts(endpointReference, endpoint)){
                    matchedEndpoint = endpoint;
                    break;
                }
            }
        }
        
        if (matchedEndpoint == null){
            return null;
        }
        
        endpointReference.setTargetEndpoint(matchedEndpoint);
        endpointReference.setBinding(endpointReference.getTargetEndpoint().getBinding());
        endpointReference.setStatus(EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED);
        endpointReference.setUnresolved(false);
        
        return null;
    }

    /**
     * Selects a callback endpoint from a list of possible candidates
     * 
     * @param endpointReference
     * @param endpoints
     */
    private Problem selectCallbackEndpoint(EndpointReference endpointReference, List<Endpoint> endpoints) {

        Problem problem = null;
        
        // find the first callback endpoint that matches a callback endpoint reference
        // at the service
        Endpoint matchedEndpoint = null;
        match:
        for ( EndpointReference callbackEndpointReference : endpointReference.getTargetEndpoint().getCallbackEndpointReferences()){
            for (Endpoint endpoint : endpoints){
                if (haveMatchingPolicy(callbackEndpointReference, endpoint) &&
                    haveMatchingInterfaceContracts(callbackEndpointReference, endpoint)){
                    matchedEndpoint = endpoint;
                    break match;
                }
            }
        }
        
        if (matchedEndpoint == null){
            return null;
        }
        
        endpointReference.setCallbackEndpoint(matchedEndpoint);
        
        return problem;
    }

    /**
     * Determine if endpoint reference and endpoint policies match. We know by this stage
     * that 
     *   - a given policy set will only contain expression from a single language
     *   - a given endpoint or endpoint reference's policy sets will only contain
     *     expresions from a single language
     */
    private boolean haveMatchingPolicy(EndpointReference endpointReference, Endpoint endpoint){
        
        ComponentReference reference = endpointReference.getReference();
        ComponentService service = endpoint.getService();

        // if no policy sets or intents are present then they match
        if ((endpointReference.getRequiredIntents().size() == 0) &&
            (endpoint.getRequiredIntents().size() == 0) &&
            (endpointReference.getPolicySets().size() == 0) &&
            (endpoint.getPolicySets().size() == 0)) {
            return true;
        }
        
        // if one of the policy set collections is empty while the 
        // other isn't then then don't match
        if (((endpointReference.getPolicySets().size() == 0) &&
             (endpoint.getPolicySets().size() != 0)) ||
            ((endpointReference.getPolicySets().size() != 0) &&
             (endpoint.getPolicySets().size() == 0))) {
            return false;
        }        
                
        // if the intent names don't match then the policies won't match
        // TODO - is this a valid check? We used to rely on this in the 
        //        builder. Not sure who added it. Need to review.
/* TUSCANY-3426 - The build phase doesn't seen to promote the intents properly        
        Set<Intent> referenceIntentSet = new HashSet<Intent>(reference.getRequiredIntents());
        Set<Intent> serviceIntentSet = new HashSet<Intent>(service.getRequiredIntents());
        if (!referenceIntentSet.equals(serviceIntentSet)){
            return false;
        }
*/
        
        // If policy set QNames from epr and er match exactly then the reference and 
        // service policies are compatible
        Set<PolicySet> referencePolicySet = new HashSet<PolicySet>(endpointReference.getPolicySets());
        Set<PolicySet> servicePolicySet = new HashSet<PolicySet>(endpoint.getPolicySets());
        if(referencePolicySet.equals(servicePolicySet)){
            return true;
        }
        
        
        // if policy set language at ep and epr are not then there is no
        // match. We get the policy language by looking at the first expression
        // of the first policy set. By this stage we know that all the policy sets
        // in an endpoint or endpoint reference will use a single language
        QName eprLanguage = endpointReference.getPolicySets().get(0).getPolicies().get(0).getName();
        QName epLanguage = endpoint.getPolicySets().get(0).getPolicies().get(0).getName();
          
        if(!eprLanguage.equals(epLanguage)){
            return false;
        }
        
        // now do a policy specific language match 
        PolicyBuilder builder = builders.getPolicyBuilder(eprLanguage);
        if (builder != null) {
            // TODO - where to get builder context from?
            BuilderContext builderContext = new BuilderContext(monitor);
            return builder.build(endpointReference, endpoint, builderContext);
        } else {
            return false;
        }
    }
    
    /**
     * Determine if endpoint reference and endpoint interface contracts match 
     */
    private boolean haveMatchingInterfaceContracts(EndpointReference endpointReference, Endpoint endpoint){
        if (endpointReference.getReference().getInterfaceContract() == null){
            return true;
        }
        
        // TODO - is there a better test for this. Would have to cast to the
        //        correct iface type to get to the resolved flag
        if (endpoint.getComponentServiceInterfaceContract().getInterface().getOperations().size() == 0){
            // the interface contract is likely remote but unresolved
            // we discussed this on the ML and decided that we could
            // live with this for the case where there is no central matching of references
            // to services. Any errors will be detected when the message flows. 
            return true;
        }
             
        return interfaceContractMapper.isCompatible(endpointReference.getReference().getInterfaceContract(), 
                                                    endpoint.getComponentServiceInterfaceContract());
    }
    
    /**
     * Checks to see if the registry has been updated since the reference was last matched
     * 
     * @return true is the registry has changed
     */
    public boolean isOutOfDate(EndpointRegistry endpointRegistry, EndpointReference endpointReference) {
        Endpoint te = endpointReference.getTargetEndpoint();
        if (te!= null && !te.isUnresolved() && te.getURI()!= null) {
            List<Endpoint> endpoints = endpointRegistry.findEndpoint(endpointReference);
            return ! endpoints.contains(endpointReference.getTargetEndpoint());
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
